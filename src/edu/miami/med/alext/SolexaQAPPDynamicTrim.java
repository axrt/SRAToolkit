package edu.miami.med.alext;

import edu.miami.med.alext.brain.SolexaQAPP;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by alext on 10/10/14.
 * TODO document class
 */
public class SolexaQAPPDynamicTrim<T extends SolexaQAPPDynamicTrim.SolexaQAPPDynamicTrimResult> extends SolexaQAPP<T> {

    protected SolexaQAPPDynamicTrim(SolexaQAPPBuilder builder) {
        super(builder);
    }

    @Override
    public List<T> getResult() {
        return super.getResult();
    }

    public static class SolexaQAPPDynamicTrimBuilder extends SolexaQAPPAnDyBuilder {

        public static final String ANCHOR = "--anchor";

        protected boolean useAnchor;

        protected SolexaQAPPDynamicTrimBuilder(File exec, List<File> inpuFiles, Path outDir) {
            super(exec, inpuFiles, outDir);
            this.params.put("",Mode.DYNAMICTRIM.getModality().concat(" ").concat(this.params.get("")));
        }

        @Override
        public SolexaQAPPDynamicTrimBuilder probeCutoff(double probeCutoff) {
            return (SolexaQAPPDynamicTrimBuilder) super.probeCutoff(probeCutoff);
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
            if (!this.params.containsKey(ANCHOR)) {
                this.params.put("", this.params.get("").concat(" ").concat(ANCHOR));
            }
            this.useAnchor = useAnchor;
            return this;
        }

        @Override
        public SolexaQAPPDynamicTrim build() {
            return (SolexaQAPPDynamicTrim) super.build();
        }
    }

    public static class SolexaQAPPDynamicTrimResult extends SolexaQAPPResult{
        //Specific parameters + getters
        protected SolexaQAPPDynamicTrimResult(Path outputDir) {
            super(outputDir);
        }
    }
}
