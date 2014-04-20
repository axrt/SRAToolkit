package edu.miami.med.alext.brain;

import process.CallableProcess;

import java.io.File;

/**
 * Created by alext on 4/20/14.
 */
public class BMTagger extends CallableProcess<File>{

    public static final String REFERENCE_BITMASK="b";
    public static final String REFERENCE_SPRISM="x";
    public static final String FASTQ_PAIRED_END_IND="q1";
    public static final String LEFT_MATE="1";
    public static final String RIGHT_MATE="2";
    public static final String TEMP_DIR="T";
    public static final String OUTPUT="0";
    public static final String OUTPUT_EXT=".blacklist";

    private final File rLane;
    private final File lLane;
    private final File referenceBitmask;
    private final File referenceSrprism;

    public BMTagger(ProcessBuilder processBuilder, File rLane, File lLane, File referenceBitmask, File referenceSrprism) {
        super(processBuilder);
        this.rLane = rLane;
        this.lLane = lLane;
        this.referenceBitmask = referenceBitmask;
        this.referenceSrprism = referenceSrprism;
    }

    @Override
    public File call() throws Exception {
        final Process p=this.processBuilder.start();
        p.waitFor();
        return new File(this.rLane.getParent(),this.rLane.getName().split("\\.")[0]+OUTPUT_EXT);
    }

    public static CallableProcess<File> newInstance(File bmtaggerExec,File rLane, File lLane, File referenceBitmask, File referenceSrprism, File tmpDir){

        final String[]processCall= {
                bmtaggerExec.toString(),
                REFERENCE_BITMASK,referenceBitmask.toString(),
                REFERENCE_SPRISM,referenceSrprism.toString(),
                TEMP_DIR,tmpDir.toString(),
                FASTQ_PAIRED_END_IND,
                LEFT_MATE,lLane.toString(),
                RIGHT_MATE,rLane.toString(),
                OUTPUT,new File(rLane.getParent(),rLane.getName().split("\\.")[0]+OUTPUT_EXT).toString()
        };


        return new BMTagger(new ProcessBuilder(processCall),rLane,lLane,referenceBitmask,referenceSrprism);
    }
}
