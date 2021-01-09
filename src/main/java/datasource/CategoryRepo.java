package datasource;

import java.io.IOException;
import java.util.List;

public interface CategoryRepo extends Repo{
    String getRandomFromCategory(String guild, String category);

    void addToCategory(String guild, String category, String entry) throws IOException;

    void addToCategoryBulk(String guild, String category, List<String> entries) throws IOException;

    void createCategory(String guild, String category) throws IOException;

    String getEntries(String guild, String category) throws IOException;

    String getCategories(String guild) throws IOException;
}
