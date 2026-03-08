package com.ignis.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Scanner;
import java.util.UUID;

public class CommitManager {

    public void createCommit(String worldPath, String message) {

        File ignisDir = new File(worldPath, ".ignis");
        File commitsDir = new File(ignisDir, "commits");
        File branchesDir = new File(ignisDir, "branches");
        File headFile = new File(ignisDir, "HEAD");

        if (!ignisDir.exists()) {
            System.out.println("Ignis repository not initialized.");
            return;
        }

        String commitId = UUID.randomUUID().toString().substring(0, 7);

        long timestamp = Instant.now().getEpochSecond();

        String branchName = "main";

        try {
            Scanner scanner = new Scanner(headFile);
            if (scanner.hasNextLine()) {
                branchName = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File branchFile = new File(branchesDir, branchName);

        String parent = null;

        try {
            Scanner scanner = new Scanner(branchFile);
            if (scanner.hasNextLine()) {
                parent = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception ignored) {}

        File commitFile = new File(commitsDir, commitId + ".json");

        String json =
                "{\n" +
                        "  \"id\": \"" + commitId + "\",\n" +
                        "  \"message\": \"" + message + "\",\n" +
                        "  \"timestamp\": " + timestamp + ",\n" +
                        "  \"parent\": " + (parent == null ? "null" : "\"" + parent + "\"") + ",\n" +
                        "  \"branch\": \"" + branchName + "\"\n" +
                        "}";

        try {
            FileWriter writer = new FileWriter(commitFile);
            writer.write(json);
            writer.close();

            FileWriter branchWriter = new FileWriter(branchFile);
            branchWriter.write(commitId);
            branchWriter.close();

            System.out.println("Commit created: " + commitId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}