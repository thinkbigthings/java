

http://openjdk.java.net/jeps/238


jar root
  - A.class
  - B.class
  - C.class
  - D.class
  - META-INF
     - versions
        - 8
           - A.class
           - B.class
        - 9
           - A.class

COMMANDS

// this is one way to do it that works today

rm -rf *.jar build/*
javac -d build -release 7 src/main/java/*.java
javac -d build/META-INF/versions/9 -release 9 src/main/java-9/*.java
jar --create --file mrjar.jar --manifest MANIFEST.MF --main-class=Application -C build .
java -jar mrjar.jar



// this is a java 9 jar command way to do it
// but getting "unrecognized option : --release"
// probably not implemented for now, so you can use the above technique.


rm -rf *.jar build/*
javac -d build -release 7 src/main/java/*.java
jar --create --file mrjar.jar --manifest MANIFEST.MF --main-class=Application -C build .
rm -rf build/*
javac -d build -release 9 src/main/java-9/*.java
jar --update --file mrjar.jar --release 9 --verbose -C build .



why keep source for different versions of java in one project?
- code that changes together should stay together
- makes more sense to have one project build to one jar instead of multiple projects targeting a single jar
- chance of defining source/target in subprojects to not be what the mrjar needs







