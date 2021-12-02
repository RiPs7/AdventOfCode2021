package com.rips7.days;

import java.util.function.Function;

/**
 * A base {@code Day} to ensure type safety and abstract away the logic of executing all the
 * different parts/examples.
 *
 * @param <ARG_T> The type of arguments that supplied to the implementation of the two parts as read
 *     from the resource files (usually a list of integers or strings).
 */
public abstract class Day<ARG_T> {

  final String exampleFileName;
  final String part1FileName;
  final String part2FileName;

  final Function<String, ?> argumentSupplier;

  Day(final DaysEnum daysEnum) {
    this.exampleFileName = daysEnum.getExampleFileName();
    this.part1FileName = daysEnum.getPart1FileName();
    this.part2FileName = daysEnum.getPart2FileName();
    this.argumentSupplier = daysEnum.getArgumentSupplier();
  }

  /**
   * The implementation of this method is the actual solution to Part 1.
   *
   * @param args any args that may be needed
   */
  public abstract void part1(final ARG_T args);

  /**
   * The implementation of this method is the actual solution to Part 2.
   *
   * @param args any args that may be needed
   */
  public abstract void part2(final ARG_T args);

  /** Runs the first example with the implementation of Part 1. */
  @SuppressWarnings("unchecked")
  public void runExample1() {
    part1((ARG_T) argumentSupplier.apply(exampleFileName));
  }

  /** Runs Part 1 with the implementation of Part 1. */
  @SuppressWarnings("unchecked")
  public void runPart1() {
    part1((ARG_T) argumentSupplier.apply(part1FileName));
  }

  /** Runs the second example with the implementation of Part 2. */
  @SuppressWarnings("unchecked")
  public void runExample2() {
    part2((ARG_T) argumentSupplier.apply(exampleFileName));
  }

  /** Runs Part 2 with the implementation of Part 2. */
  @SuppressWarnings("unchecked")
  public void runPart2() {
    part2((ARG_T) argumentSupplier.apply(part1FileName));
  }
}
