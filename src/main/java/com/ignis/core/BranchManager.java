package com.ignis.core;

public class BranchManager {

    public void createBranch(String branchName) {
        System.out.println("Creating branch: " + branchName);
    }

    public void checkoutBranch(String branchName) {
        System.out.println("Switching to branch: " + branchName);
    }
}