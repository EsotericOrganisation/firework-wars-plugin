package net.slqmy.template_paper_plugin.util;

import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.IOException;
import java.net.URL;

public class FileUtil {
  public static List<String> getResourceFolderResourceFileNames(String resourcePath) {
    ClassLoader classLoader = FileUtil.class.getClassLoader();

    try {
      URL jarURL = classLoader.getResource(resourcePath);
      if (jarURL == null) {
        return Collections.emptyList();
      }

      String jarPath = jarURL.getPath();
      int exclamationMarkIndex = jarPath.indexOf("!");

      String jarPathPrefix = "file:";
      String jarFilePath = jarPath.substring(jarPathPrefix.length(), exclamationMarkIndex);

      try (JarFile jarFile = new JarFile(jarFilePath)) {
        return jarFile.stream().map(JarEntry::getName).filter(name -> name.startsWith(resourcePath) && !name.equals(resourcePath)).map(name -> name.substring(resourcePath.length())).collect(Collectors.toList());
      }
    } catch (IOException exception) {
      exception.printStackTrace();
      return Collections.emptyList();
    }
  }
}
