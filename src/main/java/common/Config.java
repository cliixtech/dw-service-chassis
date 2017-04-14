package common;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Config {

    DB_NAME("myDB"),
    DB_USER(""),
    DB_PASSWORD("", true),
    DB_DRIVER("org.apache.derby.jdbc.EmbeddedDriver"),
    DB_ADDRESS(""),
    DB_PROTOCOL("derby"),
    LOG_LEVEL("INFO"),
    ENABLE_SWAGGER("true"),
    ENABLE_METRICS_REPORTER("false"),
    INFLUX_HOST("influx.example.com"),
    INFLUX_PORT("8086"),
    INFLUX_USER(""),
    INFLUX_PASS("", true),
    INFLUX_DB("");

    public static ConfigurationProvider PROVIDER = System::getenv;

    String defaultValue;
    boolean hide = false;

    Config(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    Config(String defaultValue, boolean sensitive) {
        this.hide = sensitive;
    }

    public String value() {
        return Optional.ofNullable(PROVIDER.getConf(this.name())).orElse(this.defaultValue);
    }

    public Boolean boolValue() {
        return Boolean.parseBoolean(this.value());
    }

    public Integer intValue() {
        return Integer.parseInt(this.value());
    }

    public static void logDump() {
        Logger log = LoggerFactory.getLogger(Config.class);
        Set<Config> hiddenConfig = stream(Config.values()).filter(x -> x.hide).collect(toSet());

        for (Config conf: Config.values()) {
            if (!hiddenConfig.contains(conf)) {
                log.info("\"{}\": {}", conf.name(), conf.value());
            } else {
                log.info("\"{}\": <hidden>", conf.name());
            }
        }
    }
}
