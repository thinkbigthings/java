
import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws Exception {

		// Coin 1: resources may be declared outside the try statement (if effectively final)
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("Main.java")));
		List<String> lines = new ArrayList<>();
		try(reader) {
			reader.lines().forEach(lines::add);
		}
		catch(Exception ex) {
			// Never swallow exceptions, right?
		}

		ListProcessor processor = new ListProcessor() {};
		
		int numOriginal = lines.size();
		int numFlat = processor.flatten(lines).size();
		System.out.println("number of duplicate lines: " + (numOriginal - numFlat));
	}

	interface ListProcessor {

		default List<String> flatten(List<String>... lists) {
			return flattenStrings(lists);
		}

		// Coin 2: @SafeVarargs can now be put on private methods (formerly only static or final)
		// Coin 3: interfaces can now have private methods (no ambiguity if extending with same method)
		@SafeVarargs
		private List<String> flattenStrings(List<String>... lists) {

			// Coin 4: anonymous classes can now use inference for generic types
			// Coin 5: single underscore can NO LONGER be used as variable name
			Set<String> _strings = new HashSet<>(){};
			for(List<String> list : lists) {
				_strings.addAll(list);
			}
			return new ArrayList<>(_strings);
		}

	}
}

