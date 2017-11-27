/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author lien
 */
public class CCMP {

    static Topology topo;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // TODO code application logic here
        if (args.length != 2) {
            System.err.print("Tham so khong hop le: can có 2 tham so.");
            System.err.println(args.length);
            System.out.print("Tham so gom : mode datafile pathfile resultfile parafile");
            System.exit(-1);
        }
        System.out.println("Tham so dau vao");
        //datafile
        System.out.println(args[0]);

        String datafile = args[1]+".txt";
        String pathfile = args[1]+"-paths.txt";
        String resultfile  = args[1]+"-results.txt";
        String parafile  = args[1]+"-paras.txt";
        switch (args[0]) {
             case "findpaths":
                topo = new Topology(datafile);
                topo.printTopology();
                topo.findAllPathsForAllDemands();
                writePaths(pathfile);
                break;
            case "test":
                topo = new Topology(datafile);
                topo.findAllPathsForAllDemands();
                topo.printTopology();
                topo.AlphaBetaMultipath();
                topo.calculateMaxUtilizationLink();
                topo.printTopology();
                topo.printPathsForBestDemands();
                topo.printBestUtilizationLink();
                topo.printDemands();
//                writeResults(resultfile);
                break;
            case "v1":
                //v1 25/11/2017)
                topo = new Topology(datafile);
                topo.getAllPathsForAllDemands(pathfile);
                topo.generateErrorLinks();
                topo.AlphaBetaMultipath();
                topo.printPathsForBestDemands();
                topo.printBestUtilizationLink();
                topo.printUsedCapacityNodes();
                topo.printBestDemands();
                writeParameters(parafile);
                writeResults(resultfile);
                break;
        }

    }

    public static void writeResults(String outputfile) {
        try {
            // writer.write("# accepted demand / maximum link utilization\n");
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));
           
            writer.write(String.valueOf(topo.Fbest) + " "
                    + String.valueOf(topo.acceptedDemandBest) + " " 
                    + String.valueOf(topo.calSatisfiedDemandsVolume() ) +" "
                    + String.valueOf(topo.bestMaxUtilizationLink ) +" "
                    + String.valueOf(topo.bestMaxUtilizationNode ) +" "
                    + String.valueOf(topo.findMinOfBandwidthVolumeFromBestDemands() ) +" "
                    + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
//    
//    public static void writeOneLineResult(String outputfile){
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));
//            writer.write(String.valueOf(topo.demands.size()) 
//                    + " " + String.valueOf(topo.numberScheduledDemands()) 
//                    + " " + String.valueOf(topo.calSatisfiedDemandsVolume()) 
//                    + " " + String.valueOf(topo.findMinOfBandwidthVolumeFromBestDemands()) 
//                    + " " + String.valueOf(topo.bestMaxUtilizationLink) 
//                    + " " + String.valueOf(topo.bestMaxUtilizationNode + "\n") );
//
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(-1);
//        }
//    }
     public static void writeParameters(String outputfile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));
            String scheme = "PRFit";
            if(topo.scheme==2) scheme = "PRPro";
             writer.write(String.valueOf("NumD   MaxP    alpha   beta    scheme  NumError \n"));
             writer.write(String.valueOf(topo.demands.size()) 
                    + " " + String.valueOf(topo.maxNumberShortestPaths) 
                    + " " + String.valueOf(topo.alpha) 
                    + " " + String.valueOf(topo.beta) 
                    + " " + String.valueOf(scheme) 
                    + " " + String.valueOf(topo.numberErrorLink + "\n") );
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public static void writePaths(String outputfile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));
            writer.write(String.valueOf(topo.demands.size()) + "\n");
            for (Demand d : topo.demands) {
                writer.write(String.valueOf(d.srcNode.getId()) + " " + String.valueOf(d.destNode.getId()) + "\n");
                writer.write(String.valueOf(d.allPaths.size()) + "\n");
                for (Path p : d.allPaths) {
                    writer.write(String.valueOf(p.nodes.size()) + " ");
                    for (Node v : p.nodes) {
                        writer.write(String.valueOf(v.getId()) + " ");
                    }
                    writer.write("\n");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
