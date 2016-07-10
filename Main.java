
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class Main {

   public static void main(String[] args) throws Exception {

	System.out.println("Hello Java 9! This process PID is " + ProcessHandle.current().getPid());

	ProcessBuilder builder = new ProcessBuilder("ls");
	builder.redirectErrorStream(true);

	Process process = builder.start();

	try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	 	String line;
		while ((line = in.readLine()) != null) {
		    System.out.println(line);
		}
	}

	// define a function to call when the child process ends
	// and block until that function completes
	process.onExit()
		.whenComplete( Main::mainFinished )
		.get();

   }

   public static void mainFinished(Process p, Throwable t) {
      System.out.println("process " + p.getPid() + " finished.");
   }

}
