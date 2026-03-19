package com.ignis.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class RestoreManager {

    public String restoreSnapshot(String worldPath, String commitId) {

        File worldDir = new File(worldPath);
        File regionDir = new File(worldDir, "region");


        File snapshotRegionDir = new File(
                worldDir,
                ".ignis/snapshots/" + commitId + "/region"
        );

        if (!snapshotRegionDir.exists()) {
            return "Snapshot for commit does not exist.";
        }

        // Deleteing current region files
        File[] currentFiles = regionDir.listFiles();

        if (currentFiles != null) {
            for (File file : currentFiles) {
                file.delete();
            }
        }

        //  Copy snapshot files
        File[] snapshotFiles = snapshotRegionDir.listFiles();

        if (snapshotFiles != null) {
            for (File file : snapshotFiles) {
                try {
                    File target = new File(regionDir, file.getName());

                    Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "World restored to commit: " + commitId;
    }
}