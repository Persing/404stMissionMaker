package datasource;

import java.io.IOException;

public interface CategoryRepo extends Repo{
    String getRandomFromCategory(String guild, String category);

    void addToCategory(String guild, String category, String entry) throws IOException;

    void createCategory(String guild, String category) throws IOException;
}
