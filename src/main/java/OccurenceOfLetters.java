import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.*;
import java.util.stream.*;

public class OccurenceOfLetters {

    private static String phrase;

    private static Scanner scn = null;

    private static Map<Character, Integer> hashMap;

    private static Map<Character, Integer> result;

    public static void main(String[] args) {

        System.out.print("Enter your string:");

        scn = new Scanner(System.in);

        phrase = scn.nextLine();

        System.out.print("Occurences:[");

        Counter();

        System.out.println(result);

        final Map<Character, Long> occurences = phrase.toUpperCase().chars()
            .mapToObj(i -> (char) i)
            .filter(c -> c != ' ')
            .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        final List<Map.Entry<Character,Long>> sortedOccurences =
            occurences.entrySet().stream()
                .sorted(Map.Entry.<Character,Long> comparingByValue().reversed())
                .collect(Collectors.toList());

        System.out.println(sortedOccurences);
    }


    private static char Counter() {

        char actLetter;
        int count;

        hashMap = new HashMap<>();

        result = new LinkedHashMap<>();


        for(actLetter=(char)65; actLetter<=90; actLetter++)
        {
            count=0;

            for(int j=0; j<phrase.length(); j++)
            {
                if(actLetter==phrase.charAt(j) || (actLetter+32)==phrase.charAt(j))
                {
                    count++;

                }

            }

            if(count>0)
            {

                hashMap.put(actLetter, count);

                System.out.printf("%c:%d,",actLetter,count);

            }

        }
                hashMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.<Character,Integer>comparingByValue().reversed())
                    .forEach(x -> result.put(x.getKey(), x.getValue()));

        System.out.print("]");
        System.out.println();

        return actLetter;
    }
}
