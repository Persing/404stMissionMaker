package datasource.FileStore;

import datasource.CategoryRepo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;

public class FileStoreCategoryRepo implements CategoryRepo {
    private static final Random rand = new Random();
    private final String categoryFolderRootPath;

    // TODO: change to RepoInitializationRepo
    public FileStoreCategoryRepo(String categoryFolderRootPath) throws IOException {
        Path root = Path.of(categoryFolderRootPath);

        // Test if directory exists
        if (Files.isDirectory(root)) {
            this.categoryFolderRootPath = categoryFolderRootPath;
            return;
        }

        Files.createDirectory(root);
        this.categoryFolderRootPath = categoryFolderRootPath;
    }

    @Override
    public String getRandomFromCategory(String guild, String category) {
        try {
            List<String> items = Files.readAllLines(
                    Path.of(categoryFolderRootPath + File.separator + guild + File.separator + category));
            return items.get(rand.nextInt(items.size() - 1));
        } catch (IOException e) {
            // TODO: localize user facing strings
            return "<!No category [" + category + "] found!>";
        }
    }

    @Override
    public void addToCategory(String guild, String category, String entry) throws IOException {
        Path guildPath = Path.of(categoryFolderRootPath + File.separator + guild);
        Path catPath = Path.of(guildPath.toString(), category);
        if (!Files.exists(catPath)) {
            Files.createDirectories(guildPath);
            Files.createFile(catPath);
        }
        Files.writeString(catPath,
                entry,
                StandardOpenOption.APPEND);
    }

    @Override
    public void createCategory(String guild, String category) throws IOException {
        Path guildPath = Path.of(categoryFolderRootPath + File.separator + guild);
        Path catPath = Path.of(guildPath.toString(), category);
        if (!Files.exists(catPath)) {
            Files.createDirectories(guildPath);
            Files.createFile(catPath);
        }
    }


    @Override
    public void close() {
        // No action needed
    }
}
