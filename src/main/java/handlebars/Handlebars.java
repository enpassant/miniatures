package handlebars;

import com.github.enpassant.ickenham.*;
import com.github.enpassant.ickenham.adapter.JavaAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;

class Handlebars {
  public static void main(String[] args) {
    Ickenham ickenham = new Ickenham(new JavaAdapter());

    scala.Function1<Object, Object> template = ickenham.compile("comment");

    try {
        String jsonString = ickenham.loadFile("comments.json");
        JSONParser parser = new JSONParser();
        Object json = parser.parse(jsonString);

        System.out.println("Html: " + template.apply(json));
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
  }
}

