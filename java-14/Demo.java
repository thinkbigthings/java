import java.util.Optional;

public class Demo {

    public static void main(String[] args) {
   
   
        // List<String> names = List.of("a", "b", null).stream()
        //    .map(String::length)
        //    .collect(toList());
       
        // java --enable-preview --source 14 Demo.java
   
        // Also useful for equals()
        Object object = "this is a string";
        if(object instanceof String s && s.length() > 1) {
            System.out.println(s + " ... has length " + s.length());
        }
        else {
            System.out.println("object is not a string");
        }
   
    }
   

    // https://dzone.com/articles/exception-handling-in-java-streams

    // this might conflict with auto generated method called "left"?

    record Either<E extends Exception, T>(Optional<E> left, Optional<T> right) {
        public Either {
            if((left == null && right == null) || (left != null && right != null)) {
                throw new IllegalArgumentException("Must have exactly one side filled");
            }
        }
        public Either<E,T> left(E e) {
            return new Either<E,T>(Optional.of(e), Optional.empty());
        }
        public Either<E,T> right(T t) {
            return new Either<E,T>(Optional.empty(), Optional.of(t));
        }
        public boolean isLeft() {
            return left.isPresent();
        }
        public boolean isRight() {
            return right.isPresent();
        }

    }
   
    /*
    record Either<E, T>(E left, T right) {
        public Either {
            if((left == null && right == null) || (left != null && right != null)) {
                throw new IllegalArgumentException("Must have exactly one side filled");
            }
        }
        public static <T> Either<Optional<? extends Exception>, Optional<T>> left(Exception e) {
            return new Either(Optional.of(e), Optional.empty());
        }
        public static <T> Either<Optional<? extends Exception>, Optional<T>> right(T r) {
            return new Either(Optional.empty(), Optional.of(r));
        }
    }
   
*/

}