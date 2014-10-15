package tools;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by alext on 10/14/14.
 * TODO document class
 */
public class SolexaQAPPDynamicTrim<T extends SolexaQAPPDynamicTrim.SolexaQAPPDynamicTrimResult> extends SolexaQAPP<T> {


    protected SolexaQAPPDynamicTrim(SolexaQAPPBuilder builder) {
        super(builder);
    }

    @Override
    public List<T> call() throws Exception {
        this.result=super.call();
        for(File f:((SolexaQAPPDynamicTrimBuilder)this.solexaBuilder).inputFiles){
            T t=(T)new SolexaQAPPDynamicTrimResult(this.outputDir,f.getName());
            this.result.add(t);
        }
        return this.result;
    }

    @Override
    public List<T> getResult() {
        return super.getResult();
    }

    public static class SolexaQAPPDynamicTrimBuilder extends SolexaQAPP.SolexaQAPPAnDyBuilder {

        public static final String ANCHOR = "--anchor";

        protected boolean useAnchor;

        public SolexaQAPPDynamicTrimBuilder(File exec, List<File> inpuFiles, Path outDir) {
            super(exec, inpuFiles, outDir);
            this.params.put("",Mode.DYNAMICTRIM.getModality().concat(this.params.get("")));
        }

        @Override
        public SolexaQAPPDynamicTrimBuilder probeCutoff(double probeCutoff) {
            return (SolexaQAPPDynamicTrimBuilder)super.probeCutoff(probeCutoff);
        }

        @Override
        public SolexaQAPPDynamicTrimBuilder phredCutoff(int phredCutoff) {
            return (SolexaQAPPDynamicTrimBuilder) super.phredCutoff(phredCutoff);
        }

        @Override
        public SolexaQAPPDynamicTrimBuilder bwa(boolean bwa) {
            return (SolexaQAPPDynamicTrimBuilder) super.bwa(bwa);
        }

        @Override
        public SolexaQAPPDynamicTrimBuilder format(SeqFormat format) {
            return (SolexaQAPPDynamicTrimBuilder) super.format(format);
        }

        public SolexaQAPPDynamicTrimBuilder anchor(boolean useAnchor) {
            if (useAnchor && !this.useAnchor) {
                this.params.put(ANCHOR, "");
            }
            if (!useAnchor && this.params.containsKey(ANCHOR)) {
                this.params.remove(ANCHOR);
            }
            this.useAnchor = useAnchor;
            return this;
        }

        @Override
        public SolexaQA build() {
            this.command=this.assembleCommand();
            this.processBuilder=new ProcessBuilder(this.command);
            return new SolexaQAPPDynamicTrim(this);
        }
    }

    public static class SolexaQAPPDynamicTrimResult extends SolexaQAPP.SolexaQAPPResult {


        protected SolexaQAPPDynamicTrimResult(Path outputDir,String name) {
            super(outputDir);
        }


    }
}
