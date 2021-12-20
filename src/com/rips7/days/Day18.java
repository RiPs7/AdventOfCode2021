package com.rips7.days;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// Cribbed and refactored from
// https://github.com/jerchende/advent-of-code-2021/blob/master/src/main/java/net/erchen/adventofcode2021/day18/SnailfishMath.java
public class Day18 extends Day<List<String>> {

  public Day18() {
    super(DaysEnum.DAY_18);
  }

  @Override
  public void part1(final List<String> args) {
    final SnailFishNumber snailFishSum =
        args.stream()
            .map(SnailFishNumber::parseNumber)
            .reduce(null, (a, b) -> a == null ? b : SnailFishNumber.add(a, b));

    System.out.println(
        String.format("The magnitude of the final sum is %s.", snailFishSum.magnitude()));
  }

  @Override
  public void part2(final List<String> args) {
    final List<SnailFishNumber> snailFishNumbers =
        args.stream().map(SnailFishNumber::parseNumber).collect(Collectors.toList());

    int highestMagnitude =
        snailFishNumbers.stream()
            .flatMap(
                first ->
                    snailFishNumbers.stream()
                        .filter(second -> first != second)
                        .map(
                            second ->
                                SnailFishNumber.add(first.cloneNumber(), second.cloneNumber())))
            .mapToInt(SnailFishNumber::magnitude)
            .max()
            .orElseThrow();

    System.out.println(
        String.format(
            "The largest magnitude of the sum of any two snailfish numbers is %s.",
            highestMagnitude));
  }

  private abstract static class SnailFishNumber {

    public static SnailFishNumber parseNumber(final String input) {
      return parseNumber(input, null);
    }

    private static SnailFishNumber parseNumber(
        final String input, final SnailFishNumberPair parent) {
      if (Character.isDigit(input.charAt(0))) {
        return new SnailFishRegularNumber(Integer.parseInt(input));
      }
      int depth = 0;
      for (int i = 0; i < input.length(); i++) {
        switch (input.charAt(i)) {
          case '[':
            depth++;
            break;
          case ']':
            depth--;
            break;
          case ',':
            if (depth == 1) {
              final SnailFishNumberPair pair = new SnailFishNumberPair();
              pair.setParent(parent);
              pair.setLeft(parseNumber(input.substring(1, i), pair));
              pair.setRight(parseNumber(input.substring(i + 1, input.length() - 1), pair));
              return pair;
            }
            break;
        }
      }
      throw new IllegalStateException(
          String.format("Input %s is not a valid snailfish number!", input));
    }

    public static SnailFishNumber add(
        final SnailFishNumber number1, final SnailFishNumber number2) {
      final SnailFishNumberPair pair = new SnailFishNumberPair();
      pair.setLeft(number1);
      pair.setRight(number2);
      number1.setParent(pair);
      number2.setParent(pair);
      return reduce(pair);
    }

    public static SnailFishNumber reduce(final SnailFishNumber number) {
      while (true) {
        if (!number.explode(0, parent -> {})) {
          if (!number.split(parent -> {})) {
            break;
          }
        }
      }
      return number;
    }

    public abstract void setParent(final SnailFishNumber parent);

    public abstract boolean explode(
        final int depth, final Consumer<SnailFishRegularNumber> parentSetter);

    public abstract boolean split(final Consumer<SnailFishNumberPair> parentSetter);

    public abstract int magnitude();

    public abstract SnailFishNumber cloneNumber();
  }

  public static class SnailFishNumberPair extends SnailFishNumber {
    private SnailFishNumber left;
    private SnailFishNumber right;
    private SnailFishNumberPair parent;

    public SnailFishNumber getLeft() {
      return left;
    }

    public void setLeft(final SnailFishNumber left) {
      this.left = left;
    }

    public SnailFishNumber getRight() {
      return right;
    }

    public void setRight(final SnailFishNumber right) {
      this.right = right;
    }

    @Override
    public void setParent(final SnailFishNumber parent) {
      this.parent = (SnailFishNumberPair) parent;
    }

    @Override
    public boolean explode(final int depth, final Consumer<SnailFishRegularNumber> parentSetter) {
      if (depth == 4) {
        final SnailFishRegularNumber leftAdjacent = leftAdjacent();
        final SnailFishRegularNumber rightAdjacent = rightAdjacent();
        if (leftAdjacent != null) {
          leftAdjacent.add(((SnailFishRegularNumber) left).getValue());
        }
        if (rightAdjacent != null) {
          rightAdjacent.add(((SnailFishRegularNumber) right).getValue());
        }
        parentSetter.accept(new SnailFishRegularNumber(0));
        return true;
      }
      return this.left.explode(depth + 1, parent -> this.left = parent)
          || this.right.explode(depth + 1, parent -> this.right = parent);
    }

    @Override
    public boolean split(final Consumer<SnailFishNumberPair> parentSetter) {
      if (left.split(
          parent -> {
            parent.setParent(this);
            left = parent;
          })) {
        return true;
      }
      return right.split(
          parent -> {
            parent.setParent(this);
            right = parent;
          });
    }

    @Override
    public int magnitude() {
      return 3 * getLeft().magnitude() + 2 * getRight().magnitude();
    }

    @Override
    public SnailFishNumberPair cloneNumber() {
      final SnailFishNumberPair pair = new SnailFishNumberPair();
      pair.setLeft(this.getLeft().cloneNumber());
      pair.setRight(this.getRight().cloneNumber());
      pair.getLeft().setParent(pair);
      pair.getRight().setParent(pair);
      return pair;
    }

    public SnailFishRegularNumber leftAdjacent() {
      if (parent == null) {
        return null;
      }
      if (parent.getRight() == this) {
        SnailFishNumber left = parent.getLeft();
        while (left instanceof SnailFishNumberPair) {
          left = ((SnailFishNumberPair) left).getRight();
        }
        return (SnailFishRegularNumber) left;
      }

      return parent.leftAdjacent();
    }

    public SnailFishRegularNumber rightAdjacent() {
      if (parent == null) {
        return null;
      }
      if (parent.getLeft() == this) {
        SnailFishNumber right = parent.getRight();
        while (right instanceof SnailFishNumberPair) {
          right = ((SnailFishNumberPair) right).getLeft();
        }
        return (SnailFishRegularNumber) right;
      }

      return parent.rightAdjacent();
    }
  }

  public static class SnailFishRegularNumber extends SnailFishNumber {
    private int value;

    public SnailFishRegularNumber(final int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public void setValue(final int value) {
      this.value = value;
    }

    @Override
    public void setParent(final SnailFishNumber parent) {
      // not relevant
    }

    public void add(final int value) {
      this.value += value;
    }

    public boolean explode(final int depth, Consumer<SnailFishRegularNumber> parentSetter) {
      return false;
    }

    public boolean split(final Consumer<SnailFishNumberPair> parentSetter) {
      if (value < 10) {
        return false;
      }
      int left = value / 2;
      int right = value - left;
      final SnailFishNumberPair pair = new SnailFishNumberPair();
      pair.setLeft(new SnailFishRegularNumber(left));
      pair.setRight(new SnailFishRegularNumber(right));
      parentSetter.accept(pair);
      return true;
    }

    public int magnitude() {
      return getValue();
    }

    @Override
    public SnailFishRegularNumber cloneNumber() {
      return new SnailFishRegularNumber(this.getValue());
    }
  }
}
