
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Application {

    public static void main(String[] args) throws IOException {

        List<Set<String>> stringList = new ArrayList<>();
        for(int i=0; i < 100; i++) {
            Generator gen = new Generator();
            stringList.add(gen.createStrings());
        }
        
        System.out.print("press Enter to continue:");
        System.in.read();
    }


}
