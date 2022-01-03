package com.rips7.algorithms;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;

/**
 * Implements the Dijkstra Algorithm for path finding. See:
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
@SuppressWarnings("unused")
public class Dijkstra {

  /**
   * Executes the Dijkstra Path-finding algorithm, to find the lowest-cost path from start to end,
   * given a neighbor generation function.
   *
   * @param start The start state
   * @param end The end state
   * @param getNeighbors The neighbor generation function: takes in a state and returns a map of
   *     neighbors along with their cost
   * @param <STATE> The type of state: must extend {@link DijkstraState}
   * @return Returns the reconstructed path of states
   */
  @SuppressWarnings("unchecked")
  public static <STATE extends DijkstraState> List<STATE> execute(
      final STATE start, final STATE end, final Function<STATE, Map<STATE, Long>> getNeighbors) {
    HashSet<STATE> visited = new HashSet<>();
    PriorityQueue<STATE> frontier = new PriorityQueue<>(Comparator.comparingLong(s -> s.g));
    start.g = 0;
    start.parent = null;
    frontier.add(start);
    while (!frontier.isEmpty()) {
      final STATE current = frontier.poll();
      if (visited.contains(current)) {
        continue;
      }
      if (current.equals(end)) {
          final LinkedList<STATE> path = new LinkedList<>();
          STATE temp = current;
          while (temp != null) {
              path.addFirst(temp);
              temp = (STATE) temp.parent;
          }
          return path;
      }
      visited.add(current);
      getNeighbors
          .apply(current)
          .forEach(
              (neighbor, cost) -> {
                if (visited.contains(neighbor)) {
                  return;
                }
                neighbor.g = current.g + cost;
                neighbor.parent = current;
                frontier.add(neighbor);
              });
    }

    throw new RuntimeException("Cannot find path.");
  }

  /** An abstract state class to be used when executing the algorithms in this class. */
  public abstract static class DijkstraState {

    /** The cost from start to this state */
    public long g;

    /** The parent state */
    public DijkstraState parent;

    @Override
    public abstract boolean equals(Object obj);
  }
}
