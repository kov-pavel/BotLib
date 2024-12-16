package ru.ro.botlib.utils.admin;

import lombok.Data;

@Data
public class AdminDeleteMessageDto {

    private long chatID;
    private int messageID;
}
