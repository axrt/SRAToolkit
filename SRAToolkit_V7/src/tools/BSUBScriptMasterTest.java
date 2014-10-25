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
        final String task = "TRINITY";
        final int hours = 48;
        final String queue = "general";
        final int cores = 32;
        final int stepping = 1;
        final boolean emailOnStart = true;
        final boolean emailOnFinish = true;
        final String email = "a.tuzhikov@med.miami.edu";
        final String command = "java -jar /nethome/atuzhikov/sratoolkit.jar \\\n" +
                "-tr \\\n" +
                "-drv /nethome/atuzhikov/target/SRP017329.xml \\\n" +
                "-bin /nethome/atuzhikov/bin/ \\\n" +
                "-dir /scratch/atuzhikov/SRP017329/ \\\n" +
                "-pext2 nan \\\n" +
                "-rfmt FastQ \\\n" +
                "-aext .encd.trimmed.rest \\\n" +
                "-min_contig_length 50 \\\n" +
                "-bflyHeapSpaceMax 20G \\\n" +
                "-JM 20G \\\n" +
                "-SS_lib_type F \\\n" +
                "-tout /scratch/atuzhikov/SRP017329/trim/ \\\n";
        final Path toDrvFolder = Paths.get("/nethome/atuzhikov/trinity/");
        final int fullLength = 79;
        final Path dir = Paths.get("/home/alext/Documents/Brain/SRP017329");

        try {


            new BSUBScriptMaster(name, task, hours, queue, cores, emailOnStart, emailOnFinish, email, command, fullLength, stepping, dir, toDrvFolder).generate();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
