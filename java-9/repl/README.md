
# Experiments with Java 9's REPL


## To run jshell with your own project classes and dependencies

https://github.com/johnpoth/jshell-maven-plugin

https://github.com/bitterfox/jshell-gradle-plugin

## To try out a library like Google Guava:

```
wget http://central.maven.org/maven2/com/google/guava/guava/23.5-jre/guava-23.5-jre.jar
jshell --class-path guava-23.5-jre.jar guava-test.sh
```
then inside jshell, run 

```
/edit
```

## To run Java code like a script

```
jshell hello.jsh
```

## To load definitions at startup

We can load some definitions so they are are always present (even after /reset). Note this completely replaces the default startup definitions.

```
jshell --startup bash.jsh
```

then inside jshell, run 

```
ls()
```

## Can run Java code to interactively work with remote systems

One good example is interacting with a web API that uses Protobuf
We can build objects and make network calls from the command line

## To run JShell from within your IDE:

Add your own project as a library so IntelliJ can access it from JShell


## To specify JVM args

launch with -J[args]

For example, launch with jshell -J-Xmx2g

note that now you can do something like this:
jshell> new int[1000000000];

(maybe for a fast-running script, you can specify the Epsilon GC from Java 11)



