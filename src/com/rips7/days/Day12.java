package com.rips7.days;

import com.rips7.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.function.BiFunction;

public class Day12 extends Day<List<String>> {

  private static final String START = "start";
  private static final String END = "end";

  private static final boolean PRINT_PATHS = false;

  public Day12() {
    super(DaysEnum.DAY_12);
  }

  @Override
  public void part1(final List<String> args) {
    final Map<Cave, List<Cave>> caveSystem = generateCaveSystem(args);

    final List<List<Cave>> allFoundPaths = findAllPaths(caveSystem, this::canBeAdded1);

    System.out.println(
        String.format(
            "There are %s paths through the cave system that visit small caves at most once.",
            allFoundPaths.size()));
    if (PRINT_PATHS) {
      allFoundPaths.forEach(path -> Utils.printList(path, ","));
    }
  }

  @Override
  public void part2(final List<String> args) {
    final Map<Cave, List<Cave>> caveSystem = generateCaveSystem(args);

    final List<List<Cave>> allFoundPaths = findAllPaths(caveSystem, this::canBeAdded2);

    System.out.println(
        String.format(
            "There are %s paths through the cave system that allow visiting only one small cave twice.",
            allFoundPaths.size()));
    if (PRINT_PATHS) {
      allFoundPaths.forEach(path -> Utils.printList(path, ","));
    }
  }

  private Map<Cave, List<Cave>> generateCaveSystem(List<String> args) {
    // Generate a bi-directional map for the cave system, i.e: a->b and b->a.
    final Map<Cave, List<Cave>> caveSystem = new HashMap<>();
    args.forEach(
        line -> {
          final String[] fromTo = line.split("-");
          final Cave fromCave =
              fromTo[0].equals(fromTo[0].toUpperCase())
                  ? new BigCave(fromTo[0])
                  : new SmallCave(fromTo[0]);
          final Cave toCave =
              fromTo[1].equals(fromTo[1].toUpperCase())
                  ? new BigCave(fromTo[1])
                  : new SmallCave(fromTo[1]);
          caveSystem.computeIfAbsent(fromCave, k -> new ArrayList<>());
          caveSystem.get(fromCave).add(toCave);
          caveSystem.computeIfAbsent(toCave, k -> new ArrayList<>());
          caveSystem.get(toCave).add(fromCave);
        });
    return caveSystem;
  }

  private List<List<Cave>> findAllPaths(
      final Map<Cave, List<Cave>> caveSystem,
      BiFunction<Cave, List<Cave>, Boolean> canBeAddedCheck) {
    final List<List<Cave>> allFoundPaths = new ArrayList<>();
    // Apply DFS
    // Keeps tack of all the paths to be processed.
    final Stack<List<Cave>> frontier = new Stack<>();
    // Adds a list with the starting cave to the frontier
    frontier.add(List.of(new SmallCave(START)));
    // While the frontier is not empty:
    while (!frontier.isEmpty()) {
      // Pop the first path from the frontier.
      final List<Cave> currentPath = frontier.pop();
      // Get the last cave from the current path.
      final Cave lastCave = currentPath.get(currentPath.size() - 1);
      // If the last cave is the end, increment the totalPaths and skip further processing.
      if (lastCave.name.equals(END)) {
        allFoundPaths.add(currentPath);
        continue;
      }
      // Otherwise, for each neighbor from the cave system
      caveSystem
          .get(lastCave)
          .forEach(
              neighbor -> {
                // check if neighbor can be added based on conditions of part 1 or part 2
                if (canBeAddedCheck.apply(neighbor, currentPath)) {
                  // copy the path as a new path with the new cave appended.
                  final List<Cave> newList = new ArrayList<>(currentPath);
                  newList.add(neighbor);
                  // and add it to the frontier.
                  frontier.add(newList);
                }
              });
    }
    return allFoundPaths;
  }

  private boolean canBeAdded1(final Cave cave, final List<Cave> path) {
    if (cave instanceof BigCave) {
      return true;
    } else {
      if (cave.name.equals(START)) {
        return false;
      }
      return !path.contains(cave);
    }
  }

  private boolean canBeAdded2(final Cave cave, final List<Cave> path) {
    if (cave instanceof BigCave) {
      return true;
    } else {
      if (cave.name.equals(START)) {
        return false;
      }
      if (containsSmallCaveMoreThanOnceVisited(path)) {
        return !path.contains(cave);
      } else {
        return true;
      }
    }
  }

  private boolean containsSmallCaveMoreThanOnceVisited(final List<Cave> path) {
    final Map<Cave, Long> frequencyMap = new HashMap<>();
    for (final Cave cave : path) {
      if (cave instanceof SmallCave) {
        frequencyMap.merge(cave, 1L, Long::sum);
      }
    }
    return frequencyMap.values().stream().anyMatch(freq -> freq > 1);
  }

  public abstract static class Cave {
    private final String name;

    public Cave(final String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return String.format("{%s}", name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }

      if (!(obj instanceof Cave)) {
        return false;
      }

      final Cave other = (Cave) obj;

      return Objects.equals(this.name, other.name);
    }
  }

  private static final class SmallCave extends Cave {
    public SmallCave(final String name) {
      super(name);
    }
  }

  private static final class BigCave extends Cave {
    public BigCave(final String name) {
      super(name);
    }
  }
}
