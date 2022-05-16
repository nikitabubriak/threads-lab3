package WordLengthAnalysis;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class WordLengthMain {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        String line = scanner.nextLine();

        Path[] files = new Path[] {
                Paths.get("E:\\KPI\\Threads\\files\\a.txt"),
                Paths.get("E:\\KPI\\Threads\\files\\b.txt"),
                Paths.get("E:\\KPI\\Threads\\files\\c.txt"),
        };

        ForkJoinPool pool = ForkJoinPool.commonPool();
        int[] wordLengths = pool.invoke(new WordLengthAnalysis(files));

        System.out.println("Files processed: ");
        for (int i = 0; i < files.length; i++)
        {
            System.out.println(files[i]);
        }

        for (int i = 1; i <= wordLengths.length - 1; i++) {
            System.out.println(i + "-letter words: \t" + wordLengths[i]);
        }

        pool.shutdown();
    }
}
