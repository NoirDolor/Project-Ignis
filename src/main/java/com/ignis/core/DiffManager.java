package com.ignis.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DiffManager {

    // Each .mca file is named r.X.Z.mca and covers blocks X*512 to X*512+511
    // This converts that to a readable coordinate range for the player
    private String regionToCoords(String fileName) {
        // fileName like "r.0.-1.mca"
        try {
            String stripped = fileName.replace("r.", "").replace(".mca", "");
            String[] parts = stripped.split("\\.");
            if (parts.length < 2) return fileName;
            int rx = Integer.parseInt(parts[0]);
            int rz = Integer.parseInt(parts[1]);
            int x1 = rx * 512;
            int z1 = rz * 512;
            int x2 = x1 + 511;
            int z2 = z1 + 511;
            return String.format("x[%d to %d] z[%d to %d]", x1, x2, z1, z2);
        } catch (Exception e) {
            return fileName;
        }
    }

    private String hashFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            // Fallback: use file size as a cheap proxy
            return String.valueOf(file.length());
        }
    }

    public String diff(String worldPath, String commitIdA, String commitIdB) {
        File worldDir = new File(worldPath);
        File ignisDir = new File(worldDir, ".ignis");
        File snapshotsDir = new File(ignisDir, "snapshots");

        File snapA = new File(snapshotsDir, commitIdA + "/region");
        File snapB = new File(snapshotsDir, commitIdB + "/region");

        // Validate both snapshots exist
        if (!snapA.exists()) {
            return "Error: snapshot for commit \"" + commitIdA + "\" does not exist.";
        }
        if (!snapB.exists()) {
            return "Error: snapshot for commit \"" + commitIdB + "\" does not exist.";
        }

        File[] filesA = snapA.listFiles();
        File[] filesB = snapB.listFiles();

        if (filesA == null) filesA = new File[0];
        if (filesB == null) filesB = new File[0];

        // Build maps: filename -> hash
        Map<String, String> mapA = new LinkedHashMap<>();
        for (File f : filesA) {
            if (f.getName().endsWith(".mca")) {
                mapA.put(f.getName(), hashFile(f));
            }
        }

        Map<String, String> mapB = new LinkedHashMap<>();
        for (File f : filesB) {
            if (f.getName().endsWith(".mca")) {
                mapB.put(f.getName(), hashFile(f));
            }
        }

        // Categorise
        List<String> added    = new ArrayList<>(); // in B, not in A
        List<String> removed  = new ArrayList<>(); // in A, not in B
        List<String> modified = new ArrayList<>(); // in both, different hash
        List<String> unchanged = new ArrayList<>();// in both, same hash

        Set<String> allNames = new LinkedHashSet<>();
        allNames.addAll(mapA.keySet());
        allNames.addAll(mapB.keySet());

        for (String name : allNames) {
            boolean inA = mapA.containsKey(name);
            boolean inB = mapB.containsKey(name);
            if (inA && inB) {
                if (mapA.get(name).equals(mapB.get(name))) {
                    unchanged.add(name);
                } else {
                    modified.add(name);
                }
            } else if (inA) {
                removed.add(name);
            } else {
                added.add(name);
            }
        }

        // Build output
        StringBuilder sb = new StringBuilder();
        sb.append("Ignis Diff: ").append(commitIdA).append(" -> ").append(commitIdB).append("\n");
        sb.append("-------------------------------------------\n");

        if (added.isEmpty() && removed.isEmpty() && modified.isEmpty()) {
            sb.append("No changes detected between these commits.\n");
            return sb.toString();
        }

        if (!modified.isEmpty()) {
            sb.append("Modified regions (").append(modified.size()).append("):\n");
            for (String name : modified) {
                sb.append("  ~ ").append(name)
                        .append("  ").append(regionToCoords(name)).append("\n");
            }
        }

        if (!added.isEmpty()) {
            sb.append("Added regions (").append(added.size()).append("):\n");
            for (String name : added) {
                sb.append("  + ").append(name)
                        .append("  ").append(regionToCoords(name)).append("\n");
            }
        }

        if (!removed.isEmpty()) {
            sb.append("Removed regions (").append(removed.size()).append("):\n");
            for (String name : removed) {
                sb.append("  - ").append(name)
                        .append("  ").append(regionToCoords(name)).append("\n");
            }
        }

        int totalChanged = modified.size() + added.size() + removed.size();
        sb.append("-------------------------------------------\n");
        sb.append(totalChanged).append(" region(s) changed, ")
                .append(unchanged.size()).append(" unchanged.\n");

        return sb.toString();
    }

    // Convenience: diff a commit against the current live world
    public String diffWithCurrent(String worldPath, String commitId) {
        File worldDir = new File(worldPath);
        File ignisDir = new File(worldDir, ".ignis");
        File snapshotsDir = new File(ignisDir, "snapshots");
        File liveRegion = new File(worldDir, "region");
        File snapRegion = new File(snapshotsDir, commitId + "/region");

        if (!snapRegion.exists()) {
            return "Error: snapshot for commit \"" + commitId + "\" does not exist.";
        }
        if (!liveRegion.exists()) {
            return "Error: live world region folder not found.";
        }

        File[] filesSnap = snapRegion.listFiles();
        File[] filesLive = liveRegion.listFiles();

        if (filesSnap == null) filesSnap = new File[0];
        if (filesLive == null) filesLive = new File[0];

        Map<String, String> mapSnap = new LinkedHashMap<>();
        for (File f : filesSnap) {
            if (f.getName().endsWith(".mca")) {
                mapSnap.put(f.getName(), hashFile(f));
            }
        }

        Map<String, String> mapLive = new LinkedHashMap<>();
        for (File f : filesLive) {
            if (f.getName().endsWith(".mca")) {
                mapLive.put(f.getName(), hashFile(f));
            }
        }

        List<String> added    = new ArrayList<>();
        List<String> removed  = new ArrayList<>();
        List<String> modified = new ArrayList<>();
        List<String> unchanged = new ArrayList<>();

        Set<String> allNames = new LinkedHashSet<>();
        allNames.addAll(mapSnap.keySet());
        allNames.addAll(mapLive.keySet());

        for (String name : allNames) {
            boolean inSnap = mapSnap.containsKey(name);
            boolean inLive = mapLive.containsKey(name);
            if (inSnap && inLive) {
                if (mapSnap.get(name).equals(mapLive.get(name))) {
                    unchanged.add(name);
                } else {
                    modified.add(name);
                }
            } else if (inSnap) {
                // was in snapshot, not in live = removed from world since commit
                removed.add(name);
            } else {
                // in live but not snapshot = added to world since commit
                added.add(name);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Ignis Diff: ").append(commitId).append(" -> current world\n");
        sb.append("-------------------------------------------\n");

        if (added.isEmpty() && removed.isEmpty() && modified.isEmpty()) {
            sb.append("World is identical to commit \"").append(commitId).append("\".\n");
            return sb.toString();
        }

        if (!modified.isEmpty()) {
            sb.append("Modified regions (").append(modified.size()).append("):\n");
            for (String name : modified) {
                sb.append("  ~ ").append(name)
                        .append("  ").append(regionToCoords(name)).append("\n");
            }
        }

        if (!added.isEmpty()) {
            sb.append("New regions since commit (").append(added.size()).append("):\n");
            for (String name : added) {
                sb.append("  + ").append(name)
                        .append("  ").append(regionToCoords(name)).append("\n");
            }
        }

        if (!removed.isEmpty()) {
            sb.append("Regions deleted since commit (").append(removed.size()).append("):\n");
            for (String name : removed) {
                sb.append("  - ").append(name)
                        .append("  ").append(regionToCoords(name)).append("\n");
            }
        }

        int totalChanged = modified.size() + added.size() + removed.size();
        sb.append("-------------------------------------------\n");
        sb.append(totalChanged).append(" region(s) changed, ")
                .append(unchanged.size()).append(" unchanged.\n");

        return sb.toString();
    }
}