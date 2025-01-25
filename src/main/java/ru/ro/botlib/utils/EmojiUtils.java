package ru.ro.botlib.utils;

import ru.ro.botlib.constants.EmojiConstants;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EmojiUtils {

    public static final List<String> SAND_CLOCKS = List.of(
            EmojiConstants.SAND_CLOCKS_DOWN,
            EmojiConstants.SAND_CLOCKS_UP
    );

    private static final AtomicInteger SAND_CLOCKS_POINTER = new AtomicInteger(0);

    public static String getNextSandClocks() {
        if (SAND_CLOCKS_POINTER.incrementAndGet() >= SAND_CLOCKS.size()) {
            SAND_CLOCKS_POINTER.set(0);
        }

        return SAND_CLOCKS.get(SAND_CLOCKS_POINTER.get());
    }
}
