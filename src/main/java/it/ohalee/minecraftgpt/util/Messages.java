package it.ohalee.minecraftgpt.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Messages {

    public static @NotNull String format(@Nullable String str) {
        if (str == null) return "Error - Bad Config";
        return str.replace("&", "§");
    }

}
