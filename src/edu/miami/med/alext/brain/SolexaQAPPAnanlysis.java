package edu.miami.med.alext.brain;

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
    public List<T> getResult() {
        return super.getResult();
    }

    public static class SolexaQAPPAnalysisBuilder extends SolexaQAPPAnDyBuilder{


        public static final String VARIANCE_STAT="-v";
        public static final String MINMAX_STAT="-m";
        public static final String SAMPLES="-S";

        protected boolean varianceStat;
        protected boolean minmaxStat;
        protected int numSamples;

        protected SolexaQAPPAnalysisBuilder(File exec,List<File> inpuFiles, Path outDir) {
            super(exec, inpuFiles, outDir);
            this.params.put("",Mode.ANALYSIS.getModality().concat(" ").concat(this.params.get("")));
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

        public SolexaQAPPAnalysisBuilder setVarianceStat(boolean varianceStat) {
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

        public SolexaQAPPAnalysisBuilder setNumSamples(int numSamples) {
            if(numSamples<0||numSamples>Integer.MAX_VALUE){
                throw new IllegalArgumentException("Number of samples value must be within (0,"+Integer.MAX_VALUE+"]!");
            }
            this.numSamples = numSamples;
            this.params.put(SAMPLES,String.valueOf(numSamples));
            return this;
        }

        @Override
        public SolexaQAPPAnanlysis build() {
            return (SolexaQAPPAnanlysis)super.build();
        }
    }

    public static class SolexaQAPPAnalysisResult extends SolexaQAPPResult{

        //Specific parameters + getters
        public static final String PDF=".pdf";
        public static final String MATRIX=".matrix";
        public static final String SEGMENTS=".segments";
        public static final String _COMULATIVE="_comulative";
        public static final String _HIST="_hist";

        protected SolexaQAPPAnalysisResult(Path outputDir) {
            super(outputDir);
        }

    }

}
