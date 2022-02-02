package ru.ibusewinner.reputation.data;

import org.bukkit.Bukkit;
import ru.ibusewinner.plugin.buseapi.BuseAPI;
import ru.ibusewinner.plugin.buseapi.mysql.MySQL;
import ru.ibusewinner.reputation.Reputation;
import ru.ibusewinner.reputation.data.items.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RepMySQL extends MySQL {
    public RepMySQL(String host, int port, String database, String args, String user, String password) {
        super(host, port, database, args, user, password);
    }

    public void createTables() {
        getPreparedStatement("CREATE TABLE IF NOT EXISTS `reputation` (" +
                "`id` BIGINT auto_increment DEFAULT '0'," +
                "`nickname` VARCHAR(50) DEFAULT '0'," +
                "`rep` BIGINT DEFAULT '0'," +
                "`list` VARCHAR(25) DEFAULT '0'" +
                "PRIMARY KEY (`id`) USING BTREE) COLLATE='utf8_general_ci' ENGINE=InnoDB;", preparedStatement -> {
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });
    }

    public void updateUser(User user) {
        StringBuilder listSB = new StringBuilder();
        for (User target : user.getActedUsers()) {
            listSB.append(target.getId()).append(",");
        }
        String list = listSB.substring(0, listSB.toString().length()-1);

        getPreparedStatement("UPDATE `reputation` SET " +
                "`rep`='"+user.getRep()+"'," +
                "`list`='"+list+"' WHERE `id`='"+user.getId()+"';", preparedStatement -> {
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });
    }

    public User getUserByNickname(String nickname) {
        AtomicReference<User> user = new AtomicReference<>();
        Bukkit.getScheduler().runTaskAsynchronously(Reputation.getInstance(), () -> {
            getResultSet("SELECT * FROM `reputation` WHERE `nickname`='"+nickname+"';", resultSet -> {
                try {
                    if (resultSet.next()) {
                        long id = resultSet.getLong("id");
                        long rep = resultSet.getLong("rep");
                        String listString = resultSet.getString("list");
                        List<User> actedUsers = new ArrayList<>();
                        for (String acted : listString.split(",")) {
                            actedUsers.add(getUserById(Long.parseLong(acted)));
                        }
                        user.set(new User(id, nickname, rep, actedUsers));
                    }
                    resultSet.close();
                } catch (SQLException e) {
                    BuseAPI.getBuseLogger().error(e);
                }
            });
        });
        return user.get();
    }

    public User getUserById(long id) {
        AtomicReference<User> user = new AtomicReference<>();
        Bukkit.getScheduler().runTaskAsynchronously(Reputation.getInstance(), () -> {
            getResultSet("SELECT * FROM `reputation` WHERE `id`='"+id+"';", resultSet -> {
                try {
                    if (resultSet.next()) {
                        String nickname = resultSet.getString("nickname");
                        long rep = resultSet.getLong("rep");
                        String listString = resultSet.getString("list");
                        List<User> actedUsers = new ArrayList<>();
                        for (String acted : listString.split(",")) {
                            actedUsers.add(getUserById(Long.parseLong(acted)));
                        }
                        user.set(new User(id, nickname, rep, actedUsers));
                    }
                    resultSet.close();
                } catch (SQLException e) {
                    BuseAPI.getBuseLogger().error(e);
                }
            });
        });
        return user.get();
    }

    public void createUser(User user) {
        getPreparedStatement("INSERT INTO `reputation` (`nickname`) VALUES ('"+user.getNickname()+"');", preparedStatement -> {
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });
    }

}
