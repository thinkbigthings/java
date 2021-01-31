
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class Application {

   public static void main(String[] args) throws IOException {
      Generator gen = new Generator();
      System.out.println("Generated strings: " + gen.createStrings());
   }

}
