package uk.gov.moj.cpp.platform.test.data.utils;

import uk.gov.moj.cpp.platform.test.exception.TestUtilException;

import java.io.IOException;

import org.apache.commons.io.IOUtils;


public class FileUtil {
    public static String fileToString(final String path) {
        try {
            return IOUtils.toString(FileUtil.class.getResourceAsStream(path), "UTF-8").replaceAll("\n", "");
        } catch (final IOException iox) {
            throw new TestUtilException("Error during loading a test data file file", iox);
        }
    }
}
