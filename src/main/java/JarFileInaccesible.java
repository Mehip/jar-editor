//brak możliwości otwarcia pliku

public class JarFileInaccesible extends Exception {
  public JarFileInaccesible(String message) {
    super(message);
  }
}
