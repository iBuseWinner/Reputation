package ru.fennec.free.reputation.handlers.database.date;

import org.bukkit.entity.Player;
import org.jdbi.v3.core.Jdbi;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.date.mappers.GamePlayerMapper;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MySQLDatabase implements IDatabase {

    private final MainConfig.DatabaseSection databaseSection;
    private final Jdbi jdbi;

    /*
    Удалённая БД, работает через MySQL (MariaDB)
     */
    public MySQLDatabase(ConfigManager<MainConfig> mainConfigManager) {
        MainConfig mainConfig = mainConfigManager.getConfigData();
        this.databaseSection = mainConfig.database();
        this.jdbi = Jdbi.create("jdbc:mysql://" + databaseSection.url() + "/" + databaseSection.database() + databaseSection.args(),
                databaseSection.username(), databaseSection.password());
    }

    /*
    Создание таблиц для плагина, если их нет
     */
    @Override
    public void initializeTables() {
        this.jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS `" + this.databaseSection.tableName() + "` (" +
                            "`id` BIGINT(50) auto_increment, " +
                            "`uuid` VARCHAR(50), " +
                            "`reputation` BIGINT(50), " +
                            "PRIMARY KEY (`id`) USING BTREE);");
            handle.execute("CREATE TABLE IF NOT EXISTS `" + this.databaseSection.favoritesTableName() + "` (" +
                            "`id` BIGINT(50), " +
                            "`favorite` BIGINT(50), " +
                            "`action` VARCHAR(50));");
        });
    }

    /*
    Добавить нового игрока в бд
     */
    @Override
    public void insertNewPlayer(IGamePlayer gamePlayer) {
        jdbi.useHandle(handle -> {
            handle.execute("INSERT IGNORE INTO `" + this.databaseSection.tableName() + "` " +
                            "(`uuid`, `reputation`) " +
                            "VALUES (?, '0');",
                    gamePlayer.getGamePlayerUUID().toString());
        });
    }

    /*
    Сохранить очки репутации игрока в бд
     */
    @Override
    public void savePlayer(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("UPDATE `" + this.databaseSection.tableName() + "` SET " +
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
            handle.execute("INSERT INTO `" + this.databaseSection.favoritesTableName() + "` " +
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
            handle.execute("DELETE FROM `" + this.databaseSection.favoritesTableName() + "` WHERE `id`=? OR `favorite`=?",
                    gamePlayer.getId(),
                    gamePlayer.getId());
        });
    }

    /*
    Получить игрока и его список фаворитов из бд, засунуть в объект GamePlayer (implements IGamePlayer)
     */
    @Override
    public IGamePlayer wrapPlayer(Player player) {
        AtomicReference<IGamePlayer> atomicGamePlayer = new AtomicReference<>();
        try {
            this.jdbi.useHandle(handle -> {
                IGamePlayer gamePlayer = handle.createQuery("SELECT * FROM `" + this.databaseSection.tableName() + "` WHERE `uuid`=?;")
                        .bind(0, player.getUniqueId().toString())
                        .map(new GamePlayerMapper())
                        .first();

                gamePlayer.setIDsWhomGaveReputation(handle.createQuery("SELECT * FROM `" + this.databaseSection.favoritesTableName()
                                + "` WHERE `id`=? AND `action`='INCREASE';")
                        .bind(0, gamePlayer.getId())
                        .mapTo(Long.class)
                        .list());

                gamePlayer.setIDsWhomTookReputation(handle.createQuery("SELECT * FROM `" + this.databaseSection.favoritesTableName()
                                + "` WHERE `id`=? AND `action`='DECREASE';")
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
    public UUID getTopGamePlayerUUIDByReputation(int place) {
        AtomicReference<UUID> atomicUUID = new AtomicReference<>();
        this.jdbi.useHandle(handle -> {
            atomicUUID.set(UUID.fromString(handle.createQuery("SELECT `uuid` FROM (" +
                            "SELECT uuid, RANK() OVER (ORDER BY `reputation` DESC) rnk " +
                            "FROM `" + this.databaseSection.tableName() + "` " +
                            "ORDER BY rnk" +
                            ") ranked_players " +
                            "WHERE rnk=" + place)
                    .mapTo(String.class)
                    .first()));
        });
        return atomicUUID.get();
    }

    /*
    Получить репутацию игрока с N места в топе игроков по репутации
     */
    @Override
    public Long getTopGamePlayerReputationByReputation(int place) {
        AtomicReference<Long> atomicLong = new AtomicReference<>();
        this.jdbi.useHandle(handle -> {
            atomicLong.set(handle.createQuery("SELECT `reputation` FROM (" +
                            "SELECT reputation, RANK() OVER (ORDER BY `reputation` DESC) rnk " +
                            "FROM `" + this.databaseSection.tableName() + "` " +
                            "ORDER BY rnk" +
                            ") ranked_players " +
                            "WHERE rnk=" + place)
                    .mapTo(Long.class)
                    .first());
        });
        return atomicLong.get();
    }
}
