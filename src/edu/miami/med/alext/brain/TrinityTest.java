package edu.miami.med.alext.brain;

import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by alext on 4/24/14.
 */
public class TrinityTest {

    @Test
    public void test(){
        final File trinityExec=new File("/opt/trinityrnaseq_r20131110/trinity");
        final File lLane=new File("/home/alext/Documents/Brain/full_process_of_SRP005169/SRR112674/SRR112674_1.rest.fastq");
        final File rLane=new File("/home/alext/Documents/Brain/full_process_of_SRP005169/SRR112674/SRR112674_1.rest.fastq");
        final boolean forward=true;
        final int numThreads=12;
        final int minContigLenghth=70;
        final String seqType=Trinity.FASTQ;
        final String jmMemory="50G";

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<File>future=executorService.submit(Trinity.newInstance(trinityExec, lLane, rLane, forward, numThreads, minContigLenghth, seqType, jmMemory));
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();

    }


}
