package ru.fennec.free.reputation.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;

import java.util.List;

public interface MessagesConfig {

    @ConfDefault.DefaultString("&6&lReputation &8»» &7")
    @ConfComments("Префикс плагина можно использовать во всех строках, используемых данным плагином, с помощью заменителя ${prefix}")
    String prefix();

    @SubSection
    AdminSection adminSection();

    @SubSection
    PlayerSection playerSection();

    interface AdminSection {

        @ConfDefault.DefaultString("${prefix} &aКонфиги плагина успешно перезагружены!")
        @ConfComments("Ответ Администратору, когда конфиги плагина были успешно перезагружены")
        String configsReloadedSuccessfully();

        @ConfDefault.DefaultString("${prefix} &cКонфиги плагина не были перезагружены из-за ошибок! Смотрите консоль сервера для подробностей!")
        @ConfComments("Ответ Администратору, когда конфиги плагина не были перезагружены из-за ошибок")
        String configsReloadedError();

        @ConfDefault.DefaultStrings({"${prefix} &fАктуальный список команд плагина для Администраторов:",
                "  &a/reputation help -&f открыть данный список команд",
                "  &a/reputation reload -&f перезагрузить конфиги плагина (не трогает БД)",
                "  &a/reputation player <Игрок> reset -&f сбросить репутацию игроку",
                "  &a/reputation player <Игрок> set <Очки> -&f установить репутацию игроку"})
        @ConfComments("Ответ Администратору, когда он запрашивает список команд")
        List<String> helpStrings();
    }

    interface PlayerSection {
        @ConfDefault.DefaultString("${prefix} &fДобро пожаловать! Ведите себя как милый котик, чтобы игроки делились с Вами очками репутации!")
        @ConfComments("Сообщение игроку при входе на сервер. Чтобы выключить сообщение, оставьте поле пустым.")
        String notifyOnFirstJoin();

        @ConfDefault.DefaultString("${prefix} &fС возвращением! У Вас сейчас &a${reputation}&f очков репутации!")
        @ConfComments("Сообщение игроку при входе на сервер. Чтобы выключить сообщение, оставьте поле пустым.")
        String notifyOnJoin();

        @ConfDefault.DefaultStrings({"${prefix} &fАктуальный список команд плагина для Игроков:",
                "  &a/reputation help -&f открыть данный список команд",
                "  &a/reputation <self|me|info> -&f посмотреть информацию о себе",
                "  &a/reputation <Игрок> -&f посмотреть информацию об онлайн игроке",
                "  &a/reputation give <Игрок> -&f прибавить очко репутации игроку"})
        @ConfComments("Ответ Администратору, когда он запрашивает список команд")
        List<String> helpStrings();

        @ConfDefault.DefaultString("${prefix} &cКоманда доступна только игрокам!")
        @ConfComments("Сообщение, если команду использует не игрок")
        String notAPlayer();

        @ConfDefault.DefaultString("${prefix} &fУ Вас сейчас &a${reputation}&f очков репутации!")
        String selfInfo();
    }

}
