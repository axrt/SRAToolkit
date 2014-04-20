package edu.miami.med.alext.mothur;

import edu.miami.med.alext.caseclass.Sample;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by alext on 2/18/14.
 */
public class MothurUtil {

    public static final String pathToMothur="/usr/local/bin/mothur";
    public static final String pathToUchime="/usr/local/bin/uchime";
    public static final String mothur="mothur";
    public static final String script=".script";
    public static final String primers="primers";

    public static File createMothurScript(String sra,File scriptFile) throws IOException {
        final StringBuilder scriptbuilder=new StringBuilder();
        //scriptbuilder.append(pathToMothur);
        //scriptbuilder.append(" \"#");
        //scriptbuilder.append("\"#");
        scriptbuilder.append("sffinfo(sff=".concat(sra).concat(".sff, flow=T);\n"));
        scriptbuilder.append("trim.flows(flow=".concat(sra).concat(".flow, oligos=".concat(new File(scriptFile.getParent(),primers).toString()).concat(", pdiffs=2, bdiffs=1, processors=12);\n")));
        scriptbuilder.append("shhh.flows(file=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".flow.files,lookup=/home/alext/lookup/LookUp_Titanium.pat, processors=12);\n");
        scriptbuilder.append("trim.seqs(fasta=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.fasta, name=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.names, oligos=".concat(new File(scriptFile.getParent(),primers).toString()).concat(", pdiffs=2, bdiffs=1, maxhomop=8, minlength=200, flip=T, processors=12);\n"));
        scriptbuilder.append("summary.seqs(fasta=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.trim.fasta, name=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.trim.names, processors=12);\n");
        scriptbuilder.append("chimera.uchime(fasta=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.trim.fasta, name=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.trim.names, group=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.groups, processors=12);\n");
        scriptbuilder.append("remove.seqs(accnos=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.trim.uchime.accnos, fasta=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.trim.fasta, name=");
        scriptbuilder.append(sra);
        scriptbuilder.append(".shhh.trim.names, group=");
        scriptbuilder.append(sra);
        //scriptbuilder.append("F.shhh.groups, dups=T)\"");
        scriptbuilder.append("F.shhh.groups, dups=T)");
        FileUtils.writeStringToFile(scriptFile, scriptbuilder.toString());
        return scriptFile;
    }

    public static File createPrimersFile(Sample sample,File primersFile) throws IOException {
        final StringBuilder primerFileBuilder=new StringBuilder();
        primerFileBuilder.append("forward ");
        primerFileBuilder.append(sample.getPrimer());
        primerFileBuilder.append("\tFWD\n");
        primerFileBuilder.append("barcode\t");
        primerFileBuilder.append(sample.getBarcode());
        primerFileBuilder.append('\t');
        primerFileBuilder.append(sample.getSeqAccession());
        FileUtils.writeStringToFile(primersFile,primerFileBuilder.toString());
        return primersFile;
    }
}
