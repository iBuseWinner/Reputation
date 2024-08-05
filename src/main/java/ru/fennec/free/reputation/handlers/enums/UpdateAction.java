package ru.fennec.free.reputation.handlers.enums;

public enum UpdateAction {
    INCREASE, //Когда какой-либо игрок выдаёт очко репутации другому игроку
    DECREASE, //Когда какой-либо игрок отнимает очко репутации другому игроку
    RESET, //Когда Администратор сбрасывает у игрока очки репутации
    SET, //Когда Администратор устанавливает игроку очки репутации
    ADD, //Когда Администратор добавляет игроку очки репутации
    REMOVE //Когда Администратор убирает у игрока очки репутации
}
