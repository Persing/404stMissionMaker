import datasource.nitrite.NitriteDataStore;
import exception.RepoInitializationException;

import java.io.File;
import java.io.IOException;

public class MissionMaker {

    // TODO: Split this into a seperate module to allow the repos to be separate modules
    public static void main(String[] args) throws IOException, RepoInitializationException {
        String root = "." + File.separator + "src" + File.separator + "main" + File.separator + "resources";
//        CategoryRepo categoryRepo = new FileStoreCategoryRepo(root);
//        TemplateRepo templateRepo = new FileStoreTemplateRepo(root + File.separator + "templates",
//                root + File.separator + "guildTemplateMaps");

        NitriteDataStore dataStore = new NitriteDataStore(args[1], args[2], root + File.separator + "nitrite.db");

        new JdaEventHandler().start(args[0], dataStore, dataStore, dataStore);
    }
}
