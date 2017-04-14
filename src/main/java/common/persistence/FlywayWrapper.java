package common.persistence;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlywayWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(FlywayWrapper.class);
    private Flyway flyway;

    public FlywayWrapper() {
        DBConfig dbConfig = new DBConfig();
        this.flyway = new Flyway();
        this.flyway.setDataSource(dbConfig.getConnectionURL(), dbConfig.getUser(), dbConfig.getPassword());
        this.flyway.setLocations("classpath:db.migrations");
        LOG.info("Flyway initialized");
    }

    public void runMigrations() {
        this.flyway.migrate();
        this.flyway.validate();
    }

    public void logDBInfo() {
        MigrationInfoService infoService = this.flyway.info();
        MigrationInfo[] applied = infoService.applied();
        LOG.info("Applied Migrations: {}", applied.length);
        for (MigrationInfo info: applied) {
            LOG.info("{}", info.getDescription());
        }
        MigrationInfo[] pending = infoService.pending();
        LOG.info("Pending Migrations: {}", pending.length);
        for (MigrationInfo info: pending) {
            LOG.info("{}", info.getDescription());
        }
    }
}
