package eu.darkcode.sluxrecruitment.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ComponentUtil {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .useUnusualXRepeatedCharacterHexFormat()
            .hexColors()
            .extractUrls()
            .build();

    private ComponentUtil() {}

    public static Component legacy(String message) {
        return LEGACY.deserialize(message);
    }

    public static String legacy(Component component) {
        return LEGACY.serialize(component);
    }
}