package process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by alext on 10/7/14.
 * TODO document class
 */
public class FastqDumpHelper {

    private FastqDumpHelper(){
        throw new AssertionError("Non-instantiable!");
    }

    public static List<File[]> runTaskArray(File fastqDumpExec,int numberOfThreads, List<File> sraFiles) throws ExecutionException, InterruptedException {

        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        final List<Future<File[]>> futures=new ArrayList<>(sraFiles.size());
        for(File f:sraFiles){
            futures.add(executorService.submit(FastqDump.newInstance(
                    fastqDumpExec, f
            ))) ;
        }

        final List<File[]> fastqFiles = new ArrayList<File[]>(futures.size());
        for(Future<File[]> f:futures){
            fastqFiles.add(f.get());
        }
        executorService.shutdown();

        return fastqFiles;
    }
}
