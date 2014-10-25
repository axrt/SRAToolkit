package script;

import org.apache.commons.cli.*;
import org.xml.sax.SAXException;
import process.*;
import tools.*;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by alext on 10/15/14.
 * TODO document class
 */
public class Main {

    public static final String JVM_MEMORY = Trinity.JVM_MEMORY.substring(2);
    public static final String LEFT = Trinity.LEFT.substring(2);
    public static final String RIGHT = Trinity.RIGHT.substring(2);
    public static final String SINGLE = Trinity.SINGLE.substring(2);
    public static final String MIN_CONTIG_LENGTH = Trinity.MIN_CONTIG_LENGTH.substring(2);
    public static final String PROCESSORS = Trinity.PROCESSORS.substring(2);
    public static final String BFLY_MAX_HEAP = Trinity.BFLY_MAX_HEAP.substring(2);
    public static final String BFLY_CPU = Trinity.BFLY_CPU.substring(2);
    public static final String TRINITY_OUTPUT_DIR = "output";
    public static final String LIBTYPE = Trinity.LIB_TYPE.LIBTYPE.substring(2);
    private static final String DOWNLOAD = "dw";
    private static final String ANALYSE = "an";
    private static final String BMTAG = "bm";
    private static final String TRIM = "dt";
    private static final String LENGTHSORT = "ls";
    private static final String ABI_DUMP = "abi";
    private static final String BMTAG_TMP = "bmtmp";
    private static final String BMTAG_REF_DIR = "bmtref";
    private static final String BMTAG_REF_NAME = "bmtrefname";
    private static final String ANALYSIS_OUT = "aout";
    private static final String TRIM_OUT = "tout";
    private static final String LENGTHSORT_OUT = "lsout";
    private static final String DECOMPRESS = "dc";
    private static final String FROM = "f";
    private static final String TO = "t";
    private static final String PROB_CUTOFF = "p";
    private static final String SAMPLES = "s";
    private static final String VARIANCE = "v";
    private static final String FORMAT = "fmt";
    private static final String REC_FORMAT = "rfmt";
    private static final String BIN_FOLDER = "bin";
    private static final String DRIVER = "drv";
    private static final String CPU = "c";
    private static final String DIRECTORY = "dir";
    private static final String LENGTHSORT_CUTOFF = "lsc";
    private static final String FASTQDUMP = "fastq-dump.2.4.1";
    private static final String SOLEXAQAPP = "solexaqapp";
    private static final String ABIDUMPAPP = "abi-dump.2.4.1";
    private static final String BMTAGGER = "bmtagger.sh";
    private static final String NGS_FASTAQ_UTILS = "fastqutils";
    private static final String NGS_FASTAQ_FORMFASTA = "ngff";
    private static final String NGS_FASTAQ_CSENCODE = "ngce";
    private static final String APPEND_EXT = "aext";
    private static final String APPEND_PREF1 = "pext1";
    private static final String APPEND_PREF2 = "pext2";
    private static final String TRINITY = "tr";
    private static final String TRINITY_EXEC = "Trinity.pl";

