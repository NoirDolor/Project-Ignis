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

        File backupDir = new File(worldDir, ".ignis/backup/region");
        File stagingDir = new File(worldDir, ".ignis/staging/region");

        // Validating if snapshot exists
        if (!snapshotRegionDir.exists()) {
            return "Snapshot for commit does not exist.";
        }

        // Validating if region folder exists
        if (!regionDir.exists()) {
            return "World region folder not found.";
        }

        // Ensures backup + staging dirs exist
        backupDir.mkdirs();
        stagingDir.mkdirs();

        File[] oldStaging = stagingDir.listFiles();
        if (oldStaging != null) {
            for (File file : oldStaging) {
                file.delete();
            }
        }

        // =========================
        //  BACKUP CURRENT
        // =========================
        File[] currentFiles = regionDir.listFiles();

        if (currentFiles != null) {
            for (File file : currentFiles) {
                try {
                    File target = new File(backupDir, file.getName());

                    Files.copy(file.toPath(), target.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    e.printStackTrace();
                    return "Backup failed. Restore aborted.";
                }
            }
        }

        // =========================
        //  COPY SNAPSHOT → STAGING
        // =========================
        File[] snapshotFiles = snapshotRegionDir.listFiles();

        if (snapshotFiles == null || snapshotFiles.length == 0) {
            return "Snapshot is empty or corrupted.";
        }

        for (File file : snapshotFiles) {
            try {
                File target = new File(stagingDir, file.getName());

                Files.copy(file.toPath(), target.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to prepare snapshot. Restore aborted.";
            }
        }

        // =========================
        //  VALIDATE STAGING
        // =========================
        File[] stagedFiles = stagingDir.listFiles();

        if (stagedFiles == null || stagedFiles.length == 0) {
            return "Staging failed. No files copied.";
        }

        // =========================
        //  SWAP (CRITICAL, might get COOKED)
        // =========================

        //  Delete current region
        if (currentFiles != null) {
            for (File file : currentFiles) {
                if (!file.delete()) {
                    return "Failed to clear current region. Restore aborted.";
                }
            }
        }

        //  Move staging → region
        for (File file : stagedFiles) {
            try {
                File target = new File(regionDir, file.getName());

                Files.move(file.toPath(), target.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();

                //  ROLLBACK TRIGGER
                rollback(regionDir, backupDir);

                return "Restore failed. Rolled back to previous state.";
            }
        }

        return "World restored to commit: " + commitId;
    }

    private void rollback(File regionDir, File backupDir) {

        File[] currentFiles = regionDir.listFiles();

        if (currentFiles != null) {
            for (File file : currentFiles) {
                file.delete();
            }
        }

        // Restore from backup
        File[] backupFiles = backupDir.listFiles();

        if (backupFiles != null) {
            for (File file : backupFiles) {
                try {
                    File target = new File(regionDir, file.getName());

                    Files.copy(file.toPath(), target.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}