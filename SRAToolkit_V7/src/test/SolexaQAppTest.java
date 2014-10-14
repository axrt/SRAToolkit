package test;

import process.CallableProcessExecutor;
import process.FixThreadCallableProcessExectuor;
import tools.SolexaQA;
import tools.SolexaQAPP;
import tools.SolexaQAPPAnanlysis;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by alext on 10/13/14.
 * TODO document class
 */
public class SolexaQAppTest {

    @org.junit.Test
    public void testAnalysis() {

        final File exec = new File("/usr/local/bin/solexaqapp");
        final List<File> input = Arrays.asList(new File[]{new File("/home/alext/Documents/Brain/SRP033725/SRR1047833_1.fastq"), new File("/home/alext/Documents/Brain/SRP033725/SRR1047833_2.fastq")});
        final Path outDir = Paths.get("/home/alext/Documents/Brain/SRP033725/out");
        final SolexaQAPPAnanlysis.SolexaQAPPAnalysisBuilder builder = new SolexaQAPPAnanlysis.SolexaQAPPAnalysisBuilder(exec, input, outDir);

        builder.probeCutoff(0.05).variance(true).sample(10000).format(SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat.IONTORRENT).build();

        final CallableProcessExecutor<List<SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult>, SolexaQA<SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult>> executor =
                FixThreadCallableProcessExectuor.newInstance(1);

        executor.addProcess(builder.build());
        try {
            for (Future<List<SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult>> future : executor.getFutures()) {

                for (SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult result : future.get()) {
                    System.out.println(result.getMatrix());
                    System.out.println(result.getMatrix_pdf());
                    System.out.println(result.getQuality());
                    System.out.println(result.getQuality_pdf());
                    System.out.println(result.getSegments());
                    System.out.println(result.getSegments_cumulative_pdf());
                    System.out.println(result.getSegments_hist_pdf());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}
