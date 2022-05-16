package FileSearch;

import java.io.File;

public class Document {
    private final String path;
    private final String name;
    public String getPath() {
        return path;
    }
    public String getName() { return name; }

    Document(String path, String name) {
        this.path = path;
        this.name = name;
    }
    static Document fromFile(File file) {
        String path = file.getPath();
        String name = file.getName();

        return new Document(path, name);
    }
}