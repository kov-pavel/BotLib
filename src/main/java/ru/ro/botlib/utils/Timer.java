package ru.ro.botlib.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Timer {

    private long startTime;
    private long stopTime;

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void stop() {
        this.stopTime = System.currentTimeMillis();
    }

    public String elapsePretty() {
        return elapse() + "с";
    }

    public long elapse() {
        return (this.stopTime - this.startTime) / 1000;
    }

    public long elapseTimedOperation(TimedOperation timedOperation) {
        try {
            start();

            timedOperation.execute();

            stop();

            return elapse();
        } catch (Exception ex) {
            var errorMsg = "Возникла ошибка при замере времени выполнения операции!";
            log.error(errorMsg, ex);
            throw new RuntimeException(errorMsg, ex);
        }
    }

    public interface TimedOperation {
        void execute() throws Exception;
    }
}
