
# Experiments with Java 9's REPL

## To try out a library like Google Guava:

```
wget http://central.maven.org/maven2/com/google/guava/guava/23.5-jre/guava-23.5-jre.jar
jshell --class-path guava-23.5-jre.jar 
```
then inside jshell, run 

```
/open guava-test.jsh
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


## To run JShell from within your IDE:

Add your own project as a library so IntelliJ can access it from JShell


## To specify JVM args

launch with -J[args]

For example, launch with jshell -J-Xmx2g

note that now you can do something like this:
jshell> new int[1000000000];

(maybe for a fast-running script, you can specify the Epsilon GC from Java 11)



