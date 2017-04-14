package application;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import rest.resources.HelloResource;

import common.Config;
import common.LogContextFilter;
import common.persistence.DatabaseHealthCheck;
import common.persistence.FlywayWrapper;
import common.persistence.PersistenceManagerFilter;
import common.rest.AbacusMetricsFilter;
import common.rest.StatusCodeFilter;

import io.cliix.abacus.Abacus;
import io.dropwizard.Application;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class App extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        if (Config.ENABLE_SWAGGER.boolValue()) {
            bootstrap.addBundle(new SwaggerBundle<AppConfiguration>() {
                @Override
                protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AppConfiguration configuration) {
                    return configuration.swaggerBundleConfiguration;
                }
            });
        }
    }

    @Override
    public void run(AppConfiguration cfg, Environment env) throws Exception {
        Config.logDump();
        this.setupMigrations();
        Abacus metrics = cfg.getAbacus();
        this.registerFilters(env);
        this.registerCORS(env);
        this.registerHealthChecks(env);
        this.registerMetricsReporter(env, metrics);

        this.registerResources(env);
    }

    private void registerResources(Environment env) {
        JerseyEnvironment jersey = env.jersey();
        jersey.register(new HelloResource());
    }

    private void registerHealthChecks(Environment env) {
        env.healthChecks().register("database", new DatabaseHealthCheck(Config.DB_ADDRESS.value()));
    }

    private void setupMigrations() {
        FlywayWrapper flyway = new FlywayWrapper();
        flyway.logDBInfo();
        flyway.runMigrations();
    }

    private void registerFilters(Environment env) {
        JerseyEnvironment jersey = env.jersey();
        jersey.register(LogContextFilter.class);
        jersey.register(PersistenceManagerFilter.class);
        jersey.register(AppExceptionMapper.class);
        jersey.register(StatusCodeFilter.class);
    }

    private void registerCORS(Environment env) {
        final FilterRegistration.Dynamic cors = env.servlets().addFilter("CORS", CrossOriginFilter.class);
        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

    private void registerMetricsReporter(Environment env, Abacus metrics) {
        if (Config.ENABLE_METRICS_REPORTER.boolValue()) {
            env.servlets().addFilter("AbacusMetricsFilter", new AbacusMetricsFilter(metrics)).addMappingForUrlPatterns(
                    EnumSet.of(DispatcherType.REQUEST),
                    true,
                    "/*");
        }
    }
}
