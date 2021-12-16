package com.rips7.days;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Day15 extends Day<List<String>> {

  public Day15() {
    super(DaysEnum.DAY_15);
  }

  @Override
  public void part1(final List<String> args) {
    final Integer[][] levels = getLevels(args);

    // Example and part 1 happened to be correct with only right-down moves.
    final Integer minScore = minScorePathRightDown(levels);
    System.out.println(
        String.format(
            "The lowest total risk of any path from the top left to the bottom right is %s",
            minScore));
  }

  @Override
  public void part2(final List<String> args) {
    final Integer[][] levels = getLevels(args);

    final Integer[][] allLevels = new Integer[levels.length * 5][levels[0].length * 5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        for (int row = 0; row < levels.length; row++) {
          for (int col = 0; col < levels[0].length; col++) {
            final int mappedRow = i * levels.length + row;
            final int mappedCol = j * levels[row].length + col;
            final int newLevel = levels[row][col] + (i + j);
            allLevels[mappedRow][mappedCol] = newLevel > 9 ? newLevel - 9 : newLevel;
          }
        }
      }
    }

    // Part 2 was only correct with right-down-left-up moves.
    final Integer minScore = minScorePathRightDownLeftUp(allLevels);
    System.out.println(
        String.format(
            "The lowest total risk of any path from the top left to the bottom right for the full map is %s",
            minScore));
  }

  private Integer[][] getLevels(final List<String> args) {
    return args.stream()
        .map(line -> line.split(""))
        .map(row -> Arrays.stream(row).map(Integer::parseInt).toArray(Integer[]::new))
        .toArray(Integer[][]::new);
  }

  private Integer minScorePathRightDown(final Integer[][] levels) {
    // Dynamic programming
    // The min score(i,j) is the min of score(i-1,j) and score(i,j-1), plus the level(i,j).
    final Integer[][] scores = new Integer[levels.length][levels[0].length];
    // Top-left score is 0
    scores[0][0] = 0;
    // Calculate first column scores
    for (int row = 1; row < levels.length; row++) {
      scores[row][0] = scores[row - 1][0] + levels[row][0];
    }
    // Calculate first row scores
    for (int col = 1; col < levels[0].length; col++) {
      scores[0][col] = scores[0][col - 1] + levels[0][col];
    }
    // Apply min score formula for the rest of the elements
    for (int row = 1; row < levels.length; row++) {
      for (int col = 1; col < levels[row].length; col++) {
        scores[row][col] = Math.min(scores[row - 1][col], scores[row][col - 1]) + levels[row][col];
      }
    }
    // Final score is bottom-right element
    return scores[scores.length - 1][scores[scores.length - 1].length - 1];
  }

  private Integer minScorePathRightDownLeftUp(final Integer[][] levels) {
    // Dijkstra implementation
    // Consider the given matrix as an adjacency matrix, with the levels representing the costs of
    // the corresponding edges on a graph

    // Initialize all scores with MAX_VALUE (and initial score according to problem definition)
    final Integer[][] scores = new Integer[levels.length][levels[0].length];
    for (int row = 0; row < levels.length; row++) {
      for (int col = 0; col < levels[row].length; col++) {
        scores[row][col] = Integer.MAX_VALUE;
      }
    }
    scores[0][0] = 0;

    // Priority queue to store all the grid elements sorted by their score.
    final Queue<Cell> queue = new PriorityQueue<>(levels.length * levels[0].length, Cell::compare);
    // Add the first grid element
    queue.add(new Cell(0, 0, scores[0][0]));

    // While the queue is not empty
    while (!queue.isEmpty()) {
      // Get the smallest score element
      final Cell current = queue.poll();
      // For all its adjacent cells:
      final int[] rowOffset = {0, 1, 0, -1};
      final int[] colOffset = {1, 0, -1, 0};
      for (int i = 0; i < rowOffset.length; i++) {
        final int newRow = current.x + rowOffset[i];
        final int newCol = current.y + colOffset[i];
        // If they are off the grid, skip.
        if (newRow < 0
            || newRow > levels.length - 1
            || newCol < 0
            || newCol > levels[0].length - 1) {
          continue;
        }
        // If their score is already less than the score to get there from the current position,
        // skip
        if (scores[newRow][newCol] <= scores[current.x][current.y] + levels[newRow][newCol]) {
          continue;
        }
        // If they have been visited before (score is not MAX_VALUE)
        if (scores[newRow][newCol] != Integer.MAX_VALUE) {
          // remove them from the priority queue.
          queue.remove(new Cell(newRow, newCol, scores[newRow][newCol]));
        }
        // Update their score, as the sum of the current element score plus the cost to get to them
        // from the current element
        scores[newRow][newCol] = scores[current.x][current.y] + levels[newRow][newCol];
        // Add them to the priority queue
        queue.add(new Cell(newRow, newCol, scores[newRow][newCol]));
      }
    }
    // In the end, the calculated minimum score is stored at the bottom-right position.
    return scores[levels.length - 1][levels[0].length - 1];
  }

  private static final class Cell {
    int x;
    int y;
    int score;

    Cell(final int x, final int y, final int score) {
      this.x = x;
      this.y = y;
      this.score = score;
    }

    static int compare(final Cell a, final Cell b) {
      return Integer.compare(a.score, b.score);
    }
  }
}
