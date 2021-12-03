package com.rips7.days;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day3 extends Day<List<String>> {

  public Day3() {
    super(DaysEnum.DAY_3);
  }

  @Override
  public void part1(final List<String> args) {
    char[][] bits = args.stream().map(String::toCharArray).toArray(char[][]::new);
    char[] gammaBits = new char[bits[0].length];
    char[] epsilonBits = new char[bits[0].length];
    for (int col = 0; col < bits[0].length; col++) {
      int bit0Count = 0;
      int bit1Count = 0;
      for (char[] bit : bits) {
        if (bit[col] == '0') {
          bit0Count++;
        } else {
          bit1Count++;
        }
      }
      gammaBits[col] = bit0Count > bit1Count ? '1' : '0';
      epsilonBits[col] = bit0Count < bit1Count ? '1' : '0';
    }
    int gamma = Integer.parseInt(new String(gammaBits), 2);
    int epsilon = Integer.parseInt(new String(epsilonBits), 2);
    System.out.println(
        String.format(
            "Gamma Rate: %s\nEpsilon Rate: %s\nPower Consumption: %s",
            gamma, epsilon, gamma * epsilon));
  }

  @Override
  public void part2(final List<String> args) {
    Map<String, char[]> bits =
        args.stream().collect(Collectors.toMap(Function.identity(), String::toCharArray));

    Map<String, char[]> oxygenRateCandidates = new HashMap<>(bits);
    for (int col = 0; col < args.get(0).length(); col++) {
      int bit0Count = 0;
      int bit1Count = 0;
      for (Map.Entry<String, char[]> entry : oxygenRateCandidates.entrySet()) {
        if (entry.getValue()[col] == '0') {
          bit0Count++;
        } else {
          bit1Count++;
        }
      }
      final char mostCommonBit = bit0Count > bit1Count ? '0' : '1';
      final int _col = col;
      oxygenRateCandidates =
          oxygenRateCandidates.entrySet().stream()
              .filter(entry -> entry.getValue()[_col] == mostCommonBit)
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      if (oxygenRateCandidates.size() == 1) {
        break;
      }
    }
    int oxygenRate =
        Integer.parseInt(new String(oxygenRateCandidates.values().toArray(char[][]::new)[0]), 2);

    Map<String, char[]> co2RateCandidates = new HashMap<>(bits);
    for (int col = 0; col < args.get(0).length(); col++) {
      int bit0Count = 0;
      int bit1Count = 0;
      for (Map.Entry<String, char[]> entry : co2RateCandidates.entrySet()) {
        if (entry.getValue()[col] == '0') {
          bit0Count++;
        } else {
          bit1Count++;
        }
      }
      final char leastCommonBit = bit0Count <= bit1Count ? '0' : '1';
      final int _col = col;
      co2RateCandidates =
          co2RateCandidates.entrySet().stream()
              .filter(entry -> entry.getValue()[_col] == leastCommonBit)
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      if (co2RateCandidates.size() == 1) {
        break;
      }
    }
    int co2Rate =
        Integer.parseInt(new String(co2RateCandidates.values().toArray(char[][]::new)[0]), 2);

    System.out.println(
        String.format(
            "Oxygen Generator Rate: %s\nCO2 Scrubber Rate Rate: %s\nLife Support Rate: %s",
            oxygenRate, co2Rate, oxygenRate * co2Rate));
  }
}
