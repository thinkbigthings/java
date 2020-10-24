package org.thinkbigthings.demo.java14;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;

public class FlightRecorderDemo {

    public static void main(String[] args) {


        // java --enable-preview --source 14 FlightRecorderDemo.java


        // https://stackoverflow.com/questions/57841060/why-have-i-more-than-one-jfr-recording
        
        // Try JFR streaming
        // https://qconsf.com/system/files/presentation-slides/mikael_vidstedt_-_qconsf-continuous_monitoring_with_jdk_flight_recorder.pdf

        // https://mbien.dev/blog/entry/jfr-event-streaming-with-java

        // https://download.java.net/java/early_access/jdk14/docs/api/jdk.jfr/jdk/jfr/package-summary.html

        // https://github.com/jiekang/jfr-datasource


        //# Start a recording
        //        java -XX:StartFlightRecording ...
        //# Start a recording, and store it to file
        //        java –XX:StartFlightRecording:filename=/tmp/foo.jfr ...
        //# Enable recording in an already running VM (pid 4711)
        //# jcmd <pid | main class name> JFR.start [options]
        //        jcmd 4711 JFR.start OR jcmd MyApplication JFR.start
        //# Dump a recording from running VM (pid 4711), at most 50MB of data
        //        jcmd 4711 JFR.dump maxsize=50MB

        //        # Print summary of recording
        //        jfr summary myrecording.jfr
        //# Print events
        //        jfr print myrecording.jfr
        //# Print events in JSON format
        //        jfr print --json myrecording.jfr
        //# Print GC related events
        //        jfr print --categories "GC" myrecording.jfr

        // https://download.java.net/java/early_access/jdk14/docs/api/jdk.jfr/jdk/jfr/consumer/RecordingStream.html

        // A list of available event names can be retrieved
        // jshell> jdk.jfr.FlightRecorder.getFlightRecorder().getEventTypes().stream().map(t -> t.getName()).forEach(n -> System.out.println(n));



        startEventStream(List.of("jdk.CPULoad"), java.time.Duration.ofSeconds(15));

        try{Thread.sleep(3_000);}
        catch(InterruptedException ie) {}

        // try pegging the CPU and telling by the event stream when the computation is happening
        // TODO log if the event if over a threshold
        IntStream.range(0, 20).parallel().forEach( s -> {
            java.security.SecureRandom random = new java.security.SecureRandom(java.math.BigInteger.ONE.toByteArray());
            for(long i=0; i < 1_000_000; i++) {
                int j = random.nextInt();
                var x = new Object() { int k = j; };
            }
        });

        try{Thread.sleep(3_000);}
        catch(InterruptedException ie) {}
    }

    public static void startEventStream(List<String> eventNames, java.time.Duration eventStreamDuration) {

        java.time.Duration pollInterval = java.time.Duration.ofMillis(500);

        // TODO can we call .close() on the RecordingStream? What if we wanted to close it manually?

        // TODO are we having trouble calling .start() in other ways because we didn't create the FlightRecorder?
        // or does creating a RecordingStream create the FlightRecorder for you?
        new Thread(() -> {
            try (jdk.jfr.consumer.RecordingStream rs = new jdk.jfr.consumer.RecordingStream()) {

                // period is when the event is emitted,
                // not when  output is flushed to consumer (seems to only be printed once per second regardless of event period)
                eventNames.forEach(name -> {
                    rs.enable(name).withPeriod(pollInterval);
                    rs.onEvent(name, event -> System.out.println(event));
                });

                // TODO try configuration with .setFlushInterval()
                // make notes contrasting with event period.
                // can we make it write out more frequently?
                // rs.setFlushInterval(pollInterval);

                rs.setEndTime(java.time.Instant.now().plus(eventStreamDuration));

                // this blocks on the current Thread
                rs.start();
            }
        }).start();

    }



            // from javadocs

    // jdk.jfr.Configuration
//Configuration c = Configuration.getConfiguration("default");
//try (var rs = new RecordingStream(c)) {
//     rs.onEvent("jdk.GarbageCollection", System.out::println);
//     rs.onEvent("jdk.CPULoad", System.out::println);
//     rs.onEvent("jdk.JVMInformation", System.out::println);
//     rs.start();
//   }
//}
//            rs.enable("jdk.CPULoad").withPeriod(java.time.Duration.ofSeconds(1));
//            rs.onEvent("jdk.CPULoad", event -> System.out.println(event)); // .getFloat("machineTotal")));

            // jdk.PhysicalMemory
            // jdk.JVMInformation
            //jdk.OSInformation
            // jdk.SocketWrite
            //jdk.SocketRead
            //jdk.FileWrite
            //jdk.FileRead
            // jdk.SystemProcess
            //jdk.CPUInformation
            //jdk.CPUTimeStampCounter
            //jdk.CPULoad
            //jdk.ThreadCPULoad
            //jdk.ThreadContextSwitchRate
            //jdk.NetworkUtilization
            //jdk.JavaThreadStatistics
            // jdk.ObjectCount




}