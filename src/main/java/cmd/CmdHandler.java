package cmd;

import cmd.response.CmdResponse;
import model.Template;

import java.util.List;
import java.util.UUID;

public interface CmdHandler {

    CmdResponse<String> addTemplate(String guild, String sentence);

    CmdResponse<Template> getTemplate(String guild, UUID id);

    CmdResponse<Template> getDefaultTemplate(String guild);

    CmdResponse<List<Template>> getAllTemplates(String guild);

    CmdResponse<String> generateSentence(String guild, UUID templateId);

    CmdResponse<String> generateSentence(String guild);

    CmdResponse<String> addEntry(String guild, String category, String entry);

    CmdResponse<String> addEntryBulk(String guild, String category, List<String> entries);

    CmdResponse<String> getEntries(String guild, String category);

    CmdResponse<String> getCategories(String guild);
}
