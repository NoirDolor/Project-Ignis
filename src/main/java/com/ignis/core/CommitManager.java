package com.ignis.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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


    public String getLog(String worldPath) {

        File ignisDir = new File(worldPath, ".ignis");
        File commitsDir = new File(ignisDir, "commits");
        File branchesDir = new File(ignisDir, "branches");
        File headFile = new File(ignisDir, "HEAD");

        StringBuilder logOutput = new StringBuilder();

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

        String currentCommit = null;

        try {
            Scanner scanner = new Scanner(branchFile);
            if (scanner.hasNextLine()) {
                currentCommit = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception ignored) {}

        while (currentCommit != null && !currentCommit.isEmpty()) {

            File commitFile = new File(commitsDir, currentCommit + ".json");

            try {

                String content = Files.readString(commitFile.toPath());

                logOutput.append("-----\n");
                logOutput.append(content).append("\n");

                int parentIndex = content.indexOf("\"parent\":");

                if (parentIndex == -1) {
                    break;
                }

                int start = content.indexOf("\"", parentIndex + 9);
                int end = content.indexOf("\"", start + 1);

                if (start == -1 || end == -1) {
                    break;
                }

                currentCommit = content.substring(start + 1, end);

                if (currentCommit.equals("null")) {
                    break;
                }

            } catch (IOException e) {
                break;
            }
        }

        return logOutput.toString();
    }



    public String getStatus(String worldPath) {

        File ignisDir = new File(worldPath, ".ignis");

        if (!ignisDir.exists()) {
            return "Ignis repository not initialized.";
        }

        File commitsDir = new File(ignisDir, "commits");
        File branchesDir = new File(ignisDir, "branches");
        File headFile = new File(ignisDir, "HEAD");

        String branchName = "main";

        try {
            Scanner scanner = new Scanner(headFile);
            if (scanner.hasNextLine()) {
                branchName = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception ignored) {}

        File branchFile = new File(branchesDir, branchName);

        String latestCommit = "none";

        try {
            Scanner scanner = new Scanner(branchFile);
            if (scanner.hasNextLine()) {
                latestCommit = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception ignored) {}

        int commitCount = 0;

        File[] commitFiles = commitsDir.listFiles();

        if (commitFiles != null) {
            commitCount = commitFiles.length;
        }

        return
                "Ignis Status\n" +
                        "Branch: " + branchName + "\n" +
                        "Latest Commit: " + latestCommit + "\n" +
                        "Total Commits: " + commitCount;
    }
}