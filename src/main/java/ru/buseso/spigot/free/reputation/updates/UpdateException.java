package ru.buseso.spigot.free.reputation.updates;

import ru.buseso.spigot.free.reputation.RepSender;
import ru.buseso.spigot.free.reputation.Reputation;

public class UpdateException extends Exception {

    private static final long serialVersionUID = -7371026016860990753L;
    private final String message;
    private final Updater updater;

    public UpdateException(String message, Updater updater) {
        super(message);
        this.message = message;
        this.updater = updater;
    }

    public void print() {
        RepSender.log(printableMessage());
    }


    public String printableMessage() {
        return "[Reputation]: Error while trying to check update!";
    }
}
