package com.ignis.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RepositoryManager {

    public void initRepository(String worldPath) {

        File ignisDir = new File(worldPath, ".ignis");
        File commitsDir = new File(ignisDir, "commits");
        File branchesDir = new File(ignisDir, "branches");
        File headFile = new File(ignisDir, "HEAD");

        if (ignisDir.exists()) {
            System.out.println("Ignis repository already exists.");
            return;
        }

        commitsDir.mkdirs();
        branchesDir.mkdirs();

        File mainBranch = new File(branchesDir, "main");

        try {
            mainBranch.createNewFile();

            FileWriter branchWriter = new FileWriter(mainBranch);
            branchWriter.write("");
            branchWriter.close();

            headFile.createNewFile();

            FileWriter headWriter = new FileWriter(headFile);
            headWriter.write("main");
            headWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Ignis repository initialized.");
    }
}