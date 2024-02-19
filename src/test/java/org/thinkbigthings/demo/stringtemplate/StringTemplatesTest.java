package org.thinkbigthings.demo.stringtemplate;

import org.junit.jupiter.api.Test;

import static java.lang.StringTemplate.RAW;

public class StringTemplatesTest {

    // TODO collect arguments from https://www.reddit.com/r/java/comments/1aros51/jep_465_string_templates/

    @Test
    public void test(){

        StringTemplate.Processor<String, RuntimeException> defaultProcessor = STR;
        StringTemplate.Processor<StringTemplate, RuntimeException> rawProcessor = RAW;

        // RAW is a standard template processor that produces an unprocessed StringTemplate object
        String name = "Joan";
        StringTemplate template = rawProcessor."My name is \{name}";
        String info = defaultProcessor.process(template);

        // or...

        String info2 = STR."My name is \{name}";

        var INTER = StringTemplate.Processor.of((StringTemplate st) -> {
            String placeHolder = "•";
            String stencil = String.join(placeHolder, st.fragments());
            for (Object value : st.values()) {
                String v = String.valueOf(value);
                stencil = stencil.replaceFirst(placeHolder, v);
            }
            return stencil;
        });

        int x = 10, y = 20;
        String s = INTER."\{x} plus \{y} equals \{x + y}";
        System.out.println(s);

        // STR doesn't help here, does it?
        String lastName = "Smith' OR p.last_name <> 'Smith";
        String query = STR."SELECT * FROM Person p WHERE p.last_name = '\{lastName}'";
        System.out.println(query);
    }
}
