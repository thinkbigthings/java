
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
        
        var isEven = (Predicate<Integer>) x -> x%2==0;  // legal with Java 10
        Predicate<Integer> isEven2 = (var x) -> x%2==0; // legal with Java 11 (var on lambda parameter)

        
        // no val/let, can use "final var"

        // can declare anonymous classes and use a new scoped type
        // note this is not dynamic typic! Everything still has a fixed type
        var person = new Object() {
           String name = "bob";
           int age = 5;
        };

        // even if declared as Object (without var), this is impossible before Java 10
        System.out.println(person.name + " aged " + person.age);


        // easier to make tuple types, 
        // can pass multiple values through the Stream API in a type safe way
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
        // Note, broken in JShell 10 (https://bugs.openjdk.java.net/browse/JDK-8199907)
        var duck = (Quacks & Waddles) Mixin::create;
        duck.quack();
        duck.waddle();
        

        // TODO try other mixin ideas from above links
        Mallard m = new Mallard();
        
        // this is one way to do it. What does this get you over composition?
        

        // need functional interface to do this trick, otherwise can't cast object to the new interface
        
        var v = (MallardExtension & Bird) () -> m;
        v.flyPlus();
        
        // what if you don't delegate and just extend Bird? 
        // you can cast as just the extension method and assign to extension interface, but extension interface needs to extend the original interface
        // it's clearer to extend just the delegate and keep the type information here at the assignment where you read it
        // also MallardExtension isn't a functional interface if it extends Bird, so assignment won't work
      //  MallardExtension me = (MallardExtension & Bird) () -> m;
      //   me.flyPlus();
        
    }
    
    public static final class Mallard implements Bird {
        public void fly() { System.out.println("fly"); }
        public void doStuff(){}
    }
    
    // if we implement Bird but don't delegate fly(), you get the fly() implementation of the default Bird interface
    // but still can't override it here. Needs single method to satisfy functional interface
    interface MallardExtension extends BirdDelegate {
       // void toStringCustom() { System.out.println("custom fly"); }
        default void flyPlus() {
            delegate().fly();
            System.out.println("and more");
        }
    }
    
    @FunctionalInterface
    interface BirdDelegate {
        Bird delegate();    
    }
    
    interface Bird {
        default void fly() {}
        //void doStuff();
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
