package uk.gov.moj.cpp.platform.data.utils.testutils;

import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.justice.services.common.http.HeaderConstants.USER_ID;

import uk.gov.justice.services.test.utils.core.rest.RestClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;

public class ZipTestUtil {

    private ZipTestUtil() {
    }

    /**
     * Transforms the binary zip file into a hash map. The key being the name of the file in the
     * zip, the value being the content of the text file
     *
     * @param zipByteData byte[] from a zip file
     */
    public static Map<String, String> unzip(final byte[] zipByteData) throws IOException {
        final Map<String, String> result = new HashMap<>();

        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipByteData);
             final ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                final String filePath = entry.getName();
                result.put(filePath, extractFile(zipInputStream));
                zipInputStream.closeEntry();
            }
        }

        return result;
    }


    /**
     * Retrieve a remote Zip file content
     *
     * @param fullUrl full URL of the zip endpoint
     * @param userId  user to use to access the resource
     * @return a map containing zip content file name as key and value being the embedded file
     * content
     * @throws IOException when things go wrong
     */
    public static Map<String, String> getZipContent(final String fullUrl, final UUID userId) throws IOException {

        final MultivaluedMap<String, Object> userHeader = new MultivaluedHashMap<>();
        userHeader.add(USER_ID, userId.toString());
        Response resp = null;
        try {
            resp = new RestClient().query(fullUrl, MediaType.ZIP.toString(), userHeader);

            assertThat(resp.getStatusInfo(), is(OK));

            final byte[] content = resp.readEntity(byte[].class);


            return unzip(content);
        } finally {
            if (resp != null) {
                resp.close();
            }
        }
    }

    private static String extractFile(final ZipInputStream zipIn) {
        final StringBuilder stringBuilder = new StringBuilder();
        final Scanner scanner = new Scanner(zipIn);
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine()).append('\n');
        }
        return stringBuilder.toString();
    }
}
