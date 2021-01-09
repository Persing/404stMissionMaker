package datasource;

import model.Template;

import java.util.List;
import java.util.UUID;

public interface TemplateRepo extends Repo{

    /**
     * Retrieves The specified Template from the specified guild
     *
     * @param guild
     * @param id
     * @return the template for with the specified id. null if nothing is found
     */
    Template getTemplate(String guild, UUID id);

    /**
     * Retrieves all available templates for specified guild
     *
     * @param guild The guild to retrieve templates for
     * @return The list of templates connected to the specified guild. Empty list if there is none found.
     */
    List<Template> getTemplates(String guild);

    /**
     * Retrieves all ids of templates connected with the specified guild.
     *
     * @param guild The guild to retrieve Template ids for.
     * @return The set of ids for Templates tied to the guild. Empty list if nothing is found.
     */
    List<UUID> getIds(String guild);

    /**
     * Adds a new template based on the template sentence for the specified guild
     *
     * @param guild
     * @param templateSentence
     * @return 0 for success, 1 for failure TODO: more codes
     */
    int addTemplate(String guild, String templateSentence);
}
