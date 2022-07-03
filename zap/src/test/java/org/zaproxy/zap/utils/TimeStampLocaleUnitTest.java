/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2018 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;
import org.parosproxy.paros.Constant;
import org.zaproxy.zap.testutils.TestUtils;

/**
 * Unit test to verify that translated time stamps (in {@code Messages.properties} files) are valid
 * patterns for {@code SimpleDateFormat}.
 *
 * @see TimeStampUtils
 */
class TimeStampLocaleUnitTest extends TestUtils {

    private static final String KEY_DATE_TIME_PATTERN = "timestamp.format.datetime";
    private static final String KEY_TIME_ONLY_PATTERN = "timestamp.format.timeonly";

    private static final Path DIRECTORY =
            getResourcePath("/" + Constant.LANG_DIR, TimeStampLocaleUnitTest.class);
    private static final String BASE_NAME = Constant.MESSAGES_PREFIX;
    private static final String FILE_NAME = BASE_NAME + "_";
    private static final String FILE_EXTENSION = ".properties";

    @Test
    void shouldHaveValidPatternsForAllMessagesFiles() throws Exception {
        try (URLClassLoader classLoader =
                new URLClassLoader(new URL[] {DIRECTORY.toUri().toURL()})) {
            List<String> errors = new ArrayList<>();
            for (Path file : getMessagesFiles()) {
                String fileName = file.getFileName().toString();
                String[] localeParts =
                        fileName.substring(FILE_NAME.length(), fileName.indexOf(FILE_EXTENSION))
                                .split("_");

                if (localeParts.length > 1) {
                    Locale locale = new Locale(localeParts[0], localeParts[1]);
                    ResourceBundle rb =
                            ResourceBundle.getBundle(
                                    BASE_NAME, locale, classLoader, new ZapResourceBundleControl());
                    verifyPattern(fileName, rb, KEY_DATE_TIME_PATTERN, errors);
                    verifyPattern(fileName, rb, KEY_TIME_ONLY_PATTERN, errors);
                } else {
                    errors.add("File with unhandled locale: " + fileName);
                }
            }
            assertThat(errors.toString(), errors, hasSize(0));
        }
    }

    private static void verifyPattern(
            String fileName, ResourceBundle rb, String keyPattern, List<String> errors) {
        if (!rb.containsKey(keyPattern)) {
            return;
        }

        String pattern = rb.getString(keyPattern);
        try {
            new SimpleDateFormat(rb.getString(keyPattern));
        } catch (Exception e) {
            errors.add(buildMessage(fileName, pattern, keyPattern, e));
        }
    }

    private static String buildMessage(
            String fileName, String pattern, String keyPattern, Exception exception) {
        return fileName
                + " uses invalid pattern '"
                + pattern
                + "' for key '"
                + keyPattern
                + "', error: "
                + exception
                + "\n";
    }

    private static List<Path> getMessagesFiles() {
        final List<Path> files = new ArrayList<>();
        try {
            Files.walkFileTree(
                    DIRECTORY,
                    Collections.<FileVisitOption>emptySet(),
                    1,
                    new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException {
                            String fileName = file.getFileName().toString();
                            if (fileName.startsWith(FILE_NAME)
                                    && fileName.endsWith(FILE_EXTENSION)) {
                                files.add(file);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(
                    "An error occurred while walking directory: " + DIRECTORY, e);
        }
        return files;
    }
}