    public static void main(String[] args) {

        final CommandLineParser parser = new GnuParser();
        final Options options = new Options();

        Option option = new Option(TRINITY, "trinity", false, "Run trinity mode.");
        options.addOption(option);
        option = new Option(JVM_MEMORY, JVM_MEMORY, true, "JVM memory for trinity.");
        options.addOption(option);
        option = new Option(LEFT, LEFT, true, "Left paried.");
        options.addOption(option);
        option = new Option(RIGHT, RIGHT, true, "Right paried.");
        options.addOption(option);
        option = new Option(SINGLE, SINGLE, true, "Single ended.");
        options.addOption(option);
        option = new Option(MIN_CONTIG_LENGTH, MIN_CONTIG_LENGTH, true, "Mininum contig lentgh for trinity.");
        options.addOption(option);
        option = new Option(BFLY_MAX_HEAP, BFLY_MAX_HEAP, true, "Maximum heap size for bfly.");
        options.addOption(option);
        option = new Option(LIBTYPE, LIBTYPE, true, "Libtype for trinity.");
        options.addOption(option);
        option = new Option(TRINITY_OUTPUT_DIR, TRINITY_OUTPUT_DIR, true, "Trinity output folder.");
        options.addOption(option);

        option = new Option(DOWNLOAD, "download", false, "Download the given list of SRAs from xml.");
        options.addOption(option);

        option = new Option(ANALYSE, "analyze", false, "Analyze SRAs in a given home folder");
        options.addOption(option);
        option = new Option(ANALYSIS_OUT, "analysis_out", true, "Analysis out folder");
        options.addOption(option);
        option = new Option(TRIM_OUT, "trim_out", true, "Trim out folder");
        options.addOption(option);
        option = new Option(APPEND_EXT, "append_ext", true, "Append additional extension to file names (for pipelines)");
        options.addOption(option);

        option = new Option(APPEND_PREF1, "append_pref1", true, "Append additional prefix to forward fastq file names (for pipelines)");
        options.addOption(option);
        option = new Option(APPEND_PREF2, "append_pref2", true, "Append additional prefix to reverse fastq file names (for pipelines)");
        options.addOption(option);

        option = new Option(ABI_DUMP, "abi-dump", false, "Abi-dump an SOLID sra.");
        options.addOption(option);
        option = new Option(DECOMPRESS, "decompress", false, "Decompress the given list of SRAs from xml.");
        options.addOption(option);
        option = new Option(LENGTHSORT, "lengthsort", false, "Sort out sequences shorter than the given cutoff.");
        options.addOption(option);
        option = new Option(LENGTHSORT_CUTOFF, "lengthsort_cutoff", true, "<int> Sequence length cutoff.");
        options.addOption(option);
        option = new Option(LENGTHSORT_OUT, "lengthsort_out", true, "A directory to save the sorted sequences to.");
        options.addOption(option);
        option = new Option(TRIM, "dynamictrim", false, "Trim the fastq files for a given p-avlue.");
        options.addOption(option);
        option = new Option(BMTAG, "bmtag", false, "BMTAG out reference genome sequences.");
        options.addOption(option);
        option = new Option(BMTAG_TMP, "bmtagger_tmp", true, "BMTAGGER tmp folder.");
        options.addOption(option);
        option = new Option(BMTAG_REF_DIR, "bmtagger_ref", true, "BMTAGGER reference folder.");
        options.addOption(option);
        option = new Option(BMTAG_REF_NAME, "bmtagger_ref_name", true, "BMTAGGER reference name.");
        options.addOption(option);
        option = new Option(NGS_FASTAQ_FORMFASTA, "fqu_formfasta", false, "Use fromfasta from the ngsutils package to convert csfasta and qual files to fastq.");
        options.addOption(option);
        option = new Option(NGS_FASTAQ_CSENCODE, "fqu_csencode", false, "Use csencode from the ngsutils package to convert colorspace to encoded fastq.");
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
        option = new Option(REC_FORMAT, "record_format", true, "Record format {FASTA,FASTQ}");
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
                    for (String sraName : sraNames) {
                        System.out.println(sraName + " is being downloaded");
                        net.DownloadSRA.downloadSRAToANewFolder(sraName, directory);
                    }
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

                //*****ADDITIONAL PARAMETERS*****
                final double probCutoff;
                if (commandLine.hasOption(PROB_CUTOFF)) {
                    probCutoff = Double.parseDouble(commandLine.getOptionValue(PROB_CUTOFF));
                } else {
                    probCutoff = 0.05;
                }

                final int samples;
                if (commandLine.hasOption(SAMPLES)) {
                    samples = Integer.parseInt(commandLine.getOptionValue(SAMPLES));
                } else {
                    samples = 10000;
                }
                final boolean variance;
                if (commandLine.hasOption(VARIANCE)) {
                    variance = true;
                } else {
                    variance = false;
                }
                final SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat format;
                if (commandLine.hasOption(FORMAT)) {
                    format = SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat.valueOf(commandLine.getOptionValue(FORMAT));
                } else {
                    format = SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat.SANGER;
                }
                final String append;
                if (commandLine.hasOption(APPEND_EXT)) {
                    append = commandLine.getOptionValue(APPEND_EXT);
                } else {
                    append = "";
                }
                final String prefix1;
                if (commandLine.hasOption(APPEND_PREF1)) {
                    prefix1 = commandLine.getOptionValue(APPEND_PREF1);
                } else {
                    prefix1 = "";
                }
                final String prefix2;
                if (commandLine.hasOption(APPEND_PREF2)) {
                    prefix2 = commandLine.getOptionValue(APPEND_PREF2);
                } else {
                    prefix2 = "";
                }

                // *****ABI-DUMP*****
                if (commandLine.hasOption(ABI_DUMP)) {
                    System.out.println(sraNames);
                    final List<File> sraFiles = new ArrayList<>();
                    for (String name : sraNames) {
                        final File sraFile = directory.toPath().resolve(name).resolve(name.concat(".sra")).toFile();
                        if (!sraFile.exists()) {
                            continue;
                        }
                        sraFiles.add(sraFile);
                    }

                    abidump(new File(binFolder, ABIDUMPAPP), sraFiles, cores);
                    return;
                }

                // *****NGSUTILS FASTQUITLS FORMFASTA*****
                if (commandLine.hasOption(NGS_FASTAQ_FORMFASTA)) {
                    final List<AbiSolidDump.AbiSolidDumpResult> abiresults = new ArrayList<>();
                    for (String name : sraNames) {
                        final File csFasta = directory.toPath().resolve(name).resolve(name.concat(AbiSolidDump.F3).concat(AbiSolidDump.CSFASTA)).toFile();
                        final File qual = directory.toPath().resolve(name).resolve(name.concat(AbiSolidDump.F3).concat(AbiSolidDump.QV).concat(AbiSolidDump.QUAL)).toFile();
                        final AbiSolidDump.AbiSolidDumpResult result = new AbiSolidDump.AbiSolidDumpResult(qual, csFasta, name);

                        if (!csFasta.exists() || !qual.exists()) {
                            continue;
                        }
                        abiresults.add(result);

                    }

                    fromfasta(new File(binFolder, NGS_FASTAQ_UTILS), abiresults, cores);
                    return;
                }

                // *****NGSUTILS FASTQUITLS CSENCODE*****
                if (commandLine.hasOption(NGS_FASTAQ_CSENCODE)) {
                    final List<File> fastqFiles = new ArrayList<>();
                    for (String name : sraNames) {
                        final File fastqFile = directory.toPath().resolve(name).resolve(name.concat(".fastq")).toFile();

                        if (!fastqFile.exists()) {
                            continue;
                        }
                        fastqFiles.add(fastqFile);

                    }

                    csencode(new File(binFolder, NGS_FASTAQ_UTILS), fastqFiles, cores);
                    return;
                }

                // *****ANALYSE*****
                if (commandLine.hasOption(ANALYSE)) {
                    if (!commandLine.hasOption(ANALYSIS_OUT)) {
                        System.out.println("Please specify an output folder for analysis with " + ANALYSIS_OUT + " flag!");
                        return;
                    }
                    final Path outDir = Paths.get(commandLine.getOptionValue(ANALYSIS_OUT));
                    final List<File[]> fastqFiles = getFastqFiles(sraNames, directory, prefix1, prefix2, append);

                    analyze(new File(binFolder, SOLEXAQAPP), fastqFiles, cores, outDir, probCutoff, variance, samples, format);
                    return;
                }

                //*****DYNAMICTRIM*****
                if (commandLine.hasOption(TRIM)) {
                    if (!commandLine.hasOption(TRIM_OUT)) {
                        System.out.println("Please specify an output folder for dynamictrim with " + TRIM_OUT + " flag!");
                        return;
                    }
                    final Path trimOut = Paths.get(commandLine.getOptionValue(TRIM_OUT));
                    final List<File[]> fastqFiles = getFastqFiles(sraNames, directory, prefix1, prefix2, append);

                    dynamictrim(new File(binFolder, SOLEXAQAPP), cores, probCutoff, fastqFiles, trimOut, format);
                    return;
                }

                //****LENGTHSORT****
                if (commandLine.hasOption(LENGTHSORT)) {
                    if (!commandLine.hasOption(LENGTHSORT_OUT)) {
                        System.out.println("Please specify an output folder for lengthsort with " + LENGTHSORT_OUT + " flag!");
                        return;
                    }
                    if (!commandLine.hasOption(LENGTHSORT_CUTOFF)) {
                        System.out.println("Please specify an cutoff value for lengthsort with " + LENGTHSORT_CUTOFF + " flag!");
                        return;
                    }
                    if (!commandLine.hasOption(TRIM_OUT)) {
                        System.out.println("Please specify an output folder containing dynamictrimmed files with " + TRIM_OUT + " flag!");
                        return;
                    }

                    final Path trimOut = Paths.get(commandLine.getOptionValue(TRIM_OUT));
                    final Path sortDir = Paths.get(commandLine.getOptionValue(LENGTHSORT_OUT));
                    final List<File[]> fastqFiles = getModFastqFiles(sraNames, trimOut.toFile(), prefix1, prefix2, append);
                    System.out.println(fastqFiles);
                    lengthsort(new File(binFolder, SOLEXAQAPP), cores, Integer.valueOf(commandLine.getOptionValue(LENGTHSORT_CUTOFF)), fastqFiles, sortDir);
                    return;
                }

                //*****BMTAG*****
                if (commandLine.hasOption(BMTAG)) {
                    if (!commandLine.hasOption(BMTAG_TMP)) {
                        System.out.println("Please specify a temp folder for BMTAGGER with " + BMTAG_TMP + " flag!");
                        return;
                    }
                    if (!commandLine.hasOption(BMTAG_REF_DIR)) {
                        System.out.println("Please specify the reference genome folder for BMTAGGER with " + BMTAG_REF_DIR + " flag!");
                        return;
                    }
                    if (!commandLine.hasOption(BMTAG_REF_NAME)) {
                        System.out.println("Please specify the reference genome name for BMTAGGER with " + BMTAG_REF_NAME + " flag!");
                        return;
                    }
                    if (!commandLine.hasOption(TRIM_OUT)) {
                        System.out.println("Please specify an output folder containing dynamictrimmed files with " + TRIM_OUT + " flag!");
                        return;
                    }
                    if (!commandLine.hasOption(REC_FORMAT)) {
                        System.out.println("Please specify the record fromatting (FastA or FastQ) " + REC_FORMAT + " flag!");
                        return;
                    }
                    final Path bmtaggerReference = Paths.get(commandLine.getOptionValue(BMTAG_REF_DIR));
                    final File bmtaggerExec = new File(binFolder, BMTAGGER);
                    final File bitmask = bmtaggerReference.resolve(commandLine.getOptionValue(BMTAG_REF_NAME).concat(".bitmask")).toFile();
                    final File srprism = bmtaggerReference.resolve(commandLine.getOptionValue(BMTAG_REF_NAME).concat(".srprism")).toFile();
                    final File tmpDir = new File(commandLine.getOptionValue(BMTAG_TMP));
                    final BMTagger.RestrictType type = BMTagger.RestrictType.valueOf(commandLine.getOptionValue(REC_FORMAT));

                    final Path trimOut = Paths.get(commandLine.getOptionValue(TRIM_OUT));
                    final List<File[]> fastqFiles = getModFastqFiles(sraNames, trimOut.toFile(), prefix1, prefix2, append);

                    final List<File> blacklists = bmtag(bmtaggerExec, bitmask, srprism, tmpDir, fastqFiles, cores, type);

                    restrict(fastqFiles, blacklists, cores, type);

                    return;
                }

                //*****TRINITY*****
                if (commandLine.hasOption(TRINITY)) {
                    if (!commandLine.hasOption(TRIM_OUT)) {
                        System.out.println("Please specify an output folder containing dynamictrimmed files with " + TRIM_OUT + " flag!");
                        return;
                    }
                    if (!commandLine.hasOption(REC_FORMAT)) {
                        System.out.println("Please specify the record fromatting (FastA or FastQ) " + REC_FORMAT + " flag!");
                        return;
                    }
                    if (!commandLine.hasOption(JVM_MEMORY)) {
                        System.out.println("Please specify the ammount of allocated JVM memory for trinity " + JVM_MEMORY + " flag!");
                        return;
                    }
                    if (!commandLine.hasOption(LIBTYPE)) {
                        System.out.println("Please specify libtype (F,R,FR,RF) for trinity " + LIBTYPE + " flag!");
                        return;
                    }
                    final Path trimOut = Paths.get(commandLine.getOptionValue(TRIM_OUT));
                    final List<File[]> fastqFiles = getModFastqFiles(sraNames, trimOut.toFile(), prefix1, prefix2, append);
                    final CallableProcessExecutor<File, Trinity> trinityExec = FixThreadCallableProcessExectuor.newInstance(1);
                    int i = 0;
                    for (File[] f : fastqFiles) {
                        final Trinity.TrinityBuilder trinityBuilder
                                = new Trinity.TrinityBuilder(
                                new File(TRINITY_EXEC),
                                Trinity.SEQ_TYPE.convertName(commandLine.getOptionValue(REC_FORMAT)),
                                Trinity.LIB_TYPE.valueOf(commandLine.getOptionValue(LIBTYPE)),
                                commandLine.getOptionValue(JVM_MEMORY));
                        if (f.length > 1) {
                            trinityBuilder.right(f[0].getPath());
                            trinityBuilder.left(f[1].getPath());
                        } else {
                            trinityBuilder.single(f[0].getPath());
                        }
                        if (commandLine.hasOption(MIN_CONTIG_LENGTH)) {
                            trinityBuilder.minCongtigLength(Integer.valueOf(commandLine.getOptionValue(MIN_CONTIG_LENGTH)));
                        }
                        if (commandLine.hasOption(TRINITY_OUTPUT_DIR)) {
                            trinityBuilder.outputDir(Paths.get(commandLine.getOptionValue(TRINITY_OUTPUT_DIR)));
                        }
                        if (commandLine.hasOption(BFLY_MAX_HEAP)) {
                            trinityBuilder.bflyMaxHeap(commandLine.getOptionValue(BFLY_MAX_HEAP));
                        }
                        if (commandLine.hasOption(MIN_CONTIG_LENGTH)) {
                            trinityBuilder.minCongtigLength(Integer.valueOf(commandLine.getOptionValue(MIN_CONTIG_LENGTH)));
                        }
                        trinityBuilder.bflyCPU(cores);
                        trinityBuilder.numCPU(cores);
                        trinityBuilder.outputDir(directory.toPath().resolve(sraNames.get(i)).resolve(Trinity.TRINITY));
                        trinityExec.addProcess(trinityBuilder.build());
                        i++;
                    }
                    for (Future<File> future : trinityExec.getFutures()) {
                        future.get();
                    }
                    trinityExec.shutdown();
                    return;
                }
            }

        } catch (IOException | SAXException | JAXBException | ParseException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param fastqutilsExec
     * @param fastqFiles
     * @param numthreads
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static List<File> csencode(File fastqutilsExec, List<File> fastqFiles, int numthreads) throws ExecutionException, InterruptedException {
        CallableProcessExecutor<File, CallableProcess<File>> executorCsencode = null;
        final List<File> outfiles = new ArrayList<>();

        try {
            executorCsencode = FixThreadCallableProcessExectuor.newInstance(numthreads);
            for (File file : fastqFiles) {
                executorCsencode.addProcess(NGSUtils.FastqUtils.csencode(fastqutilsExec, file, file.toPath().getParent()));
            }

        } finally {
            if (executorCsencode != null) {
                executorCsencode.shutdown();
            }
        }
        for (Future<File> future : executorCsencode.getFutures()) {
            outfiles.add(future.get());
        }
        return outfiles;

    }

    /**
     * @param fastqutilsExec
     * @param results
     * @param numthreads
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static List<File> fromfasta(File fastqutilsExec, List<AbiSolidDump.AbiSolidDumpResult> results, int numthreads) throws ExecutionException, InterruptedException {
        CallableProcessExecutor<File, CallableProcess<File>> executorFromFasta = null;
        final List<File> outfiles = new ArrayList<>();

        try {
            executorFromFasta = FixThreadCallableProcessExectuor.newInstance(numthreads);
            for (AbiSolidDump.AbiSolidDumpResult result : results) {
                executorFromFasta.addProcess(NGSUtils.FastqUtils.fromfasta(fastqutilsExec, result, result.getCsFasta().toPath().getParent()));
            }

        } finally {
            if (executorFromFasta != null) {
                executorFromFasta.shutdown();
            }
        }
        for (Future<File> future : executorFromFasta.getFutures()) {
            outfiles.add(future.get());
        }
        return outfiles;
    }

    /**
     * @param abiDumpExec
     * @param sras
     * @param numThreads
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static final List<AbiSolidDump.AbiSolidDumpResult> abidump(File abiDumpExec, List<File> sras, int numThreads) throws ExecutionException, InterruptedException {
        CallableProcessExecutor<AbiSolidDump.AbiSolidDumpResult, AbiSolidDump> executorAbi = null;
        final List<AbiSolidDump.AbiSolidDumpResult> results = new ArrayList<>();
        try {
            executorAbi = FixThreadCallableProcessExectuor.newInstance(numThreads);
            for (File f : sras) {
                final AbiSolidDump.AbiSolidDumpBuilder abiSolidDumpBuilder = new AbiSolidDump.AbiSolidDumpBuilder(abiDumpExec, f, f.toPath().getParent());
                executorAbi.addProcess(abiSolidDumpBuilder.build());
            }
        } finally {
            if (executorAbi != null) {
                executorAbi.shutdown();
            }
        }
        for (Future<AbiSolidDump.AbiSolidDumpResult> future : executorAbi.getFutures()) {
            results.add(future.get());
        }
        return results;
    }

    /**
     * @param driverXML
     * @param start
     * @param stop
     * @param mainFolder
     * @throws IOException
     * @throws JAXBException
     * @throws SAXException
     */
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

    /**
     * @param fastqDumpExec
     * @param numThreads
     * @param sraFiles
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static final void decompress(File fastqDumpExec, int numThreads, List<File> sraFiles) throws ExecutionException, InterruptedException {
        FastqDumpHelper.runTaskArray(fastqDumpExec, numThreads, sraFiles);
    }

    /**
     * @param solexappExec
     * @param fastqFiles
     * @param numThreads
     * @param outDir
     * @param probeCutoff
     * @param variance
     * @param sample
     * @param format
     * @throws ExecutionException
     * @throws InterruptedException
     */
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
                builder.probeCutoff(probeCutoff).variance(variance).sample(sample).format(format);
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

    /**
     * @param solexappExec
     * @param numThreads
     * @param probeCutoff
     * @param fastqFiles
     * @param trimDir
     * @param format
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static final void dynamictrim(File solexappExec, int numThreads,
                                          double probeCutoff, List<File[]> fastqFiles, Path trimDir
            , SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat format) throws ExecutionException, InterruptedException {

        CallableProcessExecutor<List<SolexaQAPPDynamicTrim.SolexaQAPPDynamicTrimResult>, SolexaQA<SolexaQAPPDynamicTrim.SolexaQAPPDynamicTrimResult>> executorTrim = null;
        try {

            executorTrim = FixThreadCallableProcessExectuor.newInstance(numThreads);
            for (File[] fs : fastqFiles) {
                final List<File> input = Arrays.asList(fs);
                final SolexaQAPPDynamicTrim.SolexaQAPPDynamicTrimBuilder builder = new SolexaQAPPDynamicTrim.SolexaQAPPDynamicTrimBuilder(solexappExec, input, trimDir);
                builder.probeCutoff(probeCutoff).format(format);
                executorTrim.addProcess(builder.build());
            }
            for (Future<List<SolexaQAPPDynamicTrim.SolexaQAPPDynamicTrimResult>> future : executorTrim.getFutures()) {

                for (SolexaQAPPDynamicTrim.SolexaQAPPDynamicTrimResult result : future.get()) {
                }
            }
        } finally {
            executorTrim.shutdown();
        }
    }

    /**
     * @param solexappExec
     * @param numThreads
     * @param cutoff
     * @param fastqFiles
     * @param sortDir
     * @param sortDir
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static final void lengthsort(File solexappExec, int numThreads,
                                         int cutoff, List<File[]> fastqFiles, Path sortDir
    ) throws ExecutionException, InterruptedException {

        CallableProcessExecutor<List<SolexaQAPPLengthSort.SolexaQAPPLengthSortResult>, SolexaQA<SolexaQAPPLengthSort.SolexaQAPPLengthSortResult>> executorTrim = null;
        try {

            executorTrim = FixThreadCallableProcessExectuor.newInstance(numThreads);
            for (File[] fs : fastqFiles) {
                final List<File> input = Arrays.asList(fs);
                final SolexaQAPPLengthSort.SolexaQAPPLengthSortBuilder builder = new SolexaQAPPLengthSort.SolexaQAPPLengthSortBuilder(solexappExec, input, sortDir);
                builder.length(cutoff);
                executorTrim.addProcess(builder.build());
            }
            for (Future<List<SolexaQAPPLengthSort.SolexaQAPPLengthSortResult>> future : executorTrim.getFutures()) {

                for (SolexaQAPPLengthSort.SolexaQAPPLengthSortResult result : future.get()) {
                }
            }
        } finally {
            executorTrim.shutdown();
        }
    }

    /**
     * @param bmTaggerExec
     * @param bitmask
     * @param sRPrism
     * @param tmpDir
     * @param fastqFiles
     * @param numThreads
     * @param type
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static final List<File> bmtag(File bmTaggerExec, File bitmask, File sRPrism, File tmpDir, List<File[]> fastqFiles, int numThreads, BMTagger.RestrictType type) throws ExecutionException, InterruptedException {
        CallableProcessExecutor<File, Callable<File>> fileCallableProcessExecutor = null;
        final List<File> blackLists;
        try {
            fileCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(numThreads);
            for (int i = 0; i < fastqFiles.size(); i++) {
                //If the right file exists do paiwise
                if (fastqFiles.get(i).length > 1) {
                    fileCallableProcessExecutor.addProcess(
                            new BMTagger.BMTaggerBuilder()
                                    .bmtaggerExecutale(bmTaggerExec)
                                    .lLane(fastqFiles.get(i)[0])
                                    .rLane(fastqFiles.get(i)[1])
                                    .referenceBitmask(bitmask)
                                    .referenceSrprism(sRPrism)
                                    .tmpDir(tmpDir)
                                    .restrictType(type)
                                    .build()
                    );
                } else {
                    fileCallableProcessExecutor.addProcess(
                            new BMTagger.BMTaggerBuilder()
                                    .bmtaggerExecutale(bmTaggerExec)
                                    .lLane(fastqFiles.get(i)[0])
                                    .referenceBitmask(bitmask)
                                    .referenceSrprism(sRPrism)
                                    .restrictType(type)
                                    .tmpDir(tmpDir).build()
                    );
                }
            }
            blackLists = new ArrayList<>();
            for (Future<File> future : fileCallableProcessExecutor.getFutures()) {
                blackLists.add(future.get());
            }
        } finally {
            if (fileCallableProcessExecutor != null) {
                fileCallableProcessExecutor.shutdown();
            }
        }
        return blackLists;
    }

    /**
     * @param fastqFiles
     * @param blacklists
     * @param numThreads
     * @param type
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */

    public static List<File> restrict(List<File[]> fastqFiles, List<File> blacklists, int numThreads, final BMTagger.RestrictType type) throws ExecutionException, InterruptedException {

        CallableProcessExecutor<File, Callable<File>> executor = null;
        final List<File> restrictedFiles;
        try {
            executor = FixThreadCallableProcessExectuor.newInstance(numThreads);
            int i = 0;
            for (File[] files : fastqFiles) {
                final File f1 = files[0];
                final File blacklist = blacklists.get(i);
                executor.addProcess(new Callable<File>() {
                    @Override
                    public File call() throws Exception {
                        return BMTagger.restrict(f1, f1.toPath().resolveSibling(f1.getName().concat(".rest")).toFile(), blacklist, type);
                    }
                });
                if (files.length > 1) {
                    final File f2 = files[1];
                    executor.addProcess(new Callable<File>() {
                        @Override
                        public File call() throws Exception {
                            return BMTagger.restrict(f2, f2.toPath().resolveSibling(f2.getName().concat(".rest")).toFile(), blacklist, type);
                        }
                    });
                }
                i++;
            }
            restrictedFiles = new ArrayList<>();
            for (Future<File> future : executor.getFutures()) {
                restrictedFiles.add(future.get());
            }

        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
        return restrictedFiles;
    }

    /**
     * @param commandLine
     * @return
     */
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

    /**
     * @param from
     * @param to
     * @param experimentpackageset
     * @return
     */
    private static final List<String> getSRANames(int from, int to, EXPERIMENTPACKAGESET experimentpackageset) {
        if (to == 0) {
            to = experimentpackageset.getEXPERIMENTPACKAGE().size();
        }
        final List<String> sraNames = new ArrayList<>();
        for (int i = from; i < to; i++) {
            final ExperimentPackageType experimentPackageType = experimentpackageset.getEXPERIMENTPACKAGE().get(i);
            for (RunType runty : experimentPackageType.getRUNSET().getRUN())
                sraNames.add(runty.getAccession());
        }
        return sraNames;
    }

    /**
     * @param sraNames
     * @param directory
     * @return
     */
    private static final List<File[]> getFastqFiles(List<String> sraNames, File directory, String prefix1, String prefix2, String extra) {
        final List<File[]> fastqFiles = new ArrayList<>();
        for (String name : sraNames) {

            final File sraFile_1 = new File(new File(directory, name), name.concat(prefix1).concat(".fastq").concat(extra));
            final File sraFile_2 = new File(new File(directory, name), name.concat(prefix2).concat(".fastq").concat(extra));
            if (!sraFile_1.exists()) {
                continue;
            } else if (!sraFile_2.exists()) {
                final File[] f = new File[]{sraFile_1};
                fastqFiles.add(f);
                System.out.println("Adding " + f + " to queue.");
            } else {
                final File[] f = new File[]{sraFile_1, sraFile_2};
                System.out.println("Adding " + f + " to queue.");
                fastqFiles.add(f);
            }
        }
        return fastqFiles;
    }

    /**
     * @param sraNames
     * @param directory
     * @param extra
     * @return
     */
    private static final List<File[]> getModFastqFiles(List<String> sraNames, File directory, String prefix1, String prefix2, String extra) {
        final List<File[]> fastqFiles = new ArrayList<>();
        for (String name : sraNames) {
            final File sraFile_1;
            final File sraFile_2;

            sraFile_1 = directory.toPath().resolve(name.concat(prefix1).concat(".fastq").concat(extra)).toFile();
            sraFile_2 = directory.toPath().resolve(name.concat(prefix2).concat(".fastq").concat(extra)).toFile();


            if (!sraFile_1.exists()) {
                continue;
            } else if (!sraFile_2.exists()) {
                final File[] f = new File[]{sraFile_1};
                fastqFiles.add(f);
                System.out.println("Adding " + f + " to queue.");
            } else {
                final File[] f = new File[]{sraFile_1, sraFile_2};
                System.out.println("Adding " + f + " to queue.");
                fastqFiles.add(f);
            }
        }
        return fastqFiles;
    }
}
