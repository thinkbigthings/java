package com.greetings;

// simple and easy to remember
import org.astro.DefaultAstroHelloWorldNameMessageStringProvider;

public class Main {

   public static void main(String[] args) {
      System.out.format("Greetings %s!%n", DefaultAstroHelloWorldNameMessageStringProvider.name());
   }
}

