package com.rips7.days;

import java.util.List;
import java.util.stream.IntStream;

public class Day1 extends Day<List<Integer>> {

  public Day1() {
    super(DaysEnum.DAY_1);
  }

  @Override
  public void part1(final List<Integer> args) {
    System.out.println(
        String.format(
            "There are %s measurements that are larger than the previous measurement.",
            IntStream.range(1, args.size())
                .boxed()
                .filter(i -> args.get(i - 1) < args.get(i))
                .count()));
  }

  @Override
  public void part2(final List<Integer> args) {
    // a[1] + a[2] + a[3] + a[4] + ... a[W] < a[2] + a[3] + a[4] + a[5] + ... + a[W] + a[W+1] =>
    // a[1] < a[W+1]
    final int window = 3;
    System.out.println(
        String.format(
            "There are %s sum measurements that are larger than the previous sum measurement, for window of value %s.",
            IntStream.range(0, args.size() - window)
                .boxed()
                .filter(i -> args.get(i) < args.get(i + window))
                .count(),
            window));
  }
}
