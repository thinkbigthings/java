package org.thinkbigthings.demo.java14;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


public class DemoTextBlocks {

    public static void main(String[] args) throws Exception {


        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        engine.eval("""
                   function hello() {
                       print('"Hello, world"');
                   }

                   hello();
                   """);

        String query = """
               SELECT `EMP_ID`, `LAST_NAME` FROM `EMPLOYEE_TB`
               WHERE `CITY` = 'INDIANAPOLIS'
               ORDER BY `EMP_ID`, `LAST_NAME`;
               """;
    }
}