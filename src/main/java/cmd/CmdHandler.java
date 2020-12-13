package cmd;

import cmd.response.CmdResponse;
import model.Template;

import java.util.List;

public interface CmdHandler {

    CmdResponse<String> addTemplate(String guild, String sentence);

    CmdResponse<Template> getTemplate(String guild, Long id);

    CmdResponse<Template> getDefaultTemplate(String guild);

    CmdResponse<List<Template>> getAllTemplates(String guild);

    CmdResponse<String> generateSentence(String guild, Long templateId);

    CmdResponse<String> generateSentence(String guild);

    CmdResponse<String> addEntry(String guild, String category, String entry);
}
