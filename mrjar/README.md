
Multi-release jars, see http://openjdk.java.net/jeps/238

Jar file has this structure:

jar root
  - A.class
  - B.class
  - C.class
  - D.class
  - META-INF
     - versions
        - 9
           - A.class
           - B.class
        - 10
           - A.class

Class file structure is in the specification...
There is still uncertainty about how source code will be structured:
multi module project (maven/etc module not JPMS), or multiple versions of Java in one project?
http://in.relation.to/2017/02/13/building-multi-release-jars-with-maven/
https://stackoverflow.com/questions/47648533/how-to-make-multi-release-jar-files-with-gradle

Multi module projects are easier for existing tools
For example, IntelliJ recommends separate IDEA modules since each project targets only one Java version
https://blog.jetbrains.com/idea/2017/10/creating-multi-release-jar-files-in-intellij-idea/

I personally like the idea of keeping source for different versions in one project:
- code that changes together should stay together
- makes more sense to have one project build to one jar instead of multiple projects targeting a single jar
- reduce risk of defining release version in subprojects to not be what the mrjar needs



COMMANDS

// this is a java 9 jar command way to do it
rm -rf *.jar build/* build9/*
javac -d build --release 8 src/main/java/*.java
javac -d build9 --release 9 src/main/java-9/*.java
jar --create --main-class=Application --file mrjar.jar -C build . --release 9 -C build9 .

// see files
jar --list --file mrjar.jar

// execute jar file
java -jar mrjar.jar
~/opt/jdk1.8.0_141/bin/java -jar mrjar.jar

// see all relevant files at once
gedit MANIFEST.MF src/main/java/Application.java src/main/java/Generator.java src/main/java/Generator.java src/main/java-9/Generator.java README.md &





