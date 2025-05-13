package cc.carm.lib.configuration.value.text.tests;

import cc.carm.lib.configuration.value.text.PreparedText;
import cc.carm.lib.configuration.value.text.data.TextContents;
import org.junit.Test;

import java.io.PrintStream;
import java.util.*;


public class ParseTest {


    @Test
    public void test() {

        List<String> lines = new ArrayList<>();
        lines.add("Hello, %(name)");
        lines.add("#more-creating#{1}");
        lines.add("This is a test message");
        lines.add("#guidance#");
        lines.add("{ -   }#websites#{0,1}");
        lines.add("Thanks for your reading!");
        lines.add("uuid: $UUID$");
        lines.add("?[click]");
        lines.add("?[click]Click to see more!");
        lines.add("?[hidden]This entry should be hidden!");

        Map<String, List<String>> optional = new HashMap<>();
        optional.put("guidance", Arrays.asList("To get more information for %(name),", " see:"));
        optional.put("websites", Arrays.asList("https://www.baidu.com", "https://www.google.com"));

        TextContents textContents = new TextContents(lines, optional);

        PreparedText<String, PrintStream> msg = new PreparedText<String, PrintStream>(textContents)
            .dispatcher((p, s) -> s.forEach(p::println))
            .parser((p, s) -> s)
            .compiler((p, s) -> s)
            .replace(    // Custom replacer, replace $UUID$ with Random UUID
                "$UUID$", () -> UUID.randomUUID().toString()
            );

        msg.placeholder("name", "Carm")
            .insert("guidance")
            .insert("click")
            .insert("websites", "Baidu", "Bilibili", "Google");


        System.out.println("----------------------------");
        msg.to(System.out);
        System.out.println("----------------------------");
        System.out.println(msg.compileLine(System.out));
        System.out.println("----------------------------");

    }


}
