package common.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import common.persistence.DatabaseHealthCheck;

public class DatabaseHealthCheckTest {

    @Test
    public void noAddress_dbUnhealth() throws Exception {
        DatabaseHealthCheck hc = new DatabaseHealthCheck("");
        assertThat(hc.check().isHealthy()).isFalse();
    }

    @Test
    public void noPort_dbUnhealth() throws Exception {
        DatabaseHealthCheck hc = new DatabaseHealthCheck("localhost");
        assertThat(hc.check().isHealthy()).isFalse();
    }

    @Test
    public void noValidPort_dbUnhealth() throws Exception {
        DatabaseHealthCheck hc = new DatabaseHealthCheck("localhost:");
        assertThat(hc.check().isHealthy()).isFalse();
        hc = new DatabaseHealthCheck("localhost:lala");
        assertThat(hc.check().isHealthy()).isFalse();
    }

    @Test
    public void validPort_dbUnhealthy() throws Exception {
        DatabaseHealthCheck hc = new DatabaseHealthCheck("localhost:6665");
        assertThat(hc.check().isHealthy()).isFalse();
    }

    @Test
    public void validPort_dbHealthy() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final DatabaseHealthCheck hc = new DatabaseHealthCheck("localhost:6666");
        Thread t = new Thread(() -> {

            try (ServerSocket ss = new ServerSocket()) {
                ss.bind(hc.parseAddress().get());
                latch.countDown();
                ss.setSoTimeout(500);
                ss.accept().close();
            } catch (Exception e) {
                fail();
            }
        });

        t.start();
        latch.await();
        assertThat(hc.check().isHealthy()).isTrue();
    }
}
