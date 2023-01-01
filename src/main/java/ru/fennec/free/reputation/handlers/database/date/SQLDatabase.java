package ru.fennec.free.reputation.handlers.database.date;

import org.bukkit.entity.Player;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.h2.H2DatabasePlugin;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.date.mappers.GamePlayerMapper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class SQLDatabase implements IDatabase {

    private final MainConfig.DatabaseSection databaseSection;
    private final Jdbi jdbi;

    public SQLDatabase(ConfigManager<MainConfig> mainConfigManager) {
        MainConfig mainConfig = mainConfigManager.getConfigData();
        this.databaseSection = mainConfig.database();
        File databaseFile = new File("Reputation", "database.rep");
        try {
            databaseFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.jdbi = Jdbi.create("jdbc:h2:~/Reputation/database.rep");
        this.jdbi.installPlugin(new H2DatabasePlugin());
    }

    @Override
    public void initializeTables() {
        this.jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS ? (" +
                            "`id` BIGINT(50) auto_increment, " +
                            "`uuid` VARCHAR(50), " +
                            "`reputation` BIGINT(50), " +
                            "PRIMARY KEY (`id`) USING BTREE);",
                    this.databaseSection.tableName());
            handle.execute("CREATE TABLE IF NOT EXISTS ? (" +
                            "`id` BIGINT(50), " +
                            "`favorite` BIGINT(50), " +
                            "USING BTREE);",
                    this.databaseSection.favoritesTableName());
        });
    }

    @Override
    public void insertNewPlayer(IGamePlayer gamePlayer) {
        jdbi.useHandle(handle -> {
            handle.execute("INSERT IGNORE INTO ? " +
                            "(`uuid`)" +
                            "VALUES (?);",
                    this.databaseSection.tableName(),
                    gamePlayer.getGamePlayerUUID().toString());
        });
    }

    @Override
    public void savePlayer(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("UPDATE ? SET " +
                            "`reputation`=?",
                    this.databaseSection.tableName(),
                    gamePlayer.getPlayerReputation());
        });
    }

    @Override
    public void saveAction(IGamePlayer acting, IGamePlayer target) {
        this.jdbi.useHandle(handle -> {
            handle.execute("INSERT INTO ? " +
                            "(`id`, `favorite`) VALUES (?, ?);",
                    this.databaseSection.favoritesTableName(),
                    acting.getId(),
                    target.getId());
        });
    }

    @Override
    public void deleteAction(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("DELETE FROM ? WHERE `id`=? OR `favorite`=?",
                    this.databaseSection.favoritesTableName(),
                    gamePlayer.getId(),
                    gamePlayer.getId());
        });
    }

    @Override
    public IGamePlayer wrapPlayer(Player player) {
        AtomicReference<IGamePlayer> atomicGamePlayer = new AtomicReference<>();
        this.jdbi.useHandle(handle -> {
            IGamePlayer gamePlayer = handle.createQuery("SELECT * FROM ? WHERE `uuid`=?;")
                    .bind(0, this.databaseSection.tableName())
                    .bind(1, player.getUniqueId().toString())
                    .map(new GamePlayerMapper())
                    .first();

            gamePlayer.setIDsWhomGaveReputation(handle.createQuery("SELECT * FROM ? WHERE `id`=?;")
                    .bind(0, this.databaseSection.favoritesTableName())
                    .bind(1, gamePlayer.getId())
                    .mapTo(Long.class)
                    .list());

            atomicGamePlayer.set(gamePlayer);
        });
        return atomicGamePlayer.get();
    }
}
