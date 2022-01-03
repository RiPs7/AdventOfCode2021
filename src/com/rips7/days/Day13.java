package com.rips7.days;

import com.rips7.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day13 extends Day<List<String>> {

  public Day13() {
    super(DaysEnum.DAY_13);
  }

  @Override
  public void part1(final List<String> args) {
    final List<Point> points = getPoints(args);
    final List<String> folds = getFolds(args);

    final int maxX = points.stream().map(pt -> pt.x).max(Integer::compareTo).orElse(0);
    final int maxY = points.stream().map(pt -> pt.y).max(Integer::compareTo).orElse(0);

    final Paper paper = new Paper(maxX + 1, maxY + 1, points);

    paper.fold(folds.get(0));
    final int visiblePoints = paper.countPoints();
    System.out.println(String.format("There %s visible points after completing the first fold instruction", visiblePoints));
  }

  @Override
  public void part2(final List<String> args) {

    final List<Point> points = getPoints(args);
    final List<String> folds = getFolds(args);

    final int maxX = points.stream().map(pt -> pt.x).max(Integer::compareTo).orElse(0);
    final int maxY = points.stream().map(pt -> pt.y).max(Integer::compareTo).orElse(0);

    final Paper paper = new Paper(maxX + 1, maxY + 1, points);

    folds.forEach(paper::fold);
    // Final result was reversed, so it had to be flipped on the vertical axis.
    paper.flipVertical();
    paper.print();
  }

  private List<Point> getPoints(final List<String> args) {
    return args.stream()
        .filter(line -> line.contains(","))
        .map(line -> Arrays.stream(line.split(",")).map(Integer::parseInt).toArray(Integer[]::new))
        .map(parts -> new Point(parts[0], parts[1]))
        .collect(Collectors.toList());
  }

  private List<String> getFolds(final List<String> args) {
    return args.stream()
        .filter(line -> line.contains("fold"))
        .map(line -> line.replace("fold along ", ""))
        .collect(Collectors.toList());
  }

  private static final class Point {
    private final int x;
    private final int y;

    public Point(final int x, final int y) {
      this.x = x;
      this.y = y;
    }
  }

  private static final class Paper {
    private int width;
    private int height;
    private Character[][] points;

    public Paper(final int width, final int height, final List<Point> points) {
      this.width = width;
      this.height = height;
      this.points = new Character[height][width];
      Utils.loopThroughAndDo(this.points, (row, col) -> this.points[row][col] = '.');
      points.forEach(pt -> this.points[pt.y][pt.x] = '#');
    }

    void print() {
      Utils.printArray(points, " ");
      System.out.println();
    }

    void fold(final String fold) {
      final String[] foldInfo = fold.split("=");
      switch (foldInfo[0]) {
        case "x":
          foldLeft(Integer.parseInt(foldInfo[1]));
          break;
        case "y":
          foldUp(Integer.parseInt(foldInfo[1]));
          break;
      }
    }

    private void foldLeft(final int x) {
      final Character[][] newPoints;
      if (width - x - 1 >= x) { // right side is larger
        newPoints = new Character[height][width - x - 1];
        // Copy right part
        for (int row = 0; row < height; row++) {
          for (int col = x + 1; col < width; col++) {
            final int mappedCol = col - x - 1;
            newPoints[row][mappedCol] = points[row][col];
          }
        }
        // Copy left part
        for (int row = 0; row < height; row++) {
          for (int col = x - 1; col >= 0; col--) {
            final int mappedCol = x - col - 1;
            if (newPoints[row][mappedCol] != '#') {
              newPoints[row][mappedCol] = points[row][col];
            }
          }
        }
      } else { // left side is larger
        newPoints = new Character[height][x];
        // Copy left part
        for (int row = 0; row < height; row++) {
          for (int col = 0; col < width; col++) {
            final int mappedCol = x - col - 1;
            newPoints[row][mappedCol] = points[row][col];
          }
        }
        // Copy right part
        for (int row = 0; row < height; row++) {
          for (int col = x + 1; col < width; col++) {
            final int mappedCol = 2 * x - col;
            if (newPoints[row][mappedCol] != '#') {
              newPoints[mappedCol][col] = points[row][col];
            }
          }
        }
      }

      this.width = Math.max(x, width - x - 1);
      this.points = newPoints;
    }

    private void foldUp(final int y) {
      final Character[][] newPoints;
      if (y >= height - y - 1) { // top side is larger
        newPoints = new Character[y][width];
        // Copy top part
        for (int row = 0; row < y; row++) {
          if (width >= 0) {
            System.arraycopy(points[row], 0, newPoints[row], 0, width);
          }
        }
        // Copy bottom part
        for (int row = height - 1; row > y; row--) {
          final int mappedRow = 2 * y - row;
          for (int col = 0; col < width; col++) {
            if (newPoints[mappedRow][col] != '#') {
              newPoints[mappedRow][col] = points[row][col];
            }
          }
        }
      } else { // bottom side is larger
        newPoints = new Character[height - y - 1][width];
        // Copy bottom part
        for (int row = height - 1; row > y; row--) {
          final int mappedRow = height - 1 - row;
          if (width >= 0) {
            System.arraycopy(points[row], 0, newPoints[mappedRow], 0, width);
          }
        }
        // Copy top part
        for (int row = y - 1; row >= 0; row--) {
          final int mappedRow = y - 1 - row;
          for (int col = 0; col < width; col++) {
            if (newPoints[mappedRow][col] != '#') {
              newPoints[mappedRow][col] = points[row][col];
            }
          }
        }
      }

      this.height = Math.max(y, height - y - 1);
      this.points = newPoints;
    }

    public void flipVertical() {
      final Character[][] newPoints = new Character[height][width];
      Utils.loopThroughAndDo(points, (row, col) -> newPoints[row][width - col - 1] = points[row][col]);
      this.points = newPoints;
    }

    public int countPoints() {
      final AtomicInteger visiblePoints = new AtomicInteger(0);
      Utils.loopThroughAndDo(this.points, (row, col) -> {
        if (this.points[row][col] == '#') {
          visiblePoints.incrementAndGet();
        }
      });
      return visiblePoints.get();
    }
  }
}
