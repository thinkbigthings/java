
project based on the example in http://openjdk.java.net/projects/jigsaw/quick-start

Build multiple modules with:
javac -d mods --module-source-path src $(find src -name "*.java")

Run module with:
java --module-path mods -m com.greetings/com.greetings.Main

Package as modular jars:
jar --create --file=mlib/org.astro-1.0.jar --module-version=1.0 -C mods/org.astro .
jar --create --file=mlib/com.greetings.jar --main-class=com.greetings.Main -C mods/com.greetings .
ls mlib

Run module directly:
java -p mlib -m com.greetings
