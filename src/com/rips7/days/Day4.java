package com.rips7.days;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4 extends Day<List<String>> {

  public Day4() {
    super(DaysEnum.DAY_4);
  }

  @Override
  public void part1(final List<String> args) {
    final List<Integer> numbers =
        Arrays.stream(args.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());

    final List<Board> boards = getBoards(args);

    for (final Integer number : numbers) {
      for (final Board board : boards) {
        if (board.tickNumberAndCheckWin(number)) {
          System.out.println(
              String.format("The score of the first winning board is %s", board.calculateScore()));
          return;
        }
      }
    }
    System.out.println("No winning board found.");
  }

  @Override
  public void part2(final List<String> args) {
    final List<Integer> numbers =
        Arrays.stream(args.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());

    final List<Board> boards = getBoards(args);

    final List<Board> winningBoards = new ArrayList<>();
    for (final Integer number : numbers) {
      for (final Board board : boards) {
        if (winningBoards.contains(board)) {
          continue;
        }
        if (board.tickNumberAndCheckWin(number)) {
          winningBoards.add(board);
        }
      }
    }

    if (!winningBoards.isEmpty()) {
      System.out.println(
          String.format(
              "The score of the last winning board is %s",
              winningBoards.get(winningBoards.size() - 1).calculateScore()));
    } else {
      System.out.println("No winning board found.");
    }
  }

  private List<Board> getBoards(final List<String> lines) {
    return IntStream.range(2, lines.size())
        .boxed()
        .filter(i -> i % 6 == 2)
        .map(
            i ->
                new Board(
                    lines.subList(i, i + 5).stream()
                        .map(
                            row ->
                                Arrays.stream(row.trim().split("\\s+"))
                                    .map(Integer::parseInt)
                                    .toArray(Integer[]::new))
                        .toArray(Integer[][]::new)))
        .collect(Collectors.toList());
  }

  private static final class Board {
    Integer[][] board;
    boolean isBoardWinning;
    int winningNumber;

    public Board(Integer[][] board) {
      this.board = board;
      this.isBoardWinning = false;
      this.winningNumber = -1;
    }

    boolean tickNumberAndCheckWin(final Integer number) {
      for (int row = 0; row < board.length; row++) {
        for (int col = 0; col < board[row].length; col++) {
          if (board[row][col].equals(number)) {
            board[row][col] = -1;
            if (checkWin()) {
              isBoardWinning = true;
              winningNumber = number;
              return true;
            }
          }
        }
      }
      return false;
    }

    boolean checkWin() {
      // check rows
      for (Integer[] integers : board) {
        boolean rowWinning = false;
        for (int col = 0; col < integers.length; col++) {
          if (integers[col] != -1) {
            break;
          } else {
            rowWinning = col == integers.length - 1;
          }
        }
        if (rowWinning) {
          return true;
        }
      }

      // check cols
      for (int col = 0; col < board[0].length; col++) {
        boolean columnWinning = false;
        for (int row = 0; row < board.length; row++) {
          if (board[row][col] != -1) {
            break;
          } else {
            columnWinning = row == board.length - 1;
          }
        }
        if (columnWinning) {
          return true;
        }
      }

      return false;
    }

    int calculateScore() {
      return winningNumber
          * Arrays.stream(board)
              .map(
                  row ->
                      Arrays.stream(row)
                          .filter(number -> number != -1)
                          .reduce(Integer::sum)
                          .orElse(0))
              .reduce(Integer::sum)
              .orElse(0);
    }

    void printBoard() {
      Arrays.stream(board)
          .map(row -> Arrays.stream(row).map(String::valueOf).collect(Collectors.joining(" ")))
          .forEach(System.out::println);
    }
  }
}
