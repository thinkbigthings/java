
package was java.net.http, was moved to package jdk.incubator.http (as of EA b149)

javadocs are downloaded with regular javadocs and available at
docs/jre/api/incubator/httpclient/spec/index.html

// to build with incubator module, need to explicitly add it
javac --add-modules jdk.incubator.httpclient Main.java

// to run it, again add the incubator module
java --add-modules jdk.incubator.httpclient Main

