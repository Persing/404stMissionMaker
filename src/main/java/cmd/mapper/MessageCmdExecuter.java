package cmd.mapper;

import cmd.CmdHandler;
import cmd.response.CmdResponse;
import model.Template;

import java.util.*;

public class MessageCmdExecuter {
    private final CmdHandler cmdHandler;

    public MessageCmdExecuter(CmdHandler cmdHandler) {
        this.cmdHandler = cmdHandler;
    }

    public String executeCmd(String guild, String input) {
        return getReplyFromCmdResponse(parseAndExecuteCmd(guild, input));
    }

    private String getReplyFromCmdResponse(CmdResponse cmdResponse) {
        if (cmdResponse == null) {
            return "Error encountered during execution";
        }

        if (0 != cmdResponse.status) {
            return cmdResponse.message != null ? cmdResponse.message : "Error encountered during execution";
        }

        return cmdResponse.payload.toString();
    }

    private CmdResponse parseAndExecuteCmd(String guild, String fullCmd) {
        String[] pieces = fullCmd.split(" ", 2);
        String cmd = pieces[0].toUpperCase(Locale.ROOT);
        String arg = "";
        if (pieces.length > 1) {
            arg = pieces[1];
        }

        return switch (cmd) {
            case "ADDTEMPLATE", "AT" -> callAddTemplate(guild, arg);
            case "MAKE", "M" -> callMake(guild, arg);
            case "ADDENTRY", "AE" -> callAddEntry(guild, arg);
            case "ADDENTRYBULK", "AEB", "BULKADDENTRY", "BAE" -> callAddEntryBulk(guild, arg);
            case "LISTTEMPLATES", "LT" -> callListTemplates(guild);
            case "LISTENTRIES", "LE" -> callListEntries(guild, arg);
            case "LISTCATEGORIES", "LC" -> callListCategories(guild);
            case "HELP", "H" -> callHelp(arg);
            default -> new CmdResponse(null, 404, "");
        };
    }

    private CmdResponse<String> callMake(String guild, String args) {
        if (args.length() < 1) {
            return cmdHandler.generateSentence(guild);
        }

        try {
            UUID id = UUID.fromString(args);
            return cmdHandler.generateSentence(guild, id);
        } catch (NumberFormatException e) {
            return cmdHandler.generateSentence(guild);
        }
    }

    private CmdResponse<String> callAddTemplate(String guild, String args) {
        return cmdHandler.addTemplate(guild, args);
    }

    private CmdResponse<String> callAddEntry(String guild, String args) {
        String[] pieces = args.split(" ",2);
        if (pieces.length != 2) {
            return new CmdResponse<String>(null,
                    400,
                    "Unable to add entry. Please specify the category followed by the entry with which you want to. These should be space separated");
        }

        return cmdHandler.addEntry(guild, pieces[0], pieces[1]);
    }

    private CmdResponse<String> callAddEntryBulk(String guild, String args) {
        String[] pieces = args.split("\\s", 2);
        if (pieces.length != 2) {
            return new CmdResponse<String>(null,
                    400,
                    "Unable to add entry. Please specify the category followed by the list of entries with which you want to. These should be new line separated");
        }

        String[] entries = pieces[1].split("\\n");

        return cmdHandler.addEntryBulk(guild, pieces[0], Arrays.asList(entries));
    }

    private CmdResponse<List<Template>> callListTemplates(String guild) {
        return cmdHandler.getAllTemplates(guild);
    }

    private CmdResponse<String> callListEntries(String guild, String args) {
        args = args.trim();
        if (args.isEmpty()) {
            return new CmdResponse<>(null, 404, "No category supplied");
        }

        return cmdHandler.getEntries(guild, args);
    }

    private CmdResponse<String> callListCategories(String guild) {
        return cmdHandler.getCategories(guild);
    }

    private CmdResponse<String> callHelp(String arg) {
        if (arg.isEmpty()) {
            return new CmdResponse<>(
                    """
                            The available commands are:
                            AddTemplate (AT)
                            AddEntry (AE)
                            AddEntryBulk (BulkAddEntry) (AEB) (BAE)
                            ListTemplates (LT)
                            ListEntries (LE)
                            ListCategories (LC)
                                        
                            For more information on using each command use HELP <CMD> 
                            """, 0, null
            );
        }

        String msg = switch (arg.toUpperCase(Locale.ROOT)) {
            case "ADDTEMPLATE", "AT" -> "Type your sentence after the cmd any words in [ ] will be considered a category. " +
                    "Categories are limited to single words with no spaces.\n(IE. addtemplate [objectives] with [things] while [conditions])";
            case "MAKE", "M" -> "Simply call this cmd and it will use the first template to build a random mission prompt. " +
                    "support for  picking desired template coming soon!";
            case "ADDENTRY", "AE" -> "Adds an entry to a category if the category does not exist yet it will be created. " +
                    "simply list the category then the word you want to add to it.\n(IE. addentry objectives Convoy) " +
                    "this will add the word \"convoy\" to the category \"objectives\"";
            case "ADDENTRYBULK", "AEB", "BULKADDENTRY", "BAE" -> """
                    Same as add entry but can add multiple entries at once. The entries should be new line separated.
                    (IE. bulkaddentry testing
                    test
                    hello
                    world
                    adios
                    mondo)
                    """;
            case "LISTTEMPLATES", "LT" -> "Lists the available templates";
            case "LISTENTRIES", "LE" -> "Lists the available entries in the provided category." +
                    "\n(IE. ListEntries objectives) This will list all entries in the category \"objectives\"";
            case "LISTCATEGORIES", "LC" -> "Lists available categories.";
            default -> "\"" + arg + "\" is not a recognized cmd";
        };

        return new CmdResponse<>(msg, 0, "");
    }
}
