
import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws Exception {

		// resources may be declared outside the try statement (if effectively final)
		Reader reader = new InputStreamReader(new FileInputStream("Main.java"));
		BufferedReader in = new BufferedReader(reader);
		List<String> lines = new ArrayList<>();
		try(in) {
			in.lines().forEach(n -> lines.add(n));
		}
		catch(Exception ex) {
			System.out.println("Don't just swallow exceptions, please.");
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

		// @SafeVarargs can now be put on private methods (formerly only static or final)
		// interfaces can now have private methods
		@SafeVarargs
		private List<String> flattenStrings(List<String>... lists) {

			// anonymous classes can now use type inference
			// single underscore can NO LONGER be used as variable name
			Set<String> _strings = new HashSet<>(){};
			for(List<String> list : lists) {
				_strings.addAll(list);
			}
			return new ArrayList<>(_strings);
		}

	}
}

