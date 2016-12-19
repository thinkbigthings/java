
Multi-release jars, see http://openjdk.java.net/jeps/238

Jar file has this structure:

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


There is still uncertainty about how source code will be structured. Mainly: multi module project, 
or multiple versions of Java in one project? Multi module projects are easier for existing tools,
But I would advocate to advance them to allow multiple source folders in one project.
Why keep source for different versions of java in one project?
- code that changes together should stay together
- makes more sense to have one project build to one jar instead of multiple projects targeting a single jar
- reduce risk of defining release version in subprojects to not be what the mrjar needs


COMMANDS

// this is a java 9 jar command way to do it
rm -rf *.jar build/* build9/*
javac -d build --release 7 src/main/java/*.java
javac -d build9 --release 9 src/main/java-9/*.java
jar --create --main-class=Application --file mrjar.jar -C build . --release 9 -C build9 .

// see files
jar --list --file mrjar.jar

// execute jar file
java -jar mrjar.jar

// see all relevant files at once
gedit src/main/java/Generator.java src/main/java/Generator.java src/main/java-9/Generator.java





