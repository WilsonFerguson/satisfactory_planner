class Resource {
  public String type;
  public float amountPerMinute;

  public Resource(String type, float amountPerMinute) {
    this.type = type;
    this.amountPerMinute = amountPerMinute;
  }

  @Override
  public String toString() {
    String s = "";
    s += "Type: " + type + "\n";
    s += "Amount per minute: " + amountPerMinute + "\n";
    return s;
  }
}
