package scripts;

import format.fasta.Fasta;
import format.fasta.nucleotide.NucleotideFasta;
import format.fasta.nucleotide.NucleotideFasta_AC_BadFormatException;
import format.fasta.nucleotide.NucleotideFasta_BadFormat_Exception;
import format.fasta.nucleotide.NucleotideFasta_Sequence_BadFormatException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by alext on 8/20/14.
 * TODO document class
 */
public class TrinityReads {


    public static void main(String[] args) {

        int [] srrs={9,8,7,6,5};
        for(int i:srrs){

                finalEffort(i);

        }

        /*try {
            System.out.println(calculateHuman(srrs[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }*/



    }
    public static void finalEffort(int i){
        final Path toRDCFile = Paths.get("/home/alext/Documents/ocular_rnaseq/sequences/eye/for_tuit/SRR59274"+i+".Trinity.rest.fasta.rdc");
        final Path toInitialFile = Paths.get("/home/alext/Documents/ocular_rnaseq/sequences/eye/for_tuit/SRR59274"+i+".Trinity.rest.fasta");
        final Path toTUITFile= Paths.get("/home/alext/Documents/ocular_rnaseq/sequences/eye/for_tuit/SRR59274"+i+".tuit");
        final Path toCountFile=Paths.get("/home/alext/Documents/ocular_rnaseq/sequences/eye/SRR59274"+i+"/count.txt");
        final Path toOutputFile=toInitialFile.resolveSibling("SRR59274"+i+".counts.tuit");
        try {
            reassignCountsToRDC(toRDCFile,toInitialFile,toTUITFile,toCountFile,toOutputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NucleotideFasta_Sequence_BadFormatException e) {
            e.printStackTrace();
        } catch (NucleotideFasta_AC_BadFormatException e) {
            e.printStackTrace();
        } catch (NucleotideFasta_BadFormat_Exception e) {
            e.printStackTrace();
        }
    }

    public static void reassignCountsToRDC(Path toRDCFile, Path toInitialFile, Path toTUITfile, Path toCountFile, Path toOutput) throws IOException, NucleotideFasta_AC_BadFormatException, NucleotideFasta_BadFormat_Exception, NucleotideFasta_Sequence_BadFormatException {
        final Map<String, Integer> acXcount = countsPerContig(toCountFile);
        final Map<String, String> sequenceXtaxa = connectToTaxa(toRDCFile, toTUITfile);
        final Map<String, List<String>> sequenceXacs = unfoldRDCFile(toRDCFile, toInitialFile);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(toOutput.toFile()))) {
            sequenceXtaxa.entrySet().stream().forEach(sXt -> {
               final StringBuilder stringBuilder=new StringBuilder();
               final List<String> acs=sequenceXacs.get(sXt.getKey());
               final int counts= acs.stream().mapToInt(ac->{
                    return acXcount.get(ac);
                }).sum();
                final String[]split=sXt.getValue().split("\t");
                final String[]sub=split[0].split("@");
                stringBuilder.append(sub[0]);
                stringBuilder.append("@");
                stringBuilder.append(counts);
                stringBuilder.append(":\t");
                stringBuilder.append(split[1]);
                if(split.length>2){
                    stringBuilder.append("\t");
                    stringBuilder.append(split[2]);
                }
                try {
                    bufferedWriter.write(stringBuilder.toString());
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    public static Map<String, String> connectToTaxa(Path toRDCFile, Path toTUITfile) throws IOException, NucleotideFasta_AC_BadFormatException, NucleotideFasta_BadFormat_Exception, NucleotideFasta_Sequence_BadFormatException {

        final Map<String, String> map = new HashMap<>();
        final List<NucleotideFasta> nucleotideFastas = null;
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(toRDCFile.toFile()))) {
            //BROKEN !!! nucleotideFastas = NucleotideFasta.loadFromText(inputStream);

        }
        final List<String> tuitLines;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(toTUITfile.toFile()))) {
            tuitLines = bufferedReader.lines().collect(Collectors.toList());
        }

        for (int i = 0; i < nucleotideFastas.size(); i++) {
            map.put(nucleotideFastas.get(i).getSequence(), tuitLines.get(i));
        }

        return map;

    }

