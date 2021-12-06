package com.rips7.days;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day6 extends Day<List<String>> {

  public Day6() {
    super(DaysEnum.DAY_6);
  }

  @Override
  public void part1(final List<String> args) {
    final List<Integer> fish =
        Arrays.stream(args.get(0).split(","))
            .map(Integer::parseInt)
            .collect(Collectors.toList());

    final int days = 80;
    long count = simulate(fish, days);

    System.out.println(
        String.format("After %s days, there will be %s lanternfish", days, count));
  }

  @Override
  public void part2(final List<String> args) {
    final List<Integer> fish =
        Arrays.stream(args.get(0).split(","))
            .map(Integer::parseInt)
            .collect(Collectors.toList());

    final int days = 256;
    long count = simulate(fish, days);

    System.out.println(
        String.format("After %s days, there will be %s lanternfish", days, count));
  }

  private long simulate(final List<Integer> fish, final int days) {
    // Keeps track of the different times-to-live and the count of how many
    // fish are on an individual time.
    Map<Integer, Long> timeToLiveToCount = new HashMap<>();
    for (final Integer f : fish) {
      // If none exists already, puts 1, otherwise increments by 1.
      timeToLiveToCount.merge(f, 1L, (a, b) -> a + 1);
    }

    for (int day = 0; day < days; day++) {
      // Create a new map so that we don't alter the main one as we process it.
      final Map<Integer, Long> newTimesToLiveCount = new HashMap<>();
      timeToLiveToCount.forEach((f, count) -> {
        if (f == 0) {
          // If no 6 TTL exists, adds the current count, otherwise increases old count by count.
          newTimesToLiveCount.merge(6, count, (a, b) -> a + count);
          // If no 8 TTL exists, adds the current count, otherwise increases old count by count.
          newTimesToLiveCount.merge(8, count, (a, b) -> a + count);
        } else {
          // Transfers and adds the current count to the count of one TTL lower.
          newTimesToLiveCount.merge(f - 1, count, (a, b) -> a + count);
        }
      });
      // Updates the time main map.
      timeToLiveToCount = newTimesToLiveCount;
    }

    // Sums all the counts from all different times-to-live.
    return timeToLiveToCount.values().stream().mapToLong(Long::longValue).sum();
  }
}