package uk.gov.moj.cpp.featurecontrol.remote;

import static java.lang.String.format;

import uk.gov.justice.services.common.util.Clock;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;

public class AzureSignedRequest {
    private static final String X_MS_DATE = "x-ms-date";

    public static final String X_MS_DATE_HOST_X_MS_CONTENT_SHA_256 = "x-ms-date;host;x-ms-content-sha256";
    public static final String SIGNED_REQUEST_PATTERN = "%s\n%s\n%s;%s;%s";
    public static final String X_MS_CONTENT_SHA_256 = "x-ms-content-sha256";
    public static final String AUTHORIZATION = "Authorization";
    public static final String HMAC_SHA_256_CREDENTIAL_S_SIGNED_HEADERS_S_SIGNATURE_S = "HMAC-SHA256 Credential=%s, SignedHeaders=%s, Signature=%s";
    public static final String HMAC_SHA_256 = "HmacSHA256";
    public static final String CONTENT = "";

    @Inject
    private Clock clock;


    public HttpUriRequest createSignedRequest(final String url, final String credential, final String secret) {
        HttpUriRequest request = new HttpGet(url);
        Map<String, String> authHeaders = null;
        authHeaders = buildHeaders(request, credential, secret);
        authHeaders.forEach(request::setHeader);
        return request;
    }

    private Map<String, String> buildHeaders(final HttpUriRequest request, final String credential, final String secret) {

        try {
            String requestTime = clock.now().toLocalDateTime().toString();
            String contentHash = buildContentHash(CONTENT);
            String signature = signContent(request, secret, requestTime, contentHash);
            Map<String, String> headers = new HashMap<>();
            headers.put(X_MS_DATE, requestTime);
            headers.put(X_MS_CONTENT_SHA_256, contentHash);
            String authorization = format(HMAC_SHA_256_CREDENTIAL_S_SIGNED_HEADERS_S_SIGNATURE_S,
                    credential, X_MS_DATE_HOST_X_MS_CONTENT_SHA_256, signature);
            headers.put(AUTHORIZATION, authorization);
            return headers;

        } catch (URISyntaxException | NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new SignedRequestException("Couldn't signed request for azure.", ex);
        }

    }

    private String signContent(HttpUriRequest request, String secret, String requestTime, final String contentHash) throws NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {
        String methodName = request.getRequestLine().getMethod().toUpperCase();
        URIBuilder uri = new URIBuilder(request.getRequestLine().getUri());
        String scheme = uri.getScheme() + "://";
        String requestPath = uri.toString().substring(scheme.length()).substring(uri.getHost().length());
        String host = new URIBuilder(request.getRequestLine().getUri()).getHost();
        String toSign = format(SIGNED_REQUEST_PATTERN, methodName, requestPath, requestTime, host, contentHash);
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        Mac sha256HMAC = Mac.getInstance(HMAC_SHA_256);
        SecretKeySpec secretKey = new SecretKeySpec(decodedKey, HMAC_SHA_256);
        sha256HMAC.init(secretKey);
        return Base64.getEncoder().encodeToString(sha256HMAC.doFinal(toSign.getBytes(StandardCharsets.UTF_8)));
    }

    private String buildContentHash(String content) {
        byte[] digest = DigestUtils.sha256(content);
        return Base64.getEncoder().encodeToString(digest);
    }
}
