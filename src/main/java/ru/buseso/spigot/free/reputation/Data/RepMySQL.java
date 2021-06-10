package ru.buseso.spigot.free.reputation.Data;

import org.bukkit.entity.Player;
import ru.buseso.spigot.free.reputation.Reputation;
import ru.buseso.spigot.free.reputation.Utils.RepPlayer;
import ru.buseso.spigot.free.reputation.Utils.RepSender;
import ru.buseso.spigot.free.reputation.Utils.RepTop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepMySQL {
    private static String sqlHost;
    private static int sqlPort;
    private static String sqlDatabase;
    private static String sqlUser;
    private static String sqlPassword;
    private static String sqlTable;

    public RepMySQL(String sqlHost, int sqlPort, String sqlDatabase, String sqlUser, String sqlPassword, String sqlTable) {
        RepMySQL.sqlHost = sqlHost;
        RepMySQL.sqlPort = sqlPort;
        RepMySQL.sqlDatabase = sqlDatabase;
        RepMySQL.sqlUser = sqlUser;
        RepMySQL.sqlPassword = sqlPassword;
        RepMySQL.sqlTable = sqlTable;
    }

    public static Connection con;

    public static boolean isCon() { return con != null; }

    public void connect() {
        if(!isCon()) {
            try {
                String url = "jdbc:mysql://"+sqlHost+":"+sqlPort+"/"+sqlDatabase+"?autoReconnect=true";
                con = DriverManager.getConnection(url,sqlUser,sqlPassword);
                RepSender.log(Reputation.config.sqlSuccConnect()
                        .replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("%type%",Reputation.config.dataType()));
            }catch (SQLException ex) {
                RepSender.log(Reputation.config.sqlErrConnect()
                        .replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("%type%",Reputation.config.dataType()));
                if(Reputation.config.debugMode()) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void reconnect() {
        if(!isCon()) {
            try {
                con = null;
                String url = "jdbc:mysql://"+sqlHost+":"+sqlPort+"/"+sqlDatabase+"?autoReconnect=true";
                con = DriverManager.getConnection(url,sqlUser,sqlPassword);
            }catch (SQLException ex) {
                RepSender.log(Reputation.config.sqlErrConnect()
                        .replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("%type%",Reputation.config.dataType()));
                if(Reputation.config.debugMode()) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void disconnect() {
        if(isCon()) {
            try {
                con.close();
                RepSender.log(Reputation.config.sqlSuccDisconnect()
                        .replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("%type%",Reputation.config.dataType()));
            }catch (SQLException ex) {
                RepSender.log(Reputation.config.sqlErrDisconnect()
                        .replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("%type%",Reputation.config.dataType()));
                if(Reputation.config.debugMode()) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static PreparedStatement getStat(String sql) {
        if(isCon()) {
            try {
                return con.prepareStatement(sql);
            }catch (SQLException ex) {
                RepSender.log(Reputation.config.sqlErrStatement()
                        .replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("%type%",Reputation.config.dataType()));
                if(Reputation.config.debugMode()) {
                    ex.printStackTrace();
                }
            }
        } else {
            reconnect();
        }
        return null;
    }

    public static int sendExecute(String sql) {
        if(isCon()) {
            try {
                return getStat(sql).executeUpdate();
            }catch (SQLException ignored) {
                RepSender.log("§6Stupid stacktrace:");
                ignored.printStackTrace();
            }
        } else {
            reconnect();
        }
        return -1;
    }

    public static ResultSet sendQuery(String sql) {
        if(isCon()) {
            try {
                return getStat(sql).executeQuery();
            }catch (SQLException ignored) {
                RepSender.log("§6Stupid stacktrace:");
                ignored.printStackTrace();
            }
        } else {
            reconnect();
        }
        return null;
    }

    public static class Requests {
        public void createTable() {
            sendExecute("CREATE TABLE IF NOT EXISTS `"+sqlTable+"` (" +
                    "`id` INT(11) NOT NULL AUTO_INCREMENT," +
                    "`nick` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci'," +
                    "`reps` INT(11) NOT NULL DEFAULT 0," +
                    "`repp` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb4_unicode_ci'," +
                    "`repm` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb4_unicode_ci'," +
                    "PRIMARY KEY (`id`)) COLLATE='utf8mb4_unicode_ci' ENGINE=InnoDB;");
        }

        public void addPlayer(Player p) {
            sendExecute("INSERT INTO `"+sqlTable+"` (`nick`) VALUES ('"+p.getName()+"');");
        }

        public RepPlayer getPlayer(Player p) {
            ResultSet rs = sendQuery("SELECT * FROM `"+sqlTable+"` WHERE `nick`='"+p.getName()+"';");
            RepPlayer rp = new RepPlayer(rs);
            try {
                rs.close();
            } catch (SQLException ignored) {}
            return rp;
        }

        public RepPlayer getPlayer(String nick) {
            ResultSet rs = sendQuery("SELECT * FROM `"+sqlTable+"` WHERE `nick`='"+nick+"';");
            RepPlayer rp = new RepPlayer(rs);
            try {
                rs.close();
            } catch (SQLException ignored) {}
            return rp;
        }

        public void setPlayer(RepPlayer pp) {
            sendExecute("UPDATE `"+sqlTable+"` SET `reps`='"+pp.getReps()+"'," +
                    " `repp`='"+pp.getRepp()+"', `repm`='"+pp.getRepm()+"'" +
                    " WHERE `nick`='"+pp.getUuid()+"';");
        }

        public List<RepTop> getTopPlayers() {
            ResultSet rs = sendQuery("SELECT * FROM `"+sqlTable+"` ORDER BY `reps` DESC LIMIT "+Reputation.config.topLimit()+";");
            List<RepTop> list = new ArrayList<>();

            try {
                for (int i = 0; i < Reputation.config.topLimit(); i++) {
                    if (rs.next()) {
                        String name = rs.getString("nick");
                        int reps = rs.getInt("reps");
                        RepTop rt = new RepTop(name, reps);
                        list.add(rt);
                    } else break;
                }

                return list;
            } catch (SQLException ignored) {
                RepSender.log(ignored.getMessage());
                ignored.printStackTrace();
            }
            return null;
        }
    }

}
