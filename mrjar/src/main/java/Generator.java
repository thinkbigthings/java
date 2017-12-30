
import java.util.Set;
import java.util.HashSet;

public class Generator {

    public Set<String> createStrings() {
        Set<String> strings = new HashSet<String>();
        strings.add("Java");
        strings.add("8");
        return java.util.Collections.unmodifiableSet(strings);
    }
}

