package datasource.FileStore;

import datasource.CategoryRepo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

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
        addToCategoryBulk(guild, category, Collections.singletonList(entry));
    }

    @Override
    public void addToCategoryBulk(String guild, String category, List<String> entries) throws IOException {
        Path guildPath = Path.of(categoryFolderRootPath + File.separator + guild);
        Path catPath = Path.of(guildPath.toString(), category);
        if (!Files.exists(catPath)) {
            Files.createDirectories(guildPath);
            Files.createFile(catPath);
        }
        StringBuilder builder = new StringBuilder();
        entries.forEach(entry -> builder.append(entry).append("\n"));
        Files.writeString(catPath,
                builder.toString(),
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

    public String getEntries(String guild, String category) throws IOException {
        Path path = Path.of(categoryFolderRootPath + File.separator + guild + File.separator + category);
        StringBuilder builder = new StringBuilder();
        Files.lines(path).forEach(l -> builder.append(l).append("\n"));
        return builder.toString();
    }

    @Override
    public String getCategories(String guild) throws IOException {
        Path root = Path.of(categoryFolderRootPath + File.separator + guild);
        StringBuilder builder = new StringBuilder();
        Files.walk(root, 1)
                .filter(p -> !root.equals(p))
                .map(Path::getFileName)
                .forEach(cat -> builder.append(cat).append("\n"));
        return builder.toString();
    }


    @Override
    public void close() {
        // No action needed
    }
}
