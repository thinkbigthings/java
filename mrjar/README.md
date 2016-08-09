

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
javac -d build/META-INF/versions/8 -source 1.8 -target 1.8 src8/*.java
javac -d build/META-INF/versions/9 -source 9 -target 9 src9/*.java

jar --create --file mrjar.jar --manifest MANIFEST.MF -C build .



