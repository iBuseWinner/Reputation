package ru.fennec.free.reputation;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;
import ru.fennec.free.reputation.handlers.database.date.MySQLDatabase;
import ru.fennec.free.reputation.handlers.database.date.SQLDatabase;
import ru.fennec.free.reputation.handlers.listeners.PlayerConnectionListener;
import ru.fennec.free.reputation.handlers.listeners.ReputationCommand;
import ru.fennec.free.reputation.handlers.listeners.ReputationUpdatedListener;
import ru.fennec.free.reputation.handlers.messages.MessageManager;
import ru.fennec.free.reputation.handlers.messages.PlaceholderHook;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;
import ru.fennec.free.reputation.handlers.players.TitlesHandler;

import java.util.logging.Level;

public final class ReputationPlugin extends JavaPlugin {

    private ConfigManager<MainConfig> mainConfigManager;
    private ConfigManager<MessagesConfig> messagesConfigManager;
    private IDatabase database;
    private PlayersContainer playersContainer;
    private MessageManager messageManager;
    private PlayerConnectionListener playerConnectionListener;
    private ReputationUpdatedListener reputationUpdatedListener;
    private TitlesHandler titlesHandler;
    private PlaceholderHook placeholderHook;

    @Override
    public void onEnable() {
        loadConfigs(); //Загружаем конфигурацию плагина (config.yml, lang.yml), создаём и записываем файлы, если их нет (MainConfig, MessagesConfig)
        initializeDatabase(); //Определение типа БД из конфига, подключение, создание таблиц, если их нет
        initializeHandlers(); //Инициализация контейнера-кэша с игроками, менеджера сообщений (цвет, плейсхолеры) и хук с PlaceholderAPI
        registerListeners(); //Инициализация слушателей событий
        registerCommand(); //Регистрация команды
    }

    private void loadConfigs() {
        this.mainConfigManager = ConfigManager.create(this.getDataFolder().toPath(), "config.yml", MainConfig.class);
        this.mainConfigManager.reloadConfig(getLogger());
        this.messagesConfigManager = ConfigManager.create(this.getDataFolder().toPath(), "lang.yml", MessagesConfig.class);
        this.messagesConfigManager.reloadConfig(getLogger());
    }

    private void initializeDatabase() {
        switch (mainConfigManager.getConfigData().database().type()) {
            case MYSQL: {
                this.database = new MySQLDatabase(this.mainConfigManager);
            }
            case SQL: {
                this.database = new SQLDatabase(this.mainConfigManager);
            }
        }
        if (this.database != null) {
            this.database.initializeTables();
        } else {
            getLogger().log(Level.SEVERE, "Не удалось подключиться к базе данных! Плагин не может продолжать работу.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initializeHandlers() {
        this.playersContainer = new PlayersContainer(this, database, mainConfigManager);
        this.titlesHandler = new TitlesHandler(mainConfigManager);
        this.messageManager = new MessageManager(messagesConfigManager, titlesHandler);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderHook = new PlaceholderHook(getDescription().getVersion(), playersContainer, database, titlesHandler, this.mainConfigManager);
            this.placeholderHook.register();
        } else {
            getLogger().log(Level.WARNING, "Плагин PlaceholderAPI не обнаружен на данном сервере!");
            getLogger().log(Level.WARNING, "Плагин Reputation может нестабильно работать!");
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        this.playerConnectionListener = new PlayerConnectionListener(this, mainConfigManager, messagesConfigManager, database,
                playersContainer, messageManager);
        pluginManager.registerEvents(playerConnectionListener, this);
        if (mainConfigManager.getConfigData().commands().enable()) {
            this.reputationUpdatedListener = new ReputationUpdatedListener(mainConfigManager, database, messageManager);
            pluginManager.registerEvents(reputationUpdatedListener, this);
        }
    }

    private void registerCommand() {
        new ReputationCommand(this, messagesConfigManager, mainConfigManager, database, playersContainer, messageManager);
    }

    @Override
    public void onDisable() {
        for (IGamePlayer gamePlayer : playersContainer.getAllCachedPlayers()) {
            database.savePlayer(gamePlayer);
        }
    }

    public void updateConfigData() {
        this.playerConnectionListener.updateConfigData(this.mainConfigManager, this.messagesConfigManager);
        if (mainConfigManager.getConfigData().commands().enable()) {
            if (this.reputationUpdatedListener == null) {
                this.reputationUpdatedListener = new ReputationUpdatedListener(mainConfigManager, database, messageManager);
                Bukkit.getPluginManager().registerEvents(reputationUpdatedListener, this);
            }
            this.reputationUpdatedListener.updateConfigData(this.mainConfigManager);
        }
        this.messageManager.updateConfigData(this.messagesConfigManager);
        this.titlesHandler.updateConfigData(this.mainConfigManager);
        if (this.placeholderHook != null) {
            this.placeholderHook.updateConfigData(this.mainConfigManager);
        }
        this.playersContainer.updateConfigData(this.mainConfigManager);
    }
}
