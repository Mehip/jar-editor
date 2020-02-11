import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

public class SaveJar {
  static void createFutureJar(String futureJarName, String pathToJar) throws IOException {
    File original = new File(pathToJar);
    Path copied = Paths.get(futureJarName);
    Path originalPath = original.toPath();
    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
  }

  public static void saveJarFile(int pack, String packStr, String nameFutureJar) throws IOException, NotFoundException, JarFileInaccesible {
    File original = new File(nameFutureJar);
    Path copied = Paths.get(nameFutureJar + ".tmp");
    Path originalPath = original.toPath();
    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
    original.delete();
    String pathToJar = nameFutureJar + ".tmp";
    JarOutputStream target = null;

    try {
      File jarFile = new File(nameFutureJar);
      OutputStream os = new FileOutputStream(jarFile);
      target = new JarOutputStream(os, ScriptRunner.manifest);
    } catch (IOException e) {
      System.out.println("Nie udana operacja");
    }

    int len = 0;
    byte[] buffer = new byte[1024];

    String jarName = nameFutureJar + ".tmp";

    for (String name : ScriptRunner.files) {

      JarEntry je = new JarEntry(name);

      File file = new File(jarName + "!/" + name);
      je.setComment("Craeting Jar");
      je.setTime(Calendar.getInstance().getTimeInMillis());
      target.putNextEntry(je);

      URL url = file.toURI().toURL();
      file.delete();
      url = new URL("jar:" + url.toString());

      InputStream is = url.openStream();
      while ((len = is.read(buffer, 0, buffer.length)) != -1) {
        target.write(buffer, 0, len);
      }
      is.close();
      target.closeEntry();
    }

    Set<String> packages = JarExplorer.getPackagesList1(pathToJar);
    List<String> cla = JarExplorer.getClassesList(pathToJar).stream().map(Class::getName).collect(Collectors.toList());
    if (pack == 1) {
      packages.add(packStr);
    } else if (pack == 2) {
      packages.remove(packStr);
    } else {
      cla.remove(packStr);
    }

    for (String s : packages) {
      JarEntry je = new JarEntry(s);
      target.putNextEntry(je);
    }

    for (String clz : cla) {
      String entry = clz.replace('.', '/')+".class";
      JarEntry je = new JarEntry(entry);
      je.setComment("Craeting Jar");
      je.setTime(Calendar.getInstance().getTimeInMillis());
      target.putNextEntry(je);

      CtClass ctClass = ScriptRunner.pool.get(clz);

      try {
        target.write(ctClass.toBytecode());
      } catch (CannotCompileException e) {
        System.out.println("Error");
      }

      target.closeEntry();
    }

    target.close();

    target.close();
  }

  static void replaceJarFile(byte[] fileByteCode, String fileName, String nameFutureJar) throws IOException {
    File jarFile = new File(nameFutureJar);
    File tempJarFile = new File(nameFutureJar + ".tmp");
    JarFile jar = new JarFile(jarFile);
    boolean jarWasUpdated = false;

    try {
      JarOutputStream tempJar =
          new JarOutputStream(new FileOutputStream(tempJarFile));

      byte[] buffer = new byte[1024];
      int bytesRead;

      try {
        try {
          JarEntry entry = new JarEntry(fileName);
          tempJar.putNextEntry(entry);
          tempJar.write(fileByteCode);

        } catch (Exception ex) {
          System.out.println("Error");

          tempJar.putNextEntry(new JarEntry("stub"));
        }

        InputStream entryStream = null;
        for (Enumeration entries = jar.entries(); entries.hasMoreElements(); ) {
          JarEntry entry = (JarEntry) entries.nextElement();

          if (!entry.getName().equals(fileName)) {
            entryStream = jar.getInputStream(entry);
            tempJar.putNextEntry(entry);

            while ((bytesRead = entryStream.read(buffer)) != -1) {
              tempJar.write(buffer, 0, bytesRead);
            }
          }
        }
        if (entryStream != null)
          entryStream.close();
        jarWasUpdated = true;
      } catch (Exception ex) {
        System.out.println("Error");

        tempJar.putNextEntry(new JarEntry("stub"));
      } finally {
        tempJar.close();
      }
    } finally {

      jar.close();

      if (!jarWasUpdated) {
        tempJarFile.delete();
      }
    }


    if (jarWasUpdated) {
      if (jarFile.delete()) {
        tempJarFile.renameTo(jarFile);
      } else
        System.out.println("Error");
    }
  }
}
