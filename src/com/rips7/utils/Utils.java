package com.rips7.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public final class Utils {

  public static String readInput(final String fileName) {
    try (final InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName)) {
      if (is == null) throw new IOException();
      try (InputStreamReader isr = new InputStreamReader(is);
           BufferedReader reader = new BufferedReader(isr)) {
        return reader.lines().collect(Collectors.joining("\n"));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<String> readLines(final String fileName) {
    try (final InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName)) {
      if (is == null) throw new IOException();
      try (InputStreamReader isr = new InputStreamReader(is);
          BufferedReader reader = new BufferedReader(isr)) {
        return reader.lines().collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Integer> readLinesAsInt(final String finalName) {
    return readLines(finalName).stream().map(Integer::parseInt).collect(Collectors.toList());
  }

  public static <T> void loopThroughAndDo(
      final T[][] array, final BiConsumer<Integer, Integer> rowColConsumer) {
    IntStream.range(0, array.length)
        .boxed()
        .forEach(
            row ->
                IntStream.range(0, array[row].length)
                    .boxed()
                    .forEach(col -> rowColConsumer.accept(row, col)));
  }

  public static <T> void printArray(final T[][] array, final String delimiter) {
    System.out.println(
        Arrays.stream(array)
            .map(
                row ->
                    Arrays.stream(row).map(String::valueOf).collect(Collectors.joining(delimiter)))
            .collect(Collectors.joining("\n")));
  }

  public static <T> void printList(final List<T> list, final String delimiter) {
    System.out.println(list.stream().map(String::valueOf).collect(Collectors.joining(delimiter)));
  }

  static final class ProgressBar {
    int limit;
    int max;
    int scale;
    int current;

    public ProgressBar(final int limit, final int max) {
      this.limit = limit;
      this.max = max;
      this.scale = limit / max;
      this.current = 0;
    }

    void update(final Runnable callback, final int step) {
      callback.run();
      if (step % scale != 0) {
        return;
      }

      String progress =
          "=".repeat(Math.max(0, current + 1)) + " ".repeat(Math.max(0, max - (current + 1) + 1));
      System.out.print("Playing the game... [" + progress + "] (" + current + "%)\r");

      current++;

      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
