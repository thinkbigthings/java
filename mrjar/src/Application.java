
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class Application {

    public static void main(String[] args) throws IOException {

        List<Set<String>> stringList = new ArrayList<>();
        for(int i=0; i < 3; i++) {
            Generator gen = new Generator();
            stringList.add(gen.createStrings());
        }

        System.out.println("Generated strings: " + stringList);
        
        System.out.print("press Enter to continue:");
        System.in.read();
    }

}
