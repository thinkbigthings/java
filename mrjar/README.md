

http://openjdk.java.net/jeps/238

In MANIFEST.MF
Multi-Release: true

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

javac -d build7 -source 1.7 -target 1.7 src7/*.java
javac -d build8 -source 1.8 -target 1.8 src8/*.java
javac -d build9 src9/*.java
