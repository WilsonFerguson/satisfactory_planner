import library.core.Applet;

class Main {
  public static void main(String[] args) {
    Applet.fixWindowsScaling();
    new Sketch().startApplet();
  }
}
