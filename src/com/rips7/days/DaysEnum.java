package com.rips7.days;

import com.rips7.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public enum DaysEnum {
  DAY_1(Day1.class, "day1/", "example1and2", "part1and2", Utils::readLinesAsInt),
  DAY_2(Day2.class, "day2/", "example1and2", "part1and2", Utils::readLines),
  DAY_3(Day3.class, "day3/", "example1and2", "part1and2", Utils::readLines),
  DAY_4(Day4.class, "day4/", "example1and2", "part1and2", Utils::readLines),
  DAY_5(Day5.class, "day5/", "example1and2", "part1and2", Utils::readLines),
  DAY_6(Day6.class, "day6/", "example1and2", "part1and2", Utils::readLines),
  DAY_7(Day7.class, "day7/", "example1and2", "part1and2", Utils::readLines),
  DAY_8(Day8.class, "day8/", "example1and2", "part1and2", Utils::readLines),
  DAY_9(Day9.class, "day9/", "example1and2", "part1and2", Utils::readLines),
  DAY_10(Day10.class, "day10/", "example1and2", "part1and2", Utils::readLines),
  DAY_11(Day11.class, "day11/", "example1and2", "part1and2", Utils::readLines),
  DAY_12(Day12.class, "day12/", "example1and2", "part1and2", Utils::readLines),
  DAY_13(Day13.class, "day13/", "example1and2", "part1and2", Utils::readLines),
  DAY_14(Day14.class, "day14/", "example1and2", "part1and2", Utils::readLines),
  DAY_15(Day15.class, "day15/", "example1and2", "part1and2", Utils::readLines),
  DAY_16(Day16.class, "day16/", "example1and2", "part1and2", Utils::readLines),
  DAY_17(Day17.class, "day17/", "example1and2", "part1and2", Utils::readLines),
  DAY_18(Day18.class, "day18/", "example1and2", "part1and2", Utils::readLines),
  DAY_19(Day19.class, "day19/", "example1and2", "part1and2", Utils::readInput),
  DAY_20(Day20.class, "day20/", "example1and2", "part1and2", Utils::readLines),
  DAY_21(Day21.class, "day21/", "example1and2", "part1and2", Utils::readLines),
  DAY_22(Day22.class, "day22/", "example1", "example2", "part1and2", Utils::readLines),
  DAY_23(Day23.class, "day23/", "example1", "example2", "part1", "part2", Utils::readLines),
  DAY_24(Day24.class, "day24/", "example1and2", "part1and2", Utils::readLines);

  private final Class<? extends Day<?>> dayClass;

  private final String example1FileName;

  private final String example2FileName;

  private final String part1FileName;

  private final String part2FileName;

  private final Function<String, ?> argumentSupplier;

  DaysEnum(
      final Class<? extends Day<?>> dayClass,
      final String resourceDirectory,
      final String example1and2FileName,
      final String part1and2FileName,
      final Function<String, ?> argumentSupplier) {
    this(
        dayClass,
        resourceDirectory,
        example1and2FileName,
        example1and2FileName,
        part1and2FileName,
        part1and2FileName,
        argumentSupplier);
  }

  DaysEnum(
      final Class<? extends Day<?>> dayClass,
      final String resourceDirectory,
      final String example1and2FileName,
      final String part1FileName,
      final String part2FileName,
      final Function<String, ?> argumentSupplier) {
    this(
        dayClass,
        resourceDirectory,
        example1and2FileName,
        example1and2FileName,
        part1FileName,
        part2FileName,
        argumentSupplier);
  }

  DaysEnum(
      final Class<? extends Day<?>> dayClass,
      final String resourceDirectory,
      final String example1FileName,
      final String example2FileName,
      final String part1FileName,
      final String part2FileName,
      final Function<String, ?> argumentSupplier) {
    this.dayClass = dayClass;
    this.example1FileName = resourceDirectory + example1FileName;
    this.example2FileName = resourceDirectory + example2FileName;
    this.part1FileName = resourceDirectory + part1FileName;
    this.part2FileName = resourceDirectory + part2FileName;
    this.argumentSupplier = argumentSupplier;
  }

  public String getExample1FileName() {
    return example1FileName;
  }

  public String getExample2FileName() {
    return example2FileName;
  }

  public String getPart1FileName() {
    return part1FileName;
  }

  public String getPart2FileName() {
    return part2FileName;
  }

  public Function<String, ?> getArgumentSupplier() {
    return argumentSupplier;
  }

  @SuppressWarnings("ConstantConditions")
  public void run() {
    final String ANSI_YELLOW = "\u001B[33m";
    final String ANSI_RED = "\u001B[31m";
    final String ANSI_RESET = "\u001B[0m";

    try {
      Day<?> day = dayClass.getConstructor().newInstance();

      System.out.println(
          String.format(
              ANSI_YELLOW + "\n--- Running %s ---\n" + ANSI_RESET, day.getClass().getSimpleName()));

      final boolean runExample1 = true;
      final boolean runPart1 = true;
      final boolean runExample2 = true;
      final boolean runPart2 = true;

      long time;

      if (runExample1) {
        System.out.println(ANSI_RED + "Example 1" + ANSI_RESET);
        time = System.currentTimeMillis();
        day.runExample1();
        System.out.println(
            String.format(
                ANSI_RED + "Example 1 took: %s ms\n" + ANSI_RESET,
                (System.currentTimeMillis() - time)));
      }

      if (runPart1) {
        System.out.println(ANSI_RED + "Part 1" + ANSI_RESET);
        time = System.currentTimeMillis();
        day.runPart1();
        System.out.println(
            String.format(
                ANSI_RED + "Part 1 took: %s ms\n" + ANSI_RESET,
                (System.currentTimeMillis() - time)));
      }

      if (runExample2) {
        System.out.println(ANSI_RED + "Example 2" + ANSI_RESET);
        time = System.currentTimeMillis();
        day.runExample2();
        System.out.println(
            String.format(
                ANSI_RED + "Example 2 took: %s ms\n" + ANSI_RESET,
                (System.currentTimeMillis() - time)));
      }

      if (runPart2) {
        System.out.println(ANSI_RED + "Part 2" + ANSI_RESET);
        time = System.currentTimeMillis();
        day.runPart2();
        System.out.println(
            String.format(
                ANSI_RED + "Part 2 took: %s ms\n" + ANSI_RESET,
                (System.currentTimeMillis() - time)));
      }

    } catch (NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
