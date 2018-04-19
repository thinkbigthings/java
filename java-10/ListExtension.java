
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**

http://openjdk.java.net/jeps/286

http://benjiweber.co.uk/blog/2018/03/03/representing-the-impractical-and-impossible-with-jdk-10-var/amp/

https://gist.github.com/benjiman/a8945f378691f4c1d258a12bed825ec2

*/
public class ListExtension {
    public static void main(String[] args) {
    
    
        List numbers = List.of(1, 2, 3, 4, 5);
        
        // have trouble putting List in cast, as List is not a functional interface
        // would need the extension method to extend List and implement its methods as in original gist (they don't actually cast to List)
        // still not sure what that gets you over a wrapper class that forwards calls that you can add methods to.
        var filterableNumbers = (Filterable<Integer>) () -> numbers;

        System.out.println(filterableNumbers.filter( n -> n%2==0));
        System.out.println(filterableNumbers.delegate());
        
    }

    // can't extend List as it has unimplemented non-default methods
    // and List is not a functional interface
    interface Filterable<T> extends ListDelegate<T>, List<T> {
        default List<T> filter(Predicate<T> predicate) {
            return delegate().stream().filter(predicate).collect(Collectors.toList());
        }
    }
    
    @FunctionalInterface
    interface ListDelegate<T> {
        List<T> delegate();    
    }
 
}
