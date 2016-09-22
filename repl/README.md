
# Experiments with Java 9's REPL


## To try out a library like Google Guava:

```
wget http://central.maven.org/maven2/com/google/guava/guava/19.0/guava-19.0.jar
jshell --class-path guava-19.0.jar 
```
then inside jshell, run 

```
/open guava-test.jsh
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

