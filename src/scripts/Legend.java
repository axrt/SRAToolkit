package scripts;

import org.xml.sax.SAXException;
import xml.jaxb.*;

import javax.xml.bind.JAXBException;
import java.io.*;

/**
 * Created by alext on 11/17/14.
 */
public class Legend {

    public static final String HEADER = "EXPERIMENT\tSAMPLE\tTITLE\tSOURCE\tTISSUE\tDISEASE_STATE";

    public static String legend(EXPERIMENTPACKAGESET experimentpackageset) {

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HEADER);
        stringBuilder.append('\n');
        for (ExperimentPackageType experimentPackageType : experimentpackageset.getEXPERIMENTPACKAGE()) {
            for (RunType run : experimentPackageType.getRUNSET().getRUN()) {
                stringBuilder.append(experimentPackageType.getEXPERIMENT().getAccession());
                stringBuilder.append('\t');
                stringBuilder.append(run.getAccession());
                stringBuilder.append('\t');
                stringBuilder.append(experimentPackageType.getSAMPLE().getTITLE());
                stringBuilder.append('\t');
                for (AttributeType attributeType : experimentPackageType.getSAMPLE().getSAMPLEATTRIBUTES().getSAMPLEATTRIBUTE()) {
                    stringBuilder.append(attributeType.getVALUE());
                    stringBuilder.append('\t');
                }
                stringBuilder.append('\n');
            }

        }
        return stringBuilder.toString().trim();
    }

    public static void main(String[] args) {

        final File legendFile = new File("/home/alext/Documents/Research/brain_rnaseq/SRP033725/SRP033725.xml");
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(legendFile))) {


            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            System.out.println(legend(experimentpackageset));


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
