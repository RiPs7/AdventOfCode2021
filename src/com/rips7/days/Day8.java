package com.rips7.days;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day8 extends Day<List<String>> {

  private static final boolean VISUALISE = false;

  public Day8() {
    super(DaysEnum.DAY_8);
  }

  @Override
  public void part1(final List<String> args) {
    final List<String> allOutputValues =
        args.stream()
            .map(line -> line.split(" \\| ")[1].split(" "))
            .flatMap(Stream::of)
            .collect(Collectors.toList());

    final long countOnesFoursSevensEights =
        allOutputValues.stream()
            .filter(value -> Arrays.asList(2, 3, 4, 7).contains(value.length()))
            .count();

    System.out.println(
        String.format(
            "Digits 1, 4, 7, or 8 appear %s times in total.", countOnesFoursSevensEights));
  }

  @Override
  public void part2(final List<String> args) {
    final Map<String[], String[]> allInput =
        args.stream()
            .map(line -> line.split(" \\| "))
            .collect(Collectors.toMap(parts -> parts[0].split(" "), parts -> parts[1].split(" ")));

    final long outputValuesSum = allInput.entrySet().stream().map(
        entry -> {
          final String[] signals = entry.getKey();
          final String[] outputs = entry.getValue();
          final char[] decodedSignals = decodeSignals(signals);
          final int output1 = encodeOutput(decodedSignals, outputs[0]);
          final int output2 = encodeOutput(decodedSignals, outputs[1]);
          final int output3 = encodeOutput(decodedSignals, outputs[2]);
          final int output4 = encodeOutput(decodedSignals, outputs[3]);
          if (VISUALISE) {
            visualise(output1, output2, output3, output4);
          }
          return 1000 * output1 + 100 * output2 + 10 * output3 + output4;
        }).mapToInt(Integer::intValue).sum();

    System.out.println(String.format("The sum of all output values is %s", outputValuesSum));
  }

  final char[] decodeSignals(final String[] signals) {
    // 0 -> top
    // 1 -> top-right
    // 2 -> bottom-right
    // 3 -> bottom
    // 4 -> bottom-left
    // 5 -> top-left
    // 6 -> middle
    final char[] decodedSignal = new char[7];

    final String one =
        Arrays.stream(signals).filter(signal -> signal.length() == 2).findFirst().orElseThrow();
    final String four =
        Arrays.stream(signals).filter(signal -> signal.length() == 4).findFirst().orElseThrow();
    final String seven =
        Arrays.stream(signals).filter(signal -> signal.length() == 3).findFirst().orElseThrow();
    final String eight =
        Arrays.stream(signals).filter(signal -> signal.length() == 7).findFirst().orElseThrow();

    // Seven - One = top
    String top = seven;
    for (char c : one.toCharArray()) {
      top = top.replace(c + "", "");
    }
    decodedSignal[0] = top.charAt(0);

    // Find 0, 1, 6 and 9 (top-right exists in 0, 1 and 9, but not in 6)
    final String[] zeroAndSixAndNine =
        Arrays.stream(signals).filter(signal -> signal.length() == 6).toArray(String[]::new);
    char topRight =
        Arrays.stream(zeroAndSixAndNine).filter(a -> a.contains(one.substring(0, 1))).count() == 3
            ? one.charAt(1)
            : one.charAt(0);
    char bottomRight = topRight == one.charAt(0) ? one.charAt(1) : one.charAt(0);
    decodedSignal[1] = topRight;
    decodedSignal[2] = bottomRight;

    // Top right is not present in 6
    final String six =
        Arrays.stream(zeroAndSixAndNine)
            .filter(a -> a.indexOf(topRight) == -1)
            .findFirst()
            .orElseThrow();

    // Retain only 0 and 9
    final String[] zeroAndNine =
        Arrays.stream(zeroAndSixAndNine).filter(a -> !a.equals(six)).toArray(String[]::new);

    // Four - One = top-left + middle
    String topLeftAndMiddle = four;
    for (char c : one.toCharArray()) {
      topLeftAndMiddle = topLeftAndMiddle.replace(c + "", "");
    }

    // top-left + middle are present in 9
    final String nine =
        zeroAndNine[0].indexOf(topLeftAndMiddle.charAt(0)) > -1
                && zeroAndNine[0].indexOf(topLeftAndMiddle.charAt(1)) > -1
            ? zeroAndNine[0]
            : zeroAndNine[1];

    // Nine - Four - top = bottom
    String bottom = nine;
    for (char c : four.toCharArray()) {
      bottom = bottom.replace(c + "", "");
    }
    bottom = bottom.replace(decodedSignal[0] + "", "");
    decodedSignal[3] = bottom.charAt(0);

    // Retain only 0
    final String zero =
        Arrays.stream(zeroAndNine).filter(a -> !a.equals(nine)).findFirst().orElseThrow();

    // Zero - Nine = bottom-left
    String bottomLeft = zero;
    for (char c : nine.toCharArray()) {
      bottomLeft = bottomLeft.replace(c + "", "");
    }
    decodedSignal[4] = bottomLeft.charAt(0);

    // Zero - top - top-right - bottom-right - bottom - bottom-left = top-left
    String topLeft = zero;
    topLeft = topLeft.replace(decodedSignal[0] + "", "");
    topLeft = topLeft.replace(decodedSignal[1] + "", "");
    topLeft = topLeft.replace(decodedSignal[2] + "", "");
    topLeft = topLeft.replace(decodedSignal[3] + "", "");
    topLeft = topLeft.replace(decodedSignal[4] + "", "");
    decodedSignal[5] = topLeft.charAt(0);

    // Eight - Zero = middle
    String middle = eight;
    for (char c : zero.toCharArray()) {
      middle = middle.replace(c + "", "");
    }
    decodedSignal[6] = middle.charAt(0);

    return decodedSignal;
  }

  private int encodeOutput(final char[] decodedSignals, final String output) {
    // Decoded Signals
    // 0 -> top
    // 1 -> top-right
    // 2 -> bottom-right
    // 3 -> bottom
    // 4 -> bottom-left
    // 5 -> top-left
    // 6 -> middle

    // Encoded output (binary)
    // 0 -> "1111110" = 126
    // 1 -> "0110000" = 48
    // 2 -> "1101101" = 109
    // 3 -> "1111001" = 121
    // 4 -> "0110011" = 51
    // 5 -> "1011011" = 91
    // 6 -> "1011111" = 95
    // 7 -> "1110000" = 112
    // 8 -> "1111111" = 127
    // 9 -> "1111011" = 123
    final String encodedOutputBinary =
        (output.indexOf(decodedSignals[0]) > -1 ? "1" : "0") +
        (output.indexOf(decodedSignals[1]) > -1 ? "1" : "0") +
        (output.indexOf(decodedSignals[2]) > -1 ? "1" : "0") +
        (output.indexOf(decodedSignals[3]) > -1 ? "1" : "0") +
        (output.indexOf(decodedSignals[4]) > -1 ? "1" : "0") +
        (output.indexOf(decodedSignals[5]) > -1 ? "1" : "0") +
        (output.indexOf(decodedSignals[6]) > -1 ? "1" : "0");

    final int encodedOutput = Integer.parseInt(encodedOutputBinary, 2);

    switch(encodedOutput) {
      case 126:
        return 0;
      case 48:
        return 1;
      case 109:
        return 2;
      case 121:
        return 3;
      case 51:
        return 4;
      case 91:
        return 5;
      case 95:
        return 6;
      case 112:
        return 7;
      case 127:
        return 8;
      case 123:
        return 9;
      default:
        throw new RuntimeException(String.format("Couldn't encode output %s", output));
    }
  }


  private static final int WIDTH = 6;
  private static final int HEIGHT = 7;
  private static final Map<Integer, String[]> DIGITS = new HashMap<>();
  static {
    DIGITS.put(0, new String[] {
        " **** ",
        "*    *",
        "*    *",
        "*    *",
        "*    *",
        "*    *",
        " **** ",
    });
    DIGITS.put(1, new String[] {
        "     *",
        "     *",
        "     *",
        "     *",
        "     *",
        "     *",
        "     *",
    });
    DIGITS.put(2, new String[] {
        " **** ",
        "     *",
        "     *",
        " **** ",
        "*     ",
        "*     ",
        " **** ",
    });
    DIGITS.put(3, new String[] {
        " **** ",
        "     *",
        "     *",
        " **** ",
        "     *",
        "     *",
        " **** ",
    });
    DIGITS.put(4, new String[] {
        "*    *",
        "*    *",
        "*    *",
        " **** ",
        "     *",
        "     *",
        "     *",
    });
    DIGITS.put(5, new String[] {
        " **** ",
        "*     ",
        "*     ",
        " **** ",
        "     *",
        "     *",
        " **** ",
    });
    DIGITS.put(6, new String[] {
        " **** ",
        "*     ",
        "*     ",
        " **** ",
        "*    *",
        "*    *",
        " **** ",
    });
    DIGITS.put(7, new String[] {
        "***** ",
        "     *",
        "     *",
        "     *",
        "     *",
        "     *",
        "     *",
    });
    DIGITS.put(8, new String[] {
        " **** ",
        "*    *",
        "*    *",
        " **** ",
        "*    *",
        "*    *",
        " **** ",
    });
    DIGITS.put(9, new String[] {
        " **** ",
        "*    *",
        "*    *",
        " **** ",
        "     *",
        "     *",
        " **** ",
    });
  }

  private void visualise(final int output1, final int output2, final int output3, final int output4) {
    final String[] digit1 = DIGITS.get(output1);
    final String[] digit2 = DIGITS.get(output2);
    final String[] digit3 = DIGITS.get(output3);
    final String[] digit4 = DIGITS.get(output4);

    for (int row = 0; row < HEIGHT; row++) {
      StringBuilder line = new StringBuilder();

      for (int col = 0; col < WIDTH; col++) {
        line.append(digit1[row].charAt(col));
      }
      line.append(" ");

      for (int col = 0; col < WIDTH; col++) {
        line.append(digit2[row].charAt(col));
      }
      line.append(" ");

      for (int col = 0; col < WIDTH; col++) {
        line.append(digit3[row].charAt(col));
      }
      line.append(" ");

      for (int col = 0; col < WIDTH; col++) {
        line.append(digit4[row].charAt(col));
      }

      System.out.println(line.toString());
    }

    System.out.println();
  }
}
