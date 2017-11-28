/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Hashtable;
import java.util.Random;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 *
 * @author liem
 */
public class Topology extends BaseTopology {

    final double INITIALIZE_WEIGHT = 1.0;
    ArrayList<Node> srcNodes;
    ArrayList<Node> destNodes;
    Node bigSrcNode;
    Node bigDestNode;
    ArrayList<Node> nodes; // routing nodes
    ArrayList<Link> links;
    ArrayList<Link> coreLinks;
    ArrayList<Demand> demands;
    ArrayList<Function> functions;
    ArrayList<Demand> bestDemands;
    double Fcurrent = 0; //F()
//    double Fneighbor;// F(wneigbor)
    double Fbest = 0; //Fbest
    double Fbest2 = 0; //Fbest
    double yMin = 0;
    double yMax = 0;
    int acceptedDemand = 0;
    int acceptedDemandCurrent = 0;
    int acceptedDemandBest = 0;
    int acceptedDemandNeighbor = 0;
    int count = 0;
    int functionNum;
    int maxFunctionRequired;
    int srcNodesNum;
    int destNodesNum;
    double srcToCoreRate;
    double coreToDestRate;
    double srcBandwidth;
    double destBandwidth;
    double bigSrcBandwidth = Double.MAX_VALUE;
    double bigDestBandwidth = Double.MAX_VALUE;
    int demandNum;
    double minDemandVolume;
    double maxDemandVolume;
    double rangeDemandVolume;
    int maxFunctionNumForDemand;
    double Fmax;//luong cuc dai tu BigSrc den BigDest
    double alpha = 0;
    double beta = 0;
    int maxNumberShortestPaths;
    int scheme = 2; //scheme 1:fit other pro
    int disjointScheme = 2; // disjointScheme: 1 default disjoint incompletely, other disjoint completely
    double bestMaxUtilizationLink = 0;
    double bestMaxUtilizationNode = 0;
    int numberErrorLink = 5 ;

    public Topology() {
    }

    public Topology(String nodefile) {
        nodes = new ArrayList<Node>();
        srcNodes = new ArrayList<Node>();
        destNodes = new ArrayList<Node>();

        links = new ArrayList<Link>();
        coreLinks = new ArrayList<Link>();
        demands = new ArrayList<Demand>();
        bestDemands = new ArrayList<Demand>();
        functions = new ArrayList<Function>();

        readInputFile(nodefile);
//        addReverseLinks();

//        getParameters(inputfile);

//        generateSrcNodes();
//        addSrcNodes();
//
//        generateDestNodes();
//        addDestNodes();
        addBigSrcNodeToTopology();
        addBigDestNodeToTopology();

//        generateFunctions();
//        generateDemands();
        setInitialWeightForLinks();
        calculateMaximumFlow();
//        demands = sortDemandsDecreaseAccordingToVolume(demands);
    }

