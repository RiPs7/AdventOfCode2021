package com.rips7.days;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day22 extends Day<List<String>> {

  public Day22() {
    super(DaysEnum.DAY_22);
  }

  @Override
  public void part1(final List<String> args) {
    final List<Map.Entry<Position, Boolean>> steps = getLimitedPositions(args);

    final boolean[][][] grid = new boolean[101][101][101];

    for (final Map.Entry<Position, Boolean> step : steps) {
      final Position pos = step.getKey();
      final Boolean onOrOff = step.getValue();
      grid[pos.x + 50][pos.y + 50][pos.z + 50] = onOrOff;
    }

    int count = 0;
    for (boolean[][] row : grid) {
      for (boolean[] column : row) {
        for (boolean onOrOff : column) {
          if (onOrOff) {
            count++;
          }
        }
      }
    }

    System.out.println(
        String.format(
            "After executing the reboot steps and considering only cubes within -50, 50 cube region, there are %s cubes on.",
            count));
  }

  @Override
  public void part2(final List<String> args) {
    // This algorithm calculates overlapping cuboid regions (partial cuboids => start off as an
    // empty list).
    final List<Map.Entry<Cuboid, Boolean>> cuboidsAndActions = getCuboidsAndActions(args);
    List<Cuboid> partialCuboids = new ArrayList<>();

    for (final Map.Entry<Cuboid, Boolean> cuboidAndAction : cuboidsAndActions) {
      // Keep track of a new list of partial cuboids
      final List<Cuboid> newPartialCuboids = new ArrayList<>();
      // Get the current cuboid and action
      final Cuboid cuboid = cuboidAndAction.getKey();
      final Boolean action = cuboidAndAction.getValue();
      // If the cuboid is on, add it to the new partial cuboids
      if (action) {
        newPartialCuboids.add(cuboid);
      }
      // For every existing partial cuboid
      for (final Cuboid partialCuboid : partialCuboids) {
        // If any of them don't overlap with the current cuboid, it's a new partial cuboid on its
        // own
        if (!cuboid.isOverlapping(partialCuboid)) {
          newPartialCuboids.add(partialCuboid);
        } else {
          // Otherwise we need to slice up the current cuboid, and adjust the overlapping
          // partial cuboids
          newPartialCuboids.addAll(cuboid.checkAndSliceAlongXAxis(partialCuboid));
          newPartialCuboids.addAll(cuboid.checkAndSliceAlongYAxis(partialCuboid));
          newPartialCuboids.addAll(cuboid.checkAndSliceAlongZAxis(partialCuboid));
        }
      }
      // Update the list of partial cuboids
      partialCuboids = newPartialCuboids;
    }

    // All the partial cuboids, are non-overlapping and active.
    // So we can add up their volumes.
    long totalVolume = 0;
    for (final Cuboid partialCuboid : partialCuboids) {
      final long xLength = Math.abs(partialCuboid.xFrom - partialCuboid.xTo - 1);
      final long yLength = Math.abs(partialCuboid.yFrom - partialCuboid.yTo - 1);
      final long zLength = Math.abs(partialCuboid.zFrom - partialCuboid.zTo - 1);
      totalVolume += (xLength * yLength * zLength);
    }

    System.out.println(
        String.format("After executing all the steps, there are %s cubes on.", totalVolume));
  }

  // Used only for Part 1 - Translates all the coordinates to individual positions; can contain up
  // to (-50..50) x (-50..50) x (-50..50) = 101 x 101 x 101 = 1030301 positions.
  private List<Map.Entry<Position, Boolean>> getLimitedPositions(final List<String> args) {
    final Function<String, List<Integer>> getCoordinates =
        coordinates -> {
          final String[] minMaxCoordinates = coordinates.split("=")[1].split("\\.\\.");
          return IntStream.range(
                  Integer.parseInt(minMaxCoordinates[0]),
                  Integer.parseInt(minMaxCoordinates[1]) + 1)
              .boxed()
              .filter(coord -> coord >= -50 && coord <= 50)
              .collect(Collectors.toList());
        };
    return args.stream()
        .map(
            line -> {
              final String[] parts = line.split(" ");
              final boolean onOrOff = parts[0].equals("on");
              final String[] coordinates = parts[1].split(",");
              final List<Integer> xCoordinates = getCoordinates.apply(coordinates[0]);
              final List<Integer> yCoordinates = getCoordinates.apply(coordinates[1]);
              final List<Integer> zCoordinates = getCoordinates.apply(coordinates[2]);
              final List<Position> positions = new ArrayList<>();
              xCoordinates.forEach(
                  x ->
                      yCoordinates.forEach(
                          y -> zCoordinates.forEach(z -> positions.add(new Position(x, y, z)))));
              return positions.stream()
                  .map(pos -> new AbstractMap.SimpleEntry<>(pos, onOrOff))
                  .collect(Collectors.toList());
            })
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  // Used only for Part 2 - Translates all the sets of coordinates to cuboids; there is no limit
  // in the amount of cuboids used.
  private List<Map.Entry<Cuboid, Boolean>> getCuboidsAndActions(final List<String> args) {
    return args.stream()
        .map(
            line -> {
              final String[] parts = line.split(" ");
              final boolean onOrOff = parts[0].equals("on");
              final Integer[] coordinates =
                  Arrays.stream(parts[1].split(","))
                      .map(system -> system.split("=")[1])
                      .map(
                          fromTo ->
                              Arrays.stream(fromTo.split("\\.\\."))
                                  .map(Integer::parseInt)
                                  .toArray(Integer[]::new))
                      .flatMap(Stream::of)
                      .toArray(Integer[]::new);
              return new AbstractMap.SimpleEntry<>(
                  new Cuboid(
                      coordinates[0],
                      coordinates[1],
                      coordinates[2],
                      coordinates[3],
                      coordinates[4],
                      coordinates[5]),
                  onOrOff);
            })
        .collect(Collectors.toList());
  }

  // Used only for Part 1 - All the individual coordinate points are translated to Position objects.
  private static final class Position {
    final int x;
    final int y;
    final int z;

    public Position(final int x, final int y, final int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
  }

  // Used only for Part 2 - All the sets for coordinates are translated to Cuboid objects.
  private static final class Cuboid {
    int xFrom;
    int xTo;
    int yFrom;
    int yTo;
    int zFrom;
    int zTo;

    public Cuboid(
        final int xFrom,
        final int xTo,
        final int yFrom,
        final int yTo,
        final int zFrom,
        final int zTo) {
      this.xFrom = xFrom;
      this.xTo = xTo;
      this.yFrom = yFrom;
      this.yTo = yTo;
      this.zFrom = zFrom;
      this.zTo = zTo;
    }

    private boolean isOverlapping(final Cuboid other) {
      return Math.max(this.xFrom, other.xFrom) <= Math.min(this.xTo, other.xTo)
          && Math.max(this.yFrom, other.yFrom) <= Math.min(this.yTo, other.yTo)
          && Math.max(this.zFrom, other.zFrom) <= Math.min(this.zTo, other.zTo);
    }

    private List<Cuboid> checkAndSliceAlongXAxis(final Cuboid other) {
      final List<Cuboid> slices = new ArrayList<>();
      if (other.xFrom < this.xFrom) { // Slice left
        slices.add(
            new Cuboid(
                other.xFrom, this.xFrom - 1, other.yFrom, other.yTo, other.zFrom, other.zTo));
        other.xFrom = this.xFrom;
      }
      if (other.xTo > this.xTo) { // Slice Right
        slices.add(
            new Cuboid(this.xTo + 1, other.xTo, other.yFrom, other.yTo, other.zFrom, other.zTo));
        other.xTo = this.xTo;
      }
      return slices;
    }

    private List<Cuboid> checkAndSliceAlongYAxis(final Cuboid other) {
      final List<Cuboid> slices = new ArrayList<>();
      if (other.yFrom < this.yFrom) { // Slice Top
        slices.add(
            new Cuboid(
                other.xFrom, other.xTo, other.yFrom, this.yFrom - 1, other.zFrom, other.zTo));
        other.yFrom = this.yFrom;
      }
      if (other.yTo > this.yTo) { // Slice Bottom
        slices.add(
            new Cuboid(other.xFrom, other.xTo, this.yTo + 1, other.yTo, other.zFrom, other.zTo));
        other.yTo = this.yTo;
      }
      return slices;
    }

    private List<Cuboid> checkAndSliceAlongZAxis(final Cuboid other) {
      final List<Cuboid> slices = new ArrayList<>();
      if (other.zFrom < this.zFrom) { // Slice Front
        slices.add(
            new Cuboid(
                other.xFrom, other.xTo, other.yFrom, other.yTo, other.zFrom, this.zFrom - 1));
        other.zFrom = this.zFrom;
      }
      if (other.zTo > this.zTo) { // Slice Back
        slices.add(
            new Cuboid(other.xFrom, other.xTo, other.yFrom, other.yTo, this.zTo + 1, other.zTo));
        other.zTo = this.zTo;
      }
      return slices;
    }
  }
}
