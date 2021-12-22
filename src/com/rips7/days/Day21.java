package com.rips7.days;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Day21 extends Day<List<String>> {

  public Day21() {
    super(DaysEnum.DAY_21);
  }

  @Override
  public void part1(final List<String> args) {
    final Player player1 = new Player(1, Integer.parseInt(args.get(0).split(": ")[1]));
    final Player player2 = new Player(2, Integer.parseInt(args.get(1).split(": ")[1]));

    final Game game = new SimpleGame(player1, player2);
    game.play();
    long outcome = game.outcome;

    System.out.println(
        String.format(
            "After the game ends, the score of the losing player times the number of times the die was rolled is %s",
            outcome));
  }

  @Override
  public void part2(final List<String> args) {
    final Player player1 = new Player(1, Integer.parseInt(args.get(0).split(": ")[1]));
    final Player player2 = new Player(2, Integer.parseInt(args.get(1).split(": ")[1]));

    final Game game = new DiracGame(player1, player2);
    game.play();
    long outcome = game.outcome;

    System.out.println(String.format("The winning player wins in %s universes", outcome));
  }

  private abstract static class Game {
    final Player player1;
    final Player player2;
    long outcome;

    private Game(final Player player1, final Player player2) {
      this.player1 = player1;
      this.player2 = player2;
    }

    abstract void play();
  }

  private static final class SimpleGame extends Game {
    final Die die;

    private SimpleGame(final Player player1, final Player player2) {
      super(player1, player2);
      this.die = new Die();
    }

    void play() {
      // Controls which player is playing
      boolean player1Plays = true;
      // If neither player has won
      while (!player1.checkWin() && !player2.checkWin()) {
        // Roll the dice three times, and add up the rolls
        int roll = die.roll() + die.roll() + die.roll();
        // Current player moves number of spaces based on the total roll number
        (player1Plays ? player1 : player2).move(roll);
        // Next player plays
        player1Plays = !player1Plays;
      }

      // Get the score of the losing player
      final int losingPlayerScore = (player1.checkWin() ? player2 : player1).score;
      // Get the number of die rolls
      final int dieRolls = die.rolls;

      outcome = losingPlayerScore * dieRolls;
    }

    private static final class Die {
      final AtomicInteger value;
      int rolls;

      private Die() {
        value = new AtomicInteger(1);
        rolls = 0;
      }

      private int roll() {
        rolls++;
        if (value.get() == 101) {
          this.value.set(1);
        }
        return value.getAndIncrement();
      }
    }
  }

  private static final class DiracGame extends Game {
    private DiracGame(final Player player1, final Player player2) {
      super(player1, player2);
    }

    @Override
    void play() {
      // Utilise dynamic programming and memoisation
      final Map<List<Integer>, List<Long>> memoisedStates = new HashMap<>();
      final List<Integer> initialState =
          Arrays.asList(player1.currentSpace, player1.score, player2.currentSpace, player2.score);
      final List<Long> universeWins = findWins(initialState, memoisedStates);
      outcome = Collections.max(universeWins);
    }

    // Gets the wins (list(0) -> player1 wins, list(1) -> player2 wins)
    // for the current state (state: [player1 space, player1 score, player2 space, player2 score])
    // utilising the memoised states
    private List<Long> findWins(
        final List<Integer> state, final Map<List<Integer>, List<Long>> memoisedStates) {
      final int player1Space = state.get(0);
      final int player1Score = state.get(1);
      final int player2Space = state.get(2);
      final int player2Score = state.get(3);
      // Base case, where player 1 wins
      if (player1Score >= 21) {
        return Arrays.asList(1L, 0L);
      }
      // Base case, where player 2 wins
      if (player2Score >= 21) {
        return Arrays.asList(0L, 1L);
      }
      // Current state is already calculated / memoised -> return cached state
      final List<Long> cachedResult = memoisedStates.get(state);
      if (cachedResult != null) {
        return cachedResult;
      }
      // Calculate the result of the current state; initialise the result with 0 wins for both
      // players
      final List<Long> result = Arrays.asList(0L, 0L);
      // For all the possible dice rolls
      for (int roll1 = 1; roll1 <= 3; roll1++) {
        for (int roll2 = 1; roll2 <= 3; roll2++) {
          for (int roll3 = 1; roll3 <= 3; roll3++) {
            // Add up all the rolls
            final int roll = roll1 + roll2 + roll3;
            // Find the landing space
            int newSpace = player1Space + roll;
            while (newSpace > 10) {
              newSpace -= 10;
            }
            // Add the space score to the player's score
            int newScore = player1Score + newSpace;
            // The new state consists of the second player's info passed as first arguments,
            // and the new landing space and score for the first player as second arguments,
            // denoting that the other player is playing
            final List<Integer> newState =
                Arrays.asList(player2Space, player2Score, newSpace, newScore);
            // Recursively calculate the result for the new state
            final List<Long> partialResult = findWins(newState, memoisedStates);
            // Add the individual wins to the corresponding player (inverted in the partial result)
            result.set(0, result.get(0) + partialResult.get(1));
            result.set(1, result.get(1) + partialResult.get(0));
          }
        }
      }
      // Add the state -> result to the memoised states
      memoisedStates.put(state, result);
      // Return the result of the current state
      return result;
    }
  }

  private static final class Player {
    final int id;
    int score;
    int currentSpace;

    private Player(final int id, final int startingSpace) {
      this.id = id;
      this.score = 0;
      this.currentSpace = startingSpace;
    }

    private void move(final int spaces) {
      this.currentSpace = this.currentSpace + spaces;
      while (this.currentSpace > 10) {
        this.currentSpace -= 10;
      }
      this.score += this.currentSpace;
    }

    private boolean checkWin() {
      return this.score >= 1000;
    }
  }
}
