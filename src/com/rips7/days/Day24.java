package com.rips7.days;

import com.rips7.algorithms.Combinatorics;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Day24 extends Day<List<String>> {

  public Day24() {
    super(DaysEnum.DAY_24);
  }

  @Override
  public void part1(final List<String> args) {
    if (args.isEmpty()) {
      return;
    }
    final String largestModelNumber = deduceAndGetValidModelNumber(true);
    final ALU alu =
        new ALU(
            args.toArray(String[]::new),
            Arrays.stream(largestModelNumber.split(""))
                .map(Long::parseLong)
                .collect(Collectors.toCollection(ArrayDeque::new)));
    System.out.println(
        String.format(
            "The largest model number accepted by the MONAD is %s. ALU outputs %s.",
            largestModelNumber, alu.z));
  }

  @Override
  public void part2(final List<String> args) {
    if (args.isEmpty()) {
      return;
    }
    final String smallestModelNumber = deduceAndGetValidModelNumber(false);
    final ALU alu =
        new ALU(
            args.toArray(String[]::new),
            Arrays.stream(smallestModelNumber.split(""))
                .map(Long::parseLong)
                .collect(Collectors.toCollection(ArrayDeque::new)));
    System.out.println(
        String.format(
            "The smallest model number accepted by the MONAD is %s. ALU outputs %s.",
            smallestModelNumber, alu.z));
  }

  /**
   * This method contains a lot of deduction logic, as it depends on observations directly on
   * the input. The actual implementation, hence, depends on these observations, and may vary
   * depending on the input.
   *
   * @return A 14 digit number as {@link String} that is generated to pass the MONAD program.
   */
  private String deduceAndGetValidModelNumber(final boolean largestOrSmaller) {
    // OBSERVATIONS and HIGH LEVEL OVERVIEW of MONAD:
    // 1. The input instructions can be grouped in 14 sets of 18 instructions each.
    // 2. Then, each instruction set:
    //    i. begins with "inp w", reading one digit from the input into register
    //       w.
    //   ii. resets register x to 0, and adds register z.
    //  iii. calculates "mod x 26".
    // 3. Then, either divides z by 1 (div z 1) or by 26 (div z 26).
    // 4. Adds a constant to x, and checks if "eql x w" and "eql x 0" (latter one inverts 0 to 1 and
    //    vice versa).
    //    Because of the instruction "mod x 26" => 0 ≤ x ≤ 25,
    //    When adding a constant and then checking equality with w, because 1 ≤ w ≤ 9, this will
    //    only be true if the addition of the constant keeps in the range 1 ≤ x ≤ 9.
    // 5. At this point, x is either 0 or 1 (rather small).
    // 6. Then each instruction set:
    //    i. resets register y to 0 and adds 25.
    //   ii. multiplies it by the value of x (0 or 1).
    //  iii. adds 1 (so, y is either 1 or 26).
    //   iv. multiplies z by the value of y (so, z either stays the same, or gets scaled up again by
    // 26).
    //    v. adds a constant value to y.
    //   vi. multiplies it by the value of x (0 or 1).
    //  vii. adds that value to z (so, z either stays the same, or gets increased by a constant
    // value).
    //
    // So, w is used as a register to read input, z is the register whose value carries over onto
    // all instruction sets, and x and y register are used as temporary registers.
    //
    // Essentially, the program scales z up and down based on the values of the input digits, which
    // are checked / used exactly once.
    // There are two types of groups based on this argument:
    //   i. "div z 1" & "add x <positive > 10>" (7 in total)
    //  ii. "div z 26" & "add x <negative>" (7 in total)
    // The first type, will always yield "eql x w" & "eql x 0" => x = 1
    // The second type, will always yield "eql x w" & "eql x 0" => x = 0
    //
    // An instruction set ends by adding the value of y to the result in z.
    // Based, on the above, we can deduce that for each group type, the final value of z will be:
    //   i. 26z + w + <constant>
    //  ii. int(z/26)        , if add x <negative>  = w
    //      26 * int(z / 26) , if add x <negative> != w
    //
    // So, we need to figure out the correct digits for the spots that correspond to the instruction
    // sets that scale z down. These are groups 5,6,8,10,11,12,13.
    // For these groups we need to loop over all the combinations that eventually yield z = 0.
    //
    // For groups 1,2,3,4,7,9,14 we can then deterministically calculate the corresponding digit.
    // since we know that "add x <negative> = w":
    // w = z % 26 + <negative>

    final List<List<Integer>> digitPermutations =
        largestOrSmaller
            ? Combinatorics.permutations(List.of(9, 8, 7, 6, 5, 4, 3, 2, 1), 7)
            : Combinatorics.permutations(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), 7);

    for (final List<Integer> digits : digitPermutations) {
      int digitIndex = 0;

      final Integer[] constantsForYNotEqualDigitGroups = {
        14, 2, 1, 13, 5, 0, 0, 9, 0, 13, 0, 0, 0, 0
      };
      final Integer[] constantsForXEqualDigitGroups = {
        0, 0, 0, 0, 0, -12, -12, 0, -7, 0, -8, -5, -10, -7
      };

      final StringBuilder result = new StringBuilder();
      int z = 0;

      for (int i = 0; i < constantsForYNotEqualDigitGroups.length; i++) {
        final int constantForY = constantsForYNotEqualDigitGroups[i];
        final int constantForX = constantsForXEqualDigitGroups[i];
        if (constantForY == 0) {
          assert constantForX != 0;
          int digit = z % 26 + constantForX;
          if (digit < 1 || digit > 9) {
            continue;
          }
          z /= 26;
          result.append(digit);
        } else {
          assert constantForX == 0;
          int digit = digits.get(digitIndex++);
          z = z * 26 + digit + constantForY;
          result.append(digit);
        }
      }

      if (result.toString().length() == 14) {
        return result.toString();
      }
    }

    throw new RuntimeException("Could not find a valid model number");
  }

  @SuppressWarnings("unused")
  private static final class ALU {
    final String[] instructions;
    final Queue<Long> inputs;
    int IP;
    long w;
    long x;
    long y;
    long z;

    public ALU(final String[] instructions, final Queue<Long> inputs) {
      this.instructions = instructions;
      this.inputs = inputs;
      this.IP = 0;
    }

    private void execute() {
      while (this.IP < this.instructions.length) {
        this.step();
      }
    }

    private void step() {
      final String instruction = instructions[IP];
      final String[] instructionParts = instruction.split(" ");
      final String command = instructionParts[0];
      switch (command) {
        case "inp":
          handleInput(instructionParts[1], inputs.poll());
          break;
        case "add":
          handleOperation(instructionParts[1], instructionParts[2], Long::sum);
          break;
        case "mul":
          handleOperation(instructionParts[1], instructionParts[2], (a, b) -> a * b);
          break;
        case "div":
          handleOperation(instructionParts[1], instructionParts[2], (a, b) -> a / b);
          break;
        case "mod":
          handleOperation(instructionParts[1], instructionParts[2], (a, b) -> a % b);
          break;
        case "eql":
          handleOperation(
              instructionParts[1], instructionParts[2], (a, b) -> a.equals(b) ? 1L : 0L);
          break;
        default:
          throw new RuntimeException(String.format("Unrecognized command %s", command));
      }
      IP++;
      //      System.out.println(String.format("%s\nw=%s\nx=%s\ny=%s\nz=%s\n---------------",
      // instruction, w, x, y, z));
    }

    private void handleInput(final String op, final Long input) {
      final Field operand = retrieveOperand(op);
      try {
        operand.setLong(this, input);
      } catch (final IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    private void handleOperation(
        final String op1, final String op2, final BiFunction<Long, Long, Long> operation) {
      final Field operand1 = retrieveOperand(op1);
      try {
        final long operand2 = Long.parseLong(op2);
        operand1.setLong(this, operation.apply(operand1.getLong(this), operand2));
      } catch (final NumberFormatException nfe) {
        try {
          final Field operand2 = retrieveOperand(op2);
          operand1.setLong(this, operation.apply(operand1.getLong(this), operand2.getLong(this)));
        } catch (final IllegalAccessException iae) {
          iae.printStackTrace();
        }
      } catch (final IllegalAccessException iae) {
        iae.printStackTrace();
      }
    }

    private Field retrieveOperand(final String operand) {
      try {
        return ALU.class.getDeclaredField(operand);
      } catch (final NoSuchFieldException e) {
        e.printStackTrace();
      }
      throw new RuntimeException();
    }
  }
}
