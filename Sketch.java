import GameEngine.*;
import java.util.ArrayList;
import library.core.*;

class Sketch extends Applet {

  private ArrayList<Machine> machines = new ArrayList<Machine>();

  private Machine newMachine; // When user is adding a new machine, this is the
                              // one to keep track of their changes
  private InputChain newMachineInputChain;
  InputField newMachineInputField;
  Runnable newMachineRunnable;

  private Panel newObjectPanel; // Opens when clicking the screen to open a
                                // panel to add machines, splitters, etc.
  private boolean newObjectShouldActivate = false;

  private Button exitButton;

  public void setup() {
    fullScreen();
    exitOnEscape(false);

    newMachineInputChain = new InputChain();
    newMachineInputChain.onEscape(() -> { newMachine = null; });

    generateUI();
  }

  public void draw() {
    Animator.Run();
    background(Settings.backgroundColor);

    for (Machine machine : machines) {
      machine.draw();
    }

    if (newObjectShouldActivate) {
      newObjectPanel.setActive(true);
      newObjectPanel.pos = mouse.copy();
      for (int i = 0; i < newObjectPanel.getElements().size(); i++) {
        UIElement element = newObjectPanel.getElements().get(i);
        ((Button)element).setActive(true);
        element.pos = new PVector(mouseX, mouseY + element.size.y * (i - 1));
      }

      newObjectShouldActivate = false;
    }

    newMachineInputChain.draw();
    exitButton.draw();
    newObjectPanel.draw();
  }

  private void generateUI() {
    InputField.setDefaults(new PVector(width / 5, width / 10), color(0, 0.2),
                           color(0, 0.2), color(0, 0.3), color(255), "", 32,
                           color(255));

    // Note for this button: I'm using its text size to store the rotation for
    // the X (so that I can easily animate it)
    int exitButtonSize = 45;
    exitButton = new Button(
        new PVector(width - exitButtonSize / 2 - 10, exitButtonSize / 2 + 10),
        new PVector(exitButtonSize, exitButtonSize), color(0, 0.2),
        color(0, 0.15), color(0, 0.3), color(255), "", 0, color(255)) {
      public void drawText() {
        push();
        translate(pos.x, pos.y);
        rotate(radians(textSize));
        stroke(textColor);
        strokeWeight(3);
        line(-size.x / 4, -size.y / 4, size.x / 4, size.y / 4);
        line(size.x / 4, -size.y / 4, -size.x / 4, size.y / 4);
        pop();
      }
    };
    exitButton.cornerRadius = exitButtonSize;
    exitButton.onClick(() -> { exit(); });
    exitButton.onHover(() -> {
      new Animator(exitButton, 0.2)
          .setSize(exitButtonSize * 1.15f)
          .setCornerRadius(exitButtonSize * 1.15f)
          .setTextSize(90);
    });
    exitButton.onHoverExit(() -> {
      new Animator(exitButton, 0.2)
          .setSize(exitButtonSize)
          .setCornerRadius(exitButtonSize)
          .setTextSize(0);
    });

    textSize(Settings.inputFieldTextSize);
    float textHeight = textAscent() + textDescent();
    Button.setDefaults(new PVector(250, textHeight), Settings.backgroundColor,
                       color(20, 19, 46), color(8, 10, 45), color(0, 0),
                       Settings.inputFieldTextSize, color(255));

    Button newMachineButton = createBasicButton("New Machine", 0, textHeight);
    newMachineButton.onClick(() -> {
      createNewMachineChain();
      for (UIElement element : newObjectPanel.getElements()) {
        element.setActive(false);
      }
      newObjectPanel.setActive(false);
    });
    Button insertSplitterButton =
        createBasicButton("Insert Splitter", 1, textHeight);
    insertSplitterButton.onClick(() -> {
      createSplitter();
      for (UIElement element : newObjectPanel.getElements()) {
        element.setActive(false);
      }
      newObjectPanel.setActive(false);
    });
    Button insertMergerButton =
        createBasicButton("Insert Merger", 2, textHeight);
    insertMergerButton.onClick(() -> {
      createMerger();
      for (UIElement element : newObjectPanel.getElements()) {
        element.setActive(false);
      }
      newObjectPanel.setActive(false);
    });

    ArrayList<Button> buttons = new ArrayList<Button>();
    buttons.add(newMachineButton);
    buttons.add(insertSplitterButton);
    buttons.add(insertMergerButton);
    float panelHeight = buttons.get(0).size.y * buttons.size();
    float panelWidth = buttons.get(0).size.x;
    for (int i = 1; i < buttons.size(); i++) {
      panelWidth = max(panelWidth, buttons.get(i).size.x);
    }
    panelWidth += 4;
    panelHeight += 5;

    newObjectPanel =
        new Panel(PVector.zero(), new PVector(panelWidth, panelHeight),
                  color(0, 0.2), color(255));
    for (Button button : buttons) {
      button.setActive(false);
      button.size.x = panelWidth - 4;
      newObjectPanel.addElement(button);
    }
    newObjectPanel.setActive(false);
    newObjectPanel.setCornerRadius(15);
  }

