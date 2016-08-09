
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Generator {
        
    public Set<String> createStrings() {
        return Collections.unmodifiableSet(Stream.of("Java", "8").collect(toSet()));
    }

}

