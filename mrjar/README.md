

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

// this is one way to do it
source ~/.j9
rm *.jar build/*
javac -d build -source 1.7 -target 1.7 src/*.java
javac -d build/META-INF/versions/9 -source 9 -target 9 src9/*.java
jar --create --file mrjar.jar --manifest MANIFEST.MF --main-class=Application -C build .


TODO try mrjar-specific jar command toptions
  -f, --file=FILE            The archive file name
      --release VERSION      Places all following files in a versioned directory
                             of the jar (i.e. META-INF/versions/VERSION/)

but getting "unrecognized option : --release"

source ~/.j9
rm *.jar build/*
javac -d build -source 1.7 -target 1.7 src/*.java
jar --create --file mrjar.jar --manifest MANIFEST.MF --main-class=Application -C build .
rm build/*
javac -d build -source 9 -target 9 src9/*.java
jar --update --file mrjar.jar --release 9 --verbose -C build .

why keep it in one project?
- code changes together should stay together
- makes more sense to have one project build to one jar instead of multiple projects targeting a single jar
- chance of defining source/target in subprojects to not be what the mrjar needs







