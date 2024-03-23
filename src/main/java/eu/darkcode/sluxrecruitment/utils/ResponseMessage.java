package eu.darkcode.sluxrecruitment.utils;

import eu.darkcode.sluxrecruitment.Core;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Getter
public enum ResponseMessage {

    COMMAND_WORLDBORDER_SET_SUCCESS("command.worldborder.set.success", () -> Component
            .text("Successfully set border for ")
            .append(Component.text("%world%").color(TextColor.fromHexString("#FFFFFF")))
            .append(Component.text(" to "))
            .append(Component.text("%size% x %size%").color(TextColor.fromHexString("#FFFFFF")))
            .append(Component.text("!"))
            .color(TextColor.fromCSSHexString("#55FF55"))),
    COMMAND_WORLDBORDER_SET_FAILED("command.worldborder.set.failed", () -> Component
            .text("Failed to set border for ")
            .append(Component.text("%world%").color(TextColor.fromHexString("#FFFFFF")))
            .append(Component.text(" to "))
            .append(Component.text("%size% x %size%").color(TextColor.fromHexString("#FFFFFF")))
            .append(Component.text("!"))
            .color(TextColor.fromCSSHexString("#FF5555"))),
    COMMAND_WORLDBORDER_SET_FAILED_SMALL_SIZE("command.worldborder.set.failed.small-size", () -> Component
            .text("Failed to set border for ")
            .append(Component.text("%world%").color(TextColor.fromHexString("#FFFFFF")))
            .append(Component.text("!"))
            .appendNewline()
            .append(Component.text("Size must be greater or equal than 1!"))
            .color(TextColor.fromCSSHexString("#FF5555"))),
    COMMAND_WORLDBORDER_SET_FAILED_INVALID_SIZE("command.worldborder.set.failed.invalid-size", () -> Component
            .text("Failed to set border for ")
            .append(Component.text("%world%").color(TextColor.fromHexString("#FFFFFF")))
            .append(Component.text("!"))
            .appendNewline()
            .append(Component.text("Size must be a number!"))
            .color(TextColor.fromCSSHexString("#FF5555"))),
    COMMAND_WORLDBORDER_REMOVE_SUCCESS("command.worldborder.remove.success", () -> Component
            .text("Successfully removed border from ")
            .append(Component.text("%world%").color(TextColor.fromHexString("#FFFFFF")))
            .append(Component.text("!"))
            .color(TextColor.fromCSSHexString("#55FF55"))),
    COMMAND_WORLDBORDER_REMOVE_FAILED("command.worldborder.remove.failed", () -> Component
            .text("Failed to remove border from ")
            .append(Component.text("%world%").color(TextColor.fromHexString("#FFFFFF")))
            .append(Component.text("!"))
            .color(TextColor.fromCSSHexString("#FF5555"))),
    COMMAND_WORLDBORDER_USAGE("command.worldborder.usage", () -> Component.text("Usage: ")
            .color(TextColor.fromCSSHexString("#FF5555"))
            .appendNewline()
            .append(Component.text("/border set <world> ").color(TextColor.fromHexString("#555555"))
                    .append(Component.text("<size>").color(TextColor.fromHexString("#555555"))
                            .hoverEvent(new HoverEventSource<Component>() {
                                @Override
                                public @NotNull HoverEvent<Component> asHoverEvent(@NotNull UnaryOperator<Component> op) {
                                    return HoverEvent.showText(Component.text("Set world's border size"));
                                }
                            })
                    ))
            .appendNewline()
            .append(Component.text("/border remove <world>").color(TextColor.fromHexString("#555555")))),
    SERVER_SHUTDOWN("server.shutdown", () -> Component
            .text("Server is shutting down...")
            .color(TextColor.fromCSSHexString("#FF5555"))),
    LOAD_PLAYER_DATA("load.player-data", () -> Component.text("")
            .append(Component.text("------------------------").decorate(TextDecoration.OBFUSCATED))
            .appendNewline()
            .appendNewline()
            .append(Component.text("Loading player data...").color(TextColor.fromCSSHexString("#FF5555")))
            .appendNewline()
            .append(Component.text("This may take a while!").color(TextColor.fromCSSHexString("#FF5555")))
            .appendNewline()
            .append(Component.text("Please wait...").color(TextColor.fromCSSHexString("#FF5555")))
            .appendNewline()
            .appendNewline()
            .append(Component.text("------------------------").decorate(TextDecoration.OBFUSCATED))
            .color(TextColor.fromCSSHexString("#FF5555"))),
    LOAD_PLAYER_DATA_SUCCESS("load.player-data-success", () -> Component.text("")
            .append(Component.text("------------------------").decorate(TextDecoration.OBFUSCATED))
            .appendNewline()
            .appendNewline()
            .append(Component.text("Successfully loaded player data!").color(TextColor.fromCSSHexString("#55FF55")))
            .appendNewline()
            .appendNewline()
            .append(Component.text("------------------------").decorate(TextDecoration.OBFUSCATED))
            .color(TextColor.fromCSSHexString("#55FF55"))),
    INVALID_JSON("load.invalid-json", () -> Component.text("Failed to load player data!")
            .appendNewline()
            .append(Component.text("Invalid JSON!"))
            .color(TextColor.fromCSSHexString("#FF5555"))),
    ;

    private final @NotNull String key;
    private final @NotNull Supplier<Component> defValue;

    ResponseMessage(@NotNull String key, @NotNull Supplier<Component> defValue) {
        this.key = key;
        this.defValue = defValue;
    }

    public @NotNull Component make(Core core) {
        return core.getResponseMessageManager().get(this);
    }
}