

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

source ~/.j9
rm *.jar build/*

javac -d build -source 1.7 -target 1.7 src/*.java
javac -d build/META-INF/versions/9 -source 9 -target 9 src9/*.java

jar --create --file mrjar.jar --manifest MANIFEST.MF --main-class=Application -C build .


TODO try mrjar-specific jar command toptions
  -f, --file=FILE            The archive file name
      --release VERSION      Places all following files in a versioned directory
                             of the jar (i.e. META-INF/versions/VERSION/)








