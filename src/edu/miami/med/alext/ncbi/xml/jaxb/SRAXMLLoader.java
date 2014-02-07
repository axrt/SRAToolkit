package edu.miami.med.alext.ncbi.xml.jaxb;

import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alext on 2/7/14.
 */
public class SRAXMLLoader {


    public static EXPERIMENTPACKAGESET catchBLASTOutput(InputStream in)
            throws SAXException, JAXBException {
        JAXBContext jc = JAXBContext.newInstance(EXPERIMENTPACKAGESET.class);
        Unmarshaller u = jc.createUnmarshaller();
        XMLReader xmlreader = XMLReaderFactory.createXMLReader();
        xmlreader.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlreader.setFeature("http://xml.org/sax/features/namespace-prefixes",
                true);
        xmlreader.setEntityResolver(new EntityResolver() {

            @Override
            public InputSource resolveEntity(String publicId, String systemId)
                    throws SAXException, IOException {
                String file = null;
                if (systemId.contains("SRA.package.xsd")) {
                    file = "SRA.package.xsd";
                }
                return new InputSource(EXPERIMENTPACKAGESET.class
                        .getResourceAsStream(file));
            }
        });
        InputSource input = new InputSource(in);
        Source source = new SAXSource(xmlreader, input);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        return (EXPERIMENTPACKAGESET) u.unmarshal(source);
    }

    @Test
    public void test(){
       try(InputStream inputStream=new FileInputStream(new File("/home/alext/Downloads/SraExperimentPackage.xml"));){

           final EXPERIMENTPACKAGESET experimentpackageset=catchBLASTOutput(inputStream);

           System.out.println();


       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
