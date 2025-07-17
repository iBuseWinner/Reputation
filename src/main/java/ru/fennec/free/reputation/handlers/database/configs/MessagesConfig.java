package ru.fennec.free.reputation.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface MessagesConfig {

    @AnnotationBasedSorter.Order(1)
    @ConfDefault.DefaultString("<gold><bold>Reputation</bold></gold> <dark_gray>»<gray>")
    @ConfComments("Префикс плагина можно использовать во всех строках, используемых данным плагином, с помощью заменителя ${prefix}")
    String prefix();

    @SubSection
    AdminSection adminSection();

    @SubSection
    PlayerSection playerSection();

    interface AdminSection {

        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultString("${prefix} <white>Конфиги плагина успешно перезагружены!")
        @ConfComments("Ответ Администратору, когда конфиги плагина были успешно перезагружены")
        String configsReloadedSuccessfully();

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultString("${prefix} <white>У Вас недостаточно прав для выполнения данной команды!")
        @ConfComments("Сообщение, если у игрока нет права reputation.admin.reset")
        String noPermission();

        @AnnotationBasedSorter.Order(3)
        @ConfDefault.DefaultString("${prefix} <white>Информация об игроке <green>${player_name}</green> успешно сброшена!")
        @ConfComments("Сообщение, когда сбрасывается информация об игроке")
        String playerReset();

        @AnnotationBasedSorter.Order(4)
        @ConfDefault.DefaultString("${prefix} <white>Игроку <green>${player_name}</green> было установлено <green>${player_reputation}</green> очков репутации!")
        @ConfComments("Сообщение, когда игроку устанавливается/добавляется кол-во очков репутации")
        String playerSet();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("${prefix} <red>Значение должно быть числом!")
        @ConfComments("Сообщение, когда в команде вместо числа указывается что-то другое")
        String mustBeNumber();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("${prefix} <red>Число превышает максимальное значение типа Long!")
        @ConfComments("Сообщение, когда в команде число очень большое")
        String numberIsTooLong();

        @AnnotationBasedSorter.Order(6)
        @ConfDefault.DefaultStrings({"${prefix} <white>Актуальный список команд плагина для Администраторов:",
                "  <green>/reputation reload -<white> перезагрузить конфиги плагина (не трогает БД)",
                "  <green>/reputation player <Игрок> reset -<white> сбросить репутацию игроку",
                "  <green>/reputation player <Игрок> set <Очки> -<white> установить репутацию игроку",
                "  <green>/reputation player <Игрок> add <Очки> -<white> добавить репутацию игроку",
                "  <green>/reputation player <Игрок> remove <Очки> -<white> убрать репутацию игроку"})
        @ConfComments("Ответ Администратору, когда он запрашивает список команд")
        List<String> helpStrings();
    }

    interface PlayerSection {
        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultString("${prefix} <white>Добро пожаловать! Ведите себя как милый котик, чтобы игроки делились с Вами очками репутации!")
        @ConfComments("Сообщение игроку при входе на сервер. Чтобы выключить сообщение, оставьте поле пустым.")
        String notifyOnFirstJoin();

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultString("${prefix} <white>С возвращением! У Вас сейчас <green>${player_reputation}</green> очков репутации!")
        @ConfComments("Сообщение игроку при входе на сервер. Чтобы выключить сообщение, оставьте поле пустым.")
        String notifyOnJoin();

        @AnnotationBasedSorter.Order(3)
        @ConfDefault.DefaultStrings({"${prefix} <white>Актуальный список команд плагина для Игроков:",
                "  <green>/reputation help -<white> показать данный список команд",
                "  <green>/reputation <self|me|info> -<white> посмотреть информацию о себе",
                "  <green>/reputation <Игрок> -<white> посмотреть информацию об онлайн игроке",
                "  <green>/reputation give <Игрок> -<white> прибавить очко репутации игроку",
                "  <green>/reputation top -<white> показать топ игроков по репутации",
                "  <green>/reputation top online -<white> показать топ онлайн игроков по репутации",
                "  <green>/reputation reject -<white> отказаться от репутации"})
        @ConfComments("Ответ Администратору, когда он запрашивает список команд")
        List<String> helpStrings();

        @AnnotationBasedSorter.Order(4)
        @ConfDefault.DefaultString("${prefix} <red>Команда доступна только игрокам!")
        @ConfComments("Сообщение, если команду использует не игрок")
        String notAPlayer();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("${prefix} <red>Игрок оффлайн!")
        @ConfComments("Сообщение, если игрок оффлайн")
        String playerIsOffline();

        @AnnotationBasedSorter.Order(6)
        @ConfDefault.DefaultString("${prefix} <red>Внутренняя ошибка плагина: игрок не в кэше плагина!")
        @ConfComments("Сообщение, если игрок не в кэше плагина")
        String playerNotInCache();

        @AnnotationBasedSorter.Order(7)
        @ConfDefault.DefaultString("${prefix} <red>Вы уже выдавали очки репутации данному игроку!")
        @ConfComments("Сообщение, если игрок уже выдавал очки репутации данному игроку")
        String alreadyGaveReputation();

        @AnnotationBasedSorter.Order(8)
        @ConfDefault.DefaultString("${prefix} <red>Вы не можете взаимодействовать с самим собой!")
        @ConfComments("Сообщение, если игрок пытается выдать очко репутации самому себе")
        String cantSelf();

        @AnnotationBasedSorter.Order(9)
        @ConfDefault.DefaultString("${prefix} <white>Вы успешно выдали очко репутации игроку <green>${player_name}</green>!")
        @ConfComments("Сообщение, когда игроку выдали очко репутации")
        String gaveReputation();

        @AnnotationBasedSorter.Order(10)
        @ConfDefault.DefaultString("${prefix} <white>У Вас сейчас <green>${player_reputation}</green> очков репутации, Ваше звание: <green>${player_title}</green>!")
        String selfInfo();

        @AnnotationBasedSorter.Order(11)
        @ConfDefault.DefaultString("${prefix} <white>У игрока <green>${player_name}</green> сейчас <green>${player_reputation}</green> очков репутации, его звание: <green>${player_title}</green>!")
        String playerInfo();

        @AnnotationBasedSorter.Order(12)
        @ConfDefault.DefaultString("${prefix} <white>Топ игроков по репутации:")
        String topMessage();

        @AnnotationBasedSorter.Order(12)
        @ConfDefault.DefaultString("<white><green>${place}</green>. <green>${player_name}</green> - <green>${player_reputation}</green> очков")
        String topFormat();

        @AnnotationBasedSorter.Order(13)
        @ConfDefault.DefaultString("${prefix} <red>Данная функция отключена в конфигурации плагина!")
        String functionDisabled();

        @AnnotationBasedSorter.Order(14)
        @ConfDefault.DefaultString("${prefix} <red>Вы уже забирали очки репутации у данного игрока!")
        @ConfComments("Сообщение, если игрок уже забирал очки репутации данному игроку")
        String alreadyTookReputation();

        @AnnotationBasedSorter.Order(15)
        @ConfDefault.DefaultString("${prefix} <white>Вы успешно отняли очко репутации у игрока <green>${player_name}</green>!")
        @ConfComments("Сообщение, когда у игрока отняли очко репутации")
        String tookReputation();

        @AnnotationBasedSorter.Order(16)
        @ConfDefault.DefaultString("${prefix} <red>Вы достигли лимита для взаимодействия с очками репутации игроков!")
        @ConfComments("Сообщение, когда игрок превысил свой лимит по выдаче/забирании очков репутации")
        String maxReputation();

        @AnnotationBasedSorter.Order(17)
        @ConfDefault.DefaultString("${prefix} <red>Вы отказались от репутации!")
        @ConfComments("Сообщение, если игрок отказался от репутации")
        String youDeclinedReputation();

        @AnnotationBasedSorter.Order(18)
        @ConfDefault.DefaultString("${prefix} <red>Игрок <yellow>${player_name}</yellow> отказался от репутации!")
        @ConfComments("Сообщение, если указанный игрок отказался от репутации")
        String playerDeclinedReputation();

        @AnnotationBasedSorter.Order(19)
        @ConfDefault.DefaultString("${prefix} <white>Игрок <green>${player_name}</green> поделился с Вами положительной репутацией!")
        @ConfComments("Сообщение, когда игроку добавляют репутацию")
        String youGotReputation();

        @AnnotationBasedSorter.Order(20)
        @ConfDefault.DefaultString("${prefix} <white>Игрок <green>${player_name}</green> поделился с Вами отрицательной репутацией!")
        @ConfComments("Сообщение, когда у игрока отнимают репутацию")
        String youGotNegativeReputation();

        @AnnotationBasedSorter.Order(21)
        @ConfDefault.DefaultString("${prefix} <green>Вы отказались от репутации!")
        @ConfComments("Сообщение, когда игрок отказывается от репутации")
        String rejectReputation();

        @AnnotationBasedSorter.Order(22)
        @ConfDefault.DefaultString("${prefix} <green>Вы вновь можете взаимодействовать с репутацией!")
        @ConfComments("Сообщение, когда игрок возвращается к репутации")
        String removeRejectionReputation();

        @AnnotationBasedSorter.Order(23)
        @ConfDefault.DefaultString("${prefix} <red>Данная функция отключена Администрацией сервера!")
        @ConfComments("Сообщение, когда отказ от репутации выключен в конфигурации плагина")
        String rejectionDisabled();
    }

}
