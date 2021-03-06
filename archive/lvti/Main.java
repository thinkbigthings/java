


package org.thinkbigthings.demo.lvti;

import java.lang.annotation.*;
import java.util.*;
import java.util.function.*;
import java.time.*;
import static java.util.stream.Collectors.*;


/**

http://openjdk.java.net/jeps/286

http://benjiweber.co.uk/blog/2018/03/03/representing-the-impractical-and-impossible-with-jdk-10-var/amp/

https://gist.github.com/benjiman/a8945f378691f4c1d258a12bed825ec2

*/
public class Main {

    public static void main(String[] args) {
    
        // can simplify simple declarations, but that's just convenience, doesn't change much
        var names = List.of("here", "is", "a", "word", "list");
        System.out.println(names);
        
        // "Poly Expressions" that require such a type,
        // like lambdas, method references, and array initializers, trigger an error
        //
        //  https://stackoverflow.com/questions/49134118/array-initializer-needs-an-explicit-target-type-why
        //
        //  var out = System.out::println;  // ILLEGAL!
        //  var f = (int x) -> x*x;         // ILLEGAL!
        //
        //  var f1 = (UnaryOperator<Integer>)((Integer x) -> x * x);  // LEGAL (but should we do this?)
        //  UnaryOperator<Integer> f2 = x -> x * x;                   // LEGAL (and probably more readable)

        // but this is legal
        var f = (IntUnaryOperator) (int a) -> a * a;
        System.out.println(f.applyAsInt(2));

        // and this may be more readable
        IntUnaryOperator f2 =  a -> a * a;


        // you can put @NotNull on a lambda parameter
        // annotations can be applied to local variables and lambda variables
        var isEven = (Predicate<Integer>) x -> x%2==0;  // legal with Java 10
        Predicate<Integer> isEven2 = (@NotNull var x) -> x%2==0; // legal with Java 11 (var on lambda parameter)

        // there is no val/let, can use "final var"
        final var string = "can't touch this";
        // string = ""; // Causes error at compile time
            
        // can declare anonymous classes and use a new scoped type
        // note this is not dynamic typing! Everything still has a fixed type known at compile time
        var person = new Object() {
           String name = "bob";
           int age = 5;
        };

        // even if declared as Object (without var), this is impossible before Java 10
        System.out.println(person.name + " aged " + person.age);


        // what if you want to process a stream of data and retain the original with its processed version?
        // here's a Java 8 approach. This works but only with two values
        // what if you wanted to maintain a stream of three processed values? Nothing built-in anymore!
        // not to mention, using the built-in Map Entry type is not so readable.
        names.stream()
            .map(n -> new AbstractMap.SimpleEntry<>(n, n.length()))
            .filter(t -> t.getValue() > 3)
            .map(t -> t.getKey())
            .forEach(System.out::println);
            

        // easier to make tuple types, 
        // can pass multiple values through the Stream API in a type safe way
        // this is impossible before Java 10
        // https://stackoverflow.com/questions/43987285/implied-anonymous-types-inside-lambdas
        // https://blog.codefx.org/java/tricks-var-anonymous-classes/
        // cons: could affect readability, memory, and risk linking to enclosing class
        names.stream()
            .map(n -> new Object() {
                    String word = n;
                    int length = n.length();
                    Instant processedTimestamp = Instant.now();
                })
            .filter(t -> t.length > 3)
            .map(t -> t.word)
            .forEach(System.out::println);

        // an anonymous inner class hold a reference to the enclosing class, 
        // what is the enclosing class in this case: the stream ? 
        // I think this is a poor practice like double brace initialization
        // this is called out as a "neat trick" but not by the JEP or language designers themselves,
        // which likely indicates that this is not an intended use case
        
        // we can collect the anonymous type to a Set
        // the destination set needs to be "var" instead of "Set" 
        // otherwise it will think you mean Set<Object> and you'll lose access to the type
        // Note that this is LOCAL type inference... 
        // the type of an anonymous subclass can't be in the return signature of a method
        System.out.println("collecting");
        var longNames = names.stream()
            .map(n -> new Object() {
                    String word = n;
                    int length = n.length();
                    Instant processedTimestamp = Instant.now();
                })
            .filter(t -> t.length > 3)
            .collect(toSet());
        System.out.println(longNames.iterator().next().processedTimestamp);

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface NotNull {
	    public boolean enabled() default true;
    }


}
