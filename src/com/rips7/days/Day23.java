package com.rips7.days;

import com.rips7.algorithms.Dijkstra;
import com.rips7.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day23 extends Day<List<String>> {

  private static final List<Character> AMPHIPODS = Arrays.asList('A', 'B', 'C', 'D');

  private static final Map<Character, Long> AMPHIPOD_COSTS =
      AMPHIPODS.stream()
          .collect(
              Collectors.toMap(
                  Function.identity(), a -> (long) Math.pow(10, AMPHIPODS.indexOf(a))));

  private static final Map<Character, Integer> AMPHIPOD_ROOMS =
      AMPHIPODS.stream()
          .collect(Collectors.toMap(Function.identity(), a -> 3 + 2 * AMPHIPODS.indexOf(a)));

  private static final List<Integer> COLUMNS_ALLOWED_TO_STOP =
      IntStream.range(1, 12)
          .boxed()
          .filter(i -> !AMPHIPOD_ROOMS.containsValue(i))
          .collect(Collectors.toList());

  private static final int HALLWAY_ROW = 1;

  public Day23() {
    super(DaysEnum.DAY_23);
  }

  @Override
  public void part1(final List<String> args) {
    final AmphipodState start = parseState(args);

    final AmphipodState end =
        new AmphipodState(
            new Character[][] {
              {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
              {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
              {'#', '#', '#', 'A', '#', 'B', '#', 'C', '#', 'D', '#', '#', '#'},
              {'#', '#', '#', 'A', '#', 'B', '#', 'C', '#', 'D', '#', '#', '#'},
              {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
            });

    final List<AmphipodState> path =
        Dijkstra.execute(start, end, AmphipodState::getAllNextStatesAndCosts);
    final long leastEnergy = path.get(path.size() - 1).g;
    System.out.println(
        String.format("The least energy required to sort\n%s\nis %s.", start, leastEnergy));
  }

  @Override
  public void part2(final List<String> args) {
    final AmphipodState start = parseState(args);

    final AmphipodState end =
        new AmphipodState(
            new Character[][] {
              {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
              {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
              {'#', '#', '#', 'A', '#', 'B', '#', 'C', '#', 'D', '#', '#', '#'},
              {'#', '#', '#', 'A', '#', 'B', '#', 'C', '#', 'D', '#', '#', '#'},
              {'#', '#', '#', 'A', '#', 'B', '#', 'C', '#', 'D', '#', '#', '#'},
              {'#', '#', '#', 'A', '#', 'B', '#', 'C', '#', 'D', '#', '#', '#'},
              {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
            });

    final List<AmphipodState> path =
        Dijkstra.execute(start, end, AmphipodState::getAllNextStatesAndCosts);
    final long leastEnergy = path.get(path.size() - 1).g;
    System.out.println(
        String.format("The least energy required to sort\n%s\nis %s.", start, leastEnergy));
  }

  private AmphipodState parseState(final List<String> lines) {
    final Character[][] stateGrid =
        lines.stream()
            .map(
                line -> {
                  final StringBuilder lineBuilder = new StringBuilder(line.trim());
                  while (lineBuilder.length() < 13) {
                    lineBuilder.insert(0, '#').append('#');
                  }
                  return lineBuilder.toString();
                })
            .map(line -> line.chars().mapToObj(c -> (char) c).toArray(Character[]::new))
            .toArray(Character[][]::new);
    return new AmphipodState(stateGrid);
  }

  public static final class AmphipodState extends Dijkstra.DijkstraState {

    public final Character[][] grid;

    public AmphipodState(final Character[][] grid) {
      super();
      this.grid = grid;
    }

    private static Map<AmphipodState, Long> getAllNextStatesAndCosts(final AmphipodState state) {
      final Map<AmphipodState, Long> allNextStatesAndCosts = new HashMap<>();

      final Character[][] grid = state.grid;

      // 1. Check hallway. An amphipod in the hallway should move only if it can end up in its final
      // room.

      // Loop through the hallway columns
      for (int col = 1; col < grid[HALLWAY_ROW].length - 1; col++) {
        // If an amphipod is found
        if (AMPHIPODS.contains(grid[HALLWAY_ROW][col])) {
          // Get the amphipod
          final Character amphipod = grid[HALLWAY_ROW][col];
          // Check if it can move directly to its room
          final int roomColumn = AMPHIPOD_ROOMS.get(amphipod);
          // Whether its destination is on the left (or right)
          final boolean mustGoLeft = roomColumn < col;
          // Assume it can move towards its room to begin with
          boolean canMoveTowardsRoom = true;
          // Keep track of the number of steps it takes
          int steps = 0;
          if (mustGoLeft) {
            // Loop through the columns on the left
            for (int hallwayCol = col - 1; hallwayCol >= roomColumn; hallwayCol--) {
              // If no empty space is found, break
              if (grid[HALLWAY_ROW][hallwayCol] != '.') {
                canMoveTowardsRoom = false;
                break;
              }
              // Otherwise, increment the steps
              steps++;
            }
          } else {
            // Loop through the columns on the right
            for (int hallwayCol = col + 1; hallwayCol <= roomColumn; hallwayCol++) {
              // If no empty space is found, break
              if (grid[HALLWAY_ROW][hallwayCol] != '.') {
                canMoveTowardsRoom = false;
                break;
              }
              // Otherwise, increment the steps
              steps++;
            }
          }
          // If it can move towards the room, we check if it can go in the room
          if (canMoveTowardsRoom) {
            // Assume it can to begin with
            boolean canGoInRoom = true;
            // Loop through all the rows in the room
            for (int roomRow = HALLWAY_ROW + 1; roomRow < grid.length - 1; roomRow++) {
              // It can go in as long the room has only open spaces or same amphipods
              if (grid[roomRow][roomColumn] != '.' && grid[roomRow][roomColumn] != amphipod) {
                canGoInRoom = false;
                break;
              }
            }
            if (canGoInRoom) {
              // Move it as deep as possible
              int roomRow = HALLWAY_ROW + 1;
              while (grid[roomRow][roomColumn] == '.') {
                roomRow++;
                // Increment the count
                steps++;
              }
              // MOVE!!!
              allNextStatesAndCosts.put(
                  performMove(grid, amphipod, HALLWAY_ROW, col, roomRow - 1, roomColumn),
                  steps * AMPHIPOD_COSTS.get(amphipod));
            }
          }
        }
      }

      // 2. Check rooms.
      // i) An amphipod in a CORRECT room can only go outside, if there is space, only if a
      // different amphipod is trapped under it.
      // ii) An amphipod in an INCORRECT room can only go outside, if there is space, and stop
      // on any allowed column in the hallway.

      // Loop through the room columns
      for (final int col : AMPHIPOD_ROOMS.values()) {
        // Loop through all the rows in the rooms
        for (int row = HALLWAY_ROW + 1; row < grid.length - 1; row++) {
          // If an amphipod is found
          if (AMPHIPODS.contains(grid[row][col])) {
            // Get the amphipod
            final Character amphipod = grid[row][col];
            // i) If an amphipod is in the CORRECT room
            if (AMPHIPOD_ROOMS.get(amphipod) == col) {
              // Assume another amphipod is not trapped to begin with
              boolean anotherAmphipodIsTrapped = false;
              // Check all the rows beneath
              for (int rowBeneath = row + 1; rowBeneath < grid.length - 1; rowBeneath++) {
                // If another amphipod is found, break
                if (grid[rowBeneath][col] != amphipod) {
                  anotherAmphipodIsTrapped = true;
                  break;
                }
              }
              // If another amphipod is trapped, the current amphipod needs to go out
              if (anotherAmphipodIsTrapped) {
                // Assume the amphipode can go outside to begin with
                boolean canGoOutside = true;
                // Check all the rows above
                for (int rowAbove = row - 1; rowAbove >= HALLWAY_ROW; rowAbove--) {
                  // If no empty space is found, break
                  if (grid[rowAbove][col] != '.') {
                    canGoOutside = false;
                    break;
                  }
                }
                // If it can go outside
                if (canGoOutside) {
                  // Initialize the step counter with the number of steps to get to the hallway
                  int steps = row - 1;
                  // Check all the hallway columns on the left
                  for (int hallwayCol = col - 1; hallwayCol >= 1; hallwayCol--) {
                    // If no space is found, break
                    if (grid[HALLWAY_ROW][hallwayCol] != '.') {
                      break;
                    }
                    // Increment the steps
                    steps++;
                    // If the amphipod is allowed to stop in that column
                    if (COLUMNS_ALLOWED_TO_STOP.contains(hallwayCol)) {
                      // MOVE!!!
                      allNextStatesAndCosts.put(
                          performMove(grid, amphipod, row, col, HALLWAY_ROW, hallwayCol),
                          steps * AMPHIPOD_COSTS.get(amphipod));
                    }
                  }
                  // Initialize the step counter with the number of steps to get to the hallway
                  steps = row - 1;
                  // Check all the hallway columns on the right
                  for (int hallwayCol = col + 1; hallwayCol <= 11; hallwayCol++) {
                    // If no space is found, break
                    if (grid[HALLWAY_ROW][hallwayCol] != '.') {
                      break;
                    }
                    // Increment the steps
                    steps++;
                    // If the amphipod is allowed to stop in that column
                    if (COLUMNS_ALLOWED_TO_STOP.contains(hallwayCol)) {
                      // MOVE!!!
                      allNextStatesAndCosts.put(
                          performMove(grid, amphipod, row, col, HALLWAY_ROW, hallwayCol),
                          steps * AMPHIPOD_COSTS.get(amphipod));
                    }
                  }
                }
              }
            } else { // ii) If an amphipod is in an INCORRECT room
              // Assume it can go outside to begin with
              boolean canGoOutside = true;
              // Check all the rows above
              for (int rowAbove = row - 1; rowAbove >= HALLWAY_ROW; rowAbove--) {
                // If no empty space is found, break
                if (grid[rowAbove][col] != '.') {
                  canGoOutside = false;
                  break;
                }
              }
              // If it can go outside
              if (canGoOutside) {
                // Initialize the step counter with the number of steps to get to the hallway
                int steps = row - 1;
                // Check all the hallway columns on the left
                for (int hallwayCol = col - 1; hallwayCol >= 1; hallwayCol--) {
                  // If no space is found, break
                  if (grid[HALLWAY_ROW][hallwayCol] != '.') {
                    break;
                  }
                  // Increment the steps
                  steps++;
                  // If the amphipod is allowed to stop in that column
                  if (COLUMNS_ALLOWED_TO_STOP.contains(hallwayCol)) {
                    // MOVE!!!
                    allNextStatesAndCosts.put(
                        performMove(grid, amphipod, row, col, HALLWAY_ROW, hallwayCol),
                        steps * AMPHIPOD_COSTS.get(amphipod));
                  }
                }
                // Initialize the step counter with the number of steps to get to the hallway
                steps = row - 1;
                // Check all the hallway columns on the right
                for (int hallwayCol = col + 1; hallwayCol <= 11; hallwayCol++) {
                  // If no space is found, break
                  if (grid[HALLWAY_ROW][hallwayCol] != '.') {
                    break;
                  }
                  // Increment the steps
                  steps++;
                  // If the amphipod is allowed to stop in that column
                  if (COLUMNS_ALLOWED_TO_STOP.contains(hallwayCol)) {
                    // MOVE!!!
                    allNextStatesAndCosts.put(
                        performMove(grid, amphipod, row, col, HALLWAY_ROW, hallwayCol),
                        steps * AMPHIPOD_COSTS.get(amphipod));
                  }
                }
              }
            }
          }
        }
      }

      return allNextStatesAndCosts;
    }

    private static AmphipodState performMove(
        final Character[][] grid,
        final Character amphipod,
        final int row,
        final int col,
        final int newRow,
        final int newCol) {
      final Character[][] nextStateGrid =
          Arrays.stream(grid).map(Character[]::clone).toArray(Character[][]::new);
      nextStateGrid[row][col] = '.';
      nextStateGrid[newRow][newCol] = amphipod;
      return new AmphipodState(nextStateGrid);
    }

    @Override
    public int hashCode() {
      return Arrays.deepHashCode(this.grid);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof AmphipodState)) {
        return false;
      }
      final AmphipodState other = (AmphipodState) obj;
      // Check the hallway
      for (int col = 1; col < this.grid[1].length - 1; col++) {
        if (this.grid[HALLWAY_ROW][col] != other.grid[HALLWAY_ROW][col]) {
          return false;
        }
      }
      // Check all the rooms
      for (final int col : AMPHIPOD_ROOMS.values()) {
        for (int row = HALLWAY_ROW + 1; row < this.grid.length - 1; row++) {
          if (this.grid[row][col] != other.grid[row][col]) {
            return false;
          }
        }
      }
      return true;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("\n");
      Utils.loopThroughAndDo(
          this.grid,
          (row, col) ->
              sb.append(this.grid[row][col]).append(col == this.grid[0].length - 1 ? "\n" : ""));
      return sb.toString();
    }
  }
}
