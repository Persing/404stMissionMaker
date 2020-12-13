package cmd.mapper;

import cmd.CmdHandler;
import cmd.response.CmdResponse;
import model.Template;

import java.util.List;
import java.util.Locale;

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
            case "ADDTEMPLATE" :
            case "AT" :
                return callAddTemplate(guild, arg);
            case "MAKE" :
            case "M" :
                return callMake(guild, arg);
            case "ADDENTRY" :
            case "AE" :
                return callAddEntry(guild, arg);
            case "LISTTEMPLATES" :
            case "LT" :
                return callListTemplates(guild);
            default: return new CmdResponse(null, 404, "");
        }
    }

    private CmdResponse<String> callMake(String guild, String args) {
        if (args.length() < 1) {
            return cmdHandler.generateSentence(guild);
        }

        try {
            Long id = Long.parseLong(args);
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

    private CmdResponse<List<Template>> callListTemplates(String guild) {
        return cmdHandler.getAllTemplates(guild);
    }
}
