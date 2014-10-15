package script;

import org.apache.commons.cli.*;
import org.xml.sax.SAXException;
import process.CallableProcessExecutor;
import process.FastqDumpHelper;
import process.FixThreadCallableProcessExectuor;
import tools.SolexaQA;
import tools.SolexaQAPP;
import tools.SolexaQAPPAnanlysis;
import xml.jaxb.EXPERIMENTPACKAGESET;
import xml.jaxb.ExperimentPackageType;
import xml.jaxb.RunType;
import xml.jaxb.SRAXMLLoader;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by alext on 10/15/14.
 * TODO document class
 */
public class Main {

    private static final String DOWNLOAD = "dw";
    private static final String ANALYSE = "an";
    private static final String ANALYSIS_OUT = "aout";
    private static final String DECOMPRESS = "dc";
    private static final String FROM = "f";
    private static final String TO = "t";
    private static final String PROB_CUTOFF = "p";
    private static final String SAMPLES = "s";
    private static final String VARIANCE = "v";
    private static final String FORMAT = "fmt";
    private static final String BIN_FOLDER = "bin";
    private static final String DRIVER = "drv";
    private static final String CPU = "c";
    private static final String DIRECTORY = "dir";
    private static final String FASTQDUMP = "fastq-dump.2.4.1";
    private static final String SOLEXAQAPP = "solexaqapp";

