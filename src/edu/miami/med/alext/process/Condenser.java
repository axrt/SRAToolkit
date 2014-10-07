package edu.miami.med.alext.process;

import format.fasta.Fasta;
import format.fasta.nucleotide.NucleotideFasta;
import format.fasta.nucleotide.NucleotideFasta_AC_BadFormatException;
import format.fasta.nucleotide.NucleotideFasta_BadFormat_Exception;
import format.fasta.nucleotide.NucleotideFasta_Sequence_BadFormatException;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by alext on 8/22/14.
 * TODO document class
 */
public class Condenser {


    public static Map<String, Integer> condense(Path toSamOutFile, Path toTrinityOutFile) throws IOException, NucleotideFasta_AC_BadFormatException, NucleotideFasta_BadFormat_Exception, NucleotideFasta_Sequence_BadFormatException {

        //1. Read SAM outfile
        final Map<String, Integer> samReads = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(toSamOutFile.toFile()))) {

            bufferedReader.lines().filter(line -> line.length() != 0).forEach(line -> {
                final String[] split = line.split("\t");
                samReads.put(split[0], Integer.valueOf(split[2]));
            });
        }

        //2. Get the contigs
        final List<NucleotideFasta> nucleotideFastas;
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(toTrinityOutFile.toFile()))) {
            nucleotideFastas = NucleotideFasta.loadFromText(inputStream);
        }

        //3. Condense and map the sequences to the summ of corresponding reads
        final Map<String, Integer> sequenceNumberOfReadsMap = new HashMap<>();
        nucleotideFastas.stream().forEach(nf -> {

            final String[] split = nf.getAC().split(" ");
            final String ac = split[0].substring(1);
            if (sequenceNumberOfReadsMap.containsKey(nf.getSequence())) {
                Integer numReads = sequenceNumberOfReadsMap.get(nf.getSequence());
                numReads += samReads.get(ac);
                sequenceNumberOfReadsMap.put(nf.getSequence(), numReads);
            } else {
                sequenceNumberOfReadsMap.put(nf.getSequence(), samReads.get(ac));
            }

        });

        return sequenceNumberOfReadsMap;
    }

    public static File saveCondensedSeqs(Map<String, Integer> seqsWithCounts, Path toOutFile) throws IOException {

        final File out = toOutFile.toFile();
        int[] counter = {0};
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(out))) {
            seqsWithCounts.entrySet().stream().forEach(entry -> {
                final String record = Fasta.fastaStart + counter[0] + '@' + entry.getValue() + '\n' + entry.getKey();

                try {

                    bufferedWriter.write(record);
                    bufferedWriter.newLine();

                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                counter[0]++;
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }

        return out;
    }


}
