package uk.gov.moj.cpp.platform.data.utils.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ZipCreatorTest {

    private static final String FILE_NAME_01 = "file-01.csv";
    private static final String FILE_NAME_02 = "file-02.csv";
    private static final String FILE_CONTENT_01 = "file 01 content";
    private static final String FILE_CONTENT_02 = "file 02 content";
    private static final int BUFFER_SIZE = FILE_CONTENT_01.length();

    private ZipCreator zipCreator;

    @BeforeEach
    public void setUp() {
        zipCreator = new ZipCreator();
    }

    @Test
    public void testZip() throws Exception {
        final Map<String, String> content = new HashMap<>();
        content.put(FILE_NAME_01, FILE_CONTENT_01);
        content.put(FILE_NAME_02, FILE_CONTENT_02);

        // Zip multiple files and return the zip file as bite[]
        final byte[] zipFileAsBites = zipCreator.zip(content);
        assertNotNull(zipFileAsBites);

        final Map<String, String> unzippedData = unzip(zipFileAsBites);
        assertThat(unzippedData.keySet(), hasSize(2));
        assertThat(unzippedData.get(FILE_NAME_01), is(FILE_CONTENT_01));
        assertThat(unzippedData.get(FILE_NAME_02), is(FILE_CONTENT_02));
    }


    @Test
    public void shouldZipFilesToTemp() throws IOException {
        final File file01 = File.createTempFile(FILE_NAME_01, ".txt");
        final File file02 = File.createTempFile(FILE_NAME_02, ".txt");

        FileUtils.writeStringToFile(file01, FILE_CONTENT_01);
        FileUtils.writeStringToFile(file02, FILE_CONTENT_02);

        final Map<String, File> map = ImmutableMap.of(FILE_NAME_01, file01, FILE_NAME_02, file02);
        final Path finalFile = zipCreator.zipFilesToTemp(map);

        assertThat(finalFile, CoreMatchers.is(CoreMatchers.notNullValue()));
        final byte[] zipFileAsBites = FileUtils.readFileToByteArray(finalFile.toFile());

        final Map<String, String> unzippedData = unzip(zipFileAsBites);
        assertThat(unzippedData.keySet(), hasSize(2));
        assertThat(unzippedData.get(FILE_NAME_01), is(FILE_CONTENT_01));
        assertThat(unzippedData.get(FILE_NAME_02), is(FILE_CONTENT_02));
    }

    public Map<String, String> unzip(byte[] zipByteData) throws IOException {
        final Map<String, String> result = new HashMap<>();

        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipByteData);
             final ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String filePath = entry.getName();
                result.put(filePath, extractFile(zipInputStream));
                zipInputStream.closeEntry();
            }
        }

        return result;
    }

    private String extractFile(ZipInputStream zipIn) throws IOException {
        final StringWriter bos = new StringWriter();
        final byte[] bytesIn = new byte[BUFFER_SIZE];
        while ((zipIn.read(bytesIn)) != -1) {
            bos.append(new String(bytesIn));
        }
        bos.close();

        return bos.toString();
    }

}