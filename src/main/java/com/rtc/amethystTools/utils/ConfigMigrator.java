package com.rtc.amethystTools.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public final class ConfigMigrator {

    private static final List<String> REQUIRED_KEYS = List.of("block-breaking-mode", "break-particles", "break-sound", "enable-item-enchant", "blacklist-blocks");
    private static final String MESSAGES = "messages";

    private static final Pattern CHILD_KEY = Pattern.compile("^(\\s+)[\"']?([A-Za-z0-9_.\\-]+)[\"']?\\s*:(?:\\s.*)?$");

    private ConfigMigrator() {
    }

    public static boolean migrate(List<String> lines, List<String> defaults) {
        boolean changed = false;

        for (String key : REQUIRED_KEYS) {
            if (indexOfTopLevelKey(lines, key) != -1) continue;

            List<String> block = extractBlock(defaults, key);
            if (block.isEmpty()) continue;

            if (!lines.isEmpty() && !lines.get(lines.size() - 1).isBlank()) {
                lines.add("");
            }
            lines.addAll(block);
            changed = true;
        }

        changed |= removeUnknownChildren(lines, MESSAGES, childKeys(defaults, MESSAGES));
        return changed;
    }

    private static List<String> extractBlock(List<String> defaults, String key) {
        int keyIndex = indexOfTopLevelKey(defaults, key);
        if (keyIndex == -1) return List.of();

        int start = keyIndex;
        while (start > 0 && defaults.get(start - 1).trim().startsWith("#")) {
            start--;
        }

        int end = keyIndex + 1;
        while (end < defaults.size() && !isTopLevel(defaults.get(end), true)) {
            end++;
        }
        while (end > keyIndex + 1 && defaults.get(end - 1).isBlank()) {
            end--;
        }

        return new ArrayList<>(defaults.subList(start, end));
    }

    private static boolean removeUnknownChildren(List<String> lines, String section, Set<String> allowed) {
        if (allowed.isEmpty()) return false;

        int sectionIndex = indexOfTopLevelKey(lines, section);
        if (sectionIndex == -1) return false;

        boolean changed = false;
        int childIndent = -1;
        boolean dropping = false;

        int i = sectionIndex + 1;
        while (i < lines.size()) {
            String line = lines.get(i);
            if (isTopLevel(line, false)) break;

            if (line.isBlank() || line.trim().startsWith("#")) {
                dropping = false;
                i++;
                continue;
            }

            Matcher matcher = CHILD_KEY.matcher(line);
            if (matcher.matches()) {
                int indent = matcher.group(1).length();
                if (childIndent == -1) childIndent = indent;
                if (indent == childIndent) {
                    dropping = !allowed.contains(matcher.group(2));
                }
            }

            if (dropping) {
                lines.remove(i);
                changed = true;
            } else {
                i++;
            }
        }
        return changed;
    }

    private static Set<String> childKeys(List<String> lines, String section) {
        Set<String> keys = new LinkedHashSet<>();

        int sectionIndex = indexOfTopLevelKey(lines, section);
        if (sectionIndex == -1) return keys;

        int childIndent = -1;
        for (int i = sectionIndex + 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (isTopLevel(line, false)) break;
            if (line.isBlank() || line.trim().startsWith("#")) continue;

            Matcher matcher = CHILD_KEY.matcher(line);
            if (!matcher.matches()) continue;

            int indent = matcher.group(1).length();
            if (childIndent == -1) childIndent = indent;
            if (indent == childIndent) keys.add(matcher.group(2));
        }
        return keys;
    }

    private static int indexOfTopLevelKey(List<String> lines, String key) {
        Pattern pattern = Pattern.compile("^[\"']?" + Pattern.quote(key) + "[\"']?\\s*:(?:\\s.*)?$");
        for (int i = 0; i < lines.size(); i++) {
            if (pattern.matcher(lines.get(i)).matches()) return i;
        }
        return -1;
    }

    private static boolean isTopLevel(String line, boolean commentsCount) {
        if (line.isBlank() || Character.isWhitespace(line.charAt(0))) return false;
        if (line.equals("-") || line.startsWith("- ")) return false;
        if (line.charAt(0) == '#') return commentsCount;
        return true;
    }
}