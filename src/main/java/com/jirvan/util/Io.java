/*

Copyright (c) 2012,2013 Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.util;

import com.jirvan.lang.FileNotFoundRuntimeException;
import com.jirvan.lang.NotFoundRuntimeException;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static com.jirvan.util.Assertions.*;

public class Io {

    public static File ensureDirectoryExists(File directory) {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new RuntimeException(String.format("\"%s\" exists but is not a directory", directory.getAbsolutePath()));
            } else {
                return directory;
            }
        } else {
            if (directory.mkdirs()) {
                return directory;
            } else {
                throw new RuntimeException(String.format("Error creating directory \"%s\" (some parent directories may have been successfully created", directory.getAbsolutePath()));
            }
        }
    }

    public static String getResourcePropertyValue(Class anchorClass, String propertiesFileRelativePath, String key) {
        Properties properties = getProperties(anchorClass, propertiesFileRelativePath);
        String version = properties.getProperty(key);
        assertNotNull(version, String.format("%s not found in %s", key, propertiesFileRelativePath));
        return version;
    }

    public static Properties getProperties(Class anchorClass, String propertiesFileRelativePath) {
        try {
            InputStream inputStream = anchorClass.getResourceAsStream(propertiesFileRelativePath);
            if (inputStream == null) {
                throw new NotFoundRuntimeException("Couldn't find resource \"" + propertiesFileRelativePath + "\" associated with class \"" + anchorClass.getName() + "\"");
            }
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                return properties;
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getFileBytes(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            try {
                return IOUtils.toByteArray(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String readStreamIntoString(InputStream inputStream) {
        try {
            StringWriter stringWriter = new StringWriter();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                int character;
                while ((character = bufferedReader.read()) != -1) {
                    stringWriter.write(character);
                }
            } finally {
                bufferedReader.close();
            }
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExtendedProperties getHomeDirectoryConfigFileProperties(String filename) {
        return new ExtendedProperties(getHomeDirectoryFile(filename));
    }

    public static String getHomeDirectoryConfigFileProperty(String filename, String property) {
        ExtendedProperties properties = new ExtendedProperties(getHomeDirectoryFile(filename));
        return properties.getMandatoryProperty(property);
    }

    public static File getHomeDirectoryFile(String filename) {
        File homedir = getHomeDirectory();
        File configFile = new File(homedir, filename);
        if (!configFile.exists()) {
            throw new FileNotFoundRuntimeException("File \"" + configFile.getAbsolutePath() + "\" does not exist");
        }
        return configFile;
    }

    public static File getHomeDirectory() {
        String usersHomePath = System.getProperty("user.home");
        if (usersHomePath == null) {
            throw new RuntimeException("Couldn't get user.home system property");
        }
        File homedir = new File(usersHomePath);
        if (!homedir.exists()) {
            throw new FileNotFoundRuntimeException("Couldn't find home directory for user (" + homedir.getAbsolutePath() + " does not exist)");
        }
        return homedir;
    }

    public static byte[] getResourceFileBytes(Class anchorClass, String filename) {
        try {
            InputStream inputStream = anchorClass.getResourceAsStream(filename);
            if (inputStream == null) {
                throw new NotFoundRuntimeException("Couldn't find resource \"" + filename + "\" associated with class \"" + anchorClass.getName() + "\"");
            }
            try {
                return IOUtils.toByteArray(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getResourceFileString(Class anchorClass, String filename) {
        try {
            InputStream inputStream = anchorClass.getResourceAsStream(filename);
            if (inputStream == null) {
                throw new NotFoundRuntimeException("Couldn't find resource \"" + filename + "\" associated with class \"" + anchorClass.getName() + "\"");
            }
            try {
                return readStreamIntoString(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileString(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            try {
                return readStreamIntoString(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHomeDirectoryFileString(String filename) {
        return getFileString(getHomeDirectoryFile(filename));
    }

    public static void toHomeDirectoryFile(String string, String filename) {
        toFile(string, new File(getHomeDirectory(), filename), false);
    }

    public static void toHomeDirectoryFile(String string, String filename, boolean overwriteExistingFileIfAny) {
        toFile(string, new File(getHomeDirectory(), filename), overwriteExistingFileIfAny);
    }

    public static void toFile(String string, String pathname) {
        toFile(string, new File(pathname), false);
    }

    public static void toFile(String string, File file) {
        toFile(string, file, false);
    }

    public static void toFile(String string, File file, boolean overwriteExistingFileIfAny) {
        assertIsDirectory(file.getParentFile());
        if (!overwriteExistingFileIfAny) {
            assertFileDoesNotExist(file);
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFiles(File tablesDir, final String filenameRegexpPattern) {
        File[] files = tablesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().matches(filenameRegexpPattern);
            }
        });
        if (files != null) {
            for (File child : files) {
                child.delete();
                //System.out.printf("Would have deleted %s\n", child.getPath());
            }
        }
    }

    public static void copyFileSorted(File inputFile, File outputFile) {

        assertFileExists(inputFile);
        assertFileDoesNotExist(outputFile);

        try {
            List<String> list = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                Collections.sort(list);

            }

            try (FileWriter writer = new FileWriter(outputFile)) {
                for (String line : list) {
                    writer.write(line);
                    writer.write('\n');
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void copyFileSortedAndFiltered(File inputFile, File outputFile, LineFilter... lineFilters) {

        assertFileExists(inputFile);
        assertFileDoesNotExist(outputFile);

        try {
            List<String> list = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                Collections.sort(list);

            }

            try (FileWriter writer = new FileWriter(outputFile)) {
                for (String line : list) {
                    boolean linePasses = true;
                    for (LineFilter lineFilter : lineFilters) {
                        if (lineFilter.linePasses(line)) {
                            line = lineFilter.transformedLine(line);
                        } else {
                            linePasses = false;
                            break;
                        }
                    }
                    if (linePasses) {
                        writer.write(line);
                        writer.write('\n');
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public interface LineFilter {

        public boolean linePasses(String line);

        public String transformedLine(String line);

    }

}
