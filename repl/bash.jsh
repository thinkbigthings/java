// a startup file completely replaces the default
// so need to bring in the default imports yourself

import java.util.*
import java.io.*
import java.math.*
import java.net.*
import java.util.concurrent.*
import java.util.prefs.*
import java.util.regex.*



void ls() {
  File cur = new File(".");
  for(String s : cur.list())
  System.out.println(s);
}

