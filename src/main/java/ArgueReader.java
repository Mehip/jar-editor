import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ArgueReader {

  static void recognize(String[] args) throws WrongParametersException, JarFileInaccesible, IOException, NotFoundException, CannotCompileException {
    if (args.length < 3 || args.length > 6) throw new WrongParametersException("Zła liczba argumentów");

    if (!args[0].equals("--i")) {
      throw new WrongParametersException("Niepoprawne argumenty");
    }

    if (args.length == 3) {
      //parametry 3 argumentowe
      switch (args[2]) {
        case "--list-packages":
          Set<String> packagesList = JarExplorer.getPackagesList(args[1]);
          packagesList.forEach(System.out::println);
          return;
        case "--list-classes":
          List<Class> classes = JarExplorer.getClassesList(args[1]);
          classes.forEach(c -> System.out.println((c.isInterface() ? "Interface: " : "Class: ") + c.getName()));
          return;
      }
    } else if (args.length == 4) {
      //parametry 4 argumentowe
      switch (args[2]) {
        case "--list-methods":
          List<String> methodsList = JarExplorer.getMethodsList(args[1], args[3]);
          methodsList.forEach(System.out::println);
          return;
        case "--list-fields":
          List<String> fieldsList = JarExplorer.getFieldList(args[1], args[3]);
          fieldsList.forEach(System.out::println);
          return;
        case "--list-ctors":
          List<String> ctorsList = JarExplorer.getConstructorList(args[1], args[3]);
          ctorsList.forEach(System.out::println);
          return;
      }
    } else if (args.length == 6) {
      if (args[2].equals("--script") && args[4].equals("--o")) {
        ScriptRunner.readScript(args[1], args[3], args[5]);
        return;
      }
    }

    throw new WrongParametersException("Podano niepoprawne parametry");
  }
}
