package ru.ro.botlib.chat.censorship;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class CensorshipAggregator {

    @Autowired
    private TimeBasedMessageCensorshipPredicate timeBasedMessageCensorshipPredicate;

    private final List<CensorshipPredicate> censorshipPredicates = new ArrayList<>();

    @PostConstruct
    public void init() {
        censorshipPredicates.add(timeBasedMessageCensorshipPredicate);
    }

    public boolean censor(Update update) {
        return censorshipPredicates.stream()
                .allMatch(predicate -> predicate.test(update));
    }
}
