package datasource.FileStore;

import datasource.TemplateRepo;
import exception.RepoInitializationException;
import model.Template;
import model.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileStoreTemplateRepo implements TemplateRepo {
    // TODO: replace with a real cache
    HashMap<Long, Template> templates = new HashMap<>();
    HashMap<String, List<Long>> guildTemplateMap = new HashMap<>();

    //TODO: make a factory to get instances instead of allowing constructor calls
    public FileStoreTemplateRepo(String pathToDataStore, String pathToGuildTemplateMapFile) throws RepoInitializationException {
        try {
            populateTemplates(pathToDataStore);
            populateGuildTemplateMap(pathToGuildTemplateMapFile);
        } catch (IOException e) {
            // TODO replace with real logger
            System.out.println("ERROR - File not found and could not be created");
            throw new RepoInitializationException();
        }
    }

    /**
     * Populates the in memory template store with data from the file at the specified path.
     *
     * @param sourceFile The file to read data in from
     * @throws IOException if the file file does not exist and can't be created
     */
    private void populateTemplates(String sourceFile) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Path.of(sourceFile));
            for (String s : lines) {
                String[] pieces = s.split(" ", 2);
                List<Token> tokens = Arrays.stream(pieces[1].split(" "))
                        .sequential()
                        .map(Token::fromString)
                        .collect(Collectors.toList());

                Template template = new Template(Long.parseLong(pieces[0]), tokens);
                templates.put(template.getId(), template);
            }
        } catch (IOException e) {
            Files.createFile(Path.of(sourceFile));
        }
    }

    /**
     * Populates the map that links the Guild to the templates owned by them
     *
     * @param sourceFilePath The file to read data in from
     * @throws IOException if the file file does not exist and can't be created
     */
    private void populateGuildTemplateMap(String sourceFilePath) throws IOException {
        try {
            List<String> lines = Files.readAllLines(Path.of(sourceFilePath));
            for (String s : lines) {
                String[] entry = s.split(" ", 2);
                guildTemplateMap.put(entry[0],
                        Arrays.stream(entry[1].split(" "))
                                .map(Long::parseLong)
                                .collect(Collectors.toList()));
            }
        } catch (IOException e) {
            Files.createFile(Path.of(sourceFilePath));
        }
    }

    @Override
    public Template getTemplate(String guild, Long id) {
        List<Long> templateIds = guildTemplateMap.get(guild);

        // Early return for no template ids found for guild
        if (templateIds.isEmpty()) {
            return null;
        }

        // No template with provided id found in the list of guild ids
        if (!templateIds.contains(id)) {
            return null;
        }

        return templates.get(id);
    }

    @Override
    public List<Template> getTemplates(String guild) {
        List<Long> templateIds = guildTemplateMap.get(guild);

        // Early return for no template ids found for guild
        if (templateIds.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        ArrayList<Template> guildTemplates = new ArrayList<>(templateIds.size());
        for (Long id : templateIds) {
            if (templates.containsKey(id)) {
                guildTemplates.add(templates.get(id));
            }
        }

        return guildTemplates;
    }

    @Override
    public List<Long> getIds(String guild) {
        return guildTemplateMap.get(guild);
    }

    //TODO: write datastore changes back out to disk
    @Override
    public int addTemplate(String guild, String templateSentence) {

        // Get the max already used id + 1
        Long id = templates.keySet().stream().max(Long::compareTo).orElseGet(() -> 1L) + 1;

        // Create the template
        Template template = new Template(id, templateSentence);

        // Store the template in the datastore
        templates.put(id, template);

        // Retrieve the guild template list, if it doesn't exist get an empty array
        List<Long> guildTemplateList = guildTemplateMap.getOrDefault(guild, new ArrayList<>());

        // add the new template id to the guild template id set and add it back to the guild template map
        guildTemplateList.add(id);
        guildTemplateMap.put(guild,guildTemplateList);



        return 0;
    }

    @Override
    public void close() {
        // No action needed
    }
}
