// a startup file completely replaces the default
// so need to bring in the default imports yourself

import java.util.*
import java.io.*
import java.nio.file.*
import java.math.*
import java.net.*
import java.util.concurrent.*
import java.util.prefs.*
import java.util.regex.*


void ls() throws IOException {
  Files.list(Paths.get(".")).forEach(System.out::println);
}

