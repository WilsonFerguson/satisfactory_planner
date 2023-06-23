import java.util.ArrayList;
import library.core.*;

class Machine extends PComponent implements EventIgnorer {
  private ArrayList<Resource> inputs;
  private Resource output;

  private PVector position;
  private PVector size;

  private float gapBetweenNodes;
  private float nodeRadius = 30;

  public Machine(PVector position, PVector size) {
    this.position = position.copy();
    this.size = size.copy();
    gapBetweenNodes = size.y;
  }

  public void setInputResourceCount(int count) {
    inputs = new ArrayList<>(count);
    gapBetweenNodes = size.y / (count + 1);
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

  public void setSize(PVector size) {
    this.size = size;
    gapBetweenNodes = size.y / (inputs.size() + 1);
  }

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

  public Resource[] getInputResources() {
    Resource[] resources = new Resource[inputs.size()];
    for (int i = 0; i < inputs.size(); i++) {
      resources[i] = inputs.get(i);
    }
    return resources;
  }

  public Resource getInputResource(int index) { return inputs.get(index); }

  public Resource getOutputResource() { return output; }

  private PVector getInputNodePosition(int index) {
    return new PVector(position.x - size.x / 2,
                       position.y - size.y / 2 + gapBetweenNodes * (index + 1));
  }

  public boolean isInputNodeHovered() {
    for (int i = 0; i < inputs.size(); i++) {
      if (PVector.distSq(getInputNodePosition(i),
                         new PVector(mouseX, mouseY)) <= nodeRadius) {
        return true;
      }
    }
    return false;
  }

  public boolean isOutputNodeHovered() {
    return PVector.distSq(new PVector(position.x + size.x / 2, position.y),
                          new PVector(mouseX, mouseY)) <= nodeRadius;
  }

  public boolean isNodeHovered() {
    return isInputNodeHovered() || isOutputNodeHovered();
  }

  /**
   * Note this function returns null if no node is hovered.
   */
  public Resource getHoveredResource() {
    if (isInputNodeHovered()) {
      for (int i = 0; i < inputs.size(); i++) {
        if (PVector.distSq(getInputNodePosition(i),
                           new PVector(mouseX, mouseY)) <= nodeRadius) {
          return inputs.get(i);
        }
      }
    } else if (isOutputNodeHovered()) {
      return output;
    }

    return null;
  }

  /**
   * Note this function returns null if no node is hovered.
   */
  public PVector getHoveredNodePosition() {
    if (isInputNodeHovered()) {
      for (int i = 0; i < inputs.size(); i++) {
        if (PVector.distSq(getInputNodePosition(i),
                           new PVector(mouseX, mouseY)) <= nodeRadius) {
          return getInputNodePosition(i);
        }
      }
    } else if (isOutputNodeHovered()) {
      return new PVector(position.x + size.x / 2, position.y, nodeRadius / 2);
    }

    return null;
  }

  public void draw() {
    fill(Settings.machineBackgroundColor);
    stroke(Settings.machineStrokeColor);
    rectMode(CENTER);
    rect(position.x, position.y, size.x, size.y, 15);

    // Input/Output nodes
    fill(255);
    noStroke();
    for (int i = 0; i < inputs.size(); i++) {
      circle(getInputNodePosition(i), nodeRadius / 2);
    }

    circle(position.x + size.x / 2, position.y, nodeRadius / 2);
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
