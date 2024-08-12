package ru.fennec.free.reputation.handlers.enums;

public enum OrderBy {
    ASC("ASC"), //Сортировать по возрастанию (От худшего к лучшему)
    DESC("DESC"); //Сортировать по убыванию (От лучшего к худшему)
    private final String value;

    OrderBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
