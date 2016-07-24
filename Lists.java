
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Lists {

    public static void main(String[] args) throws IOException {

        List<Set<String>> stringList = new ArrayList<>();
        for(int i=0; i < 100; i++) {
            Generator gen = new Generator();
            stringList.add(gen.createStringsJava9());
        }
        
        System.out.print("press Enter to continue:");
        System.in.read();
    }

    public static class Generator {
        
        private byte[] lotsOfHiddenStuff = new byte[5_000_000];
        
        public Set<String> createStringsJava9() {
            return Set.of("Java", "9");
        }

        public Set<String> createStringsJava8() {
            return Collections.unmodifiableSet(Stream.of("Java", "8").collect(toSet()));
        }

        public Set<String> createStringsJava7() {
            Set<String> strings = new HashSet<String>();
            strings.add("Java");
            strings.add("7");
            return strings;
        }
    }
}
