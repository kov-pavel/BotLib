package ru.ro.botlib.chat.censorship;

import org.apache.commons.lang3.NotImplementedException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Predicate;

public abstract class CensorshipPredicate implements Predicate<Update> {

    @Override
    public boolean test(Update update) {
        throw new NotImplementedException("Логика предиката не имплементирована!");
    }
}
