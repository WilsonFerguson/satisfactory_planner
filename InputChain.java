import GameEngine.*;
import java.util.ArrayList;
import library.core.*;

class InputChain extends PComponent {
  private ArrayList<InputField> inputFields;
  private String latestText;
  private boolean shouldProgress = false;

  private Runnable onEscape;

  public InputChain() { inputFields = new ArrayList<InputField>(); }

  /**
   * Starts the chain by setting the first input field to active.
   */
  public void begin() {
    if (inputFields.size() == 0) {
      println(
          "InputChain failed to start: Size of the chain is 0 so cannot set first input field to active.");
      return;
    }
    latestText = "";

    inputFields.get(0).setActive(true);
    inputFields.get(0).setSelected(true);
    shouldProgress = true;
  }

  public boolean isFinished() { return inputFields.size() == 0; }

  public String getLatestText() { return latestText; }

  public void draw() {
    if (inputFields.size() == 0)
      return;

    if (shouldProgress) {
      inputFields.get(0).setSelected(true);
      shouldProgress = false;
    }

    inputFields.get(0).draw();
  }

  /**
   * Adds an input field to the chain. Once the user submits the content of the
   * field, the next input field in the chain will be displayed.
   *
   */
  public void addInputField(InputField inputField) {
    inputField.onEnter(new Runnable() {
      @Override
      public void run() {
        latestText = inputField.getText();

        removeFirstInputField();
        if (inputFields.size() >= 1) {
          inputFields.get(0).setActive(true);
          shouldProgress = true;
        }
      }
    });

    inputField.setActive(false);
    inputField.setSelected(false);
    inputFields.add(inputField);
  }

  /**
   * Adds an input field to the chain. Once the user submits the content of the
   * field, the second argument is ran and then the next input field in the
   * chain will be displayed.
   */
  public void addInputField(InputField inputField, Runnable onEnter) {
    Runnable finalOnEnter = new Runnable() {
      @Override
      public void run() {
        latestText = inputField.getText();
        onEnter.run();

        removeFirstInputField();
        if (inputFields.size() >= 1) {
          inputFields.get(0).setActive(true);
          shouldProgress = true;
        }
      }
    };

    inputField.onEnter(finalOnEnter);
    inputField.setActive(false);
    inputField.setSelected(false);
    inputFields.add(inputField);
  }

  private void removeFirstInputField() {
    delete(inputFields.get(0));
    inputFields.remove(0);
  }

  public void onEscape(Runnable onEscape) { this.onEscape = onEscape; }

  public void keyPressed() {
    if (keyString.equals("Escape")) {
      onEscape.run();
      int numInputFields = inputFields.size();
      for (int i = 0; i < numInputFields; i++) {
        removeFirstInputField();
      }
    }
  }
}
