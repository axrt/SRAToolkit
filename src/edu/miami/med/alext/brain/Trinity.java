package edu.miami.med.alext.brain;

import process.CallableProcess;
import sun.plugin2.gluegen.runtime.CPU;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by alext on 4/22/14.
 */
public class Trinity extends CallableProcess<File> {

    public static final String SEQ_TYPE = "--seqType";
    public static final String FASTQ = "fq";
    public static final String LIB_TYPE="--SS_lib_type";
    public static final String JVM_MEMORY="--JM";
    public static final String LEFT="--left";
    public static final String RIGHT="--right";
    public static final String SINGLE="--single";
    public static final String MIN_CONTIG_LENGTH="--min_contig_length";
    public static final String PROCESSORS="--CPU";
    public static final String BFLY_MAX_HEAP="--bflyHeapSpaceMax";
    public static final String BFLY_CPU="--bflyCPU";
    public static final String OUTPUT_DIR="trinity_out_dir";
    public static final String OUTPUT_FILE="Trinity.fasta";

    private final File rLane;
    private final File lLane;

    protected Trinity(ProcessBuilder processBuilder, File lLane,File rLane) {
        super(processBuilder);
        this.rLane = rLane;
        this.lLane = lLane;
        this.processBuilder.directory(lLane.getParentFile());
    }

    @Override
    public File call() throws Exception {
        for(String s:this.processBuilder.command()){
            System.out.print(s.concat(" "));
        }
        if(!new File(new File(this.lLane.getParent(),OUTPUT_DIR),OUTPUT_FILE).exists()) {
            System.out.println();
            final Process p = this.processBuilder.start();


            try (InputStream inputStream = p.getErrorStream();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println("ERR::" + this.lLane.getName() + " >" + line);
                }
            }
            try (InputStream inputStream = p.getInputStream();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println("OUT::" + this.lLane.getName() + " >" + line);
                }
            }
            p.waitFor();
        }
        return new File(new File(this.lLane.getParent(),OUTPUT_DIR),OUTPUT_FILE);
    }

    public static CallableProcess<File> newInstance(final File trinityExec, final File lLane, final File rLane,
                                                    final boolean ward, final int numThreads,final int minContigLenghth,
                                                    final String seqType, final String jmMemory) {
        final String libType;
        if(ward){
            libType="FR";
        }else{
            libType="RF";
        }
        final String[]processCall= {
                trinityExec.toString(),
                SEQ_TYPE,seqType,
                LEFT,lLane.toString(),
                RIGHT,rLane.toString(),
                JVM_MEMORY, jmMemory,
                LIB_TYPE,libType,
                PROCESSORS,String.valueOf(numThreads),
                MIN_CONTIG_LENGTH,String.valueOf(minContigLenghth),
                BFLY_MAX_HEAP, jmMemory,
                BFLY_CPU,String.valueOf(numThreads)
        };
        return new Trinity(new ProcessBuilder(processCall),lLane,rLane);

    }
    public static CallableProcess<File> newInstance(final File trinityExec, final File lane,
                                                    final boolean ward, final int numThreads,final int minContigLenghth,
                                                    final String seqType, final String jmMemory) {
        final String libType;
        if(ward){
            libType="F";
        }else{
            libType="R";
        }
        final String[]processCall= {
                trinityExec.toString(),
                SEQ_TYPE,seqType,
                SINGLE,lane.toString(),
                JVM_MEMORY, jmMemory,
                LIB_TYPE,libType,
                PROCESSORS,String.valueOf(numThreads),
                MIN_CONTIG_LENGTH,String.valueOf(minContigLenghth),
                BFLY_MAX_HEAP, jmMemory,
                BFLY_CPU,String.valueOf(numThreads)
        };
        return new Trinity(new ProcessBuilder(processCall),lane,null);
    }

}
