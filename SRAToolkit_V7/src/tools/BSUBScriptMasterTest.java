package tools;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by alext on 10/16/14.
 * TODO document class
 */
public class BSUBScriptMasterTest {

    @Test
    public void test() {

        final String name = "SRP017329";
        final String task = "BMT";
        final int hours = 48;
        final String queue = "general";
        final int cores = 5;
        final boolean emailOnStart = true;
        final boolean emailOnFinish = true;
        final String email = "a.tuzhikov@med.miami.edu";
        final String command = "test";
        final int fullLength = 79;
        final int stepping = cores;
        final Path dir = Paths.get("/home/alext/Documents/Brain/SRP017329");

        try {


            new BSUBScriptMaster(name, task, hours, queue, cores, emailOnStart, emailOnFinish, email, command, fullLength, stepping, dir).generate();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
