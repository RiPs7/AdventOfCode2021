package com.rips7.days;

import com.rips7.Utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Day20 extends Day<List<String>> {

  public Day20() {
    super(DaysEnum.DAY_20);
  }

  @Override
  public void part1(final List<String> args) {
    final ImageEnhancerAlgorithm algorithm = new ImageEnhancerAlgorithm(args.get(0).toCharArray());
    final Image originalImage = loadImage(args.subList(2, args.size()));

    final int steps = 2;
    final int litPixels = enhanceImageXTimes(originalImage, algorithm, steps);

    System.out.println(
        String.format(
            "After running the enhancement algorithm %s steps, there are %s lit pixels",
            steps, litPixels));
  }

  @Override
  public void part2(final List<String> args) {
    final ImageEnhancerAlgorithm algorithm = new ImageEnhancerAlgorithm(args.get(0).toCharArray());
    final Image originalImage = loadImage(args.subList(2, args.size()));

    final int steps = 50;
    final int litPixels = enhanceImageXTimes(originalImage, algorithm, steps);

    System.out.println(
        String.format(
            "After running the enhancement algorithm %s steps, there are %s lit pixels",
            steps, litPixels));
  }

  private Image loadImage(final List<String> lines) {
    return new Image(
        lines.stream()
            .map(line -> line.chars().mapToObj(c -> (char) c).toArray(Character[]::new))
            .toArray(Character[][]::new),
        '.');
  }

  private int enhanceImageXTimes(
      final Image originalImage, final ImageEnhancerAlgorithm algorithm, final int times) {
    // Pad image once in the beginning, to account for enhanced image growth
    Image image = originalImage.pad(times);

    for (int time = 0; time < times; time++) {
      image = algorithm.enhanceImage(image);
    }

    return image.countLitPixels();
  }

  private static final class ImageEnhancerAlgorithm {
    final char[] algorithm;

    public ImageEnhancerAlgorithm(final char[] algorithm) {
      this.algorithm = algorithm;
    }

    public Image enhanceImage(final Image originalImage) {
      // Create a new image, with the same dimensions and background as the original
      final Image enhancedImage =
          new Image(originalImage.w, originalImage.h, originalImage.background);
      // Calculate the values of all the pixels based on the enhancement algorithm
      for (int i = 0; i < enhancedImage.h; i++) {
        for (int j = 0; j < enhancedImage.w; j++) {
          final int enhancementIndex = getEnhancementIndex(originalImage, i, j);
          enhancedImage.pixels[i][j] = algorithm[enhancementIndex];
        }
      }
      enhancedImage.background = enhancedImage.pixels[0][0];
      return enhancedImage;
    }

    private int getEnhancementIndex(final Image img, final int row, final int col) {
      int[] dx = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
      int[] dy = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < dx.length; i++) {
        char pixel;
        // If the index is out of bounds, get the background character
        try {
          pixel = img.pixels[row + dy[i]][col + dx[i]];
        } catch (final ArrayIndexOutOfBoundsException aioobe) {
          pixel = img.background;
        }
        sb.append(pixel == '.' ? '0' : '1');
      }
      return Integer.parseInt(sb.toString(), 2);
    }
  }

  private static final class Image {
    final int w;
    final int h;
    final Character[][] pixels;

    Character background;

    public Image(final Character[][] pixels, final Character background) {
      this.h = pixels.length;
      this.w = pixels[0].length;
      this.pixels = pixels;
      this.background = background;
    }

    public Image(final int w, final int h, final char background) {
      this.w = w;
      this.h = h;
      this.pixels = new Character[h][w];
      for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
          this.pixels[i][j] = background;
        }
      }
      this.background = background;
    }

    public Image pad(final int pad) {
      final Image paddedImage = new Image(this.w + 2 * pad, this.h + 2 * pad, '.');
      for (int i = 0; i < this.h; i++) {
        if (this.w >= 0)
          System.arraycopy(this.pixels[i], 0, paddedImage.pixels[i + pad], pad, this.w);
      }
      return paddedImage;
    }

    public int countLitPixels() {
      final AtomicInteger litPixelsCount = new AtomicInteger(0);
      Utils.loopThroughAndDo(
          pixels,
          (row, col) -> {
            if (pixels[row][col] == '#') {
              litPixelsCount.incrementAndGet();
            }
          });
      return litPixelsCount.get();
    }
  }
}
