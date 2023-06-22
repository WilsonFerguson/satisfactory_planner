import java.util.ArrayList;
import library.core.*;

class Machine extends PComponent implements EventIgnorer {
  private ArrayList<Resource> inputs;
  private Resource output;

  public PVector position;
  public PVector size;

  public Machine(PVector position, PVector size) {
    this.position = position.copy();
    this.size = size.copy();
  }

  public void setInputResourceCount(int count) {
    inputs = new ArrayList<Resource>(count);
  }

  public void addInputResource(Resource input) { inputs.add(input); }

  public void addInputResourceFromType(String type) {
    inputs.add(new Resource(type, 0));
  }

  public void addInputResourceFromAmount(float amount) {
    inputs.get(inputs.size() - 1).amountPerMinute = amount;
  }

  public void setOutputResource(Resource output) { this.output = output; }

  public void setPosition(PVector position) { this.position = position; }

  public void setSize(PVector size) { this.size = size; }

  public String[] getInputTypes() {
    String[] types = new String[inputs.size()];
    for (int i = 0; i < inputs.size(); i++) {
      types[i] = inputs.get(i).type;
    }
    return types;
  }

  public String getOutputType() { return output.type; }

  public float[] getInputAmountPerMinutes() {
    float[] amounts = new float[inputs.size()];
    for (int i = 0; i < inputs.size(); i++) {
      amounts[i] = inputs.get(i).amountPerMinute;
    }
    return amounts;
  }

  public float getOutputAmountPerMinute() { return output.amountPerMinute; }

  public void draw() {
    fill(Settings.machineBackgroundColor);
    stroke(Settings.machineStrokeColor);
    rectMode(CENTER);
    rect(position.x, position.y, size.x, size.y, 15);

    // Input/Output nodes
    fill(255);
    noStroke();
    float gapBetweenNodes = size.y / (inputs.size() + 1);
    for (int i = 0; i < inputs.size(); i++) {
      PVector nodePosition =
          new PVector(position.x - size.x / 2,
                      position.y - size.y / 2 + gapBetweenNodes * (i + 1));
      circle(nodePosition, 15);
    }

    circle(position.x + size.x / 2, position.y, 15);
  }

  @Override
  public String toString() {
    String s = "";
    s += "Position: " + position + "\n";
    s += "Size: " + size + "\n";
    s += "Inputs: " + inputs.toString() + "\n";
    s += "Output: " + output + "\n";
    return s;
  }
}
