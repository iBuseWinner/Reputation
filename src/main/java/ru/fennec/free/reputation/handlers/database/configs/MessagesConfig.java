package ru.fennec.free.reputation.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;

import java.util.List;

public interface MessagesConfig {

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

        @ConfDefault.DefaultStrings({"${prefix} &fАктуальный список команд плагина:",
                "  &a/reputation help -&f открыть данный список команд",
                "  &a/reputation player <Игрок> reset -&f сбросить репутацию игроку",
                "  &a/reputation player <Игрок> set <Очки> -&f установить репутацию игроку"})
        @ConfComments("Ответ Администратору, когда он запрашивает список команд")
        List<String> helpStrings();
    }

    interface PlayerSection {
        @ConfDefault.DefaultString("${prefix} &fС возвращением! У Вас сейчас &a${reputation}&f очков репутации!")
        @ConfComments("Сообщение игроку при входе на сервер. Чтобы выключить сообщение, оставьте поле пустым.")
        String notifyOnJoin();
    }

}
