package script;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alext on 10/13/14.
 * TODO document class
 */
public class PegasusTest {

    public static void main (String[]args){
        List<String> list= Arrays.asList(new String[]{"Hello, ", "Pegasus2!\n", "I am ", "trying ", "Java 8 under", "Java 7"});
               for(String s:list){
                   System.out.print(s);
               }
    }
}
