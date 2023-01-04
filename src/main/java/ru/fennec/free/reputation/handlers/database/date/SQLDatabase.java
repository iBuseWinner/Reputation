package ru.fennec.free.reputation.handlers.database.date;

import org.bukkit.entity.Player;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.date.mappers.GamePlayerMapper;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class SQLDatabase implements IDatabase {

    private final MainConfig.DatabaseSection databaseSection;
    private final Jdbi jdbi;

    public SQLDatabase(ConfigManager<MainConfig> mainConfigManager) {
        MainConfig mainConfig = mainConfigManager.getConfigData();
        this.databaseSection = mainConfig.database();
        File databaseFile = new File("plugins/Reputation", "database.db");
        try {
            databaseFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.jdbi = Jdbi.create("jdbc:sqlite:" + databaseFile.toPath()).installPlugin(new SQLitePlugin());
    }

    @Override
    public void initializeTables() {
        this.jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS \"" + this.databaseSection.tableName() + "\" (" +
                    "`id` INTEGER, " +
                    "`uuid` VARCHAR(50), " +
                    "`reputation` BIGINT(50), " +
                    "PRIMARY KEY (`id` AUTOINCREMENT));");
            handle.execute("CREATE TABLE IF NOT EXISTS \"" + this.databaseSection.favoritesTableName() + "\" (" +
                    "`id` BIGINT(50), " +
                    "`favorite` BIGINT(50));");
        });
    }

    @Override
    public void insertNewPlayer(IGamePlayer gamePlayer) {
        jdbi.useHandle(handle -> {
            handle.execute("INSERT IGNORE INTO \"" + this.databaseSection.tableName() + "\" " +
                            "(`uuid`, `reputation`)" +
                            "VALUES (?, '0');",
                    gamePlayer.getGamePlayerUUID().toString());
        });
    }

    @Override
    public void savePlayer(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("UPDATE \"" + this.databaseSection.tableName() + "\" SET " +
                            "`reputation`=? WHERE `id`=?",
                    gamePlayer.getPlayerReputation(),
                    gamePlayer.getId());
        });
    }

    @Override
    public void saveAction(IGamePlayer acting, IGamePlayer target) {
        this.jdbi.useHandle(handle -> {
            handle.execute("INSERT INTO \"" + this.databaseSection.favoritesTableName() + "\" " +
                            "(`id`, `favorite`) VALUES (?, ?);",
                    acting.getId(),
                    target.getId());
        });
    }

    @Override
    public void deleteAction(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("DELETE FROM \"" + this.databaseSection.favoritesTableName() + "\" WHERE `id`=? OR `favorite`=?",
                    gamePlayer.getId(),
                    gamePlayer.getId());
        });
    }

    @Override
    public IGamePlayer wrapPlayer(Player player) {
        AtomicReference<IGamePlayer> atomicGamePlayer = new AtomicReference<>();
        this.jdbi.useHandle(handle -> {
            IGamePlayer gamePlayer = handle.createQuery("SELECT * FROM \"" + this.databaseSection.tableName() + "\" WHERE `uuid`=?;")
                    .bind(0, player.getUniqueId().toString())
                    .map(new GamePlayerMapper())
                    .first();

            gamePlayer.setIDsWhomGaveReputation(handle.createQuery("SELECT * FROM \"" + this.databaseSection.favoritesTableName()
                            + "\" WHERE `id`=?;")
                    .bind(0, gamePlayer.getId())
                    .mapTo(Long.class)
                    .list());

            atomicGamePlayer.set(gamePlayer);
        });
        return atomicGamePlayer.get();
    }

    @Override
    public UUID getTopGamePlayerUUIDByReputation(int place) {
        AtomicReference<UUID> atomicUUID = new AtomicReference<>();
        this.jdbi.useHandle(handle -> {
            atomicUUID.set(UUID.fromString(handle.createQuery("SELECT `uuid` FROM \"" + this.databaseSection.tableName()
                            + "\" ORDER BY `reputation` DESC " +
                            "LIMIT " + place + " OFFSET " + place)
                    .mapTo(String.class)
                    .first()));
        });
        return atomicUUID.get();
    }

    @Override
    public Long getTopGamePlayerReputationByReputation(int place) {
        AtomicReference<Long> atomicLong = new AtomicReference<>();
        this.jdbi.useHandle(handle -> {
            atomicLong.set(handle.createQuery("SELECT `reputation` FROM \"" + this.databaseSection.tableName()
                            + "\" ORDER BY `reputation` DESC " +
                            "LIMIT " + place + " OFFSET " + place)
                    .mapTo(Long.class)
                    .first());
        });
        return atomicLong.get();
    }
}
