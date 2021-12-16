package com.rips7.days;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day16 extends Day<List<String>> {

  private static final Map<Character, String> HEX_TO_BINARY = new HashMap<>();

  static {
    HEX_TO_BINARY.put('0', "0000");
    HEX_TO_BINARY.put('1', "0001");
    HEX_TO_BINARY.put('2', "0010");
    HEX_TO_BINARY.put('3', "0011");
    HEX_TO_BINARY.put('4', "0100");
    HEX_TO_BINARY.put('5', "0101");
    HEX_TO_BINARY.put('6', "0110");
    HEX_TO_BINARY.put('7', "0111");
    HEX_TO_BINARY.put('8', "1000");
    HEX_TO_BINARY.put('9', "1001");
    HEX_TO_BINARY.put('A', "1010");
    HEX_TO_BINARY.put('B', "1011");
    HEX_TO_BINARY.put('C', "1100");
    HEX_TO_BINARY.put('D', "1101");
    HEX_TO_BINARY.put('E', "1110");
    HEX_TO_BINARY.put('F', "1111");
  }

  public Day16() {
    super(DaysEnum.DAY_16);
  }

  @Override
  public void part1(final List<String> args) {
    final BITS bits = new BITS(args.get(0));
    System.out.println(
        String.format(
            "The accumulated value of the version numbers in all packets is: %s",
            accumulateVersions(bits.wrappingPacket)));
  }

  @Override
  public void part2(final List<String> args) {
    final BITS bits = new BITS(args.get(0));
    System.out.println(
        String.format(
            "The given hexadecimal-encoded BITS evaluates to %s", bits.wrappingPacket.value));
  }

  private int accumulateVersions(final BITS.Packet packet) {
    if (packet == null) {
      return 0;
    }
    return packet.version
        + (packet.subPackets == null
            ? 0
            : packet.subPackets.stream()
                .map(this::accumulateVersions)
                .mapToInt(Integer::intValue)
                .sum());
  }

  public static final class BITS {
    private final Packet wrappingPacket;

    public BITS(final String hex) {
      final String bits =
          hex.chars().mapToObj(c -> (char) c).map(HEX_TO_BINARY::get).collect(Collectors.joining());
      wrappingPacket = new Packet(bits, new AtomicInteger(0));
    }

    public static final class Packet {
      private final int version;
      private final long value;
      private final List<Packet> subPackets;

      public Packet(final String bits, final AtomicInteger bitParserCount) {
        version =
            Integer.parseInt(bits.substring(bitParserCount.get(), bitParserCount.addAndGet(3)), 2);
        final int typeId =
            Integer.parseInt(bits.substring(bitParserCount.get(), bitParserCount.addAndGet(3)), 2);
        if (typeId == 4) {
          subPackets = null;
          final StringBuilder literalValueBuilder = new StringBuilder();
          int i = bitParserCount.get();
          do {
            literalValueBuilder.append(bits, i + 1, i + 5);
            final char prefix = bits.charAt(i);
            if (prefix == '0') {
              break;
            } else if (prefix != '1') {
              throw new RuntimeException(String.format("Invalid prefix [%s]", prefix));
            }
            i += 5;
          } while (i < bits.length());
          bitParserCount.set(i + 5);
          value = Long.parseLong(literalValueBuilder.toString(), 2);
        } else {
          final char lengthTypeId = bits.charAt(bitParserCount.getAndIncrement());
          if (lengthTypeId == '0') {
            final int totalLengthInBits =
                Integer.parseInt(
                    bits.substring(bitParserCount.get(), bitParserCount.addAndGet(15)), 2);
            subPackets = new ArrayList<>();
            int startingPosition = bitParserCount.get();
            do {
              subPackets.add(new Packet(bits, bitParserCount));
            } while (bitParserCount.get() - startingPosition < totalLengthInBits);
          } else if (lengthTypeId == '1') {
            final int numSubPackets =
                Integer.parseInt(
                    bits.substring(bitParserCount.get(), bitParserCount.addAndGet(11)), 2);
            subPackets = new ArrayList<>();
            for (int i = 0; i < numSubPackets; i++) {
              subPackets.add(new Packet(bits, bitParserCount));
            }
          } else {
            throw new RuntimeException(String.format("Invalid length type id [%s]", lengthTypeId));
          }
          // Assumes the values of all subPackets have been computed, as they all eventually depend
          // on literal values.
          value = getPacketValue(typeId, subPackets);
        }
      }

      long getPacketValue(final int typeId, final List<Packet> subPackets) {
        final LongStream values =
            subPackets.stream().map(subPacket -> subPacket.value).mapToLong(Long::longValue);
        switch (typeId) {
          case 0:
            return values.sum();
          case 1:
            return values.reduce(1, (a, b) -> a * b);
          case 2:
            return values.min().orElse(0);
          case 3:
            return values.max().orElse(0);
          case 5:
            return subPackets.get(0).value > subPackets.get(1).value ? 1 : 0;
          case 6:
            return subPackets.get(0).value < subPackets.get(1).value ? 1 : 0;
          case 7:
            return subPackets.get(0).value == subPackets.get(1).value ? 1 : 0;
          default:
            throw new RuntimeException(String.format("Invalid type id [%s]", typeId));
        }
      }
    }
  }
}
