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
        File databaseFile = new File("plugins/Reputation", "database.db");
        try {
            databaseFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.jdbi = Jdbi.create("jdbc:h2:file:"+databaseFile.toPath().toAbsolutePath()); //Ничего не работает, я плакать.
        this.jdbi.installPlugin(new H2DatabasePlugin());
    }

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
                            "`favorite` BIGINT(50));");
        });
    }

    @Override
    public void insertNewPlayer(IGamePlayer gamePlayer) {
        jdbi.useHandle(handle -> {
            handle.execute("INSERT IGNORE INTO `" + this.databaseSection.tableName() + "` " +
                            "(`uuid`, `reputation`)" +
                            "VALUES (?, '0');",
                    gamePlayer.getGamePlayerUUID().toString());
        });
    }

    @Override
    public void savePlayer(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("UPDATE `" + this.databaseSection.tableName() + "` SET " +
                            "`reputation`=? WHERE `id`=?",
                    gamePlayer.getPlayerReputation(),
                    gamePlayer.getId());
        });
    }

    @Override
    public void saveAction(IGamePlayer acting, IGamePlayer target) {
        this.jdbi.useHandle(handle -> {
            handle.execute("INSERT INTO `" + this.databaseSection.favoritesTableName() + "` " +
                            "(`id`, `favorite`) VALUES (?, ?);",
                    acting.getId(),
                    target.getId());
        });
    }

    @Override
    public void deleteAction(IGamePlayer gamePlayer) {
        this.jdbi.useHandle(handle -> {
            handle.execute("DELETE FROM `" + this.databaseSection.favoritesTableName() + "` WHERE `id`=? OR `favorite`=?",
                    gamePlayer.getId(),
                    gamePlayer.getId());
        });
    }

    @Override
    public IGamePlayer wrapPlayer(Player player) {
        AtomicReference<IGamePlayer> atomicGamePlayer = new AtomicReference<>();
        this.jdbi.useHandle(handle -> {
            IGamePlayer gamePlayer = handle.createQuery("SELECT * FROM `" + this.databaseSection.tableName() + "` WHERE `uuid`=?;")
                    .bind(0, player.getUniqueId().toString())
                    .map(new GamePlayerMapper())
                    .first();

            gamePlayer.setIDsWhomGaveReputation(handle.createQuery("SELECT * FROM `" + this.databaseSection.favoritesTableName() + "` WHERE `id`=?;")
                    .bind(0, gamePlayer.getId())
                    .mapTo(Long.class)
                    .list());

            atomicGamePlayer.set(gamePlayer);
        });
        return atomicGamePlayer.get();
    }
}
