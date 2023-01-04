package ru.fennec.free.reputation.handlers.enums;

public enum UpdateAction {
    INCREASE, //Когда какой-либо игрок выдаёт очко репутации другому игроку
    RESET, //Когда Администратор сбрасывает у игрока очки репутации
    SET //Когда Администратор устанавливает игроку очки репутации
}
