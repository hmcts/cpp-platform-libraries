package uk.gov.moj.cpp.platform.data.utils.service;

import static java.lang.String.format;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipCreator.class);

    private static final String TARGET_TEMP_FILE_NAME_PREFIX =format("cpp-%s-",ZipCreator.class.getSimpleName());

    /**This will not handle large files as all in done in memory
     * @param data Map containing zip entries. File name as key, the value being the content of the
     *             zip entry
     * @return zip file as binary
     */
    public byte[] zip(final Map<String, String> data) throws IOException {
        LOGGER.debug("Creating zip for '{}'", data.keySet());
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (final ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            for (final Map.Entry<String, String> entity : data.entrySet()) {
                addZipEntry(zipOutputStream, entity.getKey(), entity.getValue());
            }
        }

        //No need to close a biteArrayOutputStream. See:
        //https://docs.oracle.com/javase/10/docs/api/java/io/ByteArrayOutputStream.html#close()
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Package a set of files into a zip file
     *
     * @param targetNamesOfFilesToZips a map containing the name you want the file to have in the
     *                                 zip and a reference to the file
     * @param targetZipFile            The target zip file
     * @throws IOException if anything goes wrong
     */
    public void zipFiles(Map<String, File> targetNamesOfFilesToZips, final File targetZipFile) throws IOException {
        try (final FileOutputStream fileOutputStream = new FileOutputStream(targetZipFile);
             final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             final ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {

            for (final Map.Entry<String, File> srcFile : targetNamesOfFilesToZips.entrySet()) {
                try (final FileInputStream fis = new FileInputStream(srcFile.getValue())
                ) {
                    ZipEntry zipEntry = new ZipEntry(srcFile.getKey());
                    zipOutputStream.putNextEntry(zipEntry);

                    IOUtils.copy(fis, zipOutputStream);
                }
            }
        }
    }

    /**
     * Package a set of files into a zip file into the system temp directory
     *
     * @param targetNamesOfFilesToZips a map containing the name you want the file to have in the
     *                                 zip and a reference to the file
     * @return the path to the newly created zip file in the system temp directory
     * @throws IOException if anything goes wrong
     */
    public Path zipFilesToTemp(Map<String, File> targetNamesOfFilesToZips) throws IOException {
        final Path finalZipPath = Files.createTempFile(TARGET_TEMP_FILE_NAME_PREFIX, ".zip");
        zipFiles(targetNamesOfFilesToZips, finalZipPath.toFile());
        return finalZipPath;
    }

    private void addZipEntry(final ZipOutputStream zipOutputStream, final String fileName, final String fileContent) throws IOException {
        try {
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            zipOutputStream.write(fileContent.getBytes());
        } finally {
            try {
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                LOGGER.warn("Failed to close Zip Entry ", e);
            }
        }
    }
}
