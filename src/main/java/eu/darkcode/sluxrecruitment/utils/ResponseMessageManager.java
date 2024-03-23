package eu.darkcode.sluxrecruitment.utils;

import eu.darkcode.sluxrecruitment.Core;
import eu.darkcode.sluxrecruitment.config.IPluginConfig;
import eu.darkcode.sluxrecruitment.config.PluginConfig;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ResponseMessageManager {

    private static final JSONComponentSerializer JSON = JSONComponentSerializer.json();

    @Getter
    private final @NotNull Core core;
    private final IPluginConfig config;

    public ResponseMessageManager(@NotNull Core core) {
        this.core = core;

        config = PluginConfig.of("messages.yml");
        if (!config.loadConfig()) throw new RuntimeException("Failed to load messages.yml");

        assert config.getConfig() != null;
        config.getConfig().addDefaults(Arrays.stream(ResponseMessage.values()).collect(Collectors.toMap(ResponseMessage::getKey, this::serializeDefaultValue)));
        config.getConfig().options().copyDefaults(true).setHeader(List.of("Slux-Recruitment Messages", ""));
        config.saveConfig();
    }

    private String serializeDefaultValue(ResponseMessage r) {
        return JSON.serialize(r.getDefValue().get());
    }


    public @NotNull Component get(ResponseMessage responseMessage) {
        if(config.getConfig() == null) return responseMessage.getDefValue().get();
        String string = config.getConfig().getString(responseMessage.getKey());
        if(string == null) return responseMessage.getDefValue().get();
        return JSON.deserializeOr(string, responseMessage.getDefValue().get());
    }
}