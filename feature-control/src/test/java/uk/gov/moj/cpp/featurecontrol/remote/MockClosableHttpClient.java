package uk.gov.moj.cpp.featurecontrol.remote;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class MockClosableHttpClient extends CloseableHttpClient  {

    @Override
    protected CloseableHttpResponse doExecute(
            final HttpHost httpHost,
            final HttpRequest httpRequest,
            final HttpContext httpContext) {
        throw new UnsupportedOperationException("Mock class for testing. Please don't use");
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Mock class for testing. Please don't use");
    }

    @Override
    public HttpParams getParams() {
        throw new UnsupportedOperationException("Mock class for testing. Please don't use");
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        throw new UnsupportedOperationException("Mock class for testing. Please don't use");
    }
}
