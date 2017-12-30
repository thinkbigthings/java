
# Experiments with Java 9's REPL

## To run code using libraries you use in your project:

TODO find a way to get the dependency and project libraries on a classpath string
and pass them to jshell. Would like to use maven and gradle.
Would also like this to work if you have war packagings on the cp.

https://stackoverflow.com/questions/47705036/in-jshell-how-to-import-classpath-from-a-maven-project


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
