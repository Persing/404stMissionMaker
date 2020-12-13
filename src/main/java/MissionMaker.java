import datasource.CategoryRepo;
import datasource.FileStore.FileStoreCategoryRepo;
import datasource.FileStore.FileStoreTemplateRepo;
import datasource.TemplateRepo;
import exception.RepoInitializationException;

import java.io.File;
import java.io.IOException;

public class MissionMaker {

    public static void main(String[] args) throws IOException, RepoInitializationException {
        CategoryRepo categoryRepo = new FileStoreCategoryRepo("." + File.separator + "src" + File.separator + "main" + File.separator + "resources");
        TemplateRepo templateRepo = new FileStoreTemplateRepo("." + File.separator + "templates",
                "." + File.separator + "guildTemplateMaps");

        new JdaEventHandler().start(categoryRepo, templateRepo);
    }
}
