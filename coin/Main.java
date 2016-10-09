
import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws Exception {

		// resources may be declared outside the try statement
		Reader reader = new InputStreamReader(new FileInputStream("Main.java"));
		BufferedReader in = new BufferedReader(reader);
		try(in) {
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
		}
	}

	interface ListProcessor {

		default List<String> flatten(List<String>... lists) {
			return flattenStrings(lists);
		}

		// @SafeVarargs can be put on a private method
		// interfaces can have private methods
		@SafeVarargs
		private List<String> flattenStrings(List<String>... lists) {

			// anonymous classes can use type inference
			// single underscore now can NOT be used as variable name
			Set<String> _strings = new HashSet<>(){};
			for(List<String> list : lists) {
				_strings.addAll(list);
			}
			return new ArrayList<>(_strings);
		}

	}
}

// http://stackoverflow.com/questions/7214069/compile-error-cannot-be-used-with-anonymous-classes
/*
Main.java:7: error: cannot infer type arguments for ArrayList<E>
		List<String> strings = new ArrayList<>(){};
		                                    ^
  reason: cannot use '<>' with anonymous inner classes
  where E is a type-variable:
    E extends Object declared in class ArrayList
1 error
ELSPHIM-4170403:~ young1$
*/
