package script;

import process.CallableProcessExecutor;
import process.FastqDumpHelper;
import process.FixThreadCallableProcessExectuor;
import tools.SolexaQA;
import tools.SolexaQAPP;
import tools.SolexaQAPPAnanlysis;
import xml.jaxb.EXPERIMENTPACKAGESET;
import xml.jaxb.ExperimentPackageType;
import xml.jaxb.SRAXMLLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by alext on 10/13/14.
 * TODO document class
 */
public class SRP017329Analysis {

    public static void main(String[] args) {

        final File driverXML = new File("/nethome/atuzhikov/target/SRP017329.xml");
        final File mainFolder = new File("/scratch/atuzhikov/SRP017329");
        final Path outDir=Paths.get("/scratch/atuzhikov/SRP017329/out");
        final File fastqDumpExec = new File("/nethome/atuzhikov/bin/fastq-dump.2.4.1");
        final File exec = new File("/nethome/atuzhikov/bin/solexaqapp");
        final int numThreads = 10;

        try (InputStream inputStream = new FileInputStream(driverXML)) {

            //1. Download all the SRA files
            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames = new ArrayList<>();
            for (ExperimentPackageType experimentPackageType : experimentpackageset.getEXPERIMENTPACKAGE()) {
                sraNames.add(experimentPackageType.getRUNSET().getRUN().get(0).getAccession());
            }
            System.out.println("SRR archives: " + sraNames.size());

            //Find out which folders exist

            final List<File> sraFiles = new ArrayList<>();
            for (String s : sraNames) {
                final File folder=new File(mainFolder,s);
                if(folder.exists()){
                    final File sra=new File(folder,s.concat(".sra"));
                    sraFiles.add(sra);
                }
            }

            //2. Decompress all the SRAs
            final List<File[]> fastqFiles = FastqDumpHelper.runTaskArray(fastqDumpExec, numThreads, sraFiles);

            //3. Get quality statistics for all
            final CallableProcessExecutor<List<SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult>, SolexaQA<SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult>> executor =
                    FixThreadCallableProcessExectuor.newInstance(numThreads);
            for(File[]fs:fastqFiles){
                final List<File> input = Arrays.asList(fs);
                final SolexaQAPPAnanlysis.SolexaQAPPAnalysisBuilder builder = new SolexaQAPPAnanlysis.SolexaQAPPAnalysisBuilder(exec, input, outDir);
                builder.probeCutoff(0.05).variance(true).sample(10000).format(SolexaQAPP.SolexaQAPPAnDyBuilder.SeqFormat.IONTORRENT).build();
                executor.addProcess(builder.build());
            }
            for (Future<List<SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult>> future : executor.getFutures()) {

                for (SolexaQAPPAnanlysis.SolexaQAPPAnalysisResult result : future.get()) {
                    System.out.println(result.getMatrix());
                    System.out.println(result.getMatrix_pdf());
                    System.out.println(result.getQuality());
                    System.out.println(result.getQuality_pdf());
                    System.out.println(result.getSegments());
                    System.out.println(result.getSegments_cumulative_pdf());
                    System.out.println(result.getSegments_hist_pdf());
                }
            }
            executor.shutdown();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
