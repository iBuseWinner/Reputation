package ru.buseso.spigot.free.reputation.updates;

public enum UpdateResult {
    UPDATE_FOUND, UPDATE_NOT_FOUND;

    public boolean hasUpdates() {
        return this.equals(UPDATE_FOUND);
    }
}
