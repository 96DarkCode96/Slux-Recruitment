package eu.darkcode.sluxrecruitment.playerdata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;

public interface SQLAction<R> {
    @Nullable R execute(PreparedStatement ps) throws Throwable;
    void prepare(@NotNull PreparedStatement ps) throws Throwable;
    @NotNull String sql();
}