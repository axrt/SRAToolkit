package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alext on 10/17/14.
 * TODO document class
 */
public class AbiSolidDump extends CallableProcess<AbiSolidDump.AbiSolidDumpResult> {

    public static final String OUT_DIR = "-O";
    public static final String CSFASTA = ".csfasta";
    public static final String QUAL = ".qual";
    public static final String F3 = "_F3";
    public static final String QV = "_QV";

    protected final Path outDir;
    protected final String name;

    protected AbiSolidDump(AbiSolidDumpBuilder builder) {
        super(builder.processBuilder);
        this.outDir = builder.outDir;
        this.name = builder.name;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public AbiSolidDumpResult call() throws Exception {
        final Process p = this.processBuilder.start();
        try (BufferedReader inputstreamReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
             BufferedReader errorstreamReader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            String line;
            while ((line = inputstreamReader.readLine()) != null) {
                System.out.println("INFO: ".concat(line));
            }
            while ((line = errorstreamReader.readLine()) != null) {
                System.out.println("ERR: ".concat(line));
            }
        }
        //TODO input smth in case smth goes wrong
        return new AbiSolidDumpResult(
                this.outDir.resolve(this.name.concat(F3).concat(QV).concat(QUAL)).toFile(),
                this.outDir.resolve(this.name.concat(F3).concat(CSFASTA)).toFile(),
                this.name);
    }

    public static class AbiSolidDumpBuilder {

        protected final HashMap<String, String> parameters;
        protected final List<String> command;
        protected final File sraFile;
        protected final File abiDumpExec;
        protected final Path outDir;
        protected final String name;
        protected ProcessBuilder processBuilder;


        public AbiSolidDumpBuilder(File abiDumpExec, File sraFile, Path outDir) {
            this.sraFile = sraFile;
            this.name = sraFile.getName().substring(0, sraFile.getName().indexOf('.'));
            this.abiDumpExec = abiDumpExec;
            this.outDir = outDir;
            this.parameters = new HashMap<>();
            this.command = new ArrayList<>();
            this.command.add(this.abiDumpExec.getPath());
            this.command.add(this.sraFile.getPath());
            this.command.add(OUT_DIR);
            this.command.add(this.outDir.toFile().getPath());

        }

        public AbiSolidDump build() {
            this.processBuilder = new ProcessBuilder(this.command);
            return new AbiSolidDump(this);
        }
    }

    public static class AbiSolidDumpResult {
        protected final File qual;
        protected final File csFasta;
        protected final String name;

        public AbiSolidDumpResult(File qual, File csFasta, String name) {
            this.qual = qual;
            this.csFasta = csFasta;
            this.name = name;
        }

        public File getQual() {
            return qual;
        }

        public File getCsFasta() {
            return csFasta;
        }

        public String getName() {
            return name;
        }
    }
}
