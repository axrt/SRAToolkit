package scripts;

import edu.miami.med.alext.caseclass.Sample;
import edu.miami.med.alext.mothur.MothurUtil;
import edu.miami.med.alext.net.DownloadSRA;
import edu.miami.med.alext.sra.SraUtil;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alext on 2/18/14.
 */
public class Processor {


    public static void main(String[] args) {

        final File dir = new File("/home/alext/Documents/HMPSamples/production");
        final File driverFile = new File("/home/alext/Documents/HMPSamples/fullannotation.txt");

        try {
            final List<Sample> samples = loadDriverTable(driverFile);
            int count = 0;
            for (Sample sample : samples) {
                if (!new File(dir, sample.getSeqAccession()).exists()) {
                    //Download the file
                    System.out.println("Downloading SRA: ".concat(sample.getSeqAccession()));
                    final File sraFile = DownloadSRA.downloadSRAToANewFolder(sample.getSeqAccession(), dir);
                    //Deploy the sra
                    System.out.println("Extracting sff: ".concat(sample.getSeqAccession()));
                    ProcessBuilder processBuilder = new ProcessBuilder(SraUtil.PATHTOSFFDUMP_2, sraFile.toString(), "--outdir", sraFile.getParent().toString());
                    Process process = processBuilder.start();
                    process.waitFor();
                    observeSystemResponce("sff-dump.2: ", process);
                    //Create a mothur script
                    final File mothurScriptFile = MothurUtil.createMothurScript(new File(sraFile.getParent(), sample.getSeqAccession()).toString(), new File(sraFile.getParent(), MothurUtil.mothur.concat(MothurUtil.script)));
                    //Create a primer file
                    MothurUtil.createPrimersFile(sample, new File(sraFile.getParent(), MothurUtil.primers));
                    //Run mothur
                    processBuilder = new ProcessBuilder(MothurUtil.pathToMothur, mothurScriptFile.toString());
                    process = processBuilder.start();
                    process.waitFor();
                    observeSystemResponce(MothurUtil.mothur.concat(": "), process);
                    count++;
                } else{
                    System.out.println(sample.getSeqAccession()+" folder exists, proceeding..");
                }
            }
            System.out.println("TASK FINISHED, " + count + " samples processed.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static List<Sample> loadDriverTable(final File driverTable) throws IOException {

        final List<Sample> samples = new LinkedList<>();
        try (
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(driverTable));
        ) {
            String line;
            int count = 0;
            while ((line = bufferedReader.readLine()) != null) {
                final String[] split = line.split("\t");
                if (count > 1) {
                    samples.add(Sample.newInstanceFromComponents(split[0], split[1], split[2], split[3], split[5], split[4], split[6], split[7]));
                }
                count++;
            }
        }
        return samples;
    }

    public static void observeSystemResponce(final String prefix, final Process process) throws IOException {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(prefix.concat(line));
            }
        }
    }
}
