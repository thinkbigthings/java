
import java.util.concurrent.CompletableFuture;

import java.util.*;
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

        System.out.println("Program done.");
        System.exit(0);
    }

    public static void requestSync() throws Exception {

        HttpResponse response = HttpRequest
            .create(new URI("http://www.stackoverflow.com"))
            .body(noBody())
            .GET().response();

        int responseCode = response.statusCode();
        String responseBody = response.body(asString());
 
        System.out.println("Syncronous processing Done!");
    }

    public static void requestStreaming() throws Exception {

        String stackOverflow = "http://stackoverflow.com";

        HttpRequest request = HttpRequest
            .create(new URI(stackOverflow))
            .body(noBody()) // this is where you could stream the request body with .bodyAsync(asInputStream())
            .GET();

        request.response()
                .bodyAsync(HttpResponse.asInputStream())
                .thenAccept( s -> readBody(s))
                .join();
    }

    public static void readBody(InputStream stream) {

        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line = br.readLine();
            while(line != null) {
                processLine(line);
                line = br.readLine();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Stream processing Done!");
    }

    public static void processLine(String line) throws Exception {
        System.out.print(".");
    }

}



