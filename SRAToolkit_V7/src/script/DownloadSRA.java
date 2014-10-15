package script;

import xml.jaxb.EXPERIMENTPACKAGESET;
import xml.jaxb.ExperimentPackageType;
import xml.jaxb.RunType;
import xml.jaxb.SRAXMLLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alext on 10/14/14.
 * TODO document class
 */
public class DownloadSRA {

    public static void main(String[] args) {

        final int start=Integer.valueOf(args[0]);
        final int stop=Integer.valueOf(args[1]);

        final File driverXML = new File(args[2]);
        final File mainFolder = new File(args[3]);

        try (InputStream inputStream = new FileInputStream(driverXML)) {

            //1. Download all the SRA files
            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames = new ArrayList<>();
            for (int i=start;i<=stop;i++) {
                final ExperimentPackageType experimentPackageType = experimentpackageset.getEXPERIMENTPACKAGE().get(i);
                for(RunType runty: experimentPackageType.getRUNSET().getRUN())
                    sraNames.add(runty.getAccession());
            }


            System.out.println("SRR archives: " + sraNames.size());

            //Find out which folders exist

            final List<File> sraFiles = new ArrayList<>();
            for (String s : sraNames) {
                sraFiles.add(net.DownloadSRA.downloadSRAToANewFolder(s, mainFolder));
            }
            System.out.println(sraFiles.size()+" SRA downloaded.");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
