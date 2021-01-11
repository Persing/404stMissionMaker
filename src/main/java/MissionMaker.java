import datasource.nitrite.NitriteDataStore;
import exception.RepoInitializationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MissionMaker {

    // TODO: Split this into a seperate module to allow the repos to be separate modules
    public static void main(String[] args) throws IOException, RepoInitializationException {
        String pathToProperties = args[0];
        String root = "." + File.separator + "src" + File.separator + "main" + File.separator + "resources";
//        CategoryRepo categoryRepo = new FileStoreCategoryRepo(root);
//        TemplateRepo templateRepo = new FileStoreTemplateRepo(root + File.separator + "templates",
//                root + File.separator + "guildTemplateMaps");
        Properties prop = new Properties();
        try(FileInputStream input = new FileInputStream(pathToProperties)) {
            prop.load(input);
        }

        if (prop.isEmpty()) {
            System.out.println("No properties file found ");
            System.exit(1);
        }

        NitriteDataStore dataStore = new NitriteDataStore(prop.getProperty("db.user"), prop.getProperty("db.password"), prop.getProperty("db.location"));
        new JdaEventHandler().start(prop.getProperty("jda.key"), dataStore, dataStore, dataStore);

    }
}
