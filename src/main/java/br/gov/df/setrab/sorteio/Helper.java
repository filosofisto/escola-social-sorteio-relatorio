package br.gov.df.setrab.sorteio;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Helper {

    private Helper() {

    }

    public static String curdir() {
        return System.getProperty("user.dir");
    }

    public static boolean fileExists(String filename) {
        Path path = FileSystems.getDefault().getPath(filename);
        return Files.exists(path);
    }
}
