package ru.buseso.spigot.free.reputation.Data;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class RepConfig {
    public RepConfig(FileConfiguration cfg) {
        this.dataType = cfg.getString("settings.data.type");
        this.sqlHost = cfg.getString("settings.data.sql.host");
        this.sqlPort = cfg.getInt("settings.data.sql.port");
        this.sqlDatabase = cfg.getString("settings.data.sql.database");
        this.sqlUser = cfg.getString("settings.data.sql.user");
        this.sqlPassword = cfg.getString("settings.data.sql.password");
        this.sqlTable = cfg.getString("settings.data.sql.table");

        this.topLimit = cfg.getInt("settings.top-limit");
        this.canNegative = cfg.getBoolean("settings.can-negative");
        this.checkUpdates = cfg.getBoolean("settings.check-updates");
        this.debugMode = cfg.getBoolean("settings.debug-mode");
        this.autoSave = cfg.getInt("settings.auto-save");

        this.prefix = cfg.getString("messages.prefix").replaceAll("&","§");
        this.noPerm = cfg.getString("messages.no-perm").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.adminHelp = cfg.getStringList("messages.admin.help");
        this.adminSuccAdd = cfg.getString("messages.admin.success.add").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.adminSuccTake = cfg.getString("messages.admin.success.take").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.adminSuccSet = cfg.getString("messages.admin.success.set").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.adminSuccReset = cfg.getString("messages.admin.success.reset").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.adminSuccReload = cfg.getString("messages.admin.success.reload").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.adminError = cfg.getString("messages.admin.errors").replaceAll("%prefix%",prefix).replaceAll("&","§");

        this.playerHelp = cfg.getStringList("messages.player.help");
        this.playerSuccRepPlus = cfg.getString("messages.player.success.rep+").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerSuccRepMinus = cfg.getString("messages.player.success.rep-").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerSuccUndo = cfg.getString("messages.player.success.undo").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerSelfReps = cfg.getString("messages.player.success.get-self-reps").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerOtherReps = cfg.getString("messages.player.success.get-other-reps").replaceAll("%prefix%",prefix).replaceAll("&","§");

        this.playerErrorsAlreadyRepPlus = cfg.getString("messages.player.errors.already-rep+").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerErrorsAlreadyRepMinus = cfg.getString("messages.player.errors.already-rep-").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerErrorsNothingUndo = cfg.getString("messages.player.errors.nothing-undo").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerErrorsWriteNick = cfg.getString("messages.player.errors.write-nick").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerErrorsOwnNick = cfg.getString("messages.player.errors.own-nick").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerErrorsOwnIp = cfg.getString("messages.player.errors.own-ip").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.playerErrorsTargetNotFound = cfg.getString("messages.player.errors.target-not-found").replaceAll("%prefix%",prefix).replaceAll("&","§");

        this.sqlSuccConnect = cfg.getString("messages.sql.success.connect").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.sqlSuccDisconnect = cfg.getString("messages.sql.success.disconnect").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.sqlErrConnect = cfg.getString("messages.sql.errors.connect").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.sqlErrDisconnect = cfg.getString("messages.sql.errors.disconnect").replaceAll("%prefix%",prefix).replaceAll("&","§");
        this.sqlErrStatement = cfg.getString("messages.sql.errors.statement").replaceAll("%prefix%",prefix).replaceAll("&","§");

		String ver = cfg.getString("version").replace(".","");
        this.version = Integer.parseInt(ver);
    }

    private String dataType = "yaml";
    private String sqlHost = "localhost";
    private int sqlPort = 3306;
    private String sqlDatabase = "database";
    private String sqlUser = "root";
    private String sqlPassword = "";
    private String sqlTable = "reputation";

    private int topLimit = 5;
    private boolean canNegative = true;
    private boolean checkUpdates = true;
    private boolean debugMode = true;
    private int autoSave = 0;

    private String prefix = "&6Репутация &8>>&7";
    private String noPerm = "%prefix% &cУ Вас недостаточно прав для использования данной команды! Отсутствует право: &4%perm%";

    private List<String> adminHelp = new ArrayList<>();
    private String adminSuccAdd = "%prefix% &aВы успешно добавили игроку &e%player% %reputation%&a очков репутации!";
    private String adminSuccTake = "%prefix% &aВы успешно забрали у игрока &e%player% %reputation%&a очков репутации!";
    private String adminSuccSet = "%prefix% &aВы успешно установили игроку &e%player% %reputation%&a очков репутации!";
    private String adminSuccReset = "%prefix% &aВы успешно сбросили игроку &e%player% очки репутации!";
    private String adminSuccReload = "%prefix% &aВы успешно перезагрузили конфиг!";
    private String adminError = "%prefix% &cОшибка при использовании команды! Проверьте правильность ввода и повторите попытку!";

    private List<String> playerHelp = new ArrayList<>();
    private String playerSuccRepPlus = "%prefix% &aВы отдали очко репутации игроку &e%player%&a!";
    private String playerSuccRepMinus = "%prefix% &aВы забрали очко репутации у игрока &e%player%&a!";
    private String playerSuccUndo = "%prefix% &aВы отменили своё действие!";
    private String playerSelfReps = "%prefix% &aУ Вас &e%reputation%&a очков репутации!";
    private String playerOtherReps = "%prefix% &aУ игрока &e%player% %reputation%&a очков репутации!";

    private String playerErrorsAlreadyRepPlus = "%prefix% &cВы уже отдали очко репутации другому игроку!";
    private String playerErrorsAlreadyRepMinus = "%prefix% &cВы уже забрали очко репутации у другого игрока!";
    private String playerErrorsNothingUndo = "%prefix% &cНечего отменять!";
    private String playerErrorsWriteNick = "%prefix% &cУкажите игрока, которому хотите отдать/забрать очко репутации!";
    private String playerErrorsOwnNick = "%prefix% &cВы не можете отдать/забрать очко репутации самому себе!";
    private String playerErrorsOwnIp = "%prefix% &cВы не можете отдать/забрать очко репутации другому своему аккаунту!";
    private String playerErrorsTargetNotFound = "%prefix% &cИгрок не найден!";

    private String sqlSuccConnect = "%prefix% &aУспешно подключено к %type%!";
    private String sqlSuccDisconnect = "%prefix% &aУспешно отключено от %type%!";
    private String sqlErrConnect = "%prefix% &cПроизошла ошибка при подключении к %type%!";
    private String sqlErrDisconnect = "%prefix% &cПроизошла ошибка при отключении от %type%!";
    private String sqlErrStatement = "%prefix% &cПроизошла ошибка при выполнении запроса к %type%!";

    private int version = 20;

    //Methods
    public String dataType() { return this.dataType; }
    public String sqlHost() { return this.sqlHost; }
    public int sqlPort() { return this.sqlPort; }
    public String sqlDatabase() { return this.sqlDatabase; }
    public String sqlUser() { return this.sqlUser; }
    public String sqlPassword() { return this.sqlPassword; }
    public String sqlTable() { return this.sqlTable; }
    public int topLimit() { return this.topLimit; }
    public boolean canNegative() { return this.canNegative; }
    public boolean checkUpdates() { return this.checkUpdates; }
    public boolean debugMode() { return this.debugMode; }
    public int autoSave() { return this.autoSave; }
    public String prefix() { return this.prefix; }
    public String noPerm() { return this.noPerm; }
    public List<String> adminHelp() { return this.adminHelp; }
    public String adminSuccAdd() { return this.adminSuccAdd; }
    public String adminSuccTake() { return this.adminSuccTake; }
    public String adminSuccSet() { return this.adminSuccSet; }
    public String adminSuccReset() { return this.adminSuccReset; }
    public String adminSuccReload() { return this.adminSuccReload; }
    public String adminError() { return this.adminError; }
    public List<String> playerHelp() { return this.playerHelp; }
    public String playerSuccRepPlus() { return this.playerSuccRepPlus; }
    public String playerSuccRepMinus() { return this.playerSuccRepMinus; }
    public String playerSuccUndo() { return this.playerSuccUndo; }
    public String playerSelfReps() { return this.playerSelfReps; }
    public String playerOtherReps() { return this.playerOtherReps; }
    public String playerErrorsAlreadyRepPlus() { return this.playerErrorsAlreadyRepPlus; }
    public String playerErrorsAlreadyRepMinus() { return this.playerErrorsAlreadyRepMinus; }
    public String playerErrorsNothingUndo() { return this.playerErrorsNothingUndo; }
    public String playerErrorsWriteNick() { return this.playerErrorsWriteNick; }
    public String playerErrorsOwnNick() { return this.playerErrorsOwnNick; }
    public String playerErrorsOwnIp() { return this.playerErrorsOwnIp; }
    public String playerErrorsTargetNotFound() { return this.playerErrorsTargetNotFound; }
    public String sqlSuccConnect() { return this.sqlSuccConnect; }
    public String sqlSuccDisconnect() { return this.sqlSuccDisconnect; }
    public String sqlErrConnect() { return this.sqlErrConnect; }
    public String sqlErrDisconnect() { return this.sqlErrDisconnect; }
    public String sqlErrStatement() { return this.sqlErrStatement; }
    public int version() { return this.version; }
}
