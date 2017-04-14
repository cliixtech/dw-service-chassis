package common.rest;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cliix.abacus.Abacus;

public class AbacusMetricsFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AbacusMetricsFilter.class);
    private Abacus abacus;

    public AbacusMetricsFilter(Abacus metrics) {
        this.abacus = metrics;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.abacus.start(30, TimeUnit.SECONDS);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // execute request
        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long duration = System.currentTimeMillis() - start;

        // gather data for metrics
        HashMap<String, String> attr = new HashMap<>();
        try {
            HttpServletRequest httpReq = (HttpServletRequest) request;
            attr.put("path_info", httpReq.getPathInfo());
            int reqLength = parseInt(Optional.ofNullable(httpReq.getHeader("Content-Length")).orElse("0"));
            HttpServletResponse httpResp = (HttpServletResponse) response;
            int respLength = parseInt(Optional.ofNullable(httpResp.getHeader("Content-Length")).orElse("0"));
            String statusCode = valueOf(httpResp.getStatus());

            @SuppressWarnings("unchecked")
            Map<String, String> clonedMap = (HashMap<String, String>) attr.clone();
            this.abacus.addMeasurement("request.length", reqLength, clonedMap);
            this.abacus.addMeasurement("response.length", respLength, clonedMap);
            attr.put("status_code", statusCode);
            LOG.info("status {} ; took {} ms.", statusCode, duration);
        } catch (ClassCastException e) {
            // not a http request/response - ok no extra metrics and tags
        }

        this.abacus.addMeasurement("request.duration.ms", duration);
        this.abacus.addMeasurement("request.count", 1, attr);
    }

    @Override
    public void destroy() {
        this.abacus.stop();
    }
}
