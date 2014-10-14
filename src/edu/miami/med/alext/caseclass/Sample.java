package edu.miami.med.alext.caseclass;


import xml.jaxb.ExperimentPackageType;
import xml.jaxb.SampleDescriptorType;
import xml.jaxb.SpotDescriptorType;

/**
 * Created by alext on 2/7/14.
 */
public class Sample {

    protected final String samplePrimaryID;
    protected final String gender;
    protected final String sampleDefinintion;
    protected final String primer;
    protected final String barcode;
    protected final String adapter;
    protected final String primerType;
    protected final String seqAccession;

    protected Sample(String sampleName, String gender, String sampleDefinintion, String primer, String barcode, String adapter, String primerType, String seqAccession) {
        this.samplePrimaryID = sampleName;
        this.gender = gender;
        this.sampleDefinintion = sampleDefinintion.replaceAll("\\/","-").replaceAll(" ","_");
        this.primer = primer;
        this.barcode = barcode;
        this.adapter = adapter;
        this.primerType = primerType;
        this.seqAccession = seqAccession;
    }

    public String getSamplePrimaryID() {
        return samplePrimaryID;
    }

    public String getSampleDefinintion() {
        return sampleDefinintion;
    }

    public String getPrimer() {
        return primer;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getPrimerType() {
        return primerType;
    }

    public String getAdapter() {
        return adapter;
    }

    public String getSeqAccession() {
        return seqAccession;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {

        return this.samplePrimaryID.concat("\t")
                .concat(this.gender).concat("\t")
                .concat(this.sampleDefinintion).concat("\t")
                .concat(this.primer).concat("\t")
                .concat(primerType).concat("\t")
                .concat(this.barcode).concat("\t")
                .concat(this.adapter).concat("\t")
                .concat(this.seqAccession);

    }

    public static Sample fromExperimetnPackageSet(ExperimentPackageType experimentPackageType) {

        String samplePrimaryID = experimentPackageType.getSAMPLE().getAccession();
        String sampleDefinintion = experimentPackageType.getSAMPLE().getTITLE();
        String gender = "NA";
        if (sampleDefinintion.contains("DNA_")) {
            if (sampleDefinintion.contains("DNA_L_")) {
                String[] split = sampleDefinintion.split("DNA_L_");
                split = split[1].split(" participant");
                split = split[0].split(" of a ");
                sampleDefinintion = split[0];
                gender = split[1];
            } else if (sampleDefinintion.contains("DNA_R_")) {
                String[] split = sampleDefinintion.split("DNA_R_");
                split = split[1].split(" participant");
                split = split[0].split(" of a ");
                sampleDefinintion = split[0];
                gender = split[1];
            } else {
                String[] split = sampleDefinintion.split("DNA_");
                split = split[1].split(" participant");
                split = split[0].split(" of a ");
                sampleDefinintion = split[0];
                try {
                    gender = split[1];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }


        String primer = experimentPackageType.getEXPERIMENT().getDESIGN()
                .getSPOTDESCRIPTOR().getSPOTDECODESPEC().getREADSPEC().get(2)
                .getEXPECTEDBASECALLTABLE().getBASECALL().get(0).getValue();

        //Check if there is only one barcode
        String barcode = null;
        if (experimentPackageType.getEXPERIMENT().getDESIGN()
                .getSPOTDESCRIPTOR().getSPOTDECODESPEC().getREADSPEC().get(1)
                .getEXPECTEDBASECALLTABLE().getBASECALL().size() > 1) {
            barcode = "MANUAL";
            //Make an attempt to recover
            if (experimentPackageType.getEXPERIMENT().getDESIGN().getSAMPLEDESCRIPTOR().getPOOL().getMEMBER() != null
                    && experimentPackageType.getEXPERIMENT().getDESIGN().getSAMPLEDESCRIPTOR().getPOOL().getMEMBER().size() > 0) {
                for (SampleDescriptorType.POOL.MEMBER m : experimentPackageType.getEXPERIMENT().getDESIGN().getSAMPLEDESCRIPTOR().getPOOL().getMEMBER()) {
                    if (m.getAccession().equals(samplePrimaryID)) {
                        System.out.println("Barcode recovered for id:" + samplePrimaryID);
                        final String[] codeSplit = m.getMemberName().split("_");
                        //check if that is a correct place to get the barcode
                        for (Character c : codeSplit[1].toCharArray()) {
                            if (Character.isDigit(c)) {
                                barcode = codeSplit[0];
                                for (SpotDescriptorType.SPOTDECODESPEC.READSPEC.EXPECTEDBASECALLTABLE.BASECALL bc : experimentPackageType.getEXPERIMENT().getDESIGN()
                                        .getSPOTDESCRIPTOR().getSPOTDECODESPEC().getREADSPEC().get(1).getEXPECTEDBASECALLTABLE().getBASECALL()) {
                                    if (bc.getReadGroupTag().equals(barcode)) {
                                        barcode = bc.getValue();
                                        System.out.println("Barcode :" + barcode);
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                        for (Character c : barcode.toCharArray()) {
                            if (Character.isDigit(c)) {
                                if (m.getREADLABEL().size() > 1) {
                                    for(SampleDescriptorType.POOL.MEMBER.READLABEL rl:m.getREADLABEL()){
                                        if(rl.getValue().equals("barcode")){
                                            barcode = rl.getReadGroupTag();
                                            if(barcode.contains("_")){
                                                barcode=barcode.split("_")[1];
                                            }
                                            break;
                                        }
                                    }
                                    System.out.println("Barcode (readlabel):" + barcode);
                                }
                                break;
                            }

                        }
                    }

                }
            }
        } else {
            barcode = experimentPackageType.getEXPERIMENT().getDESIGN()
                    .getSPOTDESCRIPTOR().getSPOTDECODESPEC().getREADSPEC().get(1)
                    .getEXPECTEDBASECALLTABLE().getBASECALL().get(0).getValue();
        }
        String spacer = "NA";
        try {
            spacer = experimentPackageType.getEXPERIMENT().getDESIGN()
                    .getSPOTDESCRIPTOR().getSPOTDECODESPEC().getREADSPEC().get(0)
                    .getEXPECTEDBASECALLTABLE().getBASECALL().get(0).getValue();
        } catch (Exception e) {

        }
        String primerType = experimentPackageType.getEXPERIMENT().getDESIGN()
                .getSPOTDESCRIPTOR().getSPOTDECODESPEC().getREADSPEC().get(3)
                .getREADLABEL();
        String seqAccession = experimentPackageType.getRUNSET().getRUN().get(0).getAccession();

        if (samplePrimaryID == null) {
            samplePrimaryID = "NA";
        }
        if (sampleDefinintion == null) {
            sampleDefinintion = "NA";
        }
        if (primer == null) {
            primer = "NA";
        }
        if (barcode == null) {
            barcode = "NA";
        }
        if (primerType == null) {
            primerType = "NA";
        }
        if (seqAccession == null) {
            seqAccession = "NA";
        }


        return new Sample(samplePrimaryID, gender, sampleDefinintion, primer, barcode, spacer, primerType, seqAccession);
    }
    public static Sample newInstanceFromComponents(String sampleName, String gender, String sampleDefinintion, String primer, String barcode, String adapter, String primerType, String seqAccession){
        return new Sample(sampleName,gender,sampleDefinintion,primer,barcode,adapter,primerType,seqAccession);
    }
}
