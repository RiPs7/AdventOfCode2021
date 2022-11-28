package com.rips7.days;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

// Cribbed and refactored from
// https://github.com/akaritakai/AdventOfCode2021/blob/main/src/main/java/net/akaritakai/aoc2021/Puzzle19.java
public class Day19 extends Day<String> {

  public Day19() {
    super(DaysEnum.DAY_19);
  }

  @Override
  public void part1(final String input) {
    final List<Scanner> scanners = readScanners(input);
    solve(scanners);
    System.out.println(String.format("There are %s beacons in total", scanners.get(0).beacons.size()));
  }

  @Override
  public void part2(final String input) {
    final List<Scanner> scanners = readScanners(input);
    solve(scanners);

    final int maxDistance =
        scanners.stream()
            .map(
                first ->
                    scanners.stream()
                        .filter(second -> !first.equals(second))
                        .map(second -> first.position.distance(second.position))
                        .max(Integer::compareTo)
                        .orElse(0))
            .max(Integer::compareTo)
            .orElse(0);
    System.out.println(String.format("The largest Manhattan distance between any two scanners is %s", maxDistance));
  }

  private List<Scanner> readScanners(final String input) {
    return Arrays.stream(input.split("\n\n")).map(Scanner::new).collect(Collectors.toList());
  }

  private void solve(final List<Scanner> scanners) {
    scanners.get(0).position = new Point(0, 0, 0);
    final List<Scanner> unsolved = new ArrayList<>(scanners.subList(1, scanners.size()));
    while (unsolved.size() > 0) {
      unsolved.removeIf(scanner -> findTranslation(scanners, scanner));
    }
  }

  private boolean findTranslation(final List<Scanner> scanners, Scanner scanner) {
    for (final UnaryOperator<Point> rotation : ROTATIONS) {
      final Set<Point> translations = scanner.beacons.stream().map(rotation).collect(Collectors.toSet());
      final Map<Point, Integer> diffs = new HashMap<>();
      for (var point1 : scanners.get(0).beacons) {
        for (var point2 : translations) {
          diffs.merge(point1.sub(point2), 1, Integer::sum);
        }
      }

      final boolean updated = diffs.entrySet().stream()
          .filter(e -> e.getValue() >= 12)
          .map(Map.Entry::getKey)
          .findAny()
          .map(diff -> {
            scanner.position = diff;
            translations.forEach(point -> scanners.get(0).beacons.add(point.add(diff)));
            return true;
          })
          .orElse(false);
      if (updated) {
        return true;
      }
    }
    return false;
  }

  private static class Scanner {
    private final Set<Point> beacons;
    private Point position;

    private Scanner(String scannerLines) {
      beacons =
          Arrays.stream(scannerLines.split("\n"))
              .skip(1)
              .map(
                  line ->
                      Arrays.stream(line.split(",")).map(Integer::parseInt).toArray(Integer[]::new))
              .map(coords -> new Point(coords[0], coords[1], coords[2]))
              .collect(Collectors.toSet());
    }
  }

  private record Point(int x, int y, int z) {
    private Point add(final Point other) {
      return new Point(x + other.x, y + other.y, z + other.z);
    }

    private Point sub(final Point other) {
      return new Point(x - other.x, y - other.y, z - other.z);
    }

    private int distance(final Point other) {
      return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
    }
  }

  @SuppressWarnings("SuspiciousNameCombination")
  private static final List<UnaryOperator<Point>> ROTATIONS = List.of(
      p -> new Point(-p.x, -p.y, p.z),
      p -> new Point(-p.x, -p.z, -p.y),
      p -> new Point(-p.x, p.y, -p.z),
      p -> new Point(-p.x, p.z, p.y),
      p -> new Point(-p.y, -p.x, -p.z),
      p -> new Point(-p.y, -p.z, p.x),
      p -> new Point(-p.y, p.x, p.z),
      p -> new Point(-p.y, p.z, -p.x),
      p -> new Point(-p.z, -p.x, p.y),
      p -> new Point(-p.z, -p.y, -p.x),
      p -> new Point(-p.z, p.x, -p.y),
      p -> new Point(-p.z, p.y, p.x),
      p -> new Point(p.x, -p.y, -p.z),
      p -> new Point(p.x, -p.z, p.y),
      p -> new Point(p.x, p.y, p.z),
      p -> new Point(p.x, p.y, p.z),
      p -> new Point(p.x, p.z, -p.y),
      p -> new Point(p.y, -p.x, p.z),
      p -> new Point(p.y, -p.z, -p.x),
      p -> new Point(p.y, p.x, -p.z),
      p -> new Point(p.y, p.z, p.x),
      p -> new Point(p.z, -p.x, -p.y),
      p -> new Point(p.z, -p.y, p.x),
      p -> new Point(p.z, p.x, p.y),
      p -> new Point(p.z, p.y, -p.x)
  );
}
