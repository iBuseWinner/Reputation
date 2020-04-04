package ru.buseso.spigot.free.reputation;

import org.bukkit.entity.Player;

import java.sql.*;

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

    static Connection con;

    static boolean isCon() { return con != null; }

    void connect() {
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

    static void reconnect() {
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

    void disconnect() {
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

    static PreparedStatement getStat(String sql) {
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

    static int sendExecute(String sql) {
        if(isCon()) {
            try {
                return getStat(sql).executeUpdate();
            }catch (SQLException ignored) { }
        } else {
            reconnect();
        }
        return -1;
    }

    static ResultSet sendQuery(String sql) {
        if(isCon()) {
            try {
                return getStat(sql).executeQuery();
            }catch (SQLException ignored) { }
        } else {
            reconnect();
        }
        return null;
    }

    public static class Requests {
        void createTable() {
            sendExecute("CREATE TABLE IF NOT EXISTS `"+sqlTable+"` (" +
                    "`id` INT(11) NOT NULL AUTO_INCREMENT," +
                    "`nick` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci'," +
                    "`reps` INT(11) NULL DEFAULT NULL," +
                    "`repp` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci'," +
                    "`repm` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci'," +
                    "PRIMARY KEY (`id`)) COLLATE='utf8mb4_unicode_ci' ENGINE=InnoDB;");
        }

        void addPlayer(Player p) {
            sendExecute("INSERT INTO `"+sqlTable+"` (`nick`) VALUES ('"+p.getName()+"');");
        }

        RepPlayer getPlayer(Player p) {
            ResultSet rs = sendQuery("SELECT * FROM `"+sqlTable+"` WHERE `nick`='"+p.getName()+"';");
            return new RepPlayer(rs);
        }

        RepPlayer getPlayer(String nick) {
            ResultSet rs = sendQuery("SELECT * FROM `"+sqlTable+"` WHERE `nick`='"+nick+"';");
            return new RepPlayer(rs);
        }

        void setPlayer(RepPlayer pp) {
            sendExecute("UPDATE `"+sqlTable+"` SET `reps`='"+pp.getReps()+"'," +
                    " `repp`='"+pp.getRepp()+"', `repm`='"+pp.getRepm()+"'" +
                    " WHERE `nick`='"+pp.getUuid()+"';");
        }
    }

}
