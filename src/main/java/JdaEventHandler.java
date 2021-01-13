import cmd.CmdHandlerImpl;
import cmd.mapper.MessageCmdExecuter;
import datasource.CategoryRepo;
import datasource.ConfigRepo;
import datasource.TemplateRepo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.entities.EmoteImpl;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JdaEventHandler extends ListenerAdapter {
    public static final String THIRD_PLACE_MEDAL = "\uD83E\uDD49";
    private CategoryRepo categoryRepo;
    private TemplateRepo templateRepo;
    private ConfigRepo configRepo;
    private MessageCmdExecuter cmdExecuter;

    public void start(String key, ConfigRepo configRepo, CategoryRepo categoryRepo, TemplateRepo templateRepo) {
        this.configRepo = configRepo;
        this.categoryRepo = categoryRepo;
        this.templateRepo = templateRepo;
        this.cmdExecuter = new MessageCmdExecuter(new CmdHandlerImpl(categoryRepo, templateRepo));
        JDA jda = null;
        try {
            jda = JDABuilder.createDefault(key)
                    .addEventListeners(this)
                    .build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        // TODO: replace with logger
        System.out.println("Finished Building JDA");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        JDA jda = event.getJDA();
        long responseNumber = event.getResponseNumber();

        User author = event.getAuthor();

        if (author.isBot()) return;

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        String msg = message.getContentDisplay();

        if (event.isFromType(ChannelType.TEXT)) {
            Guild guild = event.getGuild();
            TextChannel textChannel = event.getTextChannel();
            Member member = event.getMember();

            String name = message.isWebhookMessage() ? author.getName() : member.getEffectiveName();

            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);

            if(msg.startsWith(configRepo.getStartFlag(guild.getId()))) {
                if (msg.substring(1).toUpperCase(Locale.ROOT).startsWith("AWARD")) {
                    if (!message.getMentionedMembers().isEmpty()) {
                        reactToMentionedUsers(message, guild, textChannel, THIRD_PLACE_MEDAL);
                    }
                } else {
                    String reply = cmdExecuter.executeCmd(guild.getId(), msg.substring(1));
                    if (reply != null && !reply.isEmpty()) {
                        textChannel.sendMessage(reply).queue();
                    }
                }
            }

        } else if (event.isFromType(ChannelType.PRIVATE)) {
            PrivateChannel privateChannel = event.getPrivateChannel();
            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
            privateChannel.retrievePinnedMessages().queue(messages -> {
                TextChannel modMail = null;
                for (Message m : messages) {
                    if (!m.getMentionedChannels().isEmpty()) {
                        modMail = m.getMentionedChannels().get(0);
                        break;
                    }
                }

                if (modMail != null) {
                    Member member = modMail.getGuild().getMember(author);
                    String name = author.getName();
                    if (member != null) {
                        name = member.getEffectiveName();
                    }
                    modMail.sendMessage(String.format("[Request]<%s>: %s\n", name, msg))
                            .queue();
                } else {
                    privateChannel.sendMessage("No mod mail channel found please contact the admins directly")
                            .queue();
                }
            });
        }
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        // TODO ask to initialize
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (event.getMember() == null)
            return;

        event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.getHistoryFromBeginning(20).queue(messageHistory -> {
                privateChannel.deleteMessageById(messageHistory.getRetrievedHistory().get(0).getId()).queue();
            });
        });
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        Long chId = configRepo.getModMailChannel(event.getGuild().getName());
        // No mod-mail channel setup so dont send a welcome message
        if (chId == null) {
            return;
        }

        event.getMember()
                .getUser()
                .openPrivateChannel()
                .queue(privateChannel -> {
                    privateChannel.sendMessage("Welcome Trooper! If you can't find something just ask. " +
                            "We are glad to see you in <#"
                            + event.getGuild().getId() + "> "
                            + "Wanna run a simulation?").queue(message -> message.pin().queue());
                    privateChannel.sendMessage("Any questions here will be sent to the Mods for "
                            + event.getGuild().getName()
                            + ". They will replay back to you directly as soon as they are available :)").queue();
                });
    }

    private void reactToMentionedUsers(Message message, Guild guild, TextChannel textChannel, String reaction) {
        List<User> recipients = message.getMentionedUsers();
        textChannel.getHistoryBefore(message, 20).queue(messageHistory -> {
            List<Message> messages = messageHistory.getRetrievedHistory().stream()
                    .filter(message1 -> recipients.contains(message1.getAuthor()))
                    .collect(Collectors.toList());
            List<User> toBeAwarded = new ArrayList<>(recipients);
            for (Message message1 : messages) {
                if (toBeAwarded.contains(message1.getAuthor())) {
                    message1.addReaction(reaction).queue();
                    toBeAwarded.remove(message1.getAuthor());
                }
                if (toBeAwarded.isEmpty())
                    return;
            }

            // Could not find a recent message to award these users so send a message mentioning them with the reaction emoji
            if (!toBeAwarded.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                toBeAwarded.forEach(user -> builder.append("<@").append(user.getId()).append("> "));
                builder.append(reaction);
                textChannel.sendMessage(builder.toString()).queue();
            }
        });
    }
}
