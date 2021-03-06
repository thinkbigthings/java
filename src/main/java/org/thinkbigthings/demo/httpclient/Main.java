package org.thinkbigthings.demo.httpclient;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.*;

import static java.net.http.HttpRequest.*;
import static java.net.http.HttpResponse.*;
import static java.nio.charset.StandardCharsets.*;

public class Main {

    public static void main(String[] args) throws Exception {

        // TODO stream request and response, all async
        // does the streaming method wait for the stream to finish before returning?
        // https://download.java.net/java/early_access/jdk11/docs/api/java.net.http/java/net/http/HttpRequest.BodyPublishers.html

        
        String stackOverflow = "https://stackoverflow.com";
        requestStreaming(stackOverflow);
        // requestSync(stackOverflow);

        System.out.println("Program done.");
        System.exit(0);
    }

    public static void requestSync(String url) throws Exception {

        // clients are immutable and thread safe
        final HttpClient client = HttpClient.newHttpClient();

        // GET
        HttpResponse<String> response = client.send(
            HttpRequest
                .newBuilder(new URI(url))
                .GET()
                .build(),
            BodyHandlers.ofString()
        );

        int statusCode = response.statusCode();
 	    processResponseBody(response.body());
    }

    public static void requestStreaming(String url) throws Exception {

        final HttpClient client = HttpClient.newHttpClient();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(
            HttpRequest
                .newBuilder(new URI(url))
                .GET()
                .build(),
            BodyHandlers.ofString()
        );

        response.thenAccept( s -> processResponseBody(s.body())).join();
    }

    public static void processResponseBody(String body) {
        processResponseBody(new ByteArrayInputStream(body.getBytes(UTF_8)));
    }

    public static void processResponseBody(InputStream stream) {

        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            br.lines().forEach(Main::processLine);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Processing Done!");
    }

    public static void processLine(String line) {
        System.out.print(".");
    }
}

