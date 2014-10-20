package process;

import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by alext on 10/20/14.
 * TODO document class
 */
public class AbiSolidDumpTest {


    @Test
    public void test() {
        try {

            final Path outDir = Paths.get("/home/alext/Backup/heavy/Brain/SRP017329/SRR620526");
            final File exec = new File("/usr/local/bin/abi-dump.2.3.4");
            final File sraFile = new File("/home/alext/Backup/heavy/Brain/SRP017329/SRR620526/SRR620526.sra");
            final AbiSolidDump.AbiSolidDumpBuilder abiSolidDumpBuilder = new AbiSolidDump.AbiSolidDumpBuilder(exec, sraFile, outDir);

            final ExecutorService executorService = Executors.newFixedThreadPool(1);
            final Future<AbiSolidDump.AbiSolidDumpResult> future = executorService.submit(abiSolidDumpBuilder.build());

            final AbiSolidDump.AbiSolidDumpResult abiSolidDumpResult = future.get();

            System.out.println(abiSolidDumpResult.getQual());
            System.out.println(abiSolidDumpResult.getCsFasta());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }


}
