package com.rips7.days;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Day9 extends Day<List<String>> {

  public Day9() {
    super(DaysEnum.DAY_9);
  }

  @Override
  public void part1(final List<String> args) {
    final int[][] heights = getHeights(args);

    final AtomicInteger sumRiskLowLevels = new AtomicInteger(0);
    findLowLevelsAndOperate(
        heights, point -> sumRiskLowLevels.addAndGet(heights[point.y][point.x] + 1));

    System.out.println(
        String.format(
            "The sum of the risk levels of all low points on the heightmap is %s",
            sumRiskLowLevels));
  }

  @Override
  public void part2(final List<String> args) {
    final int[][] heights = getHeights(args);

    final List<Integer> basinSizes = new ArrayList<>();
    findLowLevelsAndOperate(
        heights, point -> basinSizes.add(getBasinSize(heights, point.y, point.x)));

    System.out.println(
        String.format(
            "The product of the three larger basins on the heightmap is %s",
            basinSizes.stream()
                .sorted(Collections.reverseOrder())
                .limit(3)
                .reduce(1, (a, b) -> a * b)));
  }

  private int[][] getHeights(final List<String> args) {
    return args.stream()
        .map(line -> Arrays.stream(line.split("")).mapToInt(Integer::parseInt).toArray())
        .toArray(int[][]::new);
  }

  private void findLowLevelsAndOperate(final int[][] heights, final Consumer<Point> consumer) {
    for (int row = 0; row < heights.length; row++) {
      for (int col = 0; col < heights[row].length; col++) {
        final int top = row - 1;
        final int right = col + 1;
        final int bottom = row + 1;
        final int left = col - 1;

        final boolean lowerThanTop = top < 0 || heights[row][col] < heights[top][col];
        final boolean lowerThanRight =
            right > heights[row].length - 1 || heights[row][col] < heights[row][right];
        final boolean lowerThanBottom =
            bottom > heights.length - 1 || heights[row][col] < heights[bottom][col];
        final boolean lowerThanLeft = left < 0 || heights[row][col] < heights[row][left];

        if (lowerThanTop && lowerThanRight && lowerThanBottom && lowerThanLeft) {
          consumer.accept(new Point(col, row));
        }
      }
    }
  }

  private int getBasinSize(final int[][] heights, final int row, final int col) {
    // Apply a DFS algorithm to find all the points in a basin.

    int basinSize = 0;
    final Stack<Point> frontier = new Stack<>();
    final Set<Point> closedSet = new HashSet<>();
    frontier.add(new Point(col, row));
    while (!frontier.isEmpty()) {
      final Point current = frontier.pop();
      if (closedSet.contains(current)) {
        continue;
      }
      basinSize++;
      if (current.y - 1 >= 0 && heights[current.y - 1][current.x] != 9) {
        frontier.add(new Point(current.x, current.y - 1));
      }
      if (current.x + 1 < heights[current.y].length && heights[current.y][current.x + 1] != 9) {
        frontier.add(new Point(current.x + 1, current.y));
      }
      if (current.y + 1 < heights.length && heights[current.y + 1][current.x] != 9) {
        frontier.add(new Point(current.x, current.y + 1));
      }
      if (current.x - 1 >= 0 && heights[current.y][current.x - 1] != 9) {
        frontier.add(new Point(current.x - 1, current.y));
      }
      closedSet.add(current);
    }

    return basinSize;
  }

  private static final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String toString() {
      return String.format("{%s,%s}", x, y);
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }

      if (!(obj instanceof Point)) {
        return false;
      }

      final Point other = (Point) obj;

      return Objects.equals(this.x, other.x) && Objects.equals(this.y, other.y);
    }
  }
}
