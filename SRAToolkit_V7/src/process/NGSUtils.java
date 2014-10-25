package process;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alext on 10/21/14.
 * TODO document class
 */
public class NGSUtils {
    public static final String POUT = ">";

    public static class FastqUtils {
        public static final String FROMFASTA = "fromfasta";
        public static final String CSENCODE = "csencode";

        public static CallableProcess<File> fromfasta(final File exec, final File csFasta, final File qual, final String name, final Path outFolder) {

            final List<String> command = new ArrayList<>();
            command.add(exec.getPath());
            command.add(FROMFASTA);
            command.add(csFasta.getPath());
            command.add(qual.getPath());
            final File outFastq = outFolder.resolve(name.concat(".fastq")).toFile();
            final ProcessBuilder processBuilder = new ProcessBuilder(command);

            return new CallableProcess<File>(processBuilder) {
                @Override
                public File call() throws Exception {

                    final Process p = this.processBuilder.start();
                    try (BufferedReader inputstreamReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                         BufferedReader errorstreamReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                         BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFastq))) {
                        String line;
                        while ((line = inputstreamReader.readLine()) != null) {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                        }
                        while ((line = errorstreamReader.readLine()) != null) {
                            System.out.println("ERR: ".concat(line));
                        }
                    }
                    return outFastq;
                }
            };
        }

        public static CallableProcess<File> fromfasta(final File exec, final AbiSolidDump.AbiSolidDumpResult result, final Path outFolder) {
            return fromfasta(exec, result.getCsFasta(), result.getQual(), result.getName(), outFolder);
        }

        public static CallableProcess<File> csencode(final File exec, final File colorFastq, final Path outFolder) {
            final List<String> command = new ArrayList<>();
            command.add(exec.getPath());
            command.add(CSENCODE);
            command.add(colorFastq.getPath());
            final File outFastq = outFolder.resolve(colorFastq.getName().concat(".encd")).toFile();
            final ProcessBuilder processBuilder = new ProcessBuilder(command);

            return new CallableProcess<File>(processBuilder) {
                @Override
                public File call() throws Exception {

                    final Process p = this.processBuilder.start();
                    try (BufferedReader inputstreamReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                         BufferedReader errorstreamReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                         BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFastq))) {
                        String line;
                        while ((line = inputstreamReader.readLine()) != null) {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                        }
                        while ((line = errorstreamReader.readLine()) != null) {
                            System.out.println("ERR: ".concat(line));
                        }
                    }
                    return outFastq;
                }
            };
        }
    }

}
