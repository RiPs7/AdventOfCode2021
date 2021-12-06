package com.rips7;

import com.rips7.days.DaysEnum;

import java.util.Arrays;

public class Main {

  public static void main(String[] args) {
      Arrays.stream(DaysEnum.values()).forEach(DaysEnum::run);
  }
}
