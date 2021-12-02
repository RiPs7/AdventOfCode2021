package com.rips7.days;

import java.util.List;

public class Day2 extends Day<List<String>> {

  public Day2() {
    super(DaysEnum.DAY_2);
  }

  @Override
  public void part1(List<String> args) {
    final Position position = new Position(0, 0);
    args.forEach(
        (fullCommand) -> {
          final String[] parts = fullCommand.split(" ");
          final String command = parts[0];
          final int value = Integer.parseInt(parts[1]);
          switch (command) {
            case "forward":
              position.x += value;
              break;
            case "up":
              position.y -= value;
              break;
            case "down":
              position.y += value;
              break;
            default:
              throw new RuntimeException(String.format("Unsupported command found %s", command));
          }
        });
    System.out.println(
        String.format(
            "After following these instructions, you would have a horizontal position of %s and a depth of %s. (Multiplying these together produces %s.)",
            position.x, position.y, position.x * position.y));
  }

  @Override
  public void part2(List<String> args) {
    final PositionWithAim positionWithAim = new PositionWithAim(0, 0, 0);
    args.forEach(
        (fullCommand) -> {
          final String[] parts = fullCommand.split(" ");
          final String command = parts[0];
          final int value = Integer.parseInt(parts[1]);
          switch (command) {
            case "forward":
              positionWithAim.x += value;
              positionWithAim.y += value * positionWithAim.aim;
              break;
            case "up":
              positionWithAim.aim -= value;
              break;
            case "down":
              positionWithAim.aim += value;
              break;
            default:
              throw new RuntimeException(String.format("Unsupported command found %s", command));
          }
        });
    System.out.println(
        String.format(
            "After following these instructions, you would have a horizontal position of %s and a depth of %s. (Multiplying these together produces %s.)",
            positionWithAim.x, positionWithAim.y, positionWithAim.x * positionWithAim.y));
  }

  private static final class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  private static final class PositionWithAim {
    private int x;
    private int y;
    private int aim;

    public PositionWithAim(int x, int y, int aim) {
      this.x = x;
      this.y = y;
      this.aim = aim;
    }
  }
}
