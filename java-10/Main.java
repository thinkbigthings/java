


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
        
        // Poly expressions that require such a type, 
        // like lambdas, method references, and array initializers, trigger an error
        //  
        //  var out = System.out::println;  // ILLEGAL!
        //  var f = (int x) -> x*x;         // ILLEGAL!

        // but this is legal
        var f = (IntUnaryOperator) (int a) -> a*a;
        System.out.println(f.applyAsInt(2));

        // you can put @NotNull on a lambda parameter
        // annotations can be applied to local variables and lambda variables
        var isEven = (Predicate<Integer>) x -> x%2==0;  // legal with Java 10
        Predicate<Integer> isEven2 = (@NotNull var x) -> x%2==0; // legal with Java 11 (var on lambda parameter)

        // there is no val/let, can use "final var"
        final var string = "can't touch this";
        // string = ""; // Causes error at compile time
            
        // can declare anonymous classes and use a new scoped type
        // note this is not dynamic typic! Everything still has a fixed type
        var person = new Object() {
           String name = "bob";
           int age = 5;
        };

        // even if declared as Object (without var), this is impossible before Java 10
        System.out.println(person.name + " aged " + person.age);


        // what if you want to process a stream of data and retain the original with its processed version?
        // here's a Java 8 approach. This works but only with two values
        // what if you wanted to maintain a stream of three processed values? Nothing built-in anymore!
        names.stream()
            .map(n -> new AbstractMap.SimpleEntry<String,Integer>(n, n.length()))
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

        // this makes a stream of anonymous subclasses
        // anonymous subclasses are bound to their outer "this" (in this case FancyFilter)
        // and carry risk of memory leaks, so be wary about retaining or returning objects of anonymous subclasses
        List bigCollection = new ArrayList();
        for(int i=0; i < 100; i++) {
            bigCollection.addAll(new FancyFilter().suspiciousFilter(names));
        }
        

        // "&" helps define an intersection type
        // can use intersection types before Java 10, 
        // but var allows you to ASSIGN an intersection type in a type-safe way
        // (without declaring an explicit interface that extends both)
        // Note, broken in JShell 10 (https://bugs.openjdk.java.net/browse/JDK-8199907)
        // slated for fix in Java 13 (Sept 2019)
        var duck = (Quacks & Waddles) Mixin::create; // look! no classes!
        duck.quack();
        duck.waddle();
        
        doDucklikeThings(duck);
        
      
        // what else can we do with intersection types? It's an alternative way to compose behavior
        // need functional interface (single method interfaces) to do this trick, otherwise can't cast to the new interface
        // this could be helpful if have only one place you need this specific combination of functionality,
        // maybe in the context of breaking apart a large method and reducing number of method arguments to sub-calls
        FlyingMallard m = new FlyingMallard();
        var v = (BirdExtension & Mallard) () -> m;
        v.flyPlus();
        

        // what are some practical examples, though?
        // example with DataInput, Closeable: 
        // http://iteratrlearning.com/java/generics/2016/05/12/intersection-types-java-generics.html
        // you can do this without var, var just reduces boilerplate which is a common complaint about Java

        // can't do this with List because list doesn't have default method implementations, you'd need to extend with defaults
        // works best with stateless collections of functionality (an interface containing only pure functions, for example)
//        List<String> alphabet = List.of("a", "b", "c");
//        var alphaPlus = (ListExtension & List) () -> alphabet;
//        System.out.println(alphaPlus.hasItems());
    }

    public interface ListExtension extends ListDelegate {
        default boolean hasItems() {
            return ! delegate().isEmpty();
        }
    }

    @FunctionalInterface
    interface ListDelegate {
        List delegate();
    }

    public static <T extends Quacks & Waddles> void doDucklikeThings(T ducklike) {
        ducklike.quack();
        ducklike.waddle();
    }

    public interface Mallard {
        public default void beMallardy() {
            System.out.println("I'm a Mallard!");
        }
        public default void doMallardStuff() {
            System.out.println("I do Mallard stuff!");
        }
    }
    
    public static final class FlyingMallard implements Bird, Mallard {
        public void fly() { System.out.println("Mallard fly"); }
        public void doStuff(){}
    }
    
    // if we implement Bird but don't delegate fly(), you get the fly() implementation of the default Bird interface
    // but still can't override it here. Needs single method to satisfy functional interface
    interface BirdExtension extends BirdDelegate {
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
    }
    
    interface Quacks extends Mixin {
       default void quack() {
           System.out.println("Quack");
       }
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


    public static class FancyFilter { 
        
        private byte[] lotsOfHiddenStuff = new byte[5_000_000];
        
        public List suspiciousFilter(List<String> names) {
            var longNames = names.stream()
                                .map(n -> new Object() {
                                        String word = n;
                                        int length = n.length();
                                        Instant processedTime = Instant.now();
                                    })
                                .filter(t -> t.length > 3)
                                .collect(toList());
            return longNames;
        }
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface NotNull {
	    public boolean enabled() default true;
    }

        
}
