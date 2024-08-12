package ru.fennec.free.reputation.handlers.database.date;

import org.bukkit.entity.Player;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.date.mappers.GamePlayerMapper;
import ru.fennec.free.reputation.handlers.enums.OrderBy;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SQLDatabase implements IDatabase {

    private final MainConfig mainConfig;
    private final MainConfig.DatabaseSection databaseSection;
    private final Jdbi jdbi;

    /*
    Локальная БД, хранящаяся в файле /plugins/Reputation/database.db. Работает через SQLite
     */
    public SQLDatabase(ConfigManager<MainConfig> mainConfigManager) {
        this.mainConfig = mainConfigManager.getConfigData();
        this.databaseSection = mainConfig.database();
        File databaseFile = new File("plugins/Reputation", "database.db");
        try {
            databaseFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.jdbi = Jdbi.create("jdbc:sqlite:" + databaseFile.toPath()).installPlugin(new SQLitePlugin());
    }

    /*
    Создание таблиц для плагина, если их нет
     */
    @Override
    public void initializeTables() {
        this.jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS \"" + this.databaseSection.tableName() + "\" (" +
                    "`id` INTEGER, " +
                    "`uuid` VARCHAR(50) UNIQUE, " +
                    "`reputation` BIGINT(50), " +
                    "`acceptReputation` BOOLEAN, " +
                    "PRIMARY KEY (`id` AUTOINCREMENT));"); //Таблица с репутацией игроков
            handle.execute("CREATE TABLE IF NOT EXISTS \"" + this.databaseSection.favoritesTableName() + "\" (" +
                    "`id` BIGINT(50), " + //player id from main table
                    "`favorite` BIGINT(50), " + //target player id from main table
                    "`action` VARCHAR(50));"); //Таблица с историей фаворитов игроков
            handle.execute("CREATE TABLE IF NOT EXISTS \"" + this.databaseSection.commandsTableName() + "\" (" +
                    "`id` BIGINT(50), " + //player id from main table
                    "`commandId` BIGINT(50));"); //Таблица с историей команд игроков
        });
    }

    /*
    Добавить нового игрока в бд
     */
    @Override
    public void insertNewPlayer(IGamePlayer gamePlayer) {
        jdbi.useHandle(handle -> {
            handle.execute("INSERT OR IGNORE INTO \"" + this.databaseSection.tableName() + "\" " +
                            "(`uuid`, `reputation`, `acceptReputation`) " +
                            "VALUES (?, ?, ?);",
                    gamePlayer.getGamePlayerUUID().toString(),
                    mainConfig.defaultReputation(),
                    mainConfig.defaultAcceptReputation());
        });
    }

    /*
    Сохранить очки репутации игрока в бд
     */
    @Override
    public void savePlayer(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("UPDATE \"" + this.databaseSection.tableName() + "\" SET " +
                            "`reputation`=? WHERE `id`=?",
                    gamePlayer.getPlayerReputation(),
                    gamePlayer.getId());
        });
    }

    /*
    Добавить нового фаворита игрока в бд
     */
    @Override
    public void saveAction(IGamePlayer acting, IGamePlayer target, String action) {
        this.jdbi.useHandle(handle -> {
            handle.execute("INSERT INTO \"" + this.databaseSection.favoritesTableName() + "\" " +
                            "(`id`, `favorite`, `action`) VALUES (?, ?, ?);",
                    acting.getId(),
                    target.getId(),
                    action);
        });
    }

    /*
    Удалить всю историю с фаворитами, связанную с определённым игроком
     */
    @Override
    public void deleteAction(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("DELETE FROM \"" + this.databaseSection.favoritesTableName() + "\" WHERE `id`=? OR `favorite`=?",
                    gamePlayer.getId(),
                    gamePlayer.getId());
        });
    }

    /*
    Добавить новую команду игрока в бд
     */
    @Override
    public void saveCommand(IGamePlayer acting, String commandId) {
        this.jdbi.useHandle(handle -> {
            handle.execute("INSERT INTO \"" + this.databaseSection.commandsTableName() + "\" " +
                            "(`id`, `commandId`) VALUES (?, ?);",
                    acting.getId(),
                    commandId);
        });
    }

    /*
    Удалить всю историю с командами, связанную с определённым игроком
     */
    @Override
    public void deleteCommand(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("DELETE FROM \"" + this.databaseSection.commandsTableName() + "\" WHERE `id`=? OR `favorite`=?",
                    gamePlayer.getId(),
                    gamePlayer.getId());
        });
    }

    /*
    Узнать была ли использована команда
     */
    @Override
    public boolean isUsedCommand(IGamePlayer gamePlayer, String commandId) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        try {
            this.jdbi.useHandle(handle -> {
                int count = handle.createQuery("SELECT COUNT(1) FROM `"+ this.databaseSection.commandsTableName() +"` WHERE `id`=?")
                        .bind(0, gamePlayer.getId())
                        .mapTo(Integer.class)
                        .first();
                atomicBoolean.set(count == 1);
            });
        } catch (IllegalStateException ignored) {  }
        return atomicBoolean.get();
    }

    /*
    Получить игрока и его список фаворитов из бд, засунуть в объект GamePlayer (implements IGamePlayer)
     */
    @Override
    public IGamePlayer wrapPlayer(Player player) {
        AtomicReference<IGamePlayer> atomicGamePlayer = new AtomicReference<>();
        try {
            this.jdbi.useHandle(handle -> {
                IGamePlayer gamePlayer = handle.createQuery("SELECT * FROM \"" + this.databaseSection.tableName() + "\" WHERE `uuid`=?;")
                        .bind(0, player.getUniqueId().toString())
                        .map(new GamePlayerMapper())
                        .first();

                gamePlayer.setIDsWhomGaveReputation(handle.createQuery("SELECT * FROM \"" + this.databaseSection.favoritesTableName()
                                + "\" WHERE `id`=? AND `action`='INCREASE';")
                        .bind(0, gamePlayer.getId())
                        .mapTo(Long.class)
                        .list());

                gamePlayer.setIDsWhomTookReputation(handle.createQuery("SELECT * FROM \"" + this.databaseSection.favoritesTableName()
                                + "\" WHERE `id`=? AND `action`='DECREASE';")
                        .bind(0, gamePlayer.getId())
                        .mapTo(Long.class)
                        .list());

                atomicGamePlayer.set(gamePlayer);
            });
        } catch (IllegalStateException ignored) { atomicGamePlayer.set(null); }
        return atomicGamePlayer.get();
    }

    /*
    Получить UUID игрока с N места в топе игроков по репутации
     */
    @Override
    public UUID getTopGamePlayerUUIDByReputation(int place, OrderBy orderBy) {
        AtomicReference<UUID> atomicUUID = new AtomicReference<>();
        this.jdbi.useHandle(handle -> {
            atomicUUID.set(UUID.fromString(handle.createQuery("WITH ranked_players AS (" +
                            "SELECT uuid, reputation, ROW_NUMBER() OVER " +
                            "(ORDER BY reputation " + orderBy.getValue() + ") AS rank " +
                            "FROM \"" + this.databaseSection.tableName() + "\" " +
                            ") SELECT uuid FROM ranked_players WHERE rank=" + place)
                    .mapTo(String.class)
                    .first()));
        });
        return atomicUUID.get();
    }

    /*
    Получить репутацию игрока с N места в топе игроков по репутации
     */
    @Override
    public Long getTopGamePlayerReputationByReputation(int place, OrderBy orderBy) {
        AtomicReference<Long> atomicLong = new AtomicReference<>();
        this.jdbi.useHandle(handle -> {
            atomicLong.set(handle.createQuery("WITH ranked_players AS (" +
                            "SELECT uuid, reputation, ROW_NUMBER() OVER " +
                            "(ORDER BY reputation " + orderBy.getValue() + ") AS rank " +
                            "FROM \"" + this.databaseSection.tableName() + "\" " +
                            ") SELECT reputation FROM ranked_players WHERE rank=" + place)
                    .mapTo(Long.class)
                    .first());
        });
        return atomicLong.get();
    }
}
