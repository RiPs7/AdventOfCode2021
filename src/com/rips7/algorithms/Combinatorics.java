package com.rips7.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/** Utility class for combinatorics */
public class Combinatorics {

  /**
   * Computes the permutations for a given list of elements, given a specified repetition value
   *
   * @param list A list of elements
   * @param repeat A repetition value
   * @param <T> The type of elements the list holds
   * @return A {@link List} of permutations (each one being a {@link List} of elements.
   */
  public static <T> List<List<T>> permutations(final List<T> list, final int repeat) {
    List<List<T>> result = Collections.nCopies(1, Collections.emptyList());
    for (final Collection<T> pool : Collections.nCopies(repeat, new LinkedHashSet<>(list))) {
      final List<List<T>> temp = new ArrayList<>();
      for (final Collection<T> x : result) {
        for (T y : pool) {
          final List<T> z = new ArrayList<>(x);
          z.add(y);
          temp.add(z);
        }
      }
      result = temp;
    }
    return result;
  }
}