    public void readInputFile(String nodefile) {
        int nodeNum;
        int functionNum;
        int linkNum;
        int demandNum;
        double volumn;
        int functionDemandNum;

        ArrayList<Integer> functionIds = new ArrayList<Integer>();
        int functionId;
        int srcNodeIndex;
        int destNodeIndex;
        try {
            //function number
            // functionId functionDemandVolume
            File f = new File(nodefile);
            Scanner reader = new Scanner(f);
            
            maxNumberShortestPaths = Integer.parseInt(reader.nextLine());
            reader.nextLine();
            
            demandNum = Integer.parseInt(reader.nextLine());
            reader.nextLine();
            
            //lay danh sach cac function
            functionNum = Integer.parseInt(reader.nextLine());

            for (int i = 0; i < functionNum; i++) {
                Function func = new Function(reader.nextInt());
                func.setRequireResource(reader.nextInt());
                if (!functions.contains(func)) {
                    functions.add(func);
                }

            }

            reader.nextLine();
            reader.nextLine();
            //lấy danh sách các node và computing capacity
            nodeNum = Integer.parseInt(reader.nextLine());
//            System.out.println("node Num: " + nodeNum);

            //nodes number
            //nodeId nodeCapacity
            for (int i = 0; i < nodeNum; i++) {
                Node node = new Node();
                node.setId(reader.nextInt());
                node.setComputingCapacity(reader.nextInt());
                nodes.add(node);
            }

            reader.nextLine();
            nodeNum = Integer.parseInt(reader.nextLine());
            //Src node set
            //nodes number
            //nodeId nodeCapacity
            for (int i = 0; i < nodeNum; i++) {
                Node node = new Node();
                node.setId(reader.nextInt());
                node.setComputingCapacity(reader.nextInt());
                srcNodes.add(node);
            }

            reader.nextLine();
            nodeNum = Integer.parseInt(reader.nextLine());
            // dest node set
            //nodes number
            //nodeId nodeCapacity
            for (int i = 0; i < nodeNum; i++) {
                Node node = new Node();
                node.setId(reader.nextInt());
                node.setComputingCapacity(reader.nextInt());
                destNodes.add(node);
            }

            reader.nextLine();
            reader.nextLine();
            //lấy danh sách các link
            linkNum = Integer.parseInt(reader.nextLine());
            System.err.println("linkNum: " + linkNum);
            // lấy danh sách các link
            for (int i = 0; i < linkNum; i++) {
                srcNodeIndex = reader.nextInt();
                destNodeIndex = reader.nextInt();
                // link (src, dest, bandwidth)
                double bandwidth = reader.nextInt();
                Link link = new Link(getNodeById(srcNodeIndex), getNodeById(destNodeIndex), bandwidth);
                getNodeById(srcNodeIndex).getOutLinks().add(link);
                getNodeById(destNodeIndex).getInLinks().add(link);
                links.add(link);
                coreLinks.add(link);
//                  // link (src, dest, bandwidth
//                Link  linkReverse = new Link(topo.nodes.get(destNodeIndex), topo.nodes.get(srcNodeIndex), banwidth);
//                topo.nodes.get(destNodeIndex).getOutLinks().add(linkReverse);
//                topo.nodes.get(srcNodeIndex).getInLinks().add(linkReverse);
//                topo.links.add(linkReverse);
            }
            reader.nextLine();
            //lấy danh sách các link form src to core and from core to dest
            linkNum = Integer.parseInt(reader.nextLine());
            // lấy danh sách các link
            for (int i = 0; i < linkNum; i++) {
                srcNodeIndex = reader.nextInt();
                destNodeIndex = reader.nextInt();
                // link (src, dest, bandwidth)
                double bandwidth = reader.nextInt();
                Link link = new Link(getNodeById(srcNodeIndex), getNodeById(destNodeIndex), bandwidth);
                getNodeById(srcNodeIndex).getOutLinks().add(link);
                getNodeById(destNodeIndex).getInLinks().add(link);
                links.add(link);

//                  // link (src, dest, bandwidth
//                Link  linkReverse = new Link(topo.nodes.get(destNodeIndex), topo.nodes.get(srcNodeIndex), banwidth);
//                topo.nodes.get(destNodeIndex).getOutLinks().add(linkReverse);
//                topo.nodes.get(srcNodeIndex).getInLinks().add(linkReverse);
//                topo.links.add(linkReverse);
            }

            //lấy danh sách các demnad
            reader.nextLine();
            reader.nextLine();
            Integer.parseInt(reader.nextLine());//demandNum
            double maxvolume;
            // lấy danh sách các demand
            for (int i = 0; i < demandNum; i++) {
                srcNodeIndex = reader.nextInt();
                destNodeIndex = reader.nextInt();
                volumn = reader.nextInt();
                maxvolume = reader.nextInt();
                functionDemandNum = reader.nextInt();
                ArrayList<Function> functions1 = new ArrayList<Function>();
                //get danh sach function
                for (int j = 0; j < functionDemandNum; j++) {
                    functionId = reader.nextInt();
                    for (Function func : functions) {
                        if (func.getId() == functionId) {
                            functions1.add(func);
                        }
                    }
                }
                Demand demand = new Demand(getNodeById(srcNodeIndex), getNodeById(destNodeIndex), volumn, maxvolume, functions1);
                demands.add(demand);
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    //get Parameters for project
    public void getParameters(String nodefile) {
        try {
            File f = new File(nodefile);
            Scanner reader = new Scanner(f);
            reader.nextLine();
            //lay so function
            functionNum = reader.nextInt();
            maxFunctionRequired = reader.nextInt();
            demandNum = reader.nextInt();
            minDemandVolume = reader.nextDouble();
            maxDemandVolume = reader.nextDouble();
            rangeDemandVolume = reader.nextDouble();
            maxFunctionNumForDemand = reader.nextInt();
            srcNodesNum = reader.nextInt();
            srcToCoreRate = reader.nextDouble();
            srcBandwidth = reader.nextDouble();
            destNodesNum = reader.nextInt();
            coreToDestRate = reader.nextDouble();
            destBandwidth = reader.nextDouble();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void printParameters() {
        System.out.println("Parameters: ");
        System.out.println("Number of functions: " + functionNum);
        System.out.println("Max function required computing capacity: " + maxFunctionRequired);
        System.out.println("Number of demands: " + demandNum);
        System.out.println("Min of Demand Volume: " + minDemandVolume);
        System.out.println("Max of Demand Volume: " + maxDemandVolume);
        System.out.println("Range of demand volume: " + rangeDemandVolume);
        System.out.println("Max function number for Demand: " + rangeDemandVolume);
        System.out.println("Number source nodes : " + srcNodesNum);
        System.out.println("Rate of link connect src to core: " + srcToCoreRate);
        System.out.println("Source to core bandwidth: " + srcBandwidth);
        System.out.println("Number destination nodes " + destNodesNum);
        System.out.println("Rate of link connect core to dest: " + coreToDestRate);
        System.out.println("Core to dest Bandwidth : " + destBandwidth);
    }

    public void addReverseLinks() {
        ArrayList<Link> reverseLinks = new ArrayList<Link>();
        for (Link e : links) {
            Link re = new Link(e.getDestNode(), e.getSrcNode(), e.getBandwidthCapacity());
            reverseLinks.add(re);
            e.getDestNode().getOutLinks().add(re);
            e.getSrcNode().getInLinks().add(re);

        };
        for (Link e : reverseLinks) {
            links.add(e);
        };
    }

    public void generateSrcNodes() {
        for (int i = 0; i < srcNodesNum; i++) {
            Node v = new Node();
            srcNodes.add(v);
        }
    }

    public void generateDestNodes() {
        for (int i = 0; i < destNodesNum; i++) {
            Node v = new Node();
            destNodes.add(v);
        }
    }

    public void addSrcNodes() {
        int count = 0;
        for (Node src : srcNodes) {
            for (Node core : nodes) {
                if (Math.random() < srcToCoreRate) {
                    Link e = new Link(src, core, srcBandwidth);
                    links.add(e);
                    src.getOutLinks().add(e);
                    core.getInLinks().add(e);
                    count++;
                }
            }
            if (count < 1) {
                Node core = nodes.get(randomNumber(nodes.size()));
                Link e = new Link(src, core, srcBandwidth);
                links.add(e);
                src.getOutLinks().add(e);
                core.getInLinks().add(e);
            }
            count = 0;
        }

    }

    public void addDestNodes() {
        int count = 0;
        for (Node dest : destNodes) {
            for (Node core : nodes) {
                if (Math.random() < coreToDestRate) {
                    Link e = new Link(core, dest, destBandwidth);
                    links.add(e);
                    core.getOutLinks().add(e);
                    dest.getInLinks().add(e);
                    count++;
                }
            }
            if (count < 1) {
                Node core = nodes.get(randomNumber(nodes.size()));
                Link e = new Link(core, dest, destBandwidth);
                links.add(e);
                core.getOutLinks().add(e);
                dest.getInLinks().add(e);
            }
            count = 0;
        }
    }

    public void generateDemands() {
        for (int i = 0; i <= demandNum; i++) {
            Node src = srcNodes.get(randomNumber(srcNodes.size()));
            Node dest = destNodes.get(randomNumber(destNodes.size()));
            ArrayList<Function> funcs = new ArrayList<Function>();
            funcs = randomFunctionList(randomNumber(maxFunctionNumForDemand) + 1);
            //demandVolume form minDemandVolume to maxDemandVolume
            double demandVolume = randomNumber(minDemandVolume, maxDemandVolume);
            Demand d = new Demand(src, dest, demandVolume, demandVolume * (1 + rangeDemandVolume), funcs);
            demands.add(d);
        }
    }

    public ArrayList<Function> randomFunctionList(int num) {
        ArrayList<Function> allFuncs = (ArrayList<Function>) functions.clone();
        ArrayList<Function> funcs = new ArrayList<Function>();
        for (int i = 0; i < num; i++) {
            int findex = randomNumber(allFuncs.size());
            Function f = allFuncs.get(findex);
            funcs.add(f);
            allFuncs.remove(findex);
        }
        return funcs;
    }
    
     public ArrayList<Link> randomCoreLinkList(int num) {
        ArrayList<Link> allCoreLinks = (ArrayList<Link>) coreLinks.clone();
        ArrayList<Link> linklist = new ArrayList<Link>();
        for (int i = 0; i < num; i++) {
            int lindex = randomNumber(allCoreLinks.size());
            Link e = allCoreLinks.get(lindex);
            linklist.add(e);
            allCoreLinks.remove(lindex);
        }
        return linklist;
    }

    public void generateFunctions() {
        for (int i = 0; i <= functionNum; i++) {
            Function f = new Function(i);
            f.setRequireResource(randomNumber(maxFunctionRequired) + 1);
            functions.add(f);
        }
    }

    public void addBigSrcNodeToTopology() {
        bigSrcNode = new Node();
        bigSrcNode.setId(nodes.size()+srcNodes.size()+destNodes.size());
        for (Node v : srcNodes) {
            Link e = new Link(bigSrcNode, v, bigSrcBandwidth);
            links.add(e);
            bigSrcNode.getOutLinks().add(e);
            v.getInLinks().add(e);
        }
    }

    public void addBigDestNodeToTopology() {
        bigDestNode = new Node();
        bigDestNode.setId(bigSrcNode.getId()+1);
        for (Node v : destNodes) {
            Link e = new Link(v, bigDestNode, bigDestBandwidth);
            links.add(e);
            v.getOutLinks().add(e);
            bigDestNode.getInLinks().add(e);
        }
    }

    public void calculateMaximumFlow() {
        int srcId = bigSrcNode.getId();
        int destId = bigDestNode.getId();
        int numNode = srcNodes.size() + nodes.size() + destNodes.size() + 2;
        double[][] linkMatrix = new double[numNode][];
        for (int i = 0; i < numNode; i++) {
            linkMatrix[i] = new double[numNode];
            for (int j = 0; j < numNode; j++) {
                linkMatrix[i][j] = 0;
            }
        }
        for (Link e : links) {
            linkMatrix[e.getSrcNode().getId()][e.getDestNode().getId()] = e.getBandwidthCapacity();
        }
        Fmax = MaxFlow.calMaxFlow(linkMatrix, srcId, destId, numNode);
        System.out.println("MaxFlow: " + Fmax);

    }

    public void setInitialWeightForLinks() {
        for (Link v : links) {
            v.setWeight(INITIALIZE_WEIGHT);
        }
    }

    //27/11/2017
    public void generateErrorLinks(){
        ArrayList<Link> errorLinks = randomCoreLinkList(numberErrorLink);
        System.out.println("Number link core before generate Error: " + links.size());
        for(Link e : errorLinks){
            links.remove(e);
        }
        System.out.println("Number link core after generate Error: " + links.size());
    }
    //24/11/2017
    public void AlphaBetaMultipath() {
        int numberacceptBestDemand = 0;
        if (demands.isEmpty()) {
            return;
        }
        yMin = findMinOfMinBandwidthVolumeFromDemands();
        alpha = yMin;
        yMax = Fmax / demands.size();
        System.out.println("YMin:" + yMin);
        System.out.println("YMax:" + yMax);
        //khoi tao Fbest
        _AlphaBetaMultipath();
        Fbest = Fcurrent;
        acceptedDemandBest = acceptedDemandCurrent;
        updateBetterSolution();
        numberacceptBestDemand++;
        System.out.println("Update Best Demand time : " + numberacceptBestDemand);
        while (yMin<=yMax) { //(yMin<=yMax){
            resetTopology();
            _AlphaBetaMultipath();
           System.out.println("acceptedDemandBest:" + acceptedDemandBest);
           System.out.println("acceptedDemandCurrent:" + acceptedDemandCurrent);
           System.out.println("Fbest 1:" + Fbest);
            System.out.println("Fcurrent 1:" + Fcurrent);
            System.out.println("YMin 1:" + yMin);
            System.out.println("YMax 1:" + yMax);
            if (Fbest <= Fcurrent && acceptedDemandBest<=acceptedDemandCurrent) {
                numberacceptBestDemand++;
                System.out.println("Update Best Demand time : " + numberacceptBestDemand);
                Fbest = Fcurrent;
                acceptedDemandBest = acceptedDemandCurrent;
                //luu lai loi giai
                updateBetterSolution();
                updateForSatisfiedDemands();
            } else {
                updateForUnsatisfiedDemands();
            }
            System.out.println("Fbest:" + Fbest);
            System.out.println("Fcurrent:" + Fcurrent);
            System.out.println("YMin:" + yMin);
            System.out.println("YMax:" + yMax);
        }
        acceptBestDemandForTopology();
        
    }

    //24/11/2017
    public void _AlphaBetaMultipath() {
        demands = sortDemandsDecreaseAccordingToVolume(demands);
        System.out.println("Aftersort demand");
        printYd();
        for (Demand d : demands) {
            //pathrate and Node
            PathRateAndNodeEachDemand(d);
        }
        Fcurrent = calculateObjectiveF();
        acceptedDemandCurrent = calNumberScheduledDemands();
    }

    //24/11/2017
    public void updateBetterSolution() {
        bestDemands.clear();
        for (Demand d : demands) {
            Demand bd = d.clone();
            bestDemands.add(bd);
        }
        bestMaxUtilizationLink = calculateMaxUtilizationLink();
        bestMaxUtilizationNode = calculateMaxUtilizationNode();
    }
    
    //26/11/2017
    public void acceptBestDemandForTopology(){
         resetTopology();
         demands.clear();
         System.out.println("Number of Demands after clear : " + demands.size());
         for (Demand d : bestDemands) {
            demands.add(d);
            d.updateResource();
        }
         printDemands();
         printBestDemands();
        bestMaxUtilizationLink = calculateMaxUtilizationLink();
        bestMaxUtilizationNode = calculateMaxUtilizationNode();
        Fcurrent = calculateObjectiveF();
    }
    //24/11/2017
    public void PathRateAndNodeEachDemand(Demand d) {
        //tim tat ca cac duong di ngan nhat
//        d = findMultiShortestPathFor(d);
        d = findMultiDisjointShortestPathFor(d);
        
        
        d.printAllShortestPaths();

        boolean mapSatisfiedPaths = false;
//        //lienntt : kiem tra mindemandVolume
//        if(d.getVolume() < d.getMinDemandVolume()){
//            d.isAccepted = false;
//            return;
//        }
        if (scheme ==1) {
            mapSatisfiedPaths = d.PathRateFit(maxNumberShortestPaths);
        } else {
            mapSatisfiedPaths = d.PathRatePro(maxNumberShortestPaths);
        }
        d.printPaths();
        //chay thu tuc GanNode
        if (mapSatisfiedPaths == true) {
            d.mapNodesForAllPaths();
        }

    }

    // min(ymin(yd))
    public double findMinOfMinBandwidthVolumeFromDemands() {
        double minmin = demands.get(0).getMinDemandVolume();
        for (Demand d : demands) {
            if (minmin > d.getMinDemandVolume()) {
                minmin = d.getMinDemandVolume();
            }
        }
        return minmin;
    }

    //min (bandwidth(yd))
    public double findMinOfBandwidthVolumeFromDemands() {
        double min = demands.get(0).getVolume();
        for (Demand d : demands) {
            if (min > d.getVolume()) {
                min = d.getVolume();
            }
        }
        return min;
    }

     //max(bandwidth(yd))
    public double findMaxOfBandwidthVolumeFromDemands() {
        double min = demands.get(0).getVolume();
        for (Demand d : demands) {
            if (min < d.getVolume()) {
                min = d.getVolume();
            }
        }
        return min;
    }

    //min (bandwidth(ydbest))
    public double findMinOfBandwidthVolumeFromBestDemands() {
        if(bestDemands.isEmpty()){
            System.err.println("No Solution");
            return 0;
        }
        double min = bestDemands.get(0).getVolume();
        for (Demand d : bestDemands) {
            if (min > d.getVolume()) {
                min = d.getVolume();
            }
        }
        return min;
    }

    public boolean isSatisfiedDemands() {
        for (Demand d : demands) {
            if (d.isSatisfied() == false) {
                return false;
            }
        }
        return true;
    }
    
     //tat cac cac demand deu thoa man
    public void updateForSatisfiedDemands() {
        //can duoi  = min(bandwidth(yd))
        yMin = findMinOfBandwidthVolumeFromDemands()+1;
        for (Demand d : demands) {
            //bandwidth(yd) = min(max(ydmin, (can tren + can duoi)/2),ydmax)
//            d.setDemandVolume(Math.min(d.getMinDemandVolume(), Math.ceil((yMax + yMin) / 2)));
            d.setDemandVolume(Math.min(Math.max(d.getMinDemandVolume(), Math.ceil((yMax + yMin) / 2)), d.getMaxDemandVolume()));
        }
        System.out.println("UpdateFor Satisfied" );
        printYd();
    }


    //khong thoa man het cac demand
    public void updateForUnsatisfiedDemands() {
        // can tren = max(bandwidth(yd))
//        yMax = findMinOfBandwidthVolumeFromDemands()-1;
        yMax = findMaxOfBandwidthVolumeFromDemands()-1;
        
        for (Demand d : demands) {
            //bandwidth(yd) = max(min(ymax, (can tren + can duoi)/2),ymin)
            d.setDemandVolume(Math.max(Math.min(d.getMaxDemandVolume(), Math.floor((yMax + yMin) / 2)),d.getMinDemandVolume()));
        }
        System.out.println("UpdateFor Unsatisfied" );
        printYd();

    }
    
    public void printYd(){
        System.out.println("Ymax: " + yMax);
        System.out.println("Ymin: " + yMin);
        System.out.println("Ymin: " + (yMax+yMin)/2);
        int i=1;
        for (Demand d : demands) {
             System.out.println("d: " + i + ": " + d.getVolume() + " : " + d.getMinDemandVolume() + " -> " + d.getMaxDemandVolume());
             i++;
        }
    }

   
    //GanNode()
    public void mapNodeForAllPathsOfDemand(Demand d) {
        for (Path p : d.paths) {
            p.mappingFunctionNode();
        }
    }

    public double calculateObjectiveF() {
        double F = calculateMinDemandVolume() - alpha * calculateMaxUtilizationLink()
                - beta * calculateMaxUtilizationNode();
        return F;
    }

    public double calculateMinDemandVolume() {
        return findMinOfBandwidthVolumeFromDemands();
    }

//    // calcaulate multishortestPaths for a demand d
//    public Demand calculateMultiShortestPaths(Demand d) {
//
//        return d;
//    }

    public void resetOutPathForLink() {
        for (Node v : nodes) {
            v.getOutPaths().clear();
        }
    }

    public void resetTopology() {
        for (Link e : links) {
            e.reset();
        }
        for (Node v : nodes) {
            v.reset();
        }
        for (Demand d : demands) {
            d.reset();
        }
        acceptedDemand = 0;

    }

    public Demand findMultiDisjointShortestPathFor(Demand d){
        d.getAllDisjointShortestPaths(maxNumberShortestPaths,disjointScheme);
        System.out.println("Number of DisjointShortestPaths : " + d.allDisjointShortestPaths.size());
        return d;
    }
     //find all paths for demand

    public Demand findMultiShortestPathFor(Demand d) {
        d.getAllShortestPaths();
        return d;
    }
    
    
    public void getAllPathsForAllDemands(String pathfile) {

        try {
            File f = new File(pathfile);
            Scanner reader = new Scanner(f);
            //lay so demand
            int dnum = Integer.parseInt(reader.nextLine());
//            if(dnum!= demands.size()){
//                System.err.print("Demand number not match");
//                System.exit(-1);
//            }
            for (Demand d : demands) {
                int ds = reader.nextInt();
                int dd = reader.nextInt();
                if (d.srcNode.getId() != ds || d.destNode.getId() != dd) {
                    System.err.print("Demand not match");
                    System.exit(-1);
                }
                reader.nextLine();
                int pnum = Integer.parseInt(reader.nextLine());
                for (int p = 0; p < pnum; p++) {
                    Path newpath = new Path();
                    int nnum = reader.nextInt();
                    int vid;
                    for (int v = 0; v < nnum; v++) {
                        vid = reader.nextInt();
                        newpath.nodes.add(getNodeById(vid));
                        if (v == 0) {
                            continue;
                        }
                        Link e = getLinkBySrcDestId(newpath.nodes.get(v - 1).getId(), vid);
                        if (e == null) {
                            System.err.print("Link not found");
                            System.exit(-1);
                        }
                        newpath.links.add(e);
                    }
                    d.allPaths.add(newpath);
                    reader.nextLine();

                }
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
     public void findAllPathsForAllDemands() {
        for (Demand d : demands) {
            d.allPaths = findAllPathsFromSrcToNode(d.getSrcNode(), d.getDestNode());
            System.err.println("Found :" + d.allPaths.size() +  " paths");
        }
    }
    //duyet tat ca cac duong di tu nguon den dich cua demand d bang BFS
    public ArrayList<Path> findAllPathsFromSrcToNode(Node srcNode, Node destNode) {
        ArrayList<Path> paths = new ArrayList<Path>();
        System.out.println(" begin find All paths for demand d");
        //tập các đỉnh chờ xét
        Node src = destNode;
        Node dest = srcNode;
        ArrayList<Node> nextNodes = new ArrayList<Node>();
        ArrayList<Node> visitedNodes = new ArrayList<Node>();
        nextNodes.add(src);
        Node v;
        while (!nextNodes.isEmpty()) {
            v = nextNodes.get(0);
            nextNodes.remove(0);
            if (v == dest || visitedNodes.contains(v)) {
                continue;
            }
            for (Link e : v.inLinks) {
                if (!links.contains(e)) {
                    break;
                }
                if (!e.getSrcNode().outPaths.contains(e) && e.getSrcNode() != src) {
                    e.getSrcNode().outPaths.add(e);
                }
                if (!visitedNodes.contains(e.getSrcNode())) {//) && e.overload == false) {
                    nextNodes.add(e.getSrcNode());

                }
            }

            visitedNodes.add(v);
        }
//        System.out.println(" phase1 find All paths for demand d");
        ArrayList<Node> nextNode = new ArrayList<Node>();
        nextNode.add(srcNode);
        ArrayList<Path> tempPath = new ArrayList<Path>();

        //thiết lập đường đi dầu tiên từ src node
        Path path = new Path();
        path.getNodes().add(srcNode);
        //thêm đường đi đầu tiên vào tập các đường đi
        paths.add(path);
        //clone một tập các đường đi trung gian
        Path newp = new Path(path);
        tempPath.add(newp);
        ArrayList<Node> passedNodes = new ArrayList<Node>();
        int numberPathFound = 0;
        while (!nextNode.isEmpty()) {
            Node src2 = nextNode.get(0);
            nextNode.remove(0);
//            passedNodes.add(src2);
            if (foundAllPaths(paths, destNode)) {
                break;
            }
            if (src2 == destNode || src2.getOutPaths().size() < 1) {
                continue;
            }

            //duyet tat ca cac path cua d
            for (Path p : paths) {
                //neu p da den dest hoặc khong di qua src thi bỏ qua, hoặc nút cuối của path không phải là src thì bỏ qua
                if (p.getNodes().contains(destNode) || !p.getNodes().contains(src2) || p.getNodes().get(p.getNodes().size() - 1) != src2) {
                    if (p.getNodes().contains(destNode)) {
                        numberPathFound++;
                    }
                    continue;
                }
                if (numberPathFound >= 100) {//|| ((p.getLinks().size() >=(links.size()/5)) && ((links.size()/5) >= 10))) {
                    tempPath = removePath(tempPath, p);
                    continue;
                }

//                int numberp = d.getPaths().size();
                //nut cuối của path là src2
                if (p.getNodes().get(p.getNodes().size() - 1) == src2) {// p.getNodes().contains(src2)) {
                    boolean addnewpath = false;
                    //xet tung luong ra e
                    for (Link e : src2.getOutPaths()) {
//                        if (p.hasNode(e.getDestNode()) == 0) {
                        //nếu nút đích của link e không nằm trền path = cả link e không nằm trên path
                        if ((!p.getNodes().contains(e.getDestNode()))) {
                            //tao mot path moi  = path cu + link e
                            Path newpe = new Path(p);
                            newpe.getLinks().add(e);
                            newpe.getNodes().add(e.getDestNode());
                            addnewpath = true;
                            // them path moi vao
                            tempPath.add(newpe);
                            // nếu những nút đã duyệt không có nút đích của e = nút đích của e chưa duyệt
//                            if (!p.getNodes().contains(e.getDestNode()) ){//&& 
//                            if(!nextNode.contains(e.getDestNode()) && !passedNodes.contains(e.getDestNode())) {
                            if (e.getDestNode() != destNode && !nextNode.contains(e.getDestNode())) {
                                nextNode.add(e.getDestNode());
                            }
                        }
                    }
                    //loai bo path p cu
                    if (addnewpath) //                        
                    {
                        tempPath = removePath(tempPath, p);
                    }
                }
                passedNodes.add(src2);

            }
//            System.out.print("Netxt node : " + src2.id + ": ");
//            for (Node vi : nextNode) {
//                System.out.print("  " + vi.getId());
//            }
//            System.out.println();
            passedNodes.add(src2);
            paths.clear();
            paths = clonePaths(tempPath);

//            int i = 0;
//            for (Path p : paths) {
//                i++;
//                System.out.println("path " + i);
//                for (Node vi : p.getNodes()) {
//                    System.out.print("  " + vi.getId());
//                }
//                System.out.println();
//            }
        }

        for (Path p : paths) {
            if (!p.getNodes().contains(destNode)) {
                tempPath = removePath(tempPath, p);
            }
        }
        paths.clear();
        paths = clonePaths(tempPath);

//        int i = 0;
//        for (Path p : paths) {
//            i++;
//            System.out.println("path " + i);
//            for (Node vi : p.getNodes()) {
//                System.out.print("  " + vi.getId());
//            }
//            System.out.println();
//        }
//
        System.out.println(" end find All paths for demand d");
        return paths;
    }

    public double randomNumber() {
        Random rn = new Random();
        int random = rn.nextInt(2);
        if (random > 0) {
            return 1;
        }
        return -1;

    }

    //random from 0 to max
    public int randomNumber(int max) {
        Random rn = new Random();
        return rn.nextInt(max);
    }

    //random double from min to max
    public double randomNumber(double rangeMin, double rangeMax) {
        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    public double calculateTotalComputingUtilizationOnNode(Node v) {
        return v.getUsedComputingCapacity() / v.getComputingCapacity();
    }

    public double calculateMaxUtilizationNode() {
        double maxU = 0;
        double u = 0;
        for (Node v : nodes) {
            u = calculateTotalComputingUtilizationOnNode(v);
            if (u > maxU) {
                maxU = u;
            }
        }
        return maxU;
    }

    // calculate ye sum of traffic rate of all data through link e
    public double calculateTotalTrafficOnLink(Link e) {
        return e.usedCapacity;// + getReverseLink(e).usedCapacity;
    }
    //calculate r

    public double calculateUtilizationLink(Link e) {
        return calculateTotalTrafficOnLink(e) / e.getBandwidthCapacity();
    }
    //calcualte max r

    public double calculateMaxUtilizationLink() {

        double r = 0;
        double u = 0;

        for (Link e : links) {
            u = calculateUtilizationLink(e);
            if (r < u) {
                r = u;
            }
        }

        return r;
    }

    //kiem tra co link overload
    public boolean hasOverLoadLinks() {
        for (Link e : links) {
            if (e.isOverLoad()) {
                return true;
            }
        }
        return false;
    }

    //kiem tra demand d co thoa man dieu kien link
    public boolean checkOverLoadLinks(Demand d) {
        for (Link e : links) {
            double used = e.usedCapacity;
            used += getReverseLink(e).usedCapacity;
            for (Path p : d.getPaths()) {
                if (p.getLinks().contains(e) || p.getLinks().contains(getReverseLink(e))) {
                    used += p.flowRate;
                }
            }
            if (e.bandwidthCapacity < used) {
                return true;
            }
        }
        return false;
    }

    public boolean hasOverLoadNodes() {

        for (Node v : nodes) {
            if (v.isOverLoad()) {
                return true;
            }
        }
        return false;
    }

    public void checkOverLoadForAllLinks() {
        for (Link e : links) {
            if (e.isOverLoad()) {
                e.overload = true;
            }
        }
    }

    public Link getReverseLink(Link e) {
        for (Link re : links) {
            if (re.getSrcNode() == e.getDestNode() && re.getDestNode() == e.getSrcNode()) {
                return re;
            }
        }
        return null;
    }

    public int numberScheduledDemands() {
        int satifyDemand = 0;
        for (Demand d : demands) {
            if (d.isAccepted) {
                satifyDemand++;
            }
        }
        return satifyDemand;
    }

    public double calSatisfiedDemandsVolume() {
        double total = 0;
        for (Demand d : bestDemands) {
            if (d.isAccepted) {
                total += d.getVolume();
            }
        }
        return total;
    }

    public double calTotalDemandsVolume() {
        double total = 0;
        for (Demand d : demands) {
            total += d.demandVolume;

        }
        return total;
    }

    //calculate R(v,f): total traffic of all data flows through node v and require f
    public void printBestUtilizationLink() {

        System.out.println("Best Utilization Link: " + bestMaxUtilizationLink);
        System.out.println("Best Utilization Node: " + bestMaxUtilizationNode);
        System.out.println("Total satisfied demandVolume " + calSatisfiedDemandsVolume() );
        System.out.println("Accepted Demand: " + numberScheduledDemands() + "/" + acceptedDemandBest);
        System.out.println("Total traffic link: ");
        for (Link e : links) {
            System.out.println(e.getSrcNode().getId() + " -> " + e.getDestNode().getId() + " : " + calculateTotalTrafficOnLink(e) + " ~ " + calculateUtilizationLink(e));
        }
    }

    public void printBestUtilizationNode() {
        System.out.println("Best Utilization node: " + bestMaxUtilizationNode);
        printTotalUsedCapacityNodes();
        printUsedCapacityNodes();

    }

    public void printPathsForDemands() {
        System.out.println("Paths for Demand: ");
        int numDemand = 0;
        for (Demand d : demands) {
            numDemand++;
            System.out.println("Demand: " + numDemand);
            d.printPaths();
        }
    }

    public void printAllShortestPathsForDemands() {
        System.out.println("All Shortest Paths for Demand: ");
        int numDemand = 0;
        for (Demand d : demands) {
            numDemand++;
            System.out.println("Demand: " + numDemand);
            d.printAllShortestPaths();
        }
    }

    public void printPathsForBestDemands() {

         System.out.println("Paths for Best Demand: ");
        int numDemand = 0;
        for (Demand d : bestDemands) {
            numDemand++;
            System.out.println("Demand: " + numDemand);
            d.printPaths();
        }
    }

    public void printTopology() {
        System.out.println("source node set number: " + srcNodes.size());
        for (Node node : srcNodes) {
            System.out.print("node index: " + node.getId() + " cC: " + node.getComputingCapacity());
            node.printInLinks();
            node.printOutLinks();
            System.out.println();
        };
        System.out.println("dest node set number: " + destNodes.size());
        for (Node node : destNodes) {
            System.out.print("node index: " + node.getId() + " cC: " + node.getComputingCapacity());
            node.printInLinks();
            node.printOutLinks();
            System.out.println();
        };

        System.out.println("core node number: " + nodes.size());
        for (Node node : nodes) {
            System.out.print("node index: " + node.getId() + " cC: " + node.getComputingCapacity());
            node.printInLinks();
            node.printOutLinks();
            System.out.println();
        };
        System.out.println();
        System.out.println("link number: " + links.size());
        for (Link link : links) {
            System.out.println(link.getSrcNode().getId() + " -> " + link.getDestNode().getId() + " : " + link.getBandwidthCapacity());
        };
        System.out.println();
        System.out.println("function number: " + functions.size());
        for (Function func : functions) {
            System.out.print("function id: " + func.getId() + " require : " + func.getRequireResource());
            System.out.println();
        };
        System.out.println();

        printDemands();

    }

    public void printUsedCapacityNodes() {
        System.out.println("Used Capacity: ");
        for (Node v : nodes) {
            System.out.println("node: " + v.getId() + " : " + v.getUsedComputingCapacity() + " /" + v.getComputingCapacity() + " : " + calculateTotalComputingUtilizationOnNode(v));

        }

    }

    public void printTotalUsedCapacityNodes() {
        double total = 0;
        for (Node v : nodes) {
            total += v.getUsedComputingCapacity();

        }
        System.out.println("Total Capacity: " + total);
    }

    public void printTotalBandwidth() {
        double total = 0;
        for (Link e : links) {
            total += e.bandwidthCapacity;

        }
        System.out.println("total bandwidth: " + total);

    }

    public void printTotalDemandVolume() {

        System.out.println("total demands volume: " + calTotalDemandsVolume());

    }

    public void printDemands() {
        System.out.println("demand number: " + demands.size());
        for (Demand demand : demands) {
            System.out.print(demand.getSrcNode().getId() + " -> " + demand.getDestNode().getId() + " : " + demand.getVolume() + " : " + demand.getMinDemandVolume()+ "-> " + demand.getMaxDemandVolume());
            System.out.println("accepted: " + demand.isAccepted);
            System.out.print(" | fcs: " + demand.getFunctions().size() + " | : ");
            for (Function func : demand.getFunctions()) {
                System.out.print(" " + func.getId());
            }
            System.out.println();
        };
    }

    public void printMinDemandsVolume() {
        System.out.println("Min demand volume : " + findMinOfBandwidthVolumeFromBestDemands());
    }

    public void printBestDemands() {
        System.out.println("Objective Fbest: " + Fbest);
        System.out.println("accepted best demand number: " + acceptedDemandBest);
        printTotalDemandVolume();
        System.out.println("Total satisfied demandVolume " + calSatisfiedDemandsVolume());
        printMinDemandsVolume();
        for (Demand demand : bestDemands) {
            System.out.print(demand.getSrcNode().getId() + " -> " + demand.getDestNode().getId() + " : maxvolume " + demand.getVolume() + " : unscheduled " + demand.getUnscheduledDemandVolume());
             System.out.println("accepted: " + demand.isAccepted);
            System.out.print(" | fcs: " + demand.getFunctions().size() + " | : ");
            for (Function func : demand.getFunctions()) {
                System.out.print(" " + func.getId());
            }
            System.out.println();
        };
    }

    public Node getNodeById(int nodeId) {
        for (Node v : srcNodes) {
            if (v.getId() == nodeId) {
                return v;
            }
        }
        for (Node v : nodes) {
            if (v.getId() == nodeId) {
                return v;
            }
        }
        for (Node v : destNodes) {
            if (v.getId() == nodeId) {
                return v;
            }
        }
        if (bigSrcNode.getId() == nodeId) {
            return bigSrcNode;
        }
        if (bigDestNode.getId() == nodeId) {
            return bigDestNode;
        }
        return null;
    }
    
     public Link getLinkBySrcDestId(int sid, int did) {
        for (Link e : links) {
            if (e.getSrcNode().getId() == sid && e.getDestNode().getId() == did) {
                return e;
            }
        }
        return null;
    }

    public int calNumberScheduledDemands() {
        int satifyDemand = 0;
        for (Demand d : demands) {
            if (d.isSatisfied()) {
                satifyDemand++;
            }
        }
        return satifyDemand;
    }
}
