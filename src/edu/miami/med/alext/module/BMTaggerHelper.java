package edu.miami.med.alext.module;

import edu.miami.med.alext.process.CallableProcessExecutor;
import edu.miami.med.alext.process.FixThreadCallableProcessExectuor;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by alext on 10/7/14.
 * TODO document class
 */
public class BMTaggerHelper {

    private BMTaggerHelper() {
        throw new AssertionError("Non-instantiable!");
    }

    public static List<File> backlistsFromDefaultFastQ(File bmTaggerExec, File bitmask, File sRPrism, File tmpDir, List<File[]> fastqFiles, int numThreads) {

        CallableProcessExecutor<File, Callable<File>> fileCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(numThreads);
        fastqFiles.stream().forEach(file -> {
            if (file[1].exists()) {
                fileCallableProcessExecutor.addProcess(
                        new BMTagger.BMTaggerBuilder()
                                .bmtaggerExecutale(bmTaggerExec)
                                .lLane(file[0])
                                .rLane(file[1])
                                .referenceBitmask(bitmask)
                                .referenceSrprism(sRPrism)
                                .tmpDir(tmpDir)
                                .restrictType(BMTagger.RestrictType.FastQ)
                                .build()
                );
            } else {
                fileCallableProcessExecutor.addProcess(
                        new BMTagger.BMTaggerBuilder()
                                .bmtaggerExecutale(bmTaggerExec)
                                .lLane(file[0])
                                .referenceBitmask(bitmask)
                                .referenceSrprism(sRPrism)
                                .restrictType(BMTagger.RestrictType.FastQ)
                                .tmpDir(tmpDir).build()
                );
            }
        });

        final List<File> blacklistFiles = fileCallableProcessExecutor.getFutures().stream().map(
                future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        ).collect(Collectors.toList());
        fileCallableProcessExecutor.shutdown();

        return blacklistFiles;
    }

    public static List<File[]> defaultRestrict(List<File[]>fastqFiles,List<File>blacklistFiles,int numThreads) throws InterruptedException, ExecutionException {
        final CallableProcessExecutor<File[], Callable<File[]>> fileCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(numThreads);


        for (int i = 0; i < fastqFiles.size(); i++) {
            final int j=i;
            final File[] fastqPair = fastqFiles.get(i);
            fileCallableProcessExecutor.addProcess(new Callable<File[]>() {
                @Override
                public File[] call() throws IOException {
                    final File left = BMTagger.restrict(
                            fastqPair[0], new File(fastqPair[0].getParent(),
                                    fastqPair[0].getName().replaceAll("\\.fastq", ".rest.fastq")),
                            blacklistFiles.get(j), BMTagger.RestrictType.FastQ);

                    if (fastqPair[1].exists()) {
                        return new File[]{
                                left,
                                BMTagger.restrict(fastqPair[1], new File(fastqPair[1].getParent(),
                                                fastqPair[1].getName().replaceAll("\\.fastq", ".rest.fastq")),
                                        blacklistFiles.get(j), BMTagger.RestrictType.FastQ)
                        };
                    } else {
                        return new File[]{left};
                    }
                }
            });
        }
        final List<File[]> restrictedFastqFiles;
        try {
            restrictedFastqFiles = fileCallableProcessExecutor.getFutures().stream().map(file -> {
                try {
                    return file.get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
        }catch (RuntimeException e){
            if(e.getCause() instanceof InterruptedException){
               throw (InterruptedException)e.getCause();
            }
            else if (e.getCause() instanceof ExecutionException){
                throw (ExecutionException)e.getCause();
            }
            throw e;
        }

        fileCallableProcessExecutor.shutdown();

        return  restrictedFastqFiles;
    }


}
