package ru.fennec.free.reputation.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.Map;

public interface MainConfig {

    @AnnotationBasedSorter.Order(1)
    @SubSection
    DatabaseSection database();

    interface DatabaseSection {
        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultString("MYSQL")
        @ConfComments("Тип хранения данных репутации игроков. SQL - локально в папке плагина; MYSQL - удалённая база данных")
        DatabaseType type();

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultString("localhost:3306")
        String url();

        @AnnotationBasedSorter.Order(3)
        @ConfDefault.DefaultString("root")
        String username();

        @AnnotationBasedSorter.Order(4)
        @ConfDefault.DefaultString("")
        String password();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("reputation")
        String database();

        @AnnotationBasedSorter.Order(6)
        @ConfDefault.DefaultString("reputation")
        String tableName();

        @AnnotationBasedSorter.Order(7)
        @ConfDefault.DefaultString("favorites")
        String favoritesTableName();

        @AnnotationBasedSorter.Order(8)
        @ConfDefault.DefaultString("?autoReconnect=true")
        String args();
    }

    @AnnotationBasedSorter.Order(2)
    @ConfDefault.DefaultInteger(5)
    @ConfComments("Количество игроков для топа /rep top и /rep top online")
    int topAmount();

    @AnnotationBasedSorter.Order(3)
    @ConfDefault.DefaultMap({"default","10","vip","25","admin","-1"})
    @ConfComments("Максимально возможное количество репутации для выдачи другим игрокам. -1 - без ограничений. Право: reputation.max.название")
    Map<String, Long> maxReputation();

    @AnnotationBasedSorter.Order(3)
    @ConfDefault.DefaultMap({"5","Осваивающийся","500","Бог всея Руси"})
    @ConfComments("Звание, показывающееся игроку, достигшему определённого количества очков репутации")
    Map<Long, String> titles();

    @AnnotationBasedSorter.Order(4)
    @ConfDefault.DefaultBoolean(false)
    @ConfComments("Разрешить ли отнимать игрокам репутацию друг у друга")
    boolean tookReputation();

    enum DatabaseType {
        SQL, //Локальная база данных (В папке плагина)
        MYSQL //Удалённая база данных (Без создания отдельных файлов)
    }
}
