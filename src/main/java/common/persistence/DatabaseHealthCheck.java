package common.persistence;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;

import com.codahale.metrics.health.HealthCheck;

public class DatabaseHealthCheck extends HealthCheck {

    private static final int TIMEOUT_MS = 2000;
    private final String dbAddress;

    public DatabaseHealthCheck(String dbAddress) {
        this.dbAddress = dbAddress;
    }

    @Override
    protected Result check() throws Exception {
        Result result;
        final Optional<InetSocketAddress> socketAddress = this.parseAddress();
        if (socketAddress.isPresent()) {
            try (Socket db = new Socket()) {
                db.connect(socketAddress.get(), TIMEOUT_MS);
                result = Result.healthy("DB is alive!");
            } catch (final Exception e) {
                result = Result.unhealthy(e);
            }
        } else {
            result = Result.unhealthy("Invalid address, unable to check DB health status");
        }

        return result;
    }

    protected Optional<InetSocketAddress> parseAddress() {
        // We don't store the parsed address so java resolves the address each
        // time
        if (!this.dbAddress.isEmpty()) {
            final String[] parts = this.dbAddress.split(":");
            if (parts.length == 2) {
                try {
                    return Optional.of(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
                } catch (final NumberFormatException ex) {
                }
            }
        }

        return Optional.empty();
    }
}