  private Button createBasicButton(String text, int index, float textHeight) {
    Button button =
        new Button(new PVector(0, textHeight * index),
                   new PVector(textWidth(text) + 20, textHeight + 10), text);
    return button;
  }

  private InputField createBasicInputField(String defaultText) {
    textSize(Settings.inputFieldTextSize);
    float inputHeight = textAscent() + textDescent() + 10;
    float fieldWidth = textWidth(defaultText) * 1.3f;

    // For pos: 5 is padding and / 2 because rectMode(CENTER)
    PVector pos = mouse.copy().sub(0, inputHeight / 2 + 5);
    PVector size = new PVector(fieldWidth, inputHeight);
    InputField field = new InputField(pos, size, color(0, 0.2), color(0, 0.2),
                                      color(0, 0.3), color(255), defaultText,
                                      Settings.inputFieldTextSize, color(255));

    return field;
  }

  private void createNewMachineChain() {
    newMachine = new Machine(mouse, Settings.machineDefaultSize);

    // Number of input resources
    newMachineInputField = createBasicInputField("Number of input resources");
    newMachineInputField.setNumbersOnly(true);
    newMachineRunnable = new Runnable() {
      @Override
      public void run() {
        int count = parseInt(newMachineInputChain.getLatestText());
        newMachine.setInputResourceCount(count);

        for (int i = 0; i < count; i++) {
          // Input resource name
          newMachineInputField =
              createBasicInputField("Input resource of machine");
          newMachineRunnable = new Runnable() {
            @Override
            public void run() {
              newMachine.addInputResourceFromType(
                  newMachineInputChain.getLatestText());
            }
          };
          newMachineInputChain.addInputField(newMachineInputField,
                                             newMachineRunnable);

          // Input resource amount
          newMachineInputField = createBasicInputField("Input resources/min");
          newMachineInputField.setNumbersOnly(true);
          newMachineRunnable = new Runnable() {
            @Override
            public void run() {
              newMachine.addInputResourceFromAmount(
                  parseFloat(newMachineInputChain.getLatestText()));
            }
          };
          newMachineInputChain.addInputField(newMachineInputField,
                                             newMachineRunnable);
        }

        // Output resource name
        newMachineInputField =
            createBasicInputField("Output resource of machine");
        newMachineRunnable = new Runnable() {
          @Override
          public void run() {
            newMachine.setOutputResource(
                new Resource(newMachineInputChain.getLatestText(), 0));
          }
        };
        newMachineInputChain.addInputField(newMachineInputField,
                                           newMachineRunnable);

        // Output resource amount
        newMachineInputField = createBasicInputField("Output resources/min");
        newMachineInputField.setNumbersOnly(true);
        newMachineRunnable = new Runnable() {
          @Override
          public void run() {
            newMachine.setOutputResource(
                new Resource(newMachine.getOutputType(),
                             parseFloat(newMachineInputChain.getLatestText())));
            machines.add(newMachine);
            newMachine = null;
          }
        };
        newMachineInputChain.addInputField(newMachineInputField,
                                           newMachineRunnable);
      }
    };
    newMachineInputChain.addInputField(newMachineInputField,
                                       newMachineRunnable);

    newMachineInputChain.begin();
  }

  private void createSplitter() { println("Creating splitter"); }

  private void createMerger() { println("merge"); }

  public void mousePressed() {
    if (!newObjectPanel.isActive()) {
      newObjectShouldActivate = true;
    }
  }
}
