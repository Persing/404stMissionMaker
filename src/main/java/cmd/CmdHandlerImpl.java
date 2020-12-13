package cmd;

import cmd.response.CmdResponse;
import datasource.CategoryRepo;
import datasource.TemplateRepo;
import model.Template;
import util.Utils;

import java.io.IOException;
import java.util.List;

public class CmdHandlerImpl implements CmdHandler {
    private final CategoryRepo categoryRepo;
    private final TemplateRepo templateRepo;

    public CmdHandlerImpl(CategoryRepo categoryRepo, TemplateRepo templateRepo) {
        this.categoryRepo = categoryRepo;
        this.templateRepo = templateRepo;
    }

    @Override
    public CmdResponse<String> addTemplate(String guild, String sentence) {
        templateRepo.addTemplate(guild,sentence);
        return new CmdResponse<>(sentence, 0, null);
    }

    @Override
    public CmdResponse<Template> getTemplate(String guild, Long id) {
        Template t = templateRepo.getTemplate(guild, id);
        int status = 0;
        String message = null;
        if (t == null) {
            status = 404;
            message = "Requested Template Not Found";
        }
        return new CmdResponse<>(t, status, message);
    }

    @Override
    public CmdResponse<Template> getDefaultTemplate(String guild) {
        List<Long> ids = templateRepo.getIds(guild);
        if (ids == null || ids.isEmpty()) {
            return new CmdResponse<>(null, 404, "No templates founds");
        }

        return getTemplate(guild, ids.get(0));
    }

    @Override
    public CmdResponse<List<Template>> getAllTemplates(String guild) {
        return new CmdResponse<>(templateRepo.getTemplates(guild), 0, null);
    }

    @Override
    public CmdResponse<String> generateSentence(String guild, Long templateId) {
        CmdResponse<Template> templateResponse = getTemplate(guild, templateId);

        if (templateResponse.status != 0) {
            return new CmdResponse<>(null, 404, "Could not find template for provided id");
        }

        String result = Utils.SentenceFromTemplate(guild, templateResponse.payload, categoryRepo);
        return new CmdResponse<>(result, 0, null);
    }

    @Override
    public CmdResponse<String> generateSentence(String guild) {
        CmdResponse<Template> templateResponse = getDefaultTemplate(guild);

        if (templateResponse.status != 0) {
            return new CmdResponse<>(null, 404, "Could not find template for provided id");
        }

        String result = Utils.SentenceFromTemplate(guild, templateResponse.payload, categoryRepo);
        return new CmdResponse<>(result, 0, null);
    }

    @Override
    public CmdResponse<String> addEntry(String guild, String category, String entry) {
        try {
            categoryRepo.addToCategory(guild, category, entry);
            return new CmdResponse<>("Added {" + entry + "} to [" + category +"]", 0, null);
        } catch (IOException e) {
            // TODO: localize string
            return new CmdResponse<>(null, 500, "Unable to add entry {"
                    + entry + "} to category [" + category +"]");
        }
    }


}
