package WordLengthAnalysis;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WordLengthAnalysis extends RecursiveTask<int[]> {
    private final Path[] files;

    public WordLengthAnalysis(Path[] files) {
        this.files = files;
    }

    @Override
    protected int[] compute() {
        if (files.length == 0) {
            return new int[0];
        }
        if (files.length == 1) {
            return getWordLengths(files[0]);
        }
        else {
            WordLengthAnalysis task1 =
                    new WordLengthAnalysis(Arrays.copyOf(files, files.length - 1));
            WordLengthAnalysis task2 =
                    new WordLengthAnalysis(new Path[]{files[files.length - 1]});

            task2.fork();
            int[] t1 = task1.compute();
            int[] t2 = task2.join();
            int[] result;

            if (t1.length >= t2.length) {
                result = addArrays(t1,t2);
            }
            else {
                result = addArrays(t2,t1);
            }
            return result;
        }
    }

    private int[] addArrays(int[] a, int[] b) {
        int[] c = new int[a.length];
        for (int i = 0; i < c.length; i++) {
            if(i < b.length) {
                c[i] = a[i] + b[i];
            }
            else {
                c[i] = a[i];
            }
        }
        return c;
    }

    private int[] getWordLengths(Path file) {
        List<String> words = getAllWordsFromFile(file);

        int maxLength = words.stream()
                .mapToInt(String::length)
                .max().orElse(0);

        int[] wordLengths = new int[maxLength];

        for(int i = 0; i < maxLength; i++){
            int l = i;
            wordLengths[l] = (int) words.stream()
                    .filter(w -> w.length() == l)
                    .count();
        }
        return wordLengths;
    }

    private List<String> getAllWordsFromFile(Path file) {
        Pattern wordBoundary = Pattern.compile("\\b");

        try(Stream<String> lines = Files.lines(file)) {
            return lines.flatMap(wordBoundary::splitAsStream)
                    .filter(word -> word.matches("\\p{L}+"))
                    .distinct()
                    .collect(Collectors.toList());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}




