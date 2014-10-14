package scripts;

import edu.miami.med.alext.caseclass.Sample;
import xml.jaxb.EXPERIMENTPACKAGESET;
import xml.jaxb.SRAXMLLoader;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by alext on 2/7/14.
 */
public class PlayAround {

    @Test
    public void play(){
        try(InputStream inputStream=new FileInputStream(new File("/home/alext/Downloads/SraExperimentPackage.xml"));){

            final EXPERIMENTPACKAGESET experimentpackageset= SRAXMLLoader.catchXMLOutput(inputStream);

            System.out.println(Sample.fromExperimetnPackageSet(experimentpackageset.getEXPERIMENTPACKAGE().get(0)));



        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
