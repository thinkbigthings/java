
project based on the example in http://openjdk.java.net/projects/jigsaw/quick-start

// reset
rm -rf greetingsapp mlib mods

// Build multiple modules
javac -d mods --module-source-path src $(find src -name "*.java")

// Run module (specify module path and module name)
java --module-path mods -m com.greetings/com.greetings.Main

// Package as modular jars (a regular JAR file that has a module-info.class)
mkdir mlib
jar --create --file=mlib/org.astro-1.0.jar --module-version=1.0 -C mods/org.astro .
jar --create --file=mlib/com.greetings.jar --main-class=com.greetings.Main -C mods/com.greetings .
ls mlib

// Run module directly (like java -jar)
java -p mlib -m com.greetings

// create modular runtime image with my modules
// jlink --module-path $JAVA_HOME/jmods:mlib --add-modules com.greetings --output greetingsapp
jlink --strip-debug --compress=2 --module-path $JAVA_HOME/jmods:mlib --add-modules com.greetings --launcher hello=com.greetings/com.greetings.Main --output greetingsapp

// how big is it?
du -h greetingsapp/

// run with:
greetingsapp/bin/java -m com.greetings

// or run the launch command (from --launcher)
greetingsapp/bin/hello 

// edit relevant files at once:
gedit src/com.greetings/com/greetings/Main.java src/org.astro/org/astro/DefaultAstroHelloWorldNameMessageStringProvider.java  src/com.greetings/module-info.java src/org.astro/module-info.java README.md &



