import javassist.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class JarExplorer {
  static Set<String> getPackagesList(String pathToJar) throws IOException {
    File file = new File(pathToJar);
    FileInputStream fis = new FileInputStream(file);
    JarInputStream jarInputStream = new JarInputStream(fis);
    JarEntry jarEntry;

    Set<String> packages = new HashSet<>();
    while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
      if (jarEntry.isDirectory()) {
        packages.add(jarEntry.getName());
      }
    }
    return packages;
  }

  static Set<String> getPackagesList1(String pathToJar) throws IOException {
    File file = new File(pathToJar);
    FileInputStream fis = new FileInputStream(file);
    JarInputStream jarInputStream = new JarInputStream(fis);
    JarEntry jarEntry;

    Set<String> packages = new HashSet<>();
    while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
      if (jarEntry.isDirectory()) {
        packages.add(jarEntry.getName());
      }
    }
    return packages;
  }

  static List<String> getMethodsList(String pathToJar, String className) throws NotFoundException {
    CtClass ctSampleClass = getClass(pathToJar, className);
    List<CtMethod> methodsCt = Arrays.asList(ctSampleClass.getDeclaredMethods());
    List<String> methods = new ArrayList<>();

    methodsCt.forEach(c -> methods.add(c.getName()));
    return methods;
  }

  static List<String> getFieldList(String pathToJar, String className) throws NotFoundException {
    CtClass ctSampleClass = getClass(pathToJar, className);
    List<CtField> fieldCt = Arrays.asList(ctSampleClass.getDeclaredFields());
    List<String> fields = new ArrayList<>();

    fieldCt.forEach(c -> fields.add(c.getName()));
    return fields;
  }

  static List<String> getConstructorList(String pathToJar, String className) throws NotFoundException {
    CtClass ctSampleClass = getClass(pathToJar, className);
    List<CtConstructor> ctConstructors = Arrays.asList(ctSampleClass.getDeclaredConstructors());
    List<String> constructors = new ArrayList<>();

    ctConstructors.forEach(c -> constructors.add(c.getName()));
    return constructors;
  }

  private static CtClass getClass(String pathToJar, String className) throws NotFoundException {
    ClassPool classPool = ClassPool.getDefault();
    ClassPool.doPruning = false;

    classPool.appendClassPath(pathToJar);
    classPool.insertClassPath(pathToJar);
    classPool.appendSystemPath();

    CtClass ctSampleClass = classPool.getCtClass(className);
    ctSampleClass.stopPruning(true);

    return ctSampleClass;
  }

  static List<Class> getClassesList(String pathToJar) throws JarFileInaccesible {
    try {
      JarFile jarFile = new JarFile(pathToJar);
      Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
      URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
      URLClassLoader cl = URLClassLoader.newInstance(urls);

      List<Class> classes = new ArrayList<>();
      while (jarEntryEnumeration.hasMoreElements()) {
        JarEntry je = jarEntryEnumeration.nextElement();
        if (je.isDirectory() || !je.getName().endsWith(".class")) {
          continue;
        }

        String className = je.getName().substring(0, je.getName().length() - 6);
        className = className.replace('/', '.');
        classes.add(cl.loadClass(className));
      }
      return classes;
    } catch (IOException | ClassNotFoundException e) {
      throw new JarFileInaccesible("Nie można otworzyć pliku! Podaj prawidłową ścieżkę.");
    }
  }
}
