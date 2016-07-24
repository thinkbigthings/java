
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

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
            return Set.of("a", "b");
        }

        public Set<String> createStringsInnerClass() {
            Set<String> strings = new HashSet<String>() {{
                add("a"); add("b");
            }};
            return strings;
        }

        public Set<String> createStringsNormally() {
            Set<String> strings = new HashSet<String>();
            strings.add("a");
            strings.add("b");
            return strings;
        }
    }
}
