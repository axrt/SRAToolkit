package tools;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by alext on 10/10/14.
 * TODO document class
 */
public class SolexaQAPPAnanlysis<T extends SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult> extends SolexaQAPP<T> {

    protected SolexaQAPPAnanlysis(SolexaQAPPBuilder builder) {
        super(builder);
    }

    @Override
    public List<T> call() throws Exception {
       this.result=super.call();
       for(File f:((SolexaQAPPAnalysisBuilder)this.solexaBuilder).inputFiles){
           T t=(T)new SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult(this.outputDir,f.getName());
           this.result.add(t);
       }
       return this.result;
    }

    @Override
    public List<T> getResult() {
        return super.getResult();
    }

    public static class SolexaQAPPAnalysisBuilder extends SolexaQAPPAnDyBuilder{


        public static final String VARIANCE_STAT="-v";
        public static final String MINMAX_STAT="-m";
        public static final String SAMPLES="-s";

        protected boolean varianceStat;
        protected boolean minmaxStat;
        protected int numSamples;

        public SolexaQAPPAnalysisBuilder(File exec, List<File> inpuFiles, Path outDir) {
            super(exec, inpuFiles, outDir);
            this.params.put("",Mode.ANALYSIS.getModality().concat(this.params.get("")));
        }

        @Override
        public SolexaQAPPAnalysisBuilder probeCutoff(double probeCutoff) {
            return (SolexaQAPPAnalysisBuilder)super.probeCutoff(probeCutoff);
        }

        @Override
        public SolexaQAPPAnalysisBuilder phredCutoff(int phredCutoff) {
            return (SolexaQAPPAnalysisBuilder)super.phredCutoff(phredCutoff);
        }

        @Override
        public SolexaQAPPAnalysisBuilder bwa(boolean bwa) {
            return (SolexaQAPPAnalysisBuilder)super.bwa(bwa);
        }

        @Override
        public SolexaQAPPAnalysisBuilder format(SeqFormat format) {
            return (SolexaQAPPAnalysisBuilder)super.format(format);
        }

        public SolexaQAPPAnalysisBuilder variance(boolean varianceStat) {
            this.varianceStat = varianceStat;
            if(!this.params.containsKey(VARIANCE_STAT)){
                this.params.put("",this.params.get("").concat(" ").concat(VARIANCE_STAT));
            }
            return this;
        }

        public SolexaQAPPAnalysisBuilder setMinmaxStat(boolean minmaxStat) {
            this.minmaxStat = minmaxStat;
            if(!this.params.containsKey(MINMAX_STAT)){
                this.params.put("",this.params.get("").concat(" ").concat(MINMAX_STAT));
            }
            return this;
        }

        public SolexaQAPPAnalysisBuilder sample(int numSamples) {
            if(numSamples<0||numSamples>Integer.MAX_VALUE){
                throw new IllegalArgumentException("Number of samples value must be within (0,"+Integer.MAX_VALUE+"]!");
            }
            this.numSamples = numSamples;
            this.params.put(SAMPLES,String.valueOf(numSamples));
            return this;
        }
        @Override
        public SolexaQAPPAnanlysis build() {
            this.command=this.assembleCommand();
            this.processBuilder=new ProcessBuilder(this.command);
            return new SolexaQAPPAnanlysis(this);
        }
    }

    public static class SolexaQAPPAnalysisResult extends SolexaQAPPResult{

        //Specific parameters + getters
        public static final String PDF=".pdf";
        public static final String MATRIX=".matrix";
        public static final String QUALITY=".quality";
        public static final String SEGMENTS=".segments";
        public static final String _CUMULATIVE ="_comulative";
        public static final String _HIST="_hist";

        protected final File matrix;
        protected final File matrix_pdf;
        protected final File quality;
        protected final File quality_pdf;
        protected final File segments;
        protected final File segments_cumulative_pdf;
        protected final File segments_hist_pdf;

        protected SolexaQAPPAnalysisResult(Path outputDir,String name) {
            super(outputDir);
            this.matrix=this.outputDir.resolve(name+MATRIX).toFile();
            this.matrix_pdf=this.outputDir.resolve(name+MATRIX+PDF).toFile();
            this.quality=this.outputDir.resolve(name + QUALITY).toFile();
            this.quality_pdf=this.outputDir.resolve(name + QUALITY + PDF).toFile();
            this.segments=this.outputDir.resolve(name+SEGMENTS).toFile();
            this.segments_cumulative_pdf=this.outputDir.resolve(name+SEGMENTS+ _CUMULATIVE +PDF).toFile();
            this.segments_hist_pdf=this.outputDir.resolve(name+SEGMENTS+ _HIST +PDF).toFile();
        }

        public File getMatrix() {
            return matrix;
        }

        public File getMatrix_pdf() {
            return matrix_pdf;
        }

        public File getQuality() {
            return quality;
        }

        public File getQuality_pdf() {
            return quality_pdf;
        }

        public File getSegments() {
            return segments;
        }

        public File getSegments_cumulative_pdf() {
            return segments_cumulative_pdf;
        }

        public File getSegments_hist_pdf() {
            return segments_hist_pdf;
        }
    }

}
