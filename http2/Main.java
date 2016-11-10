
import java.io.*;
import java.net.http.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

import static java.net.http.HttpRequest.*;
import static java.net.http.HttpResponse.*;

public class Main {

    public static void main(String[] args) throws Exception {

        String stackOverflow = "http://stackoverflow.com";
        requestStreaming(stackOverflow);
        // requestSync(stackOverflow);

        System.out.println("Program done.");
        System.exit(0);
    }

    public static void requestSync(String url) throws Exception {

        HttpResponse response = HttpRequest
            .create(new URI(url))
            .body(noBody())
            .GET().response();

        int responseCode = response.statusCode();
        String responseBody = response.body(asString());
 
        System.out.println("Syncronous processing Done!");
    }

    public static void requestStreaming(String url) throws Exception {

        HttpRequest request = HttpRequest
            .create(new URI(url))
            .body(noBody()) // this is where you could stream the request body with .bodyAsync(asInputStream())
            .GET();

        request.response()
                .bodyAsync(HttpResponse.asInputStream())
                .thenAccept( s -> readBody(s))
                .join();
    }

    public static void readBody(InputStream stream) {

        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            br.lines().forEach(Main::processLine);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Stream processing Done!");
    }

    public static void processLine(String line) {
        System.out.print(".");
    }
}

