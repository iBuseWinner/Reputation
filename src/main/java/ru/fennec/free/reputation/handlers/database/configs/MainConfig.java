package ru.fennec.free.reputation.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;

public interface MainConfig {

    @SubSection
    DatabaseSection database();

    interface DatabaseSection {
        @ConfDefault.DefaultString("MYSQL")
        @ConfComments("Тип хранения данных репутации игроков. SQL - локально в папке плагина; MYSQL - удалённая база данных")
        DatabaseType type();

        @ConfDefault.DefaultString("localhost:3301")
        String url();

        @ConfDefault.DefaultString("root")
        String username();

        @ConfDefault.DefaultString("")
        String password();

        @ConfDefault.DefaultString("reputation")
        String database();

        @ConfDefault.DefaultString("reputation")
        String tableName();

        @ConfDefault.DefaultString("favorites")
        String favoritesTableName();

        @ConfDefault.DefaultString("?autoReconnect=true")
        String args();
    }

    enum DatabaseType {
        SQL, //Локальная база данных (В папке плагина)
        MYSQL //Удалённая база данных (Без создания отдельных файлов)
    }
}
