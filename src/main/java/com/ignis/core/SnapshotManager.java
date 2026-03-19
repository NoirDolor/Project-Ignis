package com.ignis.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SnapshotManager {

    public void createSnapshot(String worldPath, String commitId) {

        File worldDir = new File(worldPath);
        File regionDir = new File(worldDir, "region");

        File ignisDir = new File(worldDir, ".ignis");
        File snapshotsDir = new File(ignisDir, "snapshots");
        File commitSnapshotDir = new File(snapshotsDir, commitId);
        File snapshotRegionDir = new File(commitSnapshotDir, "region");

        if (!regionDir.exists()) {
            System.out.println("No region folder found.");
            return;
        }

        snapshotRegionDir.mkdirs();

        File[] regionFiles = regionDir.listFiles();

        if (regionFiles == null) return;

        for (File file : regionFiles) {

            try {
                File target = new File(snapshotRegionDir, file.getName());

                Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Snapshot created for commit: " + commitId);
    }
}