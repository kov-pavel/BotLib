package ru.ro.botlib.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomChat {

    private final long chatId;
    private final int threadId;
}