    public static Map<String, List<String>> unfoldRDCFile(Path toRDCFile, Path toInitialFile) throws IOException {

        final Map<String, List<String>> nameMap = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(toRDCFile.toFile()))) {
            final String fastas = bufferedReader.lines().filter(line -> line.length() > 0).collect(Collectors.joining("\n"));
            final String[] split = fastas.split(Fasta.fastaStart);
            for (String s : split) {
                if (s.length() == 0) {
                    continue;
                }
                nameMap.put(s.substring(s.indexOf("\n")).replaceAll("\n", ""), new ArrayList<String>());
            }
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(toInitialFile.toFile()))) {
            final String fastas = bufferedReader.lines().filter(line -> line.length() > 0).collect(Collectors.joining("\n"));
            final String[] split = fastas.split(Fasta.fastaStart);
            for (String s : split) {
                if (s.length() == 0) {
                    continue;
                }
                final String sequence = s.substring(s.indexOf("\n")).replaceAll("\n", "");
                final List<String> acs = nameMap.get(sequence);
                if (acs == null) {
                    System.out.println("Faulty: " + sequence);
                    System.exit(0);                              //TODO replace
                }
                nameMap.get(sequence).add(s.substring(0, s.indexOf("\n")).split(" ")[0]);
            }
        }

        return nameMap;
    }


    public static int calculateHuman(int i) throws IOException {

        final Path toTrinityFasta = Paths.get("/home/alext/Documents/ocular_rnaseq/sequences/eye/SRR59274" + i + "/trinity_out_dir/Trinity.fasta");
        final Path toTrinityRestFasta = Paths.get("/home/alext/Documents/ocular_rnaseq/sequences/eye/for_tuit/SRR59274" + i + ".Trinity.rest.fasta");
        final Path toCountsFile = Paths.get("/home/alext/Documents/ocular_rnaseq/sequences/eye/SRR59274" + i + "/count.txt");


        final Set<String> humanSet = selectHumanContigs(toTrinityFasta, toTrinityRestFasta);
        final int human = numberOfReadsInHuman(toCountsFile, humanSet);

        return human;
    }

    public static Set<String> selectHumanContigs(Path toTrinityFasta, Path toTrinityRestFasta) throws IOException {

        final Set<String> existingContigs = new HashSet<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(toTrinityFasta.toFile()))) {

            bufferedReader.lines().filter(line -> line.startsWith(">")).forEach(line -> {
                existingContigs.add(line.split(" ")[0].substring(1));
            });
        }
        System.out.println("total: " + existingContigs.size());
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(toTrinityRestFasta.toFile()))) {
            bufferedReader.lines().filter(line -> line.startsWith(">")).forEach(line -> {
                final String ac = line.split(" ")[0].substring(1);
                if (existingContigs.contains(ac)) {
                    existingContigs.remove(ac);
                }
            });
        }
        System.out.println("human: " + existingContigs.size());
        return existingContigs;
    }

    public static int numberOfReadsInHuman(Path toCountsFile, Set<String> humanAcs) throws IOException {

        final Map<String, Integer> map = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(toCountsFile.toFile()))) {
            bufferedReader.lines().forEach(line -> {
                final String[] split = line.split("\t");
                if (humanAcs.contains(split[0])) {
                    map.put(split[0], Integer.valueOf(split[2]));
                }
            });
        }

        return map.entrySet().stream().mapToInt(entry -> entry.getValue()).sum();

    }

    public static Map<String, Integer> countsPerContig(Path toCountsFile) throws IOException {

        final Map<String, Integer> map = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(toCountsFile.toFile()))) {
            bufferedReader.lines().filter(line -> line.length() > 0).forEach(line -> {
                final String[] split = line.split("\t");
                map.put(split[0], Integer.valueOf(split[2]));
            });
        }
        return map;
    }
}
