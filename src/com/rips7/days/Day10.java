package com.rips7.days;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day10 extends Day<List<String>> {

  private static final List<Character> OPENING_CHARACTERS = List.of('(', '[', '{', '<');
  private static final List<Character> CLOSING_CHARACTERS = List.of(')', ']', '}', '>');
  private static final Map<Character, Character> VALID_CHARACTERS =
      IntStream.range(0, OPENING_CHARACTERS.size())
          .boxed()
          .collect(Collectors.toMap(OPENING_CHARACTERS::get, CLOSING_CHARACTERS::get));
  private static final Map<Character, Integer> ILLEGAL_CHARACTERS_SCORES =
      Map.of(')', 3, ']', 57, '}', 1197, '>', 25137);
  private static final Map<Character, Integer> AUTO_COMPLETED_CHARACTER_SCORES =
      Map.of(')', 1, ']', 2, '}', 3, '>', 4);

  private static final int LINE_CORRECT = 0;
  private static final int LINE_INCOMPLETE = 1;
  private static final int LINE_CORRUPTED = 2;

  public Day10() {
    super(DaysEnum.DAY_10);
  }

  @Override
  public void part1(final List<String> args) {
    final Map<String, ParseStatus> parsedLines =
        args.stream().collect(Collectors.toMap(Function.identity(), this::parseLineSyntax));
    final int totalSyntaxErrorScore =
        parsedLines.values().stream()
            .filter(parseStatus -> parseStatus.status == LINE_CORRUPTED)
            .map(parseStatus -> parseStatus.illegalCharacter)
            .map(ILLEGAL_CHARACTERS_SCORES::get)
            .mapToInt(Integer::intValue)
            .sum();

    System.out.println(String.format("The total syntax error score is %s.", totalSyntaxErrorScore));
  }

  @Override
  public void part2(final List<String> args) {
    final Map<String, ParseStatus> parsedLines =
        args.stream().collect(Collectors.toMap(Function.identity(), this::parseLineSyntax));
    final List<Long> autoCompletionScores =
        parsedLines.values().stream()
            .filter(parseStatus -> parseStatus.status == LINE_INCOMPLETE)
            .map(parseStatus -> parseStatus.autoCompletedSequence)
            .map(this::calculateAutoCompletionScore)
            .sorted(Long::compareTo)
            .collect(Collectors.toList());

    final long totalAutoCompletionScore = autoCompletionScores.get(autoCompletionScores.size() / 2);

    System.out.println(
        String.format("The total auto-completion score is %s.", totalAutoCompletionScore));
  }

  ParseStatus parseLineSyntax(final String line) {
    final Character[] chars = line.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
    final Stack<Character> parser = new Stack<>();
    parser.push(chars[0]);
    for (int i = 1; i < chars.length; i++) {
      if (OPENING_CHARACTERS.contains(chars[i])) {
        parser.push(chars[i]);
        continue;
      }
      final Character previousOpeningChar = parser.peek();
      final Character expectedClosingChar = VALID_CHARACTERS.get(previousOpeningChar);
      if (!chars[i].equals(expectedClosingChar)) {
        return new ParseStatus(LINE_CORRUPTED, chars[i], null);
      } else {
        parser.pop();
      }
    }
    return parser.isEmpty()
        ? new ParseStatus(LINE_CORRECT, null, null)
        : new ParseStatus(LINE_INCOMPLETE, null, this.autoCompleteSequence(parser));
  }

  private String autoCompleteSequence(final Stack<Character> parser) {
    final StringBuilder autoCompletedSequence = new StringBuilder();
    while (!parser.empty()) {
      autoCompletedSequence.append(VALID_CHARACTERS.get(parser.pop()));
    }
    return autoCompletedSequence.toString();
  }

  private Long calculateAutoCompletionScore(final String autoCompleteSequence) {
    final AtomicLong score = new AtomicLong(0);
    autoCompleteSequence
        .chars()
        .mapToObj(c -> (char) c)
        .map(AUTO_COMPLETED_CHARACTER_SCORES::get)
        .forEach(charScore -> score.set(score.get() * 5 + charScore));
    return score.get();
  }

  private static final class ParseStatus {
    final int status;
    final Character illegalCharacter;
    final String autoCompletedSequence;

    public ParseStatus(
        final int status, final Character illegalCharacter, final String autoCompletedSequence) {
      this.status = status;
      this.illegalCharacter = illegalCharacter;
      this.autoCompletedSequence = autoCompletedSequence;
    }
  }
}
