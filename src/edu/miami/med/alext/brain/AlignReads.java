package edu.miami.med.alext.brain;

import edu.miami.med.alext.process.CallableProcess;

import java.io.File;

/**
 * Created by alext on 8/22/14.
 * TODO document class
 */
public class AlignReads extends CallableProcess<File>{

    public static final String SEQTYPE="--seqType";
    public static final String TARGET="--target";
    public static final String ALIGNER="--seqType";
    public static final String PROCESSORS="-p";
    public static final String LEFT="--left";
    public static final String RIGHT="--right";

    public enum FileType{
        FASTA("fa"),FASTQ("fq");
        private final String name;
        FileType(String name){
            this.name=name;
        }

        public String getName() {
            return name;
        }
    }

    protected AlignReads(ProcessBuilder processBuilder) {
        super(processBuilder);
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public File call() throws Exception {
        return null;
    }
}
