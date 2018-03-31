
import java.util.*;
import java.util.function.*;

/**

http://openjdk.java.net/jeps/286

http://benjiweber.co.uk/blog/2018/03/03/representing-the-impractical-and-impossible-with-jdk-10-var/amp/

https://gist.github.com/benjiman/a8945f378691f4c1d258a12bed825ec2

*/
public class Main {
    public static void main(String[] args) {
    
        // can simplify simple declarations, but that's just convenience, doesn't change much
        var names = Arrays.asList("here", "is", "a", "word", "list");
        System.out.println(names);
        
        // Poly expressions that require such a type, 
        // like lambdas, method references, and array initializers, trigger an error
        // ILLEGAL! var out = System.out::println;
        // ILLEGAL! var f = (int x) -> x*x;

        // but this is legal
        var f = (IntUnaryOperator) (int a) -> a*a;
        System.out.println(f.applyAsInt(2));
        
        // no val/let, can use "final var"

        // can declare anonymous classes and use a new scoped type
        // note this is not dynamic typic! Everything still has a fixed type
        var person = new Object() {
           String name = "bob";
           int age = 5;
        };

        // even if declared as Object (without var), this is impossible before Java 10
        System.out.println(person.name + " aged " + person.age);


        // easier to make tuple types, can pass multiple values through the Stream API
        // in a type safe way
        // this is impossible before Java 10
        names.stream()
        .map(n -> new Object() {
                String word = n;
                int length = n.length();
            })
        .filter(t -> t.length > 3)
        .map(t -> t.word)
        .forEach(System.out::println);

        // "&" is an intersection type
        // we can do mixins with interfaces, impossible before Java 10
        var duck = (Quacks & Waddles) Mixin::create;
        duck.quack();
        duck.waddle();
        

        // TODO try other mixin ideas from above links
        
    }
    
    
    interface Quacks extends Mixin {
       default void quack() {
           System.out.println("Quack");
       }
       // this would throw a compiler error, all methods must be implemented
       //String unimplemented();
    }

    interface Waddles extends Mixin {
       default void waddle() {
           System.out.println("Waddle");
       }
    }

    interface Mixin {
       void __noop__();
       static void create() {}
    }

}
