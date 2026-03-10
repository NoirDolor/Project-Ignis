package com.ignis.core;

import java.io.File;

public class RepositoryValidator {

    public static boolean isRepository(String worldPath) {

        File ignisDir = new File(worldPath, ".ignis");

        return ignisDir.exists() && ignisDir.isDirectory();
    }

}