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

        switch (cmd) {
            case "ADDTEMPLATE":
            case "AT":
                return callAddTemplate(guild, arg);
            case "MAKE":
            case "M":
                return callMake(guild, arg);
            case "ADDENTRY":
            case "AE":
                return callAddEntry(guild, arg);
            case "ADDENTRYBULK":
            case "AEB":
            case "BULKADDENTRY":
            case "BAE":
                return callAddEntryBulk(guild, arg);
            case "LISTTEMPLATES":
            case "LT":
                return callListTemplates(guild);
            case "LISTENTRIES":
            case "LE":
                return callListEntries(guild, arg);
            case "LISTCATEGORIES":
            case "LC":
                return callListCategories(guild);
            case "HELP":
            case "H":
                return callHelp(arg);
            default:
                return new CmdResponse(null, 404, "");
        }
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
                    " The available commands are: \n" +
                            "AddTemplate (AT)\n" +
                            "AddEntry (AE)\n" +
                            "AddEntryBulk (BulkAddEntry) (AEB) (BAE)\n" +
                            "ListTemplates (LT)\n" +
                            "ListEntries (LE)\n" +
                            "ListCategories (LC)\n\n" +
                            "For more information on using each command use HELP <CMD>",
                    0,
                    null
            );
        }

        String msg = "";
        switch (arg.toUpperCase(Locale.ROOT)) {
            case "ADDTEMPLATE" :
            case "AT" :
                msg = "Type your sentence after the cmd any words in [ ] will be considered a category. " +
                "Categories are limited to single words with no spaces.\n(IE. addtemplate [objectives] with [things] while [conditions])";
                break;
            case "MAKE" :
            case "M" : msg = "Simply call this cmd and it will use the first template to build a random mission prompt. " +
                "support for  picking desired template coming soon!";
                break;
            case "ADDENTRY" :
            case "AE" : msg = "Adds an entry to a category if the category does not exist yet it will be created. " +
                "simply list the category then the word you want to add to it.\n(IE. addentry objectives Convoy) " +
                "this will add the word \"convoy\" to the category \"objectives\"";
                break;
            case "ADDENTRYBULK" :
            case "AEB" :
            case "BULKADDENTRY" :
            case "BAE" : msg =
                "Same as add entry but can add multiple entries at once. The entries should be new line separated."+
                "(IE. bulkaddentry testing"+
                "test\n"+
                "hello\n"+
                "world\n"+
                "adios\n"+
                "mondo)";
                break;
            case "LISTTEMPLATES":
            case "LT" : msg = "Lists the available templates";
                break;
            case "LISTENTRIES" :
            case "LE" : msg = "Lists the available entries in the provided category." +
                "\n(IE. ListEntries objectives) This will list all entries in the category \"objectives\"";
                break;
            case "LISTCATEGORIES" :
            case "LC" : msg = "Lists available categories.";
                break;
            default : msg = "\"" + arg + "\" is not a recognized cmd";
        };

        return new CmdResponse<>(msg, 0, "");
    }
}
