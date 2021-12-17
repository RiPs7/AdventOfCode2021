package com.rips7.days;

import java.util.List;

public class Day17 extends Day<List<String>> {

  public Day17() {
    super(DaysEnum.DAY_17);
  }

  @Override
  public void part1(final List<String> args) {
    final int[] targetArea = parseTargetArea(args.get(0));

    int totalMaxYPos = 0;
    for (int xVel = 1; xVel <= targetArea[0]; xVel++) {
      for (int yVel = 1; yVel <= targetArea[0]; yVel++) {
        final Integer currentMaxYPos = shootTowardsTargetArea(xVel, yVel, targetArea);
        if (currentMaxYPos != null && currentMaxYPos > totalMaxYPos) {
          totalMaxYPos = currentMaxYPos;
        }
      }
    }

    System.out.println(
        String.format(
            "The highest y position the probe reaches on its trajectory is %s.", totalMaxYPos));
  }

  @Override
  public void part2(final List<String> args) {
    final int[] targetArea = parseTargetArea(args.get(0));

    int count = 0;
    for (int xVel = 1; xVel <= targetArea[1]; xVel++) {
      for (int yVel = -targetArea[0]; yVel <= targetArea[1]; yVel++) {
        if (shootTowardsTargetArea(xVel, yVel, targetArea) != null) {
          count++;
        }
      }
    }

    System.out.println(
        String.format(
            "There are %s distinct initial velocity values that cause the probe to be within the target area after any step.",
            count));
  }

  private int[] parseTargetArea(final String line) {
    final String[] xyLimits = line.replace("target area: ", "").split(", ");
    final String[] xLimits = xyLimits[0].split("=")[1].split("\\.\\.");
    final String[] yLimits = xyLimits[1].split("=")[1].split("\\.\\.");
    return new int[] {
      Integer.parseInt(xLimits[0]),
      Integer.parseInt(xLimits[1]),
      Integer.parseInt(yLimits[0]),
      Integer.parseInt(yLimits[1]),
    };
  }

  private Integer shootTowardsTargetArea(int xVel, int yVel, int[] areaLimits) {
    final Probe probe = new Probe(xVel, yVel);
    int maxYPos = -1;
    while (true) {
      probe.update();
      if (probe.pos.y > maxYPos) {
        maxYPos = probe.pos.y;
      }
      if (probe.isWithinAreaLimits(areaLimits)) {
        return maxYPos;
      }
      if (probe.pos.y < areaLimits[2] || probe.pos.x > areaLimits[1]) { // overshot
        return null;
      }
    }
  }

  private static final class Probe {
    private final Vector pos;
    private final Vector vel;

    public Probe(final int xVel, final int yVel) {
      pos = new Vector(0, 0);
      vel = new Vector(xVel, yVel);
    }

    void update() {
      pos.x += vel.x;
      pos.y += vel.y;
      vel.x += Integer.compare(0, vel.x);
      vel.y += -1;
    }

    boolean isWithinAreaLimits(final int[] areaLimits) {
      return pos.x >= areaLimits[0]
          && pos.x <= areaLimits[1]
          && pos.y >= areaLimits[2]
          && pos.y <= areaLimits[3];
    }

    private static final class Vector {
      int x, y;

      public Vector(final int x, final int y) {
        this.x = x;
        this.y = y;
      }
    }
  }
}
