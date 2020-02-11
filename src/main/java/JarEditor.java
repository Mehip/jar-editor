import javassist.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class JarEditor {

  //tryb pack na 1 -> dodawanie pakietu
  //tryb pack na 2 -> usuwanie pakietu
  static void editPackage(String line, boolean pack, String nameFutureJar) throws NotFoundException, IOException, JarFileInaccesible {
    if (pack) {
      SaveJar.saveJarFile(1, line.split(" ")[1].replace(".", "/") + "/", nameFutureJar);
    } else {
      SaveJar.saveJarFile(2, line.split(" ")[1].replace(".", "/") + "/", nameFutureJar);
    }
  }

  static void addClassInterface(boolean classa, String line, String nameFutureJar) {
    String className = line.split(" ")[1];
    CtClass ctClass;

    ctClass = classa ? ScriptRunner.pool.makeClass(className) : ScriptRunner.pool.makeInterface(className);
    ctClass.defrost();

    try {
      SaveJar.replaceJarFile(ctClass.toBytecode(), ctClass.getName().replace(".", "/") + ".class", nameFutureJar);
    } catch (IOException | CannotCompileException e) {
      System.out.println("Nie udane dodanie klasy/interfejsu");
    }
  }

  //tryb pack na 0 -> usuwanie klasy/interfejsu
  static void removeClassInterface(String line, String nameFutureJar) throws NotFoundException, IOException, JarFileInaccesible {
      SaveJar.saveJarFile(0, line.split(" ")[1], nameFutureJar);
  }

  static void editMethod(String line, Boolean add, String nameFutureJar) {
    String className = line.split(" ")[1];
    String txt1 = line.substring(line.indexOf(" ") + 1);
    String txt = txt1.substring(txt1.indexOf(" ") + 1);

    try {
      CtClass ctClass = ScriptRunner.pool.get(className);
      ctClass.defrost();
      if (add) {
        CtMethod method = txt.contains("void") ? CtMethod.make(txt + "{}", ctClass) : CtMethod.make(txt + "{return null;}", ctClass);
        ctClass.addMethod(method);
      } else {
        ctClass.removeMethod(ctClass.getDeclaredMethod(txt));
      }
      SaveJar.replaceJarFile(ctClass.toBytecode(), ctClass.getName().replace(".", "/") + ".class", nameFutureJar);
    } catch (CannotCompileException | NotFoundException | IOException e) {
      System.out.println("Nie udane dodanie/usuniecie metody: " + txt);
    }
  }

  static void setMethodCtorBody(Boolean type, String line, String nameFutureJar) throws IOException, NotFoundException, CannotCompileException {
    Scanner scanner = new Scanner(new File(line.substring(line.lastIndexOf(" ") + 1)));
    StringBuilder text = new StringBuilder();
    String className = line.split(" ")[1];
    String methodName = line.split(" ")[1];
    CtMethod ctMethodEdi = null;
    CtConstructor ctCtorEdi = null;

    while (scanner.hasNextLine()) {
      text.append(scanner.nextLine()).append("\n");
    }

    if (type) {
      className = className.substring(0, className.indexOf("("));
      className = className.substring(0, className.lastIndexOf("."));
    } else {
      className = className.substring(0, className.indexOf("("));
    }

    CtClass ctClass = ScriptRunner.pool.getCtClass(className);

    if (type) {
      for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
        if (ctMethod.getLongName().equals(methodName)) {
          ctMethodEdi = ctMethod;
        }
      }
    } else {
      for (CtConstructor ctMethod : ctClass.getDeclaredConstructors()) {
        if (ctMethod.getLongName().equals(methodName)) {
          ctCtorEdi = ctMethod;
        }
      }
    }

    try {
      ctClass.defrost();
      if (type) {
        assert ctMethodEdi != null;
        ctMethodEdi.setBody(text.toString());
      } else {
        assert ctCtorEdi != null;
        ctCtorEdi.setBody(text.toString());
      }
    } catch (Exception e) {
      System.out.println("Nie udane ustawienie ciala metody/konstruktora");
    }
    SaveJar.replaceJarFile(ctClass.toBytecode(), ctClass.getName().replace(".", "/") + ".class", nameFutureJar);
  }

  static void addToMethod(String line, Boolean after, String nameFutureJar) throws NotFoundException, CannotCompileException, IOException {
    String className = line.split(" ")[1];
    className = className.substring(0, className.indexOf("("));
    className = className.substring(0, className.lastIndexOf("."));
    String methodName = line.split(" ")[1];
    Scanner scanner = new Scanner(new File(line.substring(line.lastIndexOf(" ") + 1)));
    StringBuilder text = new StringBuilder();
    CtMethod ctMethodEdi = null;

    CtClass ctClass = ScriptRunner.pool.getCtClass(className);

    while (scanner.hasNextLine()) {
      text.append(scanner.nextLine()).append("\n");
    }

    for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
      if (ctMethod.getLongName().equals(methodName)) {
        ctMethodEdi = ctMethod;
      }
    }

    ctClass.defrost();
    if (after) {
      assert ctMethodEdi != null;
      ctMethodEdi.insertAfter(text.toString());
    } else {
      assert ctMethodEdi != null;
      ctMethodEdi.insertBefore(text.toString());
    }
    SaveJar.replaceJarFile(ctClass.toBytecode(), ctClass.getName().replace(".", "/") + ".class", nameFutureJar);
  }

  static void editField(String line, Boolean add, String nameFuturejar) {
    String className = line.split(" ")[1];
    String txt1 = line.substring(line.indexOf(" ") + 1);
    String txt = txt1.substring(txt1.indexOf(" ") + 1) + ";";

    try {
      CtClass ctClass = ScriptRunner.pool.get(className);
      ctClass.defrost();
      if (add) {
        ctClass.addField(CtField.make(txt, ctClass));
      } else {
        ctClass.removeField(ctClass.getDeclaredField(txt.substring(0, txt.length() - 1)));
      }
      SaveJar.replaceJarFile(ctClass.toBytecode(), ctClass.getName().replace(".", "/") + ".class", nameFuturejar);
    } catch (CannotCompileException | NotFoundException | IOException e) {
      System.out.println("Nie udane dodanie/usuniecie pola");
    }
  }

  static void editConstructor(String line, Boolean add, String nameFuturejar) {
    String className = line.split(" ")[1];
    String txt1 = line.substring(line.indexOf(" ") + 1);
    String txt = txt1.substring(txt1.indexOf(" ") + 1);

    try {
      CtClass ctClass = ScriptRunner.pool.get(className);
      ctClass.defrost();
      if (add) {
        CtConstructor ctConstructor = new CtNewConstructor().make(txt + "{}", ctClass);
        ctClass.addConstructor(ctConstructor);
      } else {
        CtConstructor[] constructor = ScriptRunner.pool.getCtClass(className).getConstructors();
        for (CtConstructor ctConstructor : constructor) {
          if (ctConstructor.getLongName().equals(txt1.replace(" ", ""))) {
            ctClass.removeConstructor(ctConstructor);
          }
        }
      }
      SaveJar.replaceJarFile(ctClass.toBytecode(), ctClass.getName().replace(".", "/") + ".class", nameFuturejar);
    } catch (CannotCompileException | NotFoundException | IOException e) {
      System.out.println("Nie udane dodanie/usuniecia konstruktora");
    }
  }
}
