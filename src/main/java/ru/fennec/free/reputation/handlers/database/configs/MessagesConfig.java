package ru.fennec.free.reputation.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface MessagesConfig {

    @AnnotationBasedSorter.Order(1)
    @ConfDefault.DefaultString("&6&lReputation &8»&7")
    @ConfComments("Префикс плагина можно использовать во всех строках, используемых данным плагином, с помощью заменителя ${prefix}")
    String prefix();

    @SubSection
    AdminSection adminSection();

    @SubSection
    PlayerSection playerSection();

    interface AdminSection {

        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultString("${prefix} &fКонфиги плагина успешно перезагружены!")
        @ConfComments("Ответ Администратору, когда конфиги плагина были успешно перезагружены")
        String configsReloadedSuccessfully();

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultString("${prefix} &fУ Вас недостаточно прав для выполнения данной команды!")
        @ConfComments("Сообщение, если у игрока нет права reputation.admin.reset")
        String noPermission();

        @AnnotationBasedSorter.Order(3)
        @ConfDefault.DefaultString("${prefix} &fИнформация об игроке &a%player_name%&f успешно сброшена!")
        @ConfComments("Сообщение, когда сбрасывается информация об игроке")
        String playerReset();

        @AnnotationBasedSorter.Order(4)
        @ConfDefault.DefaultString("${prefix} &fИгроку &a%player_name%&f было установлено &a${player_reputation}&f очков репутации!")
        @ConfComments("Сообщение, когда игроку устанавливается кол-во очков репутации")
        String playerSet();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("${prefix} &cЗначение должно быть числом!")
        @ConfComments("Сообщение, когда в команде вместо числа указывается что-то другое")
        String mustBeNumber();

        @AnnotationBasedSorter.Order(6)
        @ConfDefault.DefaultStrings({"${prefix} &fАктуальный список команд плагина для Администраторов:",
                "  &a/reputation reload -&f перезагрузить конфиги плагина (не трогает БД)",
                "  &a/reputation player <Игрок> reset -&f сбросить репутацию игроку",
                "  &a/reputation player <Игрок> set <Очки> -&f установить репутацию игроку"})
        @ConfComments("Ответ Администратору, когда он запрашивает список команд")
        List<String> helpStrings();
    }

    interface PlayerSection {
        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultString("${prefix} &fДобро пожаловать! Ведите себя как милый котик, чтобы игроки делились с Вами очками репутации!")
        @ConfComments("Сообщение игроку при входе на сервер. Чтобы выключить сообщение, оставьте поле пустым.")
        String notifyOnFirstJoin();

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultString("${prefix} &fС возвращением! У Вас сейчас &a${player_reputation}&f очков репутации!")
        @ConfComments("Сообщение игроку при входе на сервер. Чтобы выключить сообщение, оставьте поле пустым.")
        String notifyOnJoin();

        @AnnotationBasedSorter.Order(3)
        @ConfDefault.DefaultStrings({"${prefix} &fАктуальный список команд плагина для Игроков:",
                "  &a/reputation help -&f показать данный список команд",
                "  &a/reputation <self|me|info> -&f посмотреть информацию о себе",
                "  &a/reputation <Игрок> -&f посмотреть информацию об онлайн игроке",
                "  &a/reputation give <Игрок> -&f прибавить очко репутации игроку",
                "  &a/reputation top -&f показать топ игроков по репутации",
                "  &a/reputation top online -&f показать топ онлайн игроков по репутации"})
        @ConfComments("Ответ Администратору, когда он запрашивает список команд")
        List<String> helpStrings();

        @AnnotationBasedSorter.Order(4)
        @ConfDefault.DefaultString("${prefix} &cКоманда доступна только игрокам!")
        @ConfComments("Сообщение, если команду использует не игрок")
        String notAPlayer();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("${prefix} &cИгрок оффлайн!")
        @ConfComments("Сообщение, если игрок оффлайн")
        String playerIsOffline();

        @AnnotationBasedSorter.Order(6)
        @ConfDefault.DefaultString("${prefix} &cВнутренняя ошибка плагина: игрок не в кэше плагина!")
        @ConfComments("Сообщение, если игрок не в кэше плагина")
        String playerNotInCache();

        @AnnotationBasedSorter.Order(7)
        @ConfDefault.DefaultString("${prefix} &cВы уже выдавали очки репутации данному игроку!")
        @ConfComments("Сообщение, если игрок уже выдавал очки репутации данному игроку")
        String alreadyGaveReputation();

        @AnnotationBasedSorter.Order(8)
        @ConfDefault.DefaultString("${prefix} &cВы не можете выдавать себе очки репутации!")
        @ConfComments("Сообщение, если игрок пытается выдать очко репутации самому себе")
        String cantSelf();

        @AnnotationBasedSorter.Order(9)
        @ConfDefault.DefaultString("${prefix} &fВы успешно выдали очко репутации игроку &a%player_name%&f!")
        @ConfComments("Сообщение, когда игроку выдали очко репутации")
        String gaveReputation();

        @AnnotationBasedSorter.Order(10)
        @ConfDefault.DefaultString("${prefix} &fУ Вас сейчас &a${player_reputation}&f очков репутации!")
        String selfInfo();

        @AnnotationBasedSorter.Order(11)
        @ConfDefault.DefaultString("${prefix} &fУ игрока &a%player_name%&f сейчас &a${player_reputation}&f очков репутации!")
        String playerInfo();

        @AnnotationBasedSorter.Order(12)
        @ConfDefault.DefaultString("${prefix} &fТоп игроков по репутации:")
        String topMessage();

        @AnnotationBasedSorter.Order(12)
        @ConfDefault.DefaultString("&a${place}&f. &a${player_name}&f -&a ${player_reputation}&f очков")
        String topFormat();
    }

}
