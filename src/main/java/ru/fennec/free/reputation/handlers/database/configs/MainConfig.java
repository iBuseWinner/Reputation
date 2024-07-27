package ru.fennec.free.reputation.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface MainConfig {

    @AnnotationBasedSorter.Order(1)
    @SubSection
    DatabaseSection database();

    interface DatabaseSection {
        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultString("SQL")
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
        @ConfDefault.DefaultString("commands")
        String commandsTableName();

        @AnnotationBasedSorter.Order(9)
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
    boolean takeReputation();

    @AnnotationBasedSorter.Order(5)
    @ConfDefault.DefaultBoolean(false)
    @ConfComments("Поставить true, если используете несколько серверов")
    boolean bungeeMode();

    @AnnotationBasedSorter.Order(6)
    @ConfDefault.DefaultLong(0)
    @ConfComments("Репутация, устанавливаемая игроку при первом входе")
    long defaultReputation();

    @AnnotationBasedSorter.Order(6)
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("Разрешить ли игроку взаимодействовать с репутацией при первом входе (/rep reject)")
    boolean defaultAcceptReputation();

    @AnnotationBasedSorter.Order(7)
    @ConfDefault.DefaultBoolean(false)
    @ConfComments("Поставить true, если не хотите, чтобы игрок не мог выдавать +1 и -1 репутацию одному и тому же игроку")
    boolean oneReputationPerPlayer();

    @AnnotationBasedSorter.Order(8)
    @ConfDefault.DefaultBoolean(false)
    @ConfComments("Разрешить ли отказываться от репутации")
    boolean rejectReputation();

    @AnnotationBasedSorter.Order(9)
    @SubSection
    ReputationColor color();

    interface ReputationColor {
        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultBoolean(false)
        @ConfComments("Изменение цвета репутации в зависимости от значения относительно нуля")
        boolean enable();

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultString("&c")
        @ConfComments("Если репутация ниже нуля")
        String negativeReputation();

        @AnnotationBasedSorter.Order(3)
        @ConfDefault.DefaultString("&7")
        @ConfComments("Если репутация равна нулю")
        String neutralReputation();

        @AnnotationBasedSorter.Order(4)
        @ConfDefault.DefaultString("&a")
        @ConfComments("Если репутация выше нуля")
        String positiveReputation();
    }

    @AnnotationBasedSorter.Order(10)
    @SubSection
    ReputationCommands commands();

    interface ReputationCommands {
        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultBoolean(false)
        @ConfComments("Изменение цвета репутации в зависимости от значения относительно нуля")
        boolean enable();

        static List<ReputationNeeded> defaultNeeds() {
            List<ReputationNeeded> needs = new ArrayList<>();
            needs.add(ReputationNeeded.of("5", false, 5,
                    Arrays.asList("say У меня 5 очков репутации, ыы", "console!say у ${player_name} 5 очков репутации, ыы")));
            needs.add(ReputationNeeded.of("10", true, 10, Arrays.asList("say я впервые достиг 10 очков репутации, поздравьте меня, пжшка!")));
            needs.add(ReputationNeeded.of("500", false, 500, Arrays.asList("msg BlackBaroness пошли покушаем за счёт заведения")));
            return needs;
        }

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultObject("defaultNeeds")
        List<@SubSection ReputationNeeded> needs();

        interface ReputationNeeded {
            String id();
            boolean oneTime();
            long minReputation();
            List<String> commands();

            static ReputationNeeded of(String id, boolean oneTime, long minReputation, List<String> commands) {
                return new ReputationNeeded() {
                    @Override
                    public String id() {
                        return id;
                    }
                    @Override
                    public boolean oneTime() {
                        return oneTime;
                    }
                    @Override
                    public long minReputation() {
                        return minReputation;
                    }
                    @Override
                    public List<String> commands() {
                        return commands;
                    }
                };
            }
        }
    }

    enum DatabaseType {
        SQL, //Локальная база данных (В папке плагина)
        MYSQL //Удалённая база данных (Без создания отдельных файлов)
    }
}
