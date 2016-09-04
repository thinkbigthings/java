
import java.util.concurrent.CompletableFuture;

import java.io.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import static java.net.http.HttpRequest.*;
import static java.net.http.HttpResponse.*;

// http://download.java.net/java/jdk9/docs/api/index.html
// http://download.java.net/java/jdk9/docs/api/java/net/http/HttpRequest.html

public class Main {

    public static void main(String[] args) throws Exception {

        requestStreaming();
    }


    public static void requestStreaming() throws Exception {

	// gutenberg will lock you out if you hit it too much, proceed with care
	String mobyDick = "http://www.gutenberg.org/cache/epub/2701/pg2701.txt";
        String principia = "http://www.gutenberg.org/cache/epub/28233/pg28233.txt";
	String stackOverflow = "http://stackoverflow.com";

	HttpRequest request = HttpRequest
            .create(new URI(stackOverflow))
            .body(noBody()) // this is where you could stream the request body with .bodyAsync(asInputStream())
            .GET();

long start = System.currentTimeMillis();

        HttpResponse response = request.response();

long makeRequest = System.currentTimeMillis();

        //InputStream responseBody = response.bodyAsync(HttpResponse.asInputStream()).get();
	InputStream responseBody = new ByteArrayInputStream(response.body(asString()).getBytes("UTF-8"));

long constructStream = System.currentTimeMillis();

long firstReadBody;

	BufferedReader br = new BufferedReader(new InputStreamReader(responseBody, "UTF-8"));
	try(br) {
        	String line = br.readLine();
 		firstReadBody = System.currentTimeMillis();
		while(line != null) {
System.out.println(line);
			line = br.readLine();
   		}
	}

long done = System.currentTimeMillis();

System.out.println("make request: " + (makeRequest - start));
System.out.println("construct input stream: " + (constructStream - makeRequest));
System.out.println("time to start reading stream: " + (firstReadBody - constructStream));
System.out.println("time to process stream: " + (done - firstReadBody));


	System.out.println("Done!");

	// TODO this is hanging too, can exit with System.exit(0);
        // javadocs has it with .thenCompose() and .thenApply()
        // and with CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join()
	// do I need to do something else with the Future?
        // see what thread is hanging in visualvm?

    }

    public static void requestSync() throws Exception {

        HttpResponse response = HttpRequest
            .create(new URI("http://www.stackoverflow.com"))
            .body(noBody())
            .GET().response();

        int responseCode = response.statusCode();
        String responseBody = response.body(asString());
 
	System.out.println(responseCode);
        System.out.println(responseBody);
    }
}



