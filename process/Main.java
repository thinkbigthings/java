
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

   public static void main(String[] args) throws Exception {

	System.out.println("This process PID is " + ProcessHandle.current().pid());
	System.out.println("(We can only get the PID this way in Java 9)");

	ProcessBuilder builder = new ProcessBuilder("ls");
	builder.redirectErrorStream(true);

	Process childProcess = builder.start();

	try (BufferedReader in = new BufferedReader(new InputStreamReader(childProcess.getInputStream()))) {
		in.lines().forEach(System.out::println);
	}

	// define a function to call when the child process ends
	// and block until that function completes
	childProcess.onExit()
                    .whenComplete( Main::mainFinished )
                    .get();

   }

   public static void mainFinished(Process p, Throwable t) {
      System.out.println("Child process " + p.pid() + " finished.");
      System.out.println("(Can only post-process like this in Java 9, too)");
   }

}
