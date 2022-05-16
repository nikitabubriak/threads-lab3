package FileSearch;

import java.io.File;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;

public class FileSearch {
    String searchFilename(Document document, String searchedWord) {
        String name = document.getName();

        Pattern p =  Pattern.compile(Pattern.quote(searchedWord), Pattern.CASE_INSENSITIVE);
        if(p.matcher(name).find()) {
            return document.getPath();
        }
        return "";
    }

    class DocumentSearchTask extends RecursiveTask<List<String>> {
        private final Document document;
        private final String searchedWord;

        DocumentSearchTask(Document document, String searchedWord) {
            super();
            this.document = document;
            this.searchedWord = searchedWord;
        }

        @Override
        protected List<String> compute() {
            return Collections.singletonList(searchFilename(document, searchedWord));
        }
    }

    class FolderSearchTask extends RecursiveTask<List<String>> {
        private final Folder folder;
        private final String searchedWord;

        FolderSearchTask(Folder folder, String searchedWord) {
            super();
            this.folder = folder;
            this.searchedWord = searchedWord;
        }

        @Override
        protected List<String> compute() {
            List<String> filePaths = new ArrayList<>(Collections.emptyList());

            List<RecursiveTask<List<String>>> forks = new LinkedList<>();
            for (Folder subFolder : folder.getSubFolders()) {
                FolderSearchTask task = new FolderSearchTask(subFolder, searchedWord);
                forks.add(task);
                task.fork();
            }
            for (Document document : folder.getDocuments()) {
                DocumentSearchTask task = new DocumentSearchTask(document, searchedWord);
                forks.add(task);
                task.fork();
            }
            for (RecursiveTask<List<String>> task : forks) {
                filePaths.addAll(task.join());
            }
            for (int i = 0; i < filePaths.size(); i++) {
                if (filePaths.get(i) == ""){
                    filePaths.remove(i);
                }
            }
            return filePaths;
        }
    }

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    List<String> getFilePaths(Folder folder, String searchedWord) {
        return forkJoinPool.invoke(new FolderSearchTask(folder, searchedWord));
    }

    public static void main(String[] args) {
        String keyword = "program";
        String pathname = "E:\\KPI\\Threads\\files\\a";

        FileSearch fileSearch = new FileSearch();
        Folder folder = Folder.fromDirectory(new File(pathname));
        List<String> paths = fileSearch.getFilePaths(folder, keyword);

        System.out.println("\nFiles with \"" + keyword + "\" in their name found in directory " + pathname + "\n");
        for (int i = 0; i < paths.size(); i++) {
            System.out.println(paths.get(i));
        }
    }
}