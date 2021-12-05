package com.rips7.days;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Day5 extends Day<List<String>> {

  public Day5() {
    super(DaysEnum.DAY_5);
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void part1(final List<String> args) {
    final List<Vent> vents = getVents(args, false);

    if (false) { // toggle to true, to print the grid of vents in the console.
      printGrid(vents);
    }

    final Set<Point> overlappingPoints = getOverlappingPoints(vents);

    System.out.println(
        String.format(
            "There are %s points where at least two lines overlap", overlappingPoints.size()));
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void part2(final List<String> args) {
    final List<Vent> vents = getVents(args, true);

    if (false) { // toggle to true, to print the grid of vents in the console.
      printGrid(vents);
    }

    final Set<Point> overlappingPoints = getOverlappingPoints(vents);

    System.out.println(
        String.format(
            "There are %s points where at least two lines overlap", overlappingPoints.size()));
  }

  private List<Vent> getVents(final List<String> input, final boolean diagonalSupport) {
    return input.stream()
        .map(
            row -> {
              final String[] parts = row.split(" -> ");
              final String[] startPointParts = parts[0].split(",");
              final Point startPoint =
                  new Point(
                      Integer.parseInt(startPointParts[0]), Integer.parseInt(startPointParts[1]));
              final String[] endPointParts = parts[1].split(",");
              final Point endPoint =
                  new Point(Integer.parseInt(endPointParts[0]), Integer.parseInt(endPointParts[1]));
              try {
                return new Vent(startPoint, endPoint, diagonalSupport);
              } catch (final NonHorizontalOrVerticalException nhove) {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private void printGrid(final List<Vent> vents) {
    final Set<Point> allPoints =
        vents.stream().map(Vent::getPoints).flatMap(Collection::stream).collect(Collectors.toSet());
    final int width = allPoints.stream().map(Point::getX).max(Integer::compareTo).orElse(0);
    final int height = allPoints.stream().map(Point::getY).max(Integer::compareTo).orElse(0);
    new Grid(width + 1, height + 1, vents).print();
  }

  private Set<Point> getOverlappingPoints(final List<Vent> vents) {
    final Set<Point> uniquePoints = new HashSet<>();
    return vents.stream()
        .map(Vent::getPoints)
        .flatMap(Collection::stream)
        .filter(pt -> !uniquePoints.add(pt))
        .collect(Collectors.toSet());
  }

  private static final class Grid {
    private final int width;
    private final int height;
    private final Map<Integer, Map<Integer, Integer>> frequencies;

    public Grid(final int width, final int height, final List<Vent> vents) {
      this.width = width;
      this.height = height;
      frequencies = new HashMap<>();
      for (final Vent vent : vents) {
        for (final Point point : vent.getPoints()) {
          frequencies.computeIfAbsent(point.x, k -> new HashMap<>());
          frequencies.get(point.x).merge(point.y, 1, Integer::sum);
        }
      }
    }

    private void print() {
      for (int i = 0; i < height; i++) {
        final StringBuilder row = new StringBuilder();
        for (int j = 0; j < width; j++) {
          final Map<Integer, Integer> xFrequencies = frequencies.get(j);
          if (xFrequencies == null) {
            row.append(". ");
            continue;
          }
          final Integer xyFrequency = xFrequencies.get(i);
          row.append(xyFrequency == null ? ". " : xyFrequency + " ");
        }
        System.out.println(row.toString().trim());
      }
    }
  }

  private static final class Vent {
    private final List<Point> points;

    public Vent(final Point start, final Point end, final boolean diagonalSupport) {
      points = new ArrayList<>();
      if (start.x == end.x) {
        final int min = Math.min(start.y, end.y);
        final int max = Math.max(start.y, end.y);
        for (int i = min; i <= max; i++) {
          points.add(new Point(start.x, i));
        }
      } else if (start.y == end.y) {
        final int min = Math.min(start.x, end.x);
        final int max = Math.max(start.x, end.x);
        for (int i = min; i <= max; i++) {
          points.add(new Point(i, start.y));
        }
      } else {
        if (!diagonalSupport) {
          throw new NonHorizontalOrVerticalException(
              String.format(
                  "Given points are neither horizontal nor vertical {%s,%s} - {%s,%s}",
                  start.x, start.y, end.x, end.y));
        } else {
          final int minX = Math.min(start.x, end.x);
          final int maxX = Math.max(start.x, end.x);
          final int minY = minX == start.x ? start.y : end.y;
          int yOffset = 0;
          for (int i = minX; i <= maxX; i++) {
            points.add(new Point(i, minY + yOffset));
            if (start.y < end.y) {
              if (start.x < end.x) {
                yOffset += 1;
              } else {
                yOffset += -1;
              }
            } else {
              if (start.x < end.x) {
                yOffset += -1;
              } else {
                yOffset += 1;
              }
            }
          }
        }
      }
    }

    public List<Point> getPoints() {
      return points;
    }
  }

  private static final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
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

  private static final class NonHorizontalOrVerticalException extends RuntimeException {
    public NonHorizontalOrVerticalException(final String message) {
      super(message);
    }
  }
}
