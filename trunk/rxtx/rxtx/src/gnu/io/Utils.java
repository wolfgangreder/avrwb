/*
 * $Id$
 *
 * Author: Wolfgang Reder (w.reder@mountain-sd.at)
 *
 * Copyright (c) 2006-2015 Mountain Software Design KG
 *
 */
package gnu.io;

import java.io.File;

public final class Utils
{

  private static String searchDir(String mapped,
                                  String dir)
  {
    File tmp = new File(dir + File.separator + mapped);
    if (tmp.canRead()) {
      return tmp.getAbsolutePath();
    }
    return null;
  }

  private static String searchPath(String mapped,
                                   String osName,
                                   String osArch,
                                   String path,
                                   String pathPrefix)
  {
    String result = null;
    if (path != null && path.trim().length() > 0) {
      String[] splitted = path.split(File.pathSeparator);
      for (String p : splitted) {
        String toTest;
        if (p.endsWith(File.separator)) {
          toTest = p + pathPrefix + osArch + File.separator + osName + File.separator;
        } else {
          toTest = p + File.separator + pathPrefix + osArch + File.separator + osName + File.separator;
        }
        if ((result = searchDir(mapped,
                                toTest)) != null) {
          return result;
        }
        if (p.endsWith(File.separator)) {
          toTest = p + pathPrefix + osArch + File.separator;
        } else {
          toTest = p + File.separator + pathPrefix + osArch + File.separator;
        }
        if ((result = searchDir(mapped,
                                toTest)) != null) {
          return result;
        }
        if (p.endsWith(File.separator)) {
          toTest = p + pathPrefix;
        } else {
          toTest = p + File.separator + pathPrefix;
        }
        if ((result = searchDir(mapped,
                                toTest)) != null) {
          return result;
        }
      }
    }
    return result;
  }

  /**
   * <p>
   * findNativeLibrary.</p>
   *
   * @param name a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public static String findNativeLibrary(String name)
  {
    return findNativeLibrary(name,
                             true);
  }

  /**
   * <p>
   * findNativeLibrary.</p>
   *
   * @param baseName a {@link java.lang.String} object.
   * @param mapName a boolean.
   * @return a {@link java.lang.String} object.
   */
  public static String findNativeLibrary(String baseName,
                                         boolean mapName)
  {
    String osName = System.getProperty("os.name").toLowerCase();
    String osArch = System.getProperty("os.arch").toLowerCase();
    String mapped = mapName ? System.mapLibraryName(baseName) : baseName;
    String netbeansDir = System.getProperty("netbeans.dirs");
    String result = null;
    if (netbeansDir != null) {
      String netbeansUser = System.getProperty("netbeans.user");
      String nbPath = netbeansUser + File.pathSeparator + netbeansDir;
      result = searchPath(mapped,
                          osName,
                          osArch,
                          nbPath,
                          "modules" + File.separator + "lib" + File.separator);
    }
    if (result == null) {
      result = searchPath(mapped,
                          osName,
                          osArch,
                          System.getProperty("java.library.path"),
                          "");
    }
    return result;
  }

}
