package common.persistence;

import static java.lang.String.format;

import common.Config;

public class DBConfig {
    static final String URL_FORMAT = "jdbc:%s:%s";
    private static final String DERBY_PROTOCOL = "derby";
    private String dbDriver;
    private String dbName;
    private String dbPassword;
    private String dbUser;
    private String dbAddress;
    private String dbProtocol;

    public DBConfig() {
        this.dbName = Config.DB_NAME.value();
        this.dbDriver = Config.DB_DRIVER.value();
        this.dbUser = Config.DB_USER.value();
        this.dbPassword = Config.DB_PASSWORD.value();
        this.dbProtocol = Config.DB_PROTOCOL.value();
        this.dbAddress = Config.DB_ADDRESS.value();
    }

    public String getUser() {
        return this.dbUser;
    }

    public String getPassword() {
        return this.dbPassword;
    }

    public String getDriver() {
        return this.dbDriver;
    }

    public String getConnectionURL() {
        String url = format(URL_FORMAT, this.dbProtocol, this.buildFullAddress());
        if (this.dbProtocol.equals(DERBY_PROTOCOL)) {
            url = format("%s;create=true", url);
        }
        return url;
    }

    private String buildFullAddress() {
        String fullAddress;
        if (this.dbAddress.isEmpty()) {
            fullAddress = this.dbName;
        } else {
            fullAddress = format("//%s/%s", this.dbAddress, this.dbName);
        }
        return fullAddress;
    }
}
