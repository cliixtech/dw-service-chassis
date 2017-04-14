package common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.Config;

public class ConfigTest {

    @Before
    public void setProvider() {
        Config.PROVIDER = System::getProperty;
    }

    @After
    public void resetProperties() {
        for (Config c: Config.values()) {
            System.clearProperty(c.name());
        }
        Config.PROVIDER = System::getenv;
    }

    @Test
    public void value_returnsDefaultValue() {
        resetProperties();
        assertThat(Config.DB_NAME.value()).isEqualTo(Config.DB_NAME.defaultValue);
    }

    @Test
    public void setsSystemProperty_value_returnsSystemsValue() {
        String value = "some value";
        System.setProperty(Config.DB_NAME.name(), value);
        assertThat(Config.DB_NAME.value()).isEqualTo(value);
    }

    @Test
    public void setsSystemProperty_intValue_returnsInteger() {
        String value = "1";
        System.setProperty(Config.DB_NAME.name(), value);
        assertThat(Config.DB_NAME.intValue()).isEqualTo(1);
    }

    @Test
    public void setsSystemProperty_boolValue_returnsBoolean() {
        String value = "True";
        System.setProperty(Config.DB_NAME.name(), value);
        assertThat(Config.DB_NAME.boolValue()).isTrue();
    }
}
