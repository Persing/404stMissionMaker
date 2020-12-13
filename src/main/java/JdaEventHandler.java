import cmd.CmdHandlerImpl;
import cmd.mapper.MessageCmdExecuter;
import datasource.CategoryRepo;
import datasource.TemplateRepo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class JdaEventHandler extends ListenerAdapter {
    private CategoryRepo categoryRepo;
    private TemplateRepo templateRepo;
    private MessageCmdExecuter cmdExecuter;

    public void start(CategoryRepo categoryRepo, TemplateRepo templateRepo) {
        this.categoryRepo = categoryRepo;
        this.templateRepo = templateRepo;
        this.cmdExecuter = new MessageCmdExecuter(new CmdHandlerImpl(categoryRepo, templateRepo));
        JDA jda = null;
        try {
            jda = JDABuilder.createDefault("")
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
            //TODO: abstract the calling of cmds
            if(msg.startsWith("$")) {
                String reply = cmdExecuter.executeCmd(guild.getId(), msg.substring(1));
                textChannel.sendMessage(reply).queue();
            }

        } else if (event.isFromType(ChannelType.PRIVATE)) {
            PrivateChannel privateChannel = event.getPrivateChannel();
            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
        }
    }
}
