package com.rips7.days;

import com.rips7.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public enum DaysEnum {
  DAY_1(Day1.class, "day1/", "example1and2", "part1and2", "part1and2", Utils::readLinesAsInt),
  DAY_2(Day2.class, "day2/", "example1and2", "part1and2", "part1and2", Utils::readLines);

  private final Class<? extends Day<?>> dayClass;

  private final String exampleFileName;

  private final String part1FileName;

  private final String part2FileName;

  private final Function<String, ?> argumentSupplier;

  DaysEnum(
      final Class<? extends Day<?>> dayClass,
      final String resourceDirectory,
      final String exampleFileName,
      final String part1FileName,
      final String part2FileName,
      final Function<String, ?> argumentSupplier) {
    this.dayClass = dayClass;
    this.exampleFileName = resourceDirectory + exampleFileName;
    this.part1FileName = resourceDirectory + part1FileName;
    this.part2FileName = resourceDirectory + part2FileName;
    this.argumentSupplier = argumentSupplier;
  }

  public String getExampleFileName() {
    return exampleFileName;
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

  public void run() {
    try {
      Day<?> day = dayClass.getConstructor().newInstance();
      day.runExample1();
      day.runPart1();
      day.runExample2();
      day.runPart2();
    } catch (NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
