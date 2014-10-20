package tools;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by alext on 10/10/14.
 * TODO document class
 */
public class SolexaQAPPLengthSort<T extends SolexaQAPPLengthSort.SolexaQAPPLengthSortResult> extends SolexaQAPP<T> {

    protected SolexaQAPPLengthSort(SolexaQAPPBuilder builder) {
        super(builder);
    }

    @Override
    public List<T> getResult() {
        return super.getResult();
    }

    @Override
    public List<T> call() throws Exception {
        this.result = super.call();
        for (File f : ((SolexaQAPPLengthSortBuilder) this.solexaBuilder).inputFiles) {
            T t = (T) new SolexaQAPPLengthSortResult(this.outputDir, f.getName());
            this.result.add(t);
        }
        return this.result;
    }


    public static class SolexaQAPPLengthSortBuilder extends SolexaQAPPBuilder {

        public static final String LENGTH = "-l";
        public static final String CORRECT = "-c";

        protected int length;
        protected boolean correct;

        public SolexaQAPPLengthSortBuilder(File exec, List<File> inpuFiles, Path outDir) {
            super(exec, inpuFiles, outDir);
            this.params.put("", Mode.LENGTHSORT.getModality().concat(" ").concat(this.params.get("")));
        }

        public SolexaQAPPLengthSortBuilder length(int length) {
            this.length = length;
            this.params.put(LENGTH, String.valueOf(length));
            return this;
        }

        public SolexaQAPPLengthSortBuilder correct(boolean correct) {
            this.correct = correct;
            if (!this.params.containsKey(CORRECT)) {
                this.params.put("", this.params.get("").concat(" ").concat(CORRECT));
            }

            return this;
        }

        @Override
        public SolexaQAPPLengthSort build() {
            this.command = this.assembleCommand();
            this.processBuilder = new ProcessBuilder(this.command);
            return new SolexaQAPPLengthSort(this);
        }
    }

    public static class SolexaQAPPLengthSortResult extends SolexaQAPP.SolexaQAPPResult {
        //Specific parameters + getters
        protected SolexaQAPPLengthSortResult(Path outputDir, String name) {
            super(outputDir);
        }
    }
}
