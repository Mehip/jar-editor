import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.IOException;

public class Main {

  public static void main(String[] args) {
    try {
      try {
        ArgueReader.recognize(args);
      } catch (JarFileInaccesible | IOException | NotFoundException | CannotCompileException e) {
        System.out.println("Error");
      }
    } catch (WrongParametersException e) {
      System.out.println("Error");
    }
  }
}
