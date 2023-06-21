import library.core.EventIgnorer;
import library.core.PComponent;

class Machine extends PComponent implements EventIgnorer {
  private Resource input;
  private Resource output;

  public Machine(Resource input, Resource output) {
    this.input = input;
    this.output = output;
  }

  public String getInputType() { return input.type; }

  public String getOutputType() { return output.type; }

  public int getInputAmountPerSecond() { return input.amountPerSecond; }

  public int getOutputAmountPerSecond() { return output.amountPerSecond; }
}
