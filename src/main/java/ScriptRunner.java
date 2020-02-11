import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

class ScriptRunner {
  static Manifest manifest;
  static List<String> files;
  static ClassPool pool;
  public static File futureJar;

  static void readScript(String pathToJar, String pathToScript, String nameFutureJar) throws IOException, NotFoundException, CannotCompileException, JarFileInaccesible {
    Scanner scanner = new Scanner(new File(pathToScript));
    String line, command;

    SaveJar.createFutureJar(nameFutureJar, pathToJar);

    openJar(pathToJar);

    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      command = line.split(" ")[0];

      switch (command) {
        case "add-package":
          JarEditor.editPackage(line, true, nameFutureJar);
          break;
        case "remove-package":
          JarEditor.editPackage(line, false, nameFutureJar);
          break;
        case "add-class":
          JarEditor.addClassInterface(true, line, nameFutureJar);
          break;
        case "remove-class":
          JarEditor.removeClassInterface(line, nameFutureJar);
          break;
        case "add-interface":
          JarEditor.addClassInterface(false, line, nameFutureJar);
          break;
        case "remove-interface":
          JarEditor.removeClassInterface(line, nameFutureJar);
          break;
        case "add-method":
          JarEditor.editMethod(line, true, nameFutureJar);
          break;
        case "remove-method":
          JarEditor.editMethod(line, false, nameFutureJar);
          break;
        case "set-method-body":
          JarEditor.setMethodCtorBody(true, line, nameFutureJar);
          break;
        case "add-after-method":
          JarEditor.addToMethod(line, true, nameFutureJar);
          break;
        case "add-before-method":
          JarEditor.addToMethod(line, false, nameFutureJar);
          break;
        case "add-field":
          JarEditor.editField(line, true, nameFutureJar);
          break;
        case "remove-field":
          JarEditor.editField(line, false, nameFutureJar);
          break;
        case "add-ctor":
          JarEditor.editConstructor(line, true, nameFutureJar);
          break;
        case "remove-ctor":
          JarEditor.editConstructor(line, false, nameFutureJar);
          break;
        case "set-ctor-body":
          JarEditor.setMethodCtorBody(false, line, nameFutureJar);
          break;
      }
    }
    scanner.close();
    System.out.println("Skrypt zakonczony pomyslnie");
  }

  private static void openJar(String pathToJar) {
    try {
      JarFile jar = new JarFile(pathToJar);

      URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
      URLClassLoader.newInstance(urls);
      manifest = jar.getManifest();

      pool = new ClassPool(ClassPool.getDefault());
      pool.appendClassPath(pathToJar);

      Enumeration<JarEntry> jarEntryEnumeration = jar.entries();

      files = new ArrayList<>();
      while (jarEntryEnumeration.hasMoreElements()) {
        JarEntry je = jarEntryEnumeration.nextElement();
        String file = je.getName();
        if (file.endsWith("MANIFEST.MF")) {
          continue;
        }
        if (!file.substring(file.lastIndexOf("\\") + 1).trim().contains(".")) {
          continue;
        }

        if (!file.endsWith(".class")) {
          files.add(file);
        }
      }
    } catch (IOException | NotFoundException e) {
      System.out.println("Nie udało się otworzyć pliku jar");
    }
  }
}
