package com.rips7.days;

import com.rips7.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Day11 extends Day<List<String>> {

  public Day11() {
    super(DaysEnum.DAY_11);
  }

  @Override
  public void part1(final List<String> args) {
    final Integer[][] energyLevels =
        args.stream()
            .map(
                line ->
                    Arrays.stream(line.split("")).map(Integer::parseInt).toArray(Integer[]::new))
            .toArray(Integer[][]::new);

    final int steps = 100;
    final int totalFlashes = processEnergyLevelsForSteps(energyLevels, steps);
    System.out.println(
        String.format("After %s steps, there were a total of %s flashes", steps, totalFlashes));
  }

  @Override
  public void part2(final List<String> args) {
    final Integer[][] energyLevels =
        args.stream()
            .map(
                line ->
                    Arrays.stream(line.split("")).map(Integer::parseInt).toArray(Integer[]::new))
            .toArray(Integer[][]::new);

    final int step = processEnergyLevelsUntilAllFlash(energyLevels);
    System.out.println(String.format("After %s steps, all the octapuses flash.", step));
  }

  @SuppressWarnings("SameParameterValue")
  private int processEnergyLevelsForSteps(final Integer[][] energyLevels, final int steps) {
    // AtomicInteger keeps track of the total flashes. Keep it as an AtomicInteger to increase the
    // value of the reference object.
    final AtomicInteger totalFlashes = new AtomicInteger(0);
    for (int step = 0; step < steps; step++) {
      // Step through the algorithm.
      step(energyLevels, totalFlashes);
    }
    // Return the total flashes.
    return totalFlashes.get();
  }

  private int processEnergyLevelsUntilAllFlash(final Integer[][] energyLevels) {
    // Keeps track of the current step.
    int step = 0;
    while (true) {
      // Increment the current step.
      step++;
      // Step through the algorithm.
      step(energyLevels, null);
      // Assume the initial value is true/false depending on the status of the top-left element.
      final AtomicBoolean allFlashed = new AtomicBoolean(energyLevels[0][0] == 0);
      // If the top-left element has flashed check all the rest
      if (allFlashed.get()) {
        Utils.loopThroughAndDo(
            energyLevels,
            (row, col) -> {
              if (energyLevels[row][col] != 0) {
                allFlashed.set(false);
              }
            });
        // If the value is still true, return the current step.
        if (allFlashed.get()) {
          return step;
        }
      }
    }
  }

  private void step(final Integer[][] energyLevels, final AtomicInteger totalFlashes) {
    // Increment everyone by one
    Utils.loopThroughAndDo(energyLevels, (row, col) -> energyLevels[row][col]++);

    // The ones greater that 9, flash
    Utils.loopThroughAndDo(
        energyLevels,
        (row, col) -> {
          if (energyLevels[row][col] > 9) {
            flash(energyLevels, row, col, totalFlashes);
          }
        });
  }

  private void flash(
      final Integer[][] energyLevels,
      final int row,
      final int col,
      final AtomicInteger totalFlashes) {
    if (totalFlashes != null) {
      totalFlashes.incrementAndGet();
    }

    // The ones that flash, set their level to 0
    energyLevels[row][col] = 0;

    // Loop through all 8 neighbors
    for (int dRow = -1; dRow <= 1; dRow++) {
      for (int dCol = -1; dCol <= 1; dCol++) {
        // skip self
        if (dRow == 0 && dCol == 0) {
          continue;
        }

        // ensure the neighbor is in the grid
        final int newRow = row + dRow;
        final int newCol = col + dCol;
        if ((newRow < 0 || newRow == energyLevels.length)
            || (newCol < 0 || newCol == energyLevels[0].length)) {
          continue;
        }
        // if the neighbor has flashed this round, skip it
        if (energyLevels[newRow][newCol] == 0) {
          continue;
        }
        // increment its level by 1
        energyLevels[newRow][newCol] += 1;
        // if it's greater than
        if (energyLevels[newRow][newCol] > 9) {
          flash(energyLevels, newRow, newCol, totalFlashes);
        }
      }
    }
  }
}
