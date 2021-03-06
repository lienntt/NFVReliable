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

        String datafile = args[1] + ".txt";
        String pathfile = args[1] + "-paths.txt";
        String resultfile = args[1] + "-results.txt";
        String parafile = args[1] + "-paras.txt";
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
            case "Experiment":
                writeTitleResult(resultfile, "//npath nerror F acceptD uL uN minDVolume \n");
                for (int disjoint = 1; disjoint <= 2; disjoint++) {
                    String disjoingScheme = (disjoint == 1) ? "false" : "true";
                    for (int scheme = 1; scheme <= 2; scheme++) {
                        String pathrateScheme = (scheme == 1) ? "Fit" : "Pro";
                        writeTitleResult(resultfile, "\n //" + pathrateScheme + " /np /er = 0 disjoint = " + disjoingScheme + " \n");
                        
                        for (int np = 1; np <= 6; np++) {
                            topo = new Topology(datafile);
                            topo.getAllPathsForAllDemands(pathfile);
                            topo.scheme = scheme;//fit
                            topo.disjointScheme = disjoint;//incompletely disjoint
                            topo.numberErrorLink = 0;
                            topo.maxNumberShortestPaths = np;
                            topo.AlphaBetaMultipath();
                            writeParameters(parafile);
                            writeResults(resultfile);
                        }
                    }
                }
                for (int disjoint = 1; disjoint <= 2; disjoint++) {
                    String disjoingScheme = (disjoint == 1) ? "false" : "true";
                    for (int scheme = 1; scheme <= 2; scheme++) {
                        String pathrateScheme = (scheme == 1) ? "Fit" : "Pro";
                        writeTitleResult(resultfile, "\n //" + pathrateScheme + " /np=3 /er = 0 disjoint = " + disjoingScheme + " \n");
                        
                        for (int loop = 0; loop < 20; loop++) {
                            writeTitleResult(resultfile, "\n //#" + loop + "\n");
                            for (int ne = 0; ne < 10; ne++) {
                                topo = new Topology(datafile);
                                topo.getAllPathsForAllDemands(pathfile);
                                topo.scheme = scheme;//fit
                                topo.disjointScheme = disjoint;//incompletely disjoint
                                topo.numberErrorLink = ne;
                                topo.maxNumberShortestPaths = 3;
                                topo.generateErrorLinks();
                                topo.AlphaBetaMultipath();
                                writeParameters(parafile);
                                writeResults(resultfile);
                            }
                        }
                    }
                }
                break;
        }

    }

    public static void writeResults(String outputfile) {
        try {
            // writer.write("# accepted demand / maximum link utilization\n");
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));

            writer.write(
                    String.valueOf(topo.maxNumberShortestPaths) + " "
                    + String.valueOf(topo.numberErrorLink) + " "
                    + String.valueOf(topo.Fbest) + " "
                    + String.valueOf(topo.acceptedDemandBest) + " "
                    + String.valueOf(topo.calSatisfiedDemandsVolume()) + " "
                    + String.valueOf(topo.bestMaxUtilizationLink) + " "
                    + String.valueOf(topo.bestMaxUtilizationNode) + " "
                    + String.valueOf(topo.findMinOfBandwidthVolumeFromBestDemands()) + " "
                    + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void writeTitleResult(String outputfile, String title) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));
            writer.write(String.valueOf(title)
                    + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void writeParameters(String outputfile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));
            String scheme = "PRFit";
            String disjointScheme = "incompletely";
            if (topo.scheme != 1) {
                scheme = "PRPro";
            }
            if (topo.disjointScheme != 1) {
                scheme = "completely";
            }
            writer.write(String.valueOf("NumD   MaxP  NumError  alpha   beta    PathRateScheme DisjointScheme   \n"));
            writer.write(String.valueOf(topo.calNumberScheduledDemands())
                    + " " + String.valueOf(topo.maxNumberShortestPaths)
                    + " " + String.valueOf(topo.numberErrorLink)
                    + " " + String.valueOf(topo.alpha)
                    + " " + String.valueOf(topo.beta)
                    + " " + String.valueOf(scheme)
                    + " " + String.valueOf(disjointScheme)
                    + "\n");
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
