package edu.miami.med.alext.brain;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import tools.BMTagger;
import tools.Trinity;
import xml.jaxb.EXPERIMENTPACKAGESET;
import xml.jaxb.ExperimentPackageType;
import xml.jaxb.SRAXMLLoader;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alext on 26/4/14.
 */
public class TrinityFastaFilter {

    public static void main(String[] ags) {

        final File driverXML = new File("/home/alext/Documents/Brain/full_process_of_SRP005169/human.xml");
        final File mainFolder = new File("/home/alext/Documents/Brain/full_process_of_SRP005169");
        try (InputStream inputStream = new FileInputStream(driverXML)) {

            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames = new ArrayList<>();
            for (ExperimentPackageType experimentPackageType : experimentpackageset.getEXPERIMENTPACKAGE()) {
                sraNames.add(experimentPackageType.getRUNSET().getRUN().get(0).getAccession());
            }
            System.out.println("SRR archives for humans: " + sraNames.size());


            List<File> folders = new ArrayList<>();
            for (String s : sraNames) {
                File subFolder = new File(mainFolder, s);
                subFolder = new File(subFolder, Trinity.OUTPUT_DIR_DEFAULT);
                folders.add(subFolder);
            }
            final File outputDir = new File("/home/alext/Documents/Brain/full_process_of_SRP005169/out");
            for (int i = 0; i < sraNames.size(); i++) {
                System.out.println("Filtering: " + sraNames.get(i));
                final File input = new File(folders.get(i), Trinity.OUTPUT_FILE);
                final File output = new File(folders.get(i), Trinity.OUTPUT_FILE.replaceAll("\\.fasta", ".rest.fasta"));
                final File blacklist = new File(folders.get(i), "Trinity.blacklist");
                BMTagger.restrict(input, output, blacklist, BMTagger.RestrictType.FastA);
                FileUtils.copyFile(output, new File(outputDir, sraNames.get(i).concat(".").concat(output.getName())));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}
