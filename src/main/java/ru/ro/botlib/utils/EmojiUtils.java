package ru.ro.botlib.utils;

import ru.ro.botlib.constants.EmojiConstants;

import java.util.List;

public class EmojiUtils {

    public static final List<String> SAND_CLOCKS = List.of(EmojiConstants.SAND_CLOCKS_UP, EmojiConstants.SAND_CLOCKS_DOWN);

    public static String getRandomSandClocks() {
        return GeneralUtils.getRandomElem(SAND_CLOCKS);
    }
}
