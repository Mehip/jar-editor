//zła liczba parametrów lub nieznane parametry

public class WrongParametersException extends Exception{
  public WrongParametersException(String message) {
    super(message);
  }
}
