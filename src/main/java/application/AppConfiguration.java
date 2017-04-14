package application;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ch.qos.logback.classic.Level;

import com.fasterxml.jackson.annotation.JsonProperty;
import common.Config;

import io.cliix.abacus.Abacus;
import io.cliix.abacus.Publisher;
import io.cliix.abacus.internal.InfluxDBPublisher;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.logging.DefaultLoggingFactory;
import io.dropwizard.logging.LoggingFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class AppConfiguration extends Configuration {
    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @Valid
    @NotNull
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty("logging")
    public LoggingFactory getLoggingFactory() {
        DefaultLoggingFactory log = (DefaultLoggingFactory) super.getLoggingFactory();
        log.setLevel(Level.valueOf(Config.LOG_LEVEL.value()));
        return log;
    }

    public Abacus getAbacus() throws IOException {
        File cache = new File("/tmp/metrics.cache");
        Map<String, String> tags = new HashMap<>();
        tags.put("application", this.getClass().getCanonicalName());
        tags.put("uuid", UUID.randomUUID().toString());
        String url = format("http://%s:%s/", Config.INFLUX_HOST.value(), Config.INFLUX_PORT.intValue());
        Publisher influx =
                new InfluxDBPublisher(
                        url,
                        Config.INFLUX_USER.value(),
                        Config.INFLUX_PASS.value(),
                        Config.INFLUX_DB.value());
        return new Abacus.Builder().cacheMaxEntries(5000).cacheFile(cache).tags(tags).withPublisher(influx).build();
    }
}
