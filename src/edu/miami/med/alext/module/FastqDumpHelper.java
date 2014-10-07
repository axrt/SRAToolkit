package edu.miami.med.alext.module;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by alext on 10/7/14.
 * TODO document class
 */
public class FastqDumpHelper {

    private FastqDumpHelper(){
        throw new AssertionError("Non-instantiable!");
    }

    public static List<File[]> runTaskArray(File fastqDumpExec,int numberOfThreads, List<File> sraFiles){

        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        final List<Future<File[]>> fastqFutures = sraFiles.stream().map(
                sraFileToDecompress -> executorService.submit(
                        FastqDump.newInstance(
                                fastqDumpExec, sraFileToDecompress
                        )
                )
        ).collect(Collectors.toList());

        final List<File[]> fastqFiles = fastqFutures
                .stream().map(fastqFuture -> {
                    try {
                        return fastqFuture.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
        executorService.shutdown();

        return fastqFiles;
    }
}
