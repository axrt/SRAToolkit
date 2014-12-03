package scripts;

import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by alext on 12/3/14.
 */
public class ConvertCreepyFastQ {

    public static final String IN = "i";
    public static final String OUT = "o";

    public static void main(String[] args) {

        final CommandLineParser parser = new GnuParser();
        final Options options = new Options();

        Option option = new Option(IN, "input", true, "Input file in creepy fastq-like format");
        options.addOption(option);
        option.setRequired(true);
        option = new Option(OUT, "output", true, "Output file, where to write normal fastq outpu.");
        options.addOption(option);
        option.setRequired(true);

        Path toInput = null;
        Path toOutput = null;
        try {
            final CommandLine commandLine = parser.parse(options, args, true);
            toInput = Paths.get(commandLine.getOptionValue(IN));
            toOutput = Paths.get(commandLine.getOptionValue(OUT));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(toInput.toFile()));
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(toOutput.toFile()))) {
            bufferedReader.lines().forEach(

                    line -> {
                        final String[] record = new String[4]; //Will hold a fastq record when reformatted

                        final String[] split = line.split(":");
                        final String acCode = Arrays.asList(Arrays.copyOf(split, 5)).stream().collect(Collectors.joining(":"));//Extracts ac

                        record[0] = "@".concat(acCode);
                        record[1] = split[5];
                        record[2] = "+".concat(acCode);
                        record[3] = split[6];

                        final String reformattedRecord = Arrays.asList(record).stream().collect(Collectors.joining("\n"));//Assemble a normal record

                        try {
                            bufferedWriter.write(reformattedRecord);
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }

            );
        } catch (UncheckedIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
