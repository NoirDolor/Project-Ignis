package com.ignis.core;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class BranchManager {

    public String createBranch(String worldPath, String branchName) {

        File ignisDir = new File(worldPath, ".ignis");
        File branchesDir = new File(ignisDir, "branches");
        File headFile = new File(ignisDir, "HEAD");
        File newBranch = new File(branchesDir, branchName);

        if (newBranch.exists()) {
            return "Branch already exists.";
        }

        String currentBranch = "main";

        try {
            Scanner scanner = new Scanner(headFile);
            if (scanner.hasNextLine()) {
                currentBranch = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception ignored) {}

        File currentBranchFile = new File(branchesDir, currentBranch);

        String currentCommit = "";

        try {
            Scanner scanner = new Scanner(currentBranchFile);
            if (scanner.hasNextLine()) {
                currentCommit = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception ignored) {}

        try {
            newBranch.createNewFile();

            FileWriter writer = new FileWriter(newBranch);
            writer.write(currentCommit);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Branch created: " + branchName;
    }



    public String checkoutBranch(String worldPath, String branchName) {

        File ignisDir = new File(worldPath, ".ignis");
        File branchesDir = new File(ignisDir, "branches");
        File headFile = new File(ignisDir, "HEAD");
        File branchFile = new File(branchesDir, branchName);

        if (!branchFile.exists()) {
            return "Branch \"" + branchName + "\" does not exist.";
        }

        try {
            FileWriter writer = new FileWriter(headFile);
            writer.write(branchName);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Switched to branch: " + branchName;
    }

    public String listBranches(String worldPath) {

        File ignisDir = new File(worldPath, ".ignis");
        File branchesDir = new File(ignisDir, "branches");
        File headFile = new File(ignisDir, "HEAD");

        String currentBranch = "main";

        try {
            Scanner scanner = new Scanner(headFile);
            if (scanner.hasNextLine()) {
                currentBranch = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception ignored) {}

        StringBuilder output = new StringBuilder();

        File[] branches = branchesDir.listFiles();

        if (branches == null || branches.length == 0) {
            return "No branches found.";
        }

        for (File branch : branches) {

            if (branch.getName().equals(currentBranch)) {
                output.append("* ").append(branch.getName()).append("\n");
            } else {
                output.append("  ").append(branch.getName()).append("\n");
            }
        }

        return output.toString();
    }



    public String deleteBranch(String worldPath, String branchName) {

        File ignisDir = new File(worldPath, ".ignis");
        File branchesDir = new File(ignisDir, "branches");
        File headFile = new File(ignisDir, "HEAD");

        String currentBranch = "main";

        try {
            Scanner scanner = new Scanner(headFile);
            if (scanner.hasNextLine()) {
                currentBranch = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception ignored) {}

        if (branchName.equals(currentBranch)) {
            return "Cannot delete the currently checked out branch.";
        }

        File branchFile = new File(branchesDir, branchName);

        if (!branchFile.exists()) {
            return "Branch does not exist.";
        }

        branchFile.delete();

        return "Branch deleted: " + branchName;
    }
}