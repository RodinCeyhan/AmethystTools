
package com.rtc.amethystTools.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageUtils {

    private static final Pattern RGB_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private static final Pattern HEAD_TEXTURE_PATTERN = Pattern.compile("[A-Fa-f0-9]{64}");

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().character('&').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    private static final MiniMessage MINI_MESSAGE =
            MiniMessage.builder()
                    .editTags(tags -> tags.resolver(
                            TagResolver.resolver(
                                    "head_texture",
                                    (arguments, context) -> {
                                        String texture = arguments.popOr("Texture hash is required").value();

                                        if (!HEAD_TEXTURE_PATTERN.matcher(texture).matches()) {
                                            throw new IllegalArgumentException("Invalid head texture hash: " + texture);
                                        }

                                        return Tag.inserting(Component.empty());
                                    }
                            )
                    ))
                    .build();

    private MessageUtils() {
    }

    public static Component parse(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty().decoration(TextDecoration.ITALIC, false);
        }

        Matcher matcher = RGB_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, "<#" + matcher.group(1) + ">");
        }

        matcher.appendTail(buffer);

        String prepared = convertLegacyCodes(buffer.toString());

        Component component;

        if (prepared.contains("<")) {
            component = MINI_MESSAGE.deserialize(prepared);
        } else {
            component = LEGACY_SERIALIZER.deserialize(prepared);
        }

        return component.decoration(TextDecoration.ITALIC, false);
    }

    private static String convertLegacyCodes(String message) {
        return message
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&l", "<bold>")
                .replace("&o", "<italic>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>")
                .replace("&r", "<reset>");
    }
}
