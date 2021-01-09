package datasource.nitrite;

import datasource.CategoryRepo;
import datasource.ConfigRepo;
import datasource.TemplateRepo;
import model.Template;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import util.Utils;

import java.io.IOException;
import java.util.*;

import static org.dizitart.no2.filters.Filters.and;
import static org.dizitart.no2.filters.Filters.eq;

public class NitriteDataStore implements ConfigRepo, CategoryRepo, TemplateRepo {
    private final Nitrite db;
    private final Random rand = new Random();

    public NitriteDataStore(String user, String pw, String pathToDbStore) {
        db = Nitrite.builder()
                .filePath(pathToDbStore)
                .openOrCreate(user, pw);
    }

    @Override
    public String getRandomFromCategory(String guild, String category) {
        Cursor result = db.getCollection("Categories").find(and(eq("GUILD", guild), eq("CATEGORY", category)));
        Document document = result.firstOrDefault();
        List<String> items = document.get("ITEMS", List.class);
        return items.get(rand.nextInt(items.size()));
    }

    @Override
    public void addToCategory(String guild, String category, String entry) throws IOException {
        addToCategoryBulk(guild, category, Collections.singletonList(entry));
    }

    @Override
    public void addToCategoryBulk(String guild, String category, List<String> entries) throws IOException {
        NitriteCollection col = db.getCollection("Categories");
        Cursor result = col.find(and(eq("GUILD", guild), eq("CATEGORY", category)));

        Document document;
        if (result.idSet().isEmpty()) {
            document = createCategoryHelper(guild, category);
        } else {
            document = result.firstOrDefault();
        }

        List<String> items = document.get("ITEMS", List.class);

        if (items == null) {
            items = new ArrayList<>();
        }

        items.addAll(entries);
        document.put("ITEMS", items);
        col.update(document);
    }

    @Override
    public void createCategory(String guild, String category) throws IOException {
        createCategoryHelper(guild, category);
    }

    private Document createCategoryHelper(String guild, String category) {
        NitriteCollection col =  db.getCollection("Categories");
        Document document = Document.createDocument("GUILD", guild);
        document.put("CATEGORY", category);
        col.insert(document);
        return document;
    }

    @Override
    public String getEntries(String guild, String category) throws IOException {
        Cursor result = db.getCollection("Categories").find(and(eq("GUILD", guild), eq("CATEGORY", category)));
        Document document = result.firstOrDefault();
        if (document != null) {
            List<String> items = document.get("ITEMS", List.class);
            StringBuilder builder = new StringBuilder();
            if (items != null)
                items.forEach(item -> builder.append(item).append("\n"));
            return builder.toString();
        }
        return null;
    }

    @Override
    public String getCategories(String guild) throws IOException {
        Cursor result = db.getCollection("Categories").find(eq("GUILD", guild));
        StringBuilder builder = new StringBuilder();
        for (Document doc : result) {
            builder.append(doc.get("CATEGORY", String.class)).append("\n");
        }
        return builder.toString();
    }

    @Override
    public void close() {

    }

    @Override
    public Template getTemplate(String guild, UUID id) {
        Cursor result = db.getCollection("Templates").find(and(eq("GUILD", guild)));
        for (Document doc : result) {
            Template t = doc.get("TEMPLATE", Template.class);
            if (id.equals(t.getUuid())) {
                return t;
            }
        }
        return null;
    }

    @Override
    public List<Template> getTemplates(String guild) {
        Cursor result = db.getCollection("Templates").find(eq("GUILD", guild));
        List<Template> templates = new ArrayList<>();
        for (Document doc : result) {
            templates.add(doc.get("TEMPLATE", Template.class));
        }
        return templates;
    }

    @Override
    public List<UUID> getIds(String guild) {
        Cursor result = db.getCollection("Templates").find(eq("GUILD", guild));
        List<UUID> uuids = new ArrayList<>();
        for (Document doc : result) {
            uuids.add(doc.get("TEMPLATE", Template.class).getUuid());
        }
        return uuids;
    }

    @Override
    public int addTemplate(String guild, String templateSentence) {
        long id = 0L;
        Cursor result = db.getCollection("Templates").find(eq("GUILD", guild));
        for (Document doc : result) {
            id = Long.max(doc.get("TEMPLATE", Template.class).getId(), id);
        }

        NitriteCollection col =  db.getCollection("Templates");
        Document document = Document.createDocument("GUILD", guild);
        document.put("TEMPLATE", new Template(id + 1, Utils.stringToTokenList(templateSentence)));
        col.insert(document);
        return 0;
    }

    @Override
    public String getStartFlag(String guild) {
        return "$";
    }

    @Override
    public void setStartFlag(String guild, String flag) {

    }

    @Override
    public Long getModMailChannel(String guild) {
        return 93558188644499467L;
    }

    @Override
    public void setModMailChannel(String guild, Long channelId) {

    }
}
