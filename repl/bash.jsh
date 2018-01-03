// a startup file completely replaces the default imports
// so need to bring in the imports yourself

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

