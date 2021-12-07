package com.rips7.days;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 extends Day<List<String>> {

  public Day7() {
    super(DaysEnum.DAY_7);
  }

  @Override
  public void part1(final List<String> args) {
    final List<Integer> positions =
        Arrays.stream(args.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());

    final Integer[] result =
        findMinTotalFuelAndOptimalPosition(
            positions, (pos, targetPos) -> Math.abs(pos - targetPos));
    final int minTotalFuel = result[0];
    final int optimalPosition = result[1];

    System.out.println(
        String.format(
            "%s total fuel will be required to align to position %s",
            minTotalFuel, optimalPosition));
  }

  @Override
  public void part2(final List<String> args) {
    final List<Integer> positions =
        Arrays.stream(args.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());

    final Integer[] result =
        findMinTotalFuelAndOptimalPosition(
            positions,
            (pos, targetPos) -> {
              final int positionDiff = Math.abs(pos - targetPos);
              return positionDiff * (positionDiff + 1) / 2;
            });
    final int minTotalFuel = result[0];
    final int optimalPosition = result[1];

    System.out.println(
        String.format(
            "%s total fuel will be required to align to position %s",
            minTotalFuel, optimalPosition));
  }

  private Integer[] findMinTotalFuelAndOptimalPosition(
      final List<Integer> positions, BiFunction<Integer, Integer, Integer> fuelCalculation) {
    final Integer minPosition = positions.stream().min(Integer::compareTo).orElse(0);
    final int maxPosition = positions.stream().max(Integer::compareTo).orElse(0);

    Integer minTotalFuel = Integer.MAX_VALUE;
    Integer optimalPosition = null;
    for (int targetPosition = minPosition; targetPosition < maxPosition; targetPosition++) {
      final int targetPos =
          targetPosition; // making it final so it can be used in lambda expression
      final Integer currentTotalFuel =
          positions.stream()
              .map(pos -> fuelCalculation.apply(pos, targetPos))
              .reduce(0, Integer::sum);
      if (currentTotalFuel < minTotalFuel) {
        minTotalFuel = currentTotalFuel;
        optimalPosition = targetPosition;
      }
    }
    return Stream.of(minTotalFuel, optimalPosition).toArray(Integer[]::new);
  }
}
