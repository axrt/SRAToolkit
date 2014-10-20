package tools;

import process.CallableProcessExecutor;
import process.FixThreadCallableProcessExectuor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by alext on 10/7/14.
 * TODO document class
 */
public class BMTaggerHelper {

    private BMTaggerHelper() {
        throw new AssertionError("Non-instantiable!");
    }

    public static List<File> backlistsFromDefaultFastQ(File bmTaggerExec, File bitmask, File sRPrism, File tmpDir, List<File[]> fastqFiles, int numThreads) throws ExecutionException, InterruptedException {
        CallableProcessExecutor<File, Callable<File>> fileCallableProcessExecutor = null;
        final List<File> blacklistFiles;
        try {
            fileCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(numThreads);
            for (File[] file : fastqFiles) {
                if (file.length > 1) {
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
            }

            blacklistFiles = new ArrayList<>();
            for (Future<File> future : fileCallableProcessExecutor.getFutures()) {
                blacklistFiles.add(future.get());
            }
        } finally {
            if (fileCallableProcessExecutor != null) {
                fileCallableProcessExecutor.shutdown();
            }
        }
        return blacklistFiles;
    }

}
