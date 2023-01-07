package ru.fennec.free.reputation;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fennec.free.reputation.common.configs.ConfigManager;
import ru.fennec.free.reputation.common.interfaces.IDatabase;
import ru.fennec.free.reputation.handlers.database.configs.MainConfig;
import ru.fennec.free.reputation.handlers.database.configs.MessagesConfig;
import ru.fennec.free.reputation.handlers.database.date.MySQLDatabase;
import ru.fennec.free.reputation.handlers.database.date.SQLDatabase;
import ru.fennec.free.reputation.handlers.listeners.PlayerConnectionListener;
import ru.fennec.free.reputation.handlers.listeners.ReputationCommand;
import ru.fennec.free.reputation.handlers.messages.MessageManager;
import ru.fennec.free.reputation.handlers.messages.PlaceholderHook;
import ru.fennec.free.reputation.handlers.players.PlayersContainer;

public final class ReputationPlugin extends JavaPlugin {

    private ConfigManager<MainConfig> mainConfigManager;
    private ConfigManager<MessagesConfig> messagesConfigManager;
    private IDatabase database;
    private PlayersContainer playersContainer;
    private MessageManager messageManager;
    private PlayerConnectionListener playerConnectionListener;

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
        this.mainConfigManager.reloadConfig();
        this.messagesConfigManager = ConfigManager.create(this.getDataFolder().toPath(), "lang.yml", MessagesConfig.class);
        this.messagesConfigManager.reloadConfig();
    }

    private void initializeDatabase() {
        switch (mainConfigManager.getConfigData().database().type()) {
            case MYSQL -> this.database = new MySQLDatabase(this.mainConfigManager);
            case SQL -> this.database = new SQLDatabase(this.mainConfigManager);
        }
        if (this.database != null) {
            this.database.initializeTables();
        }
    }

    private void initializeHandlers() {
        this.playersContainer = new PlayersContainer();
        this.messageManager = new MessageManager(messagesConfigManager);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderHook(getDescription().getVersion(), playersContainer, database).register();
        }
    }

    private void registerListeners() {
        this.playerConnectionListener = new PlayerConnectionListener(mainConfigManager, messagesConfigManager, database,
                playersContainer, messageManager);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(playerConnectionListener, this);
    }

    private void registerCommand() {
        new ReputationCommand(this, messagesConfigManager, mainConfigManager, database, playersContainer, messageManager);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void updateConfigData(ConfigManager<MainConfig> mainConfigManager, ConfigManager<MessagesConfig> messagesConfigManager) {
        this.playerConnectionListener.updateConfigData(mainConfigManager, messagesConfigManager);
        this.messageManager.updateConfigData(messagesConfigManager);
    }
}
