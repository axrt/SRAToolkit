package edu.miami.med.alext.brain;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alext on 10/10/14.
 * TODO document class
 */
public class SolexaQAPP<T extends SolexaQAPP.SolexaQAPPResult> extends SolexaQA<T> {

    protected T result;
    protected final String name;
    protected final Path outputDir;

    public enum Mode{
        ANALYSIS("analysis"),DYNAMICTRIM("dynamictrim"),LENGTHSHORT("lengthshort");
        private final String modality;

        private Mode(String modality) {
            this.modality = modality;
        }

        public String getModality() {
            return modality;
        }
    }

    protected SolexaQAPP(SolexaQAPPBuilder builder) {
        super(builder);
        this.outputDir=builder.outDir;
        this.name="["+builder.inpuFiles.stream().map(f->{return f.getName();}).collect(Collectors.joining("]["))+"]";
    }

    public T getResult() {
        return result;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public T call() throws Exception {

        if(!this.outputDir.toFile().exists()){
            this.outputDir.toFile().mkdir();
        }

        final Process p = this.processBuilder.start();

        try (InputStream inputStream = p.getErrorStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                synchronized (System.out.getClass()) {
                    System.out.println("ERR::" + this.name + " >" + line);
                }
            }
        }
        try (InputStream inputStream = p.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                synchronized (System.out.getClass()) {
                    System.out.println("OUT::" + this.name + " >" + line);
                }
            }
        }

        return this.getResult();
    }




    public abstract static class SolexaQAPPBuilder extends SolexaQABuilder{

        public static final String DIRECTORY="-directory";

        protected final List<File> inpuFiles;
        protected final Path outDir;

        protected SolexaQAPPBuilder(File exec,List<File> inpuFiles, Path outDir) {
            super(exec);
            this.inpuFiles = inpuFiles;
            this.outDir = outDir;
            this.params.put("", inpuFiles.stream().map(File::getPath).collect(Collectors.joining(" ")));
            this.params.put(DIRECTORY,outDir.toFile().getPath());
        }

        @Override
        public SolexaQA build() {
            this.command=this.assembleCommand();
            this.processBuilder=new ProcessBuilder(this.command);
            return new SolexaQAPP(this);
        }
    }

    /**
     * The one for the first two options
     */
    public abstract static class SolexaQAPPAnDyBuilder extends SolexaQAPPBuilder{

        public static final String PROBCUTOFF="-p";
        public static final String PHREDCUTOFF="-h";
        public static final String BWA="-bwa";

        protected SolexaQAPPAnDyBuilder(File exec,List<File> inpuFiles, Path outDir) {
            super(exec,inpuFiles, outDir);
        }

        public enum SeqFormat{
            SANGER("--sanger"),SOLEXA("--solexa"),ILLUMINA("--ILLUMINA"),IONTORRENT("--torrent");
            private final String format;

            private SeqFormat(String format) {
                this.format = format;
            }

            public String getFormat() {
                return format;
            }
        }
        protected double probeCutoff;
        protected int phredCutoff;
        protected boolean bwa;
        protected SeqFormat format;

        public SolexaQAPPAnDyBuilder probeCutoff(double probeCutoff) {
            if(this.params.containsKey(PHREDCUTOFF)){
                throw new IllegalArgumentException("The builder has already been set to use PHRED cutoff!");
            }
            if(probeCutoff<0||probeCutoff>1){
                throw new IllegalArgumentException("PROBABILITY cutoff value must be within (0,1]!");
            }

            this.probeCutoff = probeCutoff;
            this.params.put(PROBCUTOFF,String.valueOf(probeCutoff));
            return this;
        }

        public SolexaQAPPAnDyBuilder phredCutoff(int phredCutoff) {
            if(this.params.containsKey(PROBCUTOFF)){
                throw new IllegalArgumentException("The builder has already been set to use PROBABILITY cutoff!");
            }
            if(probeCutoff<0||probeCutoff>41){
                throw new IllegalArgumentException("PHRED cutoff value must be within (0,41]!");
            }
            this.phredCutoff = phredCutoff;
            this.params.put(PHREDCUTOFF,String.valueOf(phredCutoff));
            return this;
        }

        public SolexaQAPPAnDyBuilder bwa(boolean bwa) {
            this.bwa = bwa;
            if(!this.params.containsKey(BWA)){
                this.params.put(BWA,"");
            }
            return this;
        }
        public  SolexaQAPPAnDyBuilder format(SeqFormat format){
            this.format=format;
            this.params.put("",this.params.get("").concat(" ").concat(format.getFormat()));
            return this;
        }
    }

    public static class SolexaQAPPResult extends SolexaQAResult{
        protected final Path outputDir;

        protected SolexaQAPPResult(Path outputDir) {
            this.outputDir = outputDir;
        }

        public Path getOutputDir() {
            return outputDir;
        }
    }

}