    public static void main(String[] args) {

        final CommandLineParser parser = new GnuParser();
        final Options options = new Options();

        Option option = new Option(DOWNLOAD, "download", false, "Download the given list of SRAs from xml.");
        options.addOption(option);

        option = new Option(ANALYSE, "analyze", false, "Analyze SRAs in a given home folder");
        options.addOption(option);
        option = new Option(ANALYSIS_OUT, "analysis_out", false, "Analysis out folder");
        options.addOption(option);

        option = new Option(DECOMPRESS, "decompress", false, "Decompress the given list of SRAs from xml.");
        options.addOption(option);

        option = new Option(DRIVER, "driver", true, "Path to the driver xml.");
        option.setRequired(true);
        options.addOption(option);
        option = new Option(DIRECTORY, "directory", true, "Path to the working directory.");
        option.setRequired(true);
        options.addOption(option);
        option = new Option(BIN_FOLDER, "bin_folder", true, "Path to the folder with binaries.");
        option.setRequired(true);
        options.addOption(option);
        option = new Option(CPU, "cores", true, "Path to the driver xml.");
        option.setRequired(true);
        options.addOption(option);

        option = new Option(FROM, "from", true, "<int> position in the driver to start with.");
        options.addOption(option);
        option = new Option(TO, "to", true, "<int> position in the driver to finish with.");
        options.addOption(option);
        option = new Option(SAMPLES, "samples", true, "<int> number of iterations per sample.");
        options.addOption(option);
        option = new Option(PROB_CUTOFF, "pval_cutoff", true, "<double> p-value cutoff for quality.");
        options.addOption(option);
        option = new Option(ANALYSIS_OUT, "analysis_out", true, "Path to analysis out folder (may not exist at runtime, will be created)");
        options.addOption(option);
        option = new Option(VARIANCE, "variance", false, "If applied, the variance will be calculated.");
        options.addOption(option);
        option = new Option(FORMAT, "seq_format", true, "Sequence format.");
        options.addOption(option);

        //Read command line
        try {
            final CommandLine commandLine = parser.parse(options, args, true);
            final File driverXML = new File(commandLine.getOptionValue(DRIVER));
            final int cores = Integer.parseInt(commandLine.getOptionValue(CPU));
            final File binFolder = new File(commandLine.getOptionValue(BIN_FOLDER));
            final int[] ft = initFromTo(commandLine);
            final int from = ft[0];
            final int to = ft[1];
            final EXPERIMENTPACKAGESET experimentpackageset;
            final File directory = new File(commandLine.getOptionValue(DIRECTORY));

            try (InputStream inputStream = new FileInputStream(driverXML)) {
                experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
                final List<String> sraNames = getSRANames(from, to, experimentpackageset);
                if (commandLine.hasOption(DOWNLOAD)) {

                    return;
                }
                if (commandLine.hasOption(DECOMPRESS)) {


                    final List<File> sraFiles = new ArrayList<>();
                    for (String name : sraNames) {
                        final File sraFile = new File(new File(directory, name), name.concat(".sra"));
                        if (!sraFile.exists()) {
                            continue;
                        }
                        sraFiles.add(sraFile);
                    }
                    System.out.println(sraFiles.size() + " to decompress.");
                    decompress(new File(binFolder, FASTQDUMP), cores, sraFiles);

                    return;
                }
                if (commandLine.hasOption(ANALYSE)) {
                    if (!commandLine.hasOption(ANALYSIS_OUT)) {
                        System.out.println("Please specify an output folder for analysis with -aout flag!");
                        return;
                    }

                    final Path outDir= Paths.get(commandLine.getOptionValue(ANALYSIS_OUT));
                    final double probCutoff;
                    if(commandLine.hasOption(PROB_CUTOFF)){
                        probCutoff=Double.parseDouble(commandLine.getOptionValue(PROB_CUTOFF));
                    } else{
                        probCutoff=0.05;
                    }

                    final int samples;
                    if(commandLine.hasOption(SAMPLES)){
                        samples= Integer.parseInt(commandLine.getOptionValue(SAMPLES));
                    }else{
                        samples=10000;
                    }
                    final boolean variance;
                    if(commandLine.hasOption(VARIANCE)){
                        variance=true;
                    } else{
                        variance=false;
                    }
                    final SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat format;
                    if(commandLine.hasOption(FORMAT)){
                        format= SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat.valueOf(commandLine.getOptionValue(FORMAT));
                    } else{
                        format= SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat.SANGER;
                    }

                    final List<File[]> fastqFiles=new ArrayList<>();
                    for (String name : sraNames) {
                        final File sraFile_1 = new File(new File(directory, name), name.concat("_1.fastq"));
                        final File sraFile_2 = new File(new File(directory, name), name.concat("_2.fastq"));
                        if (!sraFile_1.exists()) {
                            continue;
                        }else if(!sraFile_2.exists()){
                            fastqFiles.add(new File[]{sraFile_1});
                        }else{
                            fastqFiles.add(new File[]{sraFile_1,sraFile_2});
                        }
                    }
                    analyze(new File(binFolder,SOLEXAQAPP),fastqFiles,cores,outDir,probCutoff,variance,samples,format);
                    return;
                }
            }

        } catch (IOException | SAXException | JAXBException | ParseException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static final void download(File driverXML, int start, int stop, File mainFolder) throws IOException, JAXBException, SAXException {
        try (InputStream inputStream = new FileInputStream(driverXML)) {

            //Download all SRA files
            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames = new ArrayList<>();

            for (int i = start; i <= stop; i++) {
                final ExperimentPackageType experimentPackageType = experimentpackageset.getEXPERIMENTPACKAGE().get(i);
                for (RunType runty : experimentPackageType.getRUNSET().getRUN())
                    sraNames.add(runty.getAccession());
            }

            System.out.println("SRR archives: " + sraNames.size());

            //Find out which folders exist

            final List<File> sraFiles = new ArrayList<>();
            for (String s : sraNames) {
                sraFiles.add(net.DownloadSRA.downloadSRAToANewFolder(s, mainFolder));
            }
            System.out.println(sraFiles.size() + " SRA downloaded.");
        }
    }

    private static final void decompress(File fastqDumpExec, int numThreads, List<File> sraFiles) throws ExecutionException, InterruptedException {
        FastqDumpHelper.runTaskArray(fastqDumpExec, numThreads, sraFiles);
    }

    private static final void analyze(File solexappExec, List<File[]> fastqFiles,
                                      int numThreads, Path outDir, double probeCutoff,
                                      boolean variance, int sample, SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat format) throws ExecutionException, InterruptedException {
        CallableProcessExecutor<List<SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult>, SolexaQA<SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult>> executorAnalysis = null;
        try {
            executorAnalysis =
                    FixThreadCallableProcessExectuor.newInstance(numThreads);
            for (File[] fs : fastqFiles) {
                final List<File> input = Arrays.asList(fs);
                final SolexaQAPPAnanlysis.SolexaQAPPAnalysisBuilder builder = new SolexaQAPPAnanlysis.SolexaQAPPAnalysisBuilder(solexappExec, input, outDir);
                builder.probeCutoff(probeCutoff).variance(variance).sample(sample).format(format).build();
                executorAnalysis.addProcess(builder.build());
            }
            for (Future<List<SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult>> future : executorAnalysis.getFutures()) {
                for (SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult result : future.get()) {
                }
            }
        } finally {
            if (executorAnalysis != null) {
                executorAnalysis.shutdown();
            }
        }

    }

    private static final int[] initFromTo(CommandLine commandLine) {
        final int from;
        final int to;
        if (commandLine.hasOption(FROM)) {
            from = Integer.parseInt(commandLine.getOptionValue(FROM));
        } else {
            from = 0;
        }
        if (commandLine.hasOption(TO)) {
            to = Integer.parseInt(commandLine.getOptionValue(TO));
        } else {
            to = 0;
        }
        return new int[]{from, to};
    }

    private static final List<String> getSRANames(int from, int to, EXPERIMENTPACKAGESET experimentpackageset) {
        if (to == 0) {
            to = experimentpackageset.getEXPERIMENTPACKAGE().size() - 1;
        }
        final List<String> sraNames = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            final ExperimentPackageType experimentPackageType = experimentpackageset.getEXPERIMENTPACKAGE().get(i);
            for (RunType runty : experimentPackageType.getRUNSET().getRUN())
                sraNames.add(runty.getAccession());
        }
        return sraNames;
    }
}
