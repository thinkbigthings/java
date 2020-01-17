import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

public class Demo {

    public static void main(String[] args) {
   
   
        List<String> names = new ArrayList<>();
        names.add("a");
        names.add("b");
        names.add(null);



        List<Either<? extends Exception, Integer>> counts = names.stream()
           .map(Either.lift(String::length))
           .collect(toList());

        List<? extends Exception> exceptions = counts.stream()
            .flatMap(e -> e.streamLeft())
            .collect(toList());

        List<Integer> successes = counts.stream()
            .flatMap(e -> e.streamRight())
            .collect(toList());


	System.out.println(counts);
	System.out.println(exceptions);
	System.out.println(successes);

// java --enable-preview --source 14 Demo.java
// interesting functional reading: https://github.com/hemanth/functional-programming-jargon




// TODO try to write a collectingIf(element, predicate, collection supplier)
// or collectLeft() to assume you take an Either and extract left from it
// or collectExceptions() to collect to a List and map the successful entries to their values and continue the stream
// or unwrapTry() to collect exceptions to a list and return the possibly empty result in a stream... flatMap(t -> unwrapTry(t, () -> exceptions))
// or should a try/catch process the exception immediately to a callback, forwarding only results, eliminating need for Either or Try?
// (modify withExceptionCapture)


// TODO contrast with var as intermediate type
// https://github.com/thinkbigthings/java/blob/master/java-10/Main.java

// TODO use Try with alternative constructors instead of static factory methods

// TODO can a record type be declared twice identically in different scopes/methods? Referenced from
   

// can't assign a Range to a MinMax or cast to it
// would need to implement an interface that matches the generated methods
record MinMax(int min, int max) {}
record Range(int min, int max) {}

// records can take other records
record Range2D(Range x, Range y) {}


// static class TestInner {} // can't use static on a class defined inside a method
// inner classes aren't usually defined inside a method because... boilerplate? Less readable?

// reference to inner class has nowhere to hide!
MinMax m = new MinMax(0,0);



        List<EitherRecord<? extends Exception, Integer>> counts2 = names.stream()
           .map(withExceptionCapture(String::length))
           .collect(toList());

	List<? extends Exception> exceptions2 = counts2.stream()
		.flatMap(e -> e.streamLeft())
		.collect(toList());

	List<Integer> successes2 = counts2.stream()
		.flatMap(e -> e.streamRight())
		.collect(toList());


	System.out.println(counts2);
	System.out.println(exceptions2);
	System.out.println(successes2);





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

	record Try<R>(Exception exception, R result) {

        	public static <R> Try<R> Result(R r) {
            		return new Try<R>(null, r);
        	}
        	public static <R> Try<R> Result(Exception ex) {
            		return new Try<R>(ex, null);
        	}
		public Optional<? extends Exception> getException() {
			return Optional.ofNullable(exception());
		}
		public Optional<R> getResult() {
			return Optional.ofNullable(result());
		}
    		public Stream<? extends Exception> streamException() {
        		return getException().stream();
    		}
    		public Stream<R> streamResult() {
        		return getResult().stream();
    		}
	}

    record EitherRecord<E, T>(E left, T right) {

        public static <E,T> EitherRecord<E,T> Left(E e) {
            return new EitherRecord <E,T>(e, null);
        }
        public static <E,T> EitherRecord<E,T> Right(T t) {
            return new EitherRecord <E,T>(null, t);
        }
	public Optional<E> getLeft() {
		return Optional.ofNullable(left());
	}
	public Optional<T> getRight() {
		return Optional.ofNullable(right());
	}
    	public Stream<E> streamLeft() {
        	return getLeft().stream();
    	}
    	public Stream<T> streamRight() {
        	return getRight().stream();
    	}
    }
   
    public static <T,R> Function<T, EitherRecord<? extends Exception, R>> withExceptionCapture(CheckedFunction<T,R> function) {
    	return t -> {
    		try {
      			return EitherRecord.Right(function.apply(t));
    		} catch (Exception ex) {
      			return EitherRecord.Left(ex);
    		}
  	};
    }

public static class Either<L, R> {
    private final L left;
    private final R right;
    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }
    public static <L,R> Either<L,R> Left( L value) {
        return new Either(value, null);
    }
    public static <L,R> Either<L,R> Right( R value) {
        return new Either(null, value);
    }
    public Optional<L> getLeft() {
        return Optional.ofNullable(left);
    }
    public Stream<L> streamLeft() {
        return getLeft().stream();
    }
    public Optional<R> getRight() {
        return Optional.ofNullable(right);
    }
    public Stream<R> streamRight() {
        return getRight().stream();
    }
    public boolean isLeft() {
        return left != null;
    }
    public boolean isRight() {
        return right != null;
    }

    public static <T,R> Function<T, Either<? extends Exception, R>> lift(CheckedFunction<T,R> function) {
    	return t -> {
    		try {
      			return Either.Right(function.apply(t));
    		} catch (Exception ex) {
      			return Either.Left(ex);
    		}
  	};
    }
    public <T> Optional<T> mapLeft(Function<? super L, T> mapper) {
        return getLeft().map(mapper);
    }
    public <T> Optional<T> mapRight(Function<? super R, T> mapper) {
        return getRight().map(mapper);
    }
    public String toString() {
        if (isLeft()) {
            return "Left(" + left +")";
        }
        return "Right(" + right +")";
    }
}





@FunctionalInterface
public interface CheckedFunction<T,R> {
    R apply(T t) throws Exception;
}

public static <T,R> Function<T,R> uncheck(CheckedFunction<T,R> checkedFunction) {
  return t -> {
    try {
      return checkedFunction.apply(t);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  };
}
}