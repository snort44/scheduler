import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TuringMachine {

  public static void main(String[] args) {
    // todo: this will have to change to actually receive the instructions and the word...
    String TMAsciiFile = "1 Start, _, _, R, 1 Start\n"
        + "1 Start, _, _, R, 1 Start\n"
        + "1 Start, b, b, R, 1 Start\n"
        + "1 Start, a, a, R, 2\n"
        + "2, a, a, R, Halt 3\n"
        + "2, b, b, R, 1 Start\n"
        + "Halt 3\n";

    String word = "aabb";

    new TuringMachine().doOutput(TMAsciiFile, word);
  }

  private void doOutput(String infile, String word) {

    final List<Instruction> instructions = parseInstructions(infile);

    boolean valid = validateWord(instructions, word);
    System.out.println((valid ? "Accepted: " : "Rejected: ") + word);

  }

  private boolean validateWord(List<Instruction> instructions, String word) {

    // does the input contain only a, b, and spaces (underscores)?
    if (word == null || !word.matches("[ab_]+")) {
      return false;
    }

    // first, check to make sure we have a "start"
    final String start = findInitialFromState(instructions);
    if (start == null) {
      return false; // no start found
    }

    // Get an array of all the letters, it's just easier to work with...
    String[] letters = word.split("");
    int index = 0;

    // next, execute the instruction
    String currentState = start;
    Instruction currentInstruction;
    do {

      if (index < 0 || index >= letters.length) {
        return currentState.toLowerCase().contains("halt");
      }

      currentInstruction = findInstruction(instructions, currentState, letters[index]);
      if (currentInstruction == null) {
        return false;
      }
      System.out.println(currentInstruction);// do not submit the project with this enabled!

      // Update the state of the program
      letters[index] = currentInstruction.getNewLetter();    // this is the new_letter
      String direction = currentInstruction.getDirection();
      if ("R".equals(direction)) {
        index++;
      } else {
        index--;
      }
      currentState = currentInstruction.getToState();

    } while (!currentInstruction.isHalt());

    return true;
  }

  private Instruction findInstruction(List<Instruction> instructions, String currentState, String letter) {
    for (Instruction row : instructions) {
      if (row.getFromState().equals(currentState) && (letter.equals(row.getLetter()) || row.isHalt())) {
        return row;
      }
    }
    return null;
  }

  private String findInitialFromState(List<Instruction> instructions) {
    for (Instruction row : instructions) {
      if (row.isStart()) {
        return row.getFromState();
      }
    }
    return null;
  }

  private List<Instruction> parseInstructions(String infile) {
    final List<Instruction> listOfInstructions = new ArrayList<>();
    for (String instruction : infile.split("\n")) {
      final List<String> strings = Arrays.stream(instruction.split(",")).map(String::trim).collect(Collectors.toList());
      // This is a normal instruction
      if (strings.size() == 5) {
        listOfInstructions.add(new Instruction(strings.get(0), strings.get(1), strings.get(2), strings.get(3), strings.get(4)));
      }
      // This must be a halt
      if (strings.size() == 1) {
        // I don't think you even need these...
        listOfInstructions.add(new Instruction(strings.get(0)));
      }
    }
    return listOfInstructions;
  }

  private class Instruction {

    private final String fromState;
    private final String letter;
    private final String newLetter;
    private final String direction;
    private final String toState;
    private final boolean start;
    private final boolean halt;

    private Instruction(String fromState, String letter, String newLetter, String direction, String toState) {
      this.fromState = fromState;
      this.letter = letter;
      this.newLetter = newLetter;
      this.direction = direction;
      this.toState = toState;
      this.start = fromState != null && fromState.toLowerCase().contains("start");
      this.halt = false;
    }

    public Instruction(String fromState) {
      this.fromState = fromState;
      this.letter = null;
      this.newLetter = null;
      this.direction = null;
      this.toState = null;
      this.start = false;
      this.halt = true;
    }

    public String getFromState() {
      return fromState;
    }

    public String getLetter() {
      return letter;
    }

    public String getNewLetter() {
      return newLetter;
    }

    public String getDirection() {
      return direction;
    }

    public String getToState() {
      return toState;
    }

    public boolean isStart() {
      return start;
    }

    public boolean isHalt() {
      return halt;
    }

    @Override
    public String toString() {
      return
          "fromState='" + fromState + '\'' +
          ", letter='" + letter + '\'' +
          ", newLetter='" + newLetter + '\'' +
          ", direction='" + direction + '\'' +
          ", toState='" + toState + '\'';
    }
  }

}
