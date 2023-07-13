package ru.fennec.free.reputation.common.configs;

import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.CommentMode;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * Используется менеджер для работы с конфигами dazzleConf
 * https://github.com/A248/DazzleConf
 */
public final class ConfigManager<C> {

    private final ConfigurationHelper<C> configHelper;
    private volatile C configData;

    private ConfigManager(ConfigurationHelper<C> configHelper) {
        this.configHelper = configHelper;
    }

    /***
     * Метод создания менеджера конфигурационного файла, и создание этого файла на диске, если его ещё нет.
     *
     * @param configFolder Папка с конфигурационным файлом
     * @param fileName Название файла (включая его расширение)
     * @param configClass Класс файла (interface)
     * @return Менеджер конфигурационного файла
     */
    public static <C> ConfigManager<C> create(Path configFolder, String fileName, Class<C> configClass) {
        SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
                .commentMode(CommentMode.alternativeWriter())
                .charset(Charset.defaultCharset())
                .build();
        ConfigurationOptions options = new ConfigurationOptions.Builder().sorter(new AnnotationBasedSorter()).build();
        ConfigurationFactory<C> configFactory = SnakeYamlConfigurationFactory.create(
                configClass,
                options,
                yamlOptions);
        return new ConfigManager<>(new ConfigurationHelper<>(configFolder, fileName, configFactory));
    }

    /***
     * Метод перезагрузки конфигурационного файла
     */
    public void reloadConfig(Logger logger) {
        try {
            configData = configHelper.reloadConfigData();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);

        } catch (ConfigFormatSyntaxException ex) {
            configData = configHelper.getFactory().loadDefaults();
            logger.log(Level.WARNING, "Вы нарушили синтаксис YAML. "
                    + "Проверьте Ваш конфиг с помощью онлайн сервиса https://yaml-online-parser.appspot.com/");
            ex.printStackTrace();

        } catch (InvalidConfigException ex) {
            configData = configHelper.getFactory().loadDefaults();
            logger.log(Level.WARNING, "Одно из значений в Вашем конфиге введено неверно! "
                    + "Проверьте, пожалуйста, что Вы всё записываете верно.");
            ex.printStackTrace();
        }
    }

    /***
     * Метод получения конфигурационного файла
     *
     * @return полученный конфигурационный файл
     */
    public C getConfigData() {
        C configData = this.configData;
        if (configData == null) {
            throw new IllegalStateException("Конфигурация ещё не загружена");
        }
        return configData;
    }

}
