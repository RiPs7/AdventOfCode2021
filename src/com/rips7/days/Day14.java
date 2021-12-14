package com.rips7.days;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day14 extends Day<List<String>> {

  public Day14() {
    super(DaysEnum.DAY_14);
  }

  @Override
  public void part1(final List<String> args) {
    final String template = args.get(0);
    final Map<String, String> rules = getRules(args);

    final int steps = 10;
    final Map<Character, Long> frequencies = naivelyExpandString(template, rules, steps);

    final long frequencyDiff = getMaxMinFrequencyDiff(frequencies);

    System.out.println(
        String.format(
            "The quantity of the most common element minus the quantity of the least common element, after %s steps is: %s",
            steps, frequencyDiff));
  }

  @Override
  public void part2(final List<String> args) {
    final String template = args.get(0);
    final Map<String, String> rules = getRules(args);

    final int steps = 40;
    final Map<Character, Long> frequencies = efficientlyKeepPairCounter(template, rules, steps);

    final long frequencyDiff = getMaxMinFrequencyDiff(frequencies);

    System.out.println(
        String.format(
            "The quantity of the most common element minus the quantity of the least common element, after %s steps is: %s",
            steps, frequencyDiff));
  }

  private Map<String, String> getRules(final List<String> args) {
    return args.stream()
        .filter(line -> line.contains(" -> "))
        .map(line -> line.split(" -> "))
        .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
  }

  @SuppressWarnings("SameParameterValue")
  private Map<Character, Long> naivelyExpandString(
      final String template, final Map<String, String> rules, final int steps) {
    String currentTemplate = template;
    for (int step = 0; step < steps; step++) {
      final StringBuilder newTemplate = new StringBuilder();
      for (int i = 0; i < currentTemplate.length() - 1; i++) {
        final String produced = rules.get(currentTemplate.substring(i, i + 2));
        newTemplate.append(currentTemplate.charAt(i)).append(produced);
      }
      newTemplate.append(currentTemplate.charAt(currentTemplate.length() - 1));
      currentTemplate = newTemplate.toString();
    }

    return currentTemplate
        .chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
  }

  @SuppressWarnings("SameParameterValue")
  private Map<Character, Long> efficientlyKeepPairCounter(
      final String template, final Map<String, String> rules, final int steps) {
    // Create the original pair counter map
    Map<String, Long> pairCounter = new HashMap<>();
    for (int i = 0; i < template.length() - 1; i++) {
      pairCounter.merge(template.substring(i, i + 2), 1L, Long::sum);
    }

    // Every step
    for (int step = 0; step < steps; step++) {
      // For every pair (AB -> n) in the pair counter, get the rule (AB -> C), and create two new
      // pairs:
      // AC -> old_count (n) / new_count + old_count (n)
      // CB -> old_count (n) / new_count + old_count (n)
      final Map<String, Long> newPairCounter = new HashMap<>();
      pairCounter.forEach(
          (k, v) -> {
            newPairCounter.merge(k.substring(0, 1) + rules.get(k), v, Long::sum);
            newPairCounter.merge(rules.get(k) + k.substring(1, 2), v, Long::sum);
          });
      // Update the pair counter
      pairCounter = newPairCounter;
    }

    final Map<Character, Long> frequencies = new HashMap<>();
    // For every individual character, we only need the first character of the pair counter entries
    // (otherwise, we would be counting all the characters twice).
    pairCounter.forEach((k, v) -> frequencies.merge(k.charAt(0), v, Long::sum));
    // Increment the frequency for the last character in the template (it was not taken into account
    // when considering only the first character of the pair counter entries).
    frequencies.merge(template.charAt(template.length() - 1), 1L, Long::sum);

    return frequencies;
  }

  private long getMaxMinFrequencyDiff(final Map<Character, Long> frequencies) {
    final long maxFrequency = frequencies.values().stream().max(Long::compareTo).orElse(0L);
    final long minFrequency = frequencies.values().stream().min(Long::compareTo).orElse(0L);
    return maxFrequency - minFrequency;
  }
}
