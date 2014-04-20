package scripts;

import edu.miami.med.alext.caseclass.Sample;
import edu.miami.med.alext.ncbi.xml.jaxb.EXPERIMENTPACKAGESET;
import edu.miami.med.alext.ncbi.xml.jaxb.ExperimentPackageType;
import edu.miami.med.alext.ncbi.xml.jaxb.SRAXMLLoader;
import org.junit.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by alext on 2/7/14.
 */
public class AssembleFullAnnotationFile {

    @Test
    public void assemble(){
        try(
        final InputStream inputStream=new FileInputStream(new File("/home/alext/Downloads/SraExperimentPackage.xml"));
        final BufferedWriter bufferedWriter =new BufferedWriter(new FileWriter(new File("/home/alext/Documents/HMPSamples/fullannotation.txt")));
        ){
            bufferedWriter.write(

                    "Sample Primary ID".concat("\t")
                    .concat("Gender").concat("\t")
                    .concat("Sample Description (definition)").concat("\t")
                    .concat("Primer Sequence").concat("\t")
                    .concat("Primer Type").concat("\t")
                    .concat("Barcode Sequence").concat("\t")
                    .concat("Adapter Sequence").concat("\t")
                    .concat("SRA Sequencing Accession Number")

            );
            bufferedWriter.newLine();
            final EXPERIMENTPACKAGESET experimentpackageset= SRAXMLLoader.catchXMLOutput(inputStream);
            int count=0;

            final Set<String> samples=new HashSet<>();
            final Set<String> descriptions=new HashSet<>();
            final Set<String> sras=new HashSet<>();
            final Set<String> barcodes=new HashSet<>();
            final Set<String> primers=new HashSet<>();

            for(ExperimentPackageType expt:experimentpackageset.getEXPERIMENTPACKAGE()){
                final Sample s=Sample.fromExperimetnPackageSet(expt);

                samples.add(s.getSamplePrimaryID());
                descriptions.add(s.getSampleDefinintion());
                sras.add(s.getSeqAccession());
                barcodes.add(s.getBarcode());
                primers.add(s.getPrimer());

                bufferedWriter.write(s.toString());
                bufferedWriter.newLine();
                count++;
            }
            System.out.println("Records written:"+count);
            System.out.println("Samples:"+samples.size());
            System.out.println("Descriptions:"+descriptions.size());
            System.out.println("Primers:"+primers.size());
            System.out.println("Barcodes:"+barcodes.size());
            System.out.println("SRAs:" + sras.size());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
