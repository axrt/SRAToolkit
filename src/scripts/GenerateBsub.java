package scripts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by alext on 11/18/14.
 */
public class GenerateBsub {

    public static final String study = "SRP033725";
    public static final int from = 4;
    public static final int to = 8;
    public static final int stepping = 1;
    public static final int time = 72;
    public static final int super_cores = 1;
    public static final int java_threads = 1;
    public static final int jvm_memory = 1;
    public static final Path toOutPutFolder = Paths.get("/home/alext/Documents/Research/brain_rnaseq/SRP033725/scr");
    public static final String generalHeader =
            "#BSUB -W " + time + ":00\n" +
                    "#BSUB -q general\n" +
                    "#BSUB -n " + super_cores + "\n" +
                    "#BSUB -B\n" +
                    "#BSUB -N\n" +
                    "#BSUB -u a.tuzhikov@med.miami.edu\n" +
                    "#\n";
    public static final String solexaqa =
            "java -jar " + "-Xmx" + jvm_memory + "G" + " /nethome/atuzhikov/sratoolkit.jar \\\n" +
                    "-dt \\\n" +
                    "-drv /nethome/atuzhikov/target/" + study + ".xml \\\n" +
                    "-dir /scratch/atuzhikov/" + study + "/ \\\n" +
                    "-tout /scratch/atuzhikov/" + study + "/ \\\n" +
                    "-pext2 _2 \\\n" +
                    "-pext1 _1 \\\n" +
                    "-bin ~/bin \\\n" +
                    "-subf \\\n";
    public static final String fastq_dump =
            "java -jar " + "-Xmx" + jvm_memory + "G" + " /nethome/atuzhikov/sratoolkit.jar \\\n" +
                    "-dc \\\n" +
                    "-drv /nethome/atuzhikov/target/" + study + ".xml \\\n" +
                    "-dir /scratch/atuzhikov/" + study + "/ \\\n" +
                    "-tout /scratch/atuzhikov/" + study + "/ \\\n" +
                    "-bin ~/bin \\\n" +
                    "-subf \\\n";
    public static final String trinity =
            "java -jar " + "-Xmx" + jvm_memory + "G" + " /nethome/atuzhikov/sratoolkit.jar \\\n" +
                    "-tr \\\n" +
                    "-drv /nethome/atuzhikov/target/" + study + ".xml \\\n" +
                    "-bin /nethome/atuzhikov/bin/ \\\n" +
                    "-dir /scratch/atuzhikov/" + study + "/ \\\n" +
                    "-pext2 nan \\\n" +
                    "-pext1 _1 \\\n" +
                    "-rfmt FastQ \\\n" +
                    "-aext .trimmed.rest \\\n" +
                    "-min_contig_length 50 \\\n" +
                    "-bflyHeapSpaceMax " + jvm_memory + "G \\\n" +
                    "-JM " + jvm_memory + "G \\\n" +
                    "-SS_lib_type F \\\n" +
                    "-tout /scratch/atuzhikov/" + study + "/ \\\n";
    public static final action curact = action.TRINITY;
    public static final String currentAct = trinity;

    public static void main(String[] args) {

        for (int i = from; i < to; i += stepping) {
            final Path toOutputFile = toOutPutFolder.resolve(study + '_' + curact + '_' + i + '_' + (i + stepping) + ".bsub");
            final String header =
                    "#!/bin/bash\n\n" +
                            "#BSUB -J " + study + "_" + curact + "_" + i + "_" + (i + stepping) + "\n" +
                            "#BSUB -o " + study + "_" + curact + "_" + i + "_" + (i + stepping) + ".out\n" +
                            "#BSUB -e " + study + "_" + curact + "_" + i + "_" + (i + stepping) + ".err\n";
            final String threads = "-c " + java_threads + " -from " + i + " -to " + (i + 1);
            final String out = header + generalHeader + currentAct + threads;
            try {
                writeOut(out, toOutputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void writeOut(String string, Path toFile) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(toFile.toFile()))) {
            bufferedWriter.write(string);
        }
    }

    public enum action {
        DYNAMICTRIM,
        DUMP,
        TRINITY;
    }
}
