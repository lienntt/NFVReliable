/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.util.ArrayList;

/**
 *
 * @author liem
 */
public class Demand {

    Node srcNode;
    Node destNode;
    boolean isAccepted = false;
    boolean finishFindAllPaths = false;
    ArrayList<Function> functions;
    ArrayList<Path> paths;
    ArrayList<Path> allShortestPaths;
    ArrayList<Path> allDisjointShortestPaths;
    ArrayList<Path> allPaths;
    Node srcInSubTopology;
    Node destInSubTopology;
    //max demand Volum for Varied Demand Traffic
    double maxDemandVolume;
    double minDemandVolume;
    double originalDemandVolume; //demand Volume gốc
    double demandVolume; // demand volume sau khi thay doi
    double scheduledDemandVolume;

    public Demand(Node src, Node dest, double volume, ArrayList<Function> fs) {
        srcNode = src;
        destNode = dest;
        demandVolume = volume;
        minDemandVolume = volume;
        maxDemandVolume = volume;
        originalDemandVolume = volume;
        scheduledDemandVolume = 0;
        functions = fs;
        paths = new ArrayList<Path>();
        allPaths = new ArrayList<Path>();
        allShortestPaths = new ArrayList<Path>();
        allDisjointShortestPaths = new ArrayList<Path>();
    }

    public Demand(Node src, Node dest, double minVolume, double maxVolume, ArrayList<Function> fs) {
        srcNode = src;
        destNode = dest;
        demandVolume = minVolume;
        minDemandVolume = minVolume;
        maxDemandVolume = maxVolume;
        originalDemandVolume = minVolume;
        scheduledDemandVolume = 0;
        functions = fs;
        paths = new ArrayList<Path>();
        allPaths = new ArrayList<Path>();
        allShortestPaths = new ArrayList<Path>();
        allDisjointShortestPaths = new ArrayList<Path>();
    }

    public Demand clone() {
        Demand d = new Demand(this.srcNode, this.destNode, this.minDemandVolume, this.maxDemandVolume, this.functions);
        d.demandVolume = this.demandVolume;
        d.functions = this.functions;
        d.isAccepted = this.isAccepted;
//        d.allShortestPaths = this.allShortestPaths;
        for (Path p : allShortestPaths) {
            d.allShortestPaths.add(new Path(p));
        }
        for (Path p : paths) {
            d.paths.add(new Path(p));
        }
//        for (Path p : allPaths) {
//            d.allPaths.add(p);
//        }
        d.scheduledDemandVolume = this.scheduledDemandVolume;
        return d;
    }

    public int hasPath(Path p) {
        if (!paths.isEmpty() && paths.contains(p)) {
            return 1;
        }

        return 0;

    }

    public void reset() {
        resetPaths();
        scheduledDemandVolume = 0;
//        resetFunctionPaths();
        isAccepted = false;
//        finishFindAllPaths = false;
    }

    public void resetPaths() {
        paths.clear();
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    public ArrayList<Path> getAllPaths() {
        return allPaths;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public void addPath(Path p) {
        paths.add(p);
    }

    public void addToShortestPath(Path p) {
        allShortestPaths.add(p);
    }

    public void removeFromShortestPath(Path p) {
        allShortestPaths.remove(p);
    }

    public void setPaths(ArrayList<Path> ps) {
        paths = (ArrayList<Path>) ps.clone();
//        resetPaths();
//        for (Path p : ps) {
//            Path newp = new Path(p);
//            paths.add(newp);
//        }
    }

    public void setAllShortestPath(ArrayList<Path> ps) {
        allShortestPaths = (ArrayList<Path>) ps.clone();
    }

    public void removePath(Path p) {
        if (paths.contains(p)) {
            paths.remove(p);
        } else {
            for (Path pi : paths) {
                if (pi == p) {
                    paths.remove(p);
                }
            }
        }
    }

    public double getRequireResource(double trafficRate) {
        double requireResource = 0;
        for (Function f : functions) {
            requireResource += f.requiredResource * trafficRate;
        }
        return requireResource;
    }

    public double getTotalRequireResource() {
        return getRequireResource(demandVolume);
    }

    public int getNumPath() {
        return paths.size();
    }

    public int hasFunction(Function f) {
        if (functions.contains(f)) {
            return 1;
        }
        return 0;
    }

    public void setDestNode(Node dist) {
        destNode = dist;
    }

    public void setSrcNode(Node src) {
        srcNode = src;

    }

    public Node getDestNode() {
        return destNode;
    }

    public Node getSrcNode() {
        return srcNode;
    }

    public double getVolume() {
        return demandVolume;
    }

    public double getMaxDemandVolume() {
        return maxDemandVolume;
    }

    public void setMaxDemandVolume(double max) {
        maxDemandVolume = max;
    }

    public double getMinDemandVolume() {
        return minDemandVolume;
    }

    public void setMinDemandVolume(double min) {
        minDemandVolume = min;
    }

    public double getCurrentDemandVolume() {
        double currentDVolume = 0;
        for (Path p : paths) {
            currentDVolume += p.getFlowRate();
        }
        return currentDVolume;
    }

    public boolean checkDisjoint(Path p1, Path p2) {
        if (p1.getNodes().isEmpty() || p1.getNodes().isEmpty()) {
            System.err.println("Error: path has no node");
            return false;
        }
        if (p1.getNodes().size() <= 2 || p1.getNodes().size() <= 2) {
            System.err.println("Error: number link <2 ");
            return false;
        }
        if (p1.getNodes().get(0) != p2.getNodes().get(0)
                || p1.getNodes().get(p1.getNodes().size() - 1) != p2.getNodes().get(p2.getNodes().size() - 1)) {
            System.err.println("Error 2 paths not has same source-dest");
            return false;
        }
        Node v;
        for (int i = 1; i < p1.getNodes().size() - 1; i++) {
            v = p1.getNodes().get(i);
            //if these path use a same node => not disjoint
            if (p2.getNodes().contains(v)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Path> findTwoDisjointPaths() {
        ArrayList<Path> disjointPaths = new ArrayList<Path>();

        if (allShortestPaths.size() < 2) {
            return disjointPaths;
        }

        Path p1, p2;
        for (int i = 0; i < allShortestPaths.size(); i++) {
            p1 = allShortestPaths.get(i);
            for (int j = i + 1; j < allShortestPaths.size(); j++) {
                p2 = allShortestPaths.get(j);
                if (checkDisjoint(p1, p2)) {
                    disjointPaths.add(p1);
                    disjointPaths.add(p2);
                    return disjointPaths;
                }
            }
        }
        return disjointPaths;
    }

     public void getAllDisjointShortestPaths(int maxp, int disjointScheme) {
         allDisjointShortestPaths.clear();
         for(Path p: _getAllDisjointShortestPaths(maxp, disjointScheme)){
             allDisjointShortestPaths.add(new Path (p));
         }
//         allDisjointShortestPaths = _getAllDisjointShortestPaths(maxp);
     }
    //find a set of maxpath disjoint shortest paths
    public ArrayList<Path> _getAllDisjointShortestPaths(int maxp, int disjointScheme) {

        getAllShortestPaths();
        
        //tinh path capacity cho 
        calculateMinCapacityForAllPaths();

        if (maxp <= 1 || allShortestPaths.size() <= 1) {
            return allShortestPaths;
        }

        //if there is more than 1 paths
        //find a set of maxp that satisfy disjoint node
        //so that total bandwidth of the set of paths is the largest
        //step1: sap xep cac path theo min capacity giam dan
        sortPathsDecreaseAccordingToMinCapacity();
        
        //stetp2: lay ra 2 duong disjoint
        ArrayList<Path> disjointPaths = findTwoDisjointPaths();
        
        System.out.println("result of FindTwoDisjointPaths " + disjointPaths.size());

        //if there is no disjointPaths
        //return a path that has max of min capacity
        if (disjointPaths.isEmpty()) {
            System.out.println("Pahtvolume: " + allShortestPaths.get(0).getMinAvailableCapacity() + "; "+ allShortestPaths.get(1).getMinAvailableCapacity() );
            disjointPaths.add(allShortestPaths.get(0));
            allShortestPaths = disjointPaths;
            return allShortestPaths;
        }
        //if there are some disjointPaths
        //step3: lay them cac duong co capacity lon nhat cho du maxp
        int maxPaths;
        //if disjointScheme =1: disjoint incompletely
        if(disjointScheme == 1){
            maxPaths = Math.min(maxp, allShortestPaths.size()) - 2;
            for (Path p : allShortestPaths) {
                if (maxPaths > 0 && !disjointPaths.contains(p)) {
                    disjointPaths.add(p);
                    maxPaths -= 1;
                }
            }
            System.out.println("disjoint incomplete numpaths: " + disjointPaths.size());
            printPathList(disjointPaths);
            return disjointPaths;
        }
        
        //else disjoint completely
        //lay them cac duong disjoint cho du so duong
        maxPaths = Math.min(maxp, allShortestPaths.size()) - 2;
        boolean foundNewPath;
        for(Path p: allShortestPaths){
            foundNewPath = true;
            if(maxPaths > 0 && !disjointPaths.contains(p)){
                for(Path pi: disjointPaths){
                    if(!checkDisjoint(p, pi)){
                        foundNewPath = false;
                    }
                }
                if(foundNewPath){
                    disjointPaths.add(p);
                    maxPaths -= 1;
                }
            }
        }
        System.out.println("disjointcomplete numpaths: " + disjointPaths.size());
        printPathList(disjointPaths);
        return disjointPaths;
    }

    public ArrayList<Path> getAllShortestPaths() {
        if (allShortestPaths.size() > 0) {
            return allShortestPaths;
        }

        ArrayList<Path> tempPaths = new ArrayList<Path>();
        ArrayList<Path> shortestPaths = new ArrayList<Path>();
        tempPaths = getAllPaths();
        if (tempPaths.size() < 1) {
            allShortestPaths = shortestPaths;
            return allShortestPaths;
        }
        double minWeight = tempPaths.get(0).getWeight();
        for (Path p : tempPaths) {
            if (p.getWeight() < minWeight) {
                minWeight = p.getWeight();
            }
        }
        for (Path p : tempPaths) {
            if (p.getWeight() == minWeight) {
                p.demand = this;
                shortestPaths.add(p);
            }
        }

        allShortestPaths = shortestPaths;
        return allShortestPaths;
    }

    public double getSuccessCurrentDemandVolume() {
        double currentDVolume = 0;
        for (Path p : paths) {
            if (!p.hasOverLoad()) {
                currentDVolume += p.getFlowRate();
            }
        }
        return currentDVolume;
    }

    public void printPathList(ArrayList<Path> pathlist) {
        int numPath = 0;
        System.out.println("number of path: " + pathlist.size());
        for (Path p : pathlist) {
            numPath++;
            System.out.println("pathid: " + numPath);
            System.out.println("pathweight: " + p.getWeight());
            System.out.println("path flow rate: " + p.getFlowRate());
            System.out.println("link number: " + p.getLinks().size());
            System.out.println("node number: " + p.getNodes().size());
            for (Node v : p.getNodes()) {
                System.out.print(" " + v.getId());
            }
            System.out.println();
            System.out.print(" weight link: ");
            for (Link e : p.links) {
                System.out.print(e.getWeight());
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printPaths() {
        int numPath = 0;
        System.out.println("All path");
        System.out.println("path number: " + paths.size());
        for (Path p : paths) {
            numPath++;
            System.out.println("pathid: " + numPath);
            System.out.println("mincapacity: " + p.getMinAvailableCapacity());
            System.out.println("pathweight: " + p.getWeight());
            System.out.println("path flow rate: " + p.getFlowRate());
            System.out.println("link number: " + p.getLinks().size());
            System.out.println("node number: " + p.getNodes().size());
            for (Node v : p.getNodes()) {
                System.out.print(" " + v.getId() + " fs: ");
                if (p.mapped) {
                    for (Function f : p.mappingFunctionNode.get(v)) {
                        System.out.print(" " + f.getId());
                    }
                    System.out.println();
                }
            }
            System.out.println();
            System.out.print(" weight link: ");
            for (Link e : p.links) {
                System.out.print(e.getWeight());
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printAllShortestPaths() {
        int numPath = 0;
        System.out.println("All shortest path");
        System.out.println("path number: " + allShortestPaths.size());
        for (Path p : allShortestPaths) {
            numPath++;
            System.out.println("pathid: " + numPath);
            System.out.println("mincapacity: " + p.getMinAvailableCapacity());
            System.out.println("pathweight: " + p.getWeight());
            System.out.println("path flow rate: " + p.getFlowRate());
            System.out.println("link number: " + p.getLinks().size());
            System.out.println("node number: " + p.getNodes().size());
            for (Node v : p.getNodes()) {
                System.out.print(" " + v.getId());
            }
            System.out.println();
            System.out.print(" weight link: ");
            for (Link e : p.links) {
                System.out.print(e.getWeight());
            }
            System.out.println();
        }
        System.out.println();
    }

    //check path for all functions
    public boolean checkPathForFunctions(Path p) {
        int numberFunctionSupported = 0;
        for (Function f : getFunctions()) {
            for (Node v : p.getNodes()) {
                if ((v.getComputingCapacity() - v.getUsedComputingCapacity()) > 0) {
                    numberFunctionSupported++;
                    break;
                }
//                if (v.compUsedForFunction.containsKey(f)) {
////                    if (v.hasFunction(f) && (v.getComputingCapacity() - v.compUsedForFunction.get(f)) > 0) {
//                      if (v.hasFunction(f) && (v.getComputingCapacity() - v.getUsedComputingCapacity()) > 0) {
//                        numberFunctionSupported++;
//                        break;
//                    }
//                } else {
//                    if (v.hasFunction(f)) {
//                        numberFunctionSupported++;
//                        break;
//                    }
//                }
            }
        }

        return (numberFunctionSupported == getFunctions().size());
    }
    //check computing capacity for all node in path, function and flowrate of path.

    public boolean checkComputingCapacity(Path p) {
//        Hashtable<Function, Double> availableComputingCapacity = new Hashtable<Function, Double>();
        double availableComputingCapacity = 0.0;
//        for (Function f : functions) {
//            availableComputingCapacity.put(f, 0.0);
//        }
        // tìm khả năng cung cấp các chức năng lớn nhất có thể trên path đối với từng chức năng
//        for (Node v : p.getNodes()) {
//            for (Function f : functions) {
//                if (v.hasFunction(f) && (v.computingCapacity - v.getUsedComputingCapacity()) > availableComputingCapacity) {
//                    availableComputingCapacity = v.computingCapacity - v.getUsedComputingCapacity();
//                }
//            }
//        }
//        //nếu có 1 chức năng nào k được thực hiện đầy đủ thì trả về false
//        for (Function f : functions) {
//            if (p.flowRate * f.requiredResource > availableComputingCapacity) {
//                return false;
//            }
//        }
//         for (Node v : p.getNodes()) {
//            for (Function f : functions) {
//                if (v.hasFunction(f) && (v.computingCapacity - v.getUsedComputingCapacity()) < p.flowRate * f.requiredResource) {
//                    return false;
//                }
//            }
//        }

//                 tìm khả năng cung cấp các chức năng lớn nhất có thể trên path đối với từng chức năng
//        for (Node v : p.getNodes()) {
//            for (Function f : functions) {
//                if (v.hasFunction(f) && (v.computingCapacity - v.getUsedComputingCapacity()) > availableComputingCapacity) {
//                    availableComputingCapacity = v.computingCapacity - v.getUsedComputingCapacity();
//                }
//            }
//        }
        //nếu có 1 chức năng nào k được thực hiện đầy đủ thì trả về false
        for (Function f : functions) {
            for (Node v : p.getNodes()) {
                //neu v chua chức năng f và computingcapacity còn lại > availael thì cập nhật lại avalilable
                if ((v.computingCapacity - v.getUsedComputingCapacity()) > availableComputingCapacity) {
                    availableComputingCapacity = v.computingCapacity - v.getUsedComputingCapacity();
                }
            }
            //nếu có chức năng nào k được thỏa mãn thì trả về false
            if (p.flowRate * f.requiredResource > availableComputingCapacity) {
                return false;
            }
        }

        return true;
    }

//    public boolean isAccepted() {
//        if (paths.size() < 1) {
//            return false;
//        }
//        for (Path p : getPaths()) {
//            if (!p.isSatifyAllRequirements() || !p.mappingFunctionNode()) {
//                return false;
//            }
//        }
//        isAccepted = true;
//        return true;
//    }
    public boolean isSatisfied() {
        if (getUnscheduledDemandVolume() <= 0) {
            return true;
        }
        return false;
    }

    public boolean checkSatifyComputingCapacity() {
        for (Path p : getPaths()) {
            if (checkComputingCapacity(p) == false) {
                return false;
            }
        }
        return true;
    }

    //set outpath for links for all paths of demand 
    public void setOutPathForLink() {
        System.out.println("number path 3: " + getPaths().size());
        for (Path p : getPaths()) {
            p.setOutPathForLink();
        }
    }

    public boolean foundAllPaths() {
        if (finishFindAllPaths) {
            return true;
        }
        for (Path p : paths) {
            if (!p.getNodes().contains(destNode)) {
                return false;
            }
        }
        finishFindAllPaths = true;
        return true;
    }

    public void setDemandVolume(double dvolume) {
        demandVolume = dvolume;
    }

    public double getOriginalDemandVolume() {
        return originalDemandVolume;
    }

    public void setOriginalDemandVolume(double dvolume) {
        originalDemandVolume = dvolume;
    }

//    public void updateFlowRateForPaths() {
//        double variedVolume = demandVolume - originalDemandVolume;
//        double variedRate = variedVolume / originalDemandVolume;
//        for (Path p : paths) {
//            p.setPrevFlowRate();
//            p.setFlowRate(p.getPrevFlowRate() * (1 + variedRate));
//            p.updateUsedCapacity();
//        }
//    }
//    public void variedDemandVolumeForPaths() {
//        for (Path p : paths) {
//            if (p.hasOverLoad()) {
//                p.decreaseFlowRate();
//                continue;
//            }
//            if (this.getCurrentDemandVolume() < this.demandVolume) {
//                p.increaseFlowRate();
//            }
//        }
//    }
    protected ArrayList<Path> _sortPathsDecreaseAccordingToMinCapacity(ArrayList<Path> pathList) {
        ArrayList<Path> sorted = new ArrayList<Path>();
        for (Path p : pathList) {
            boolean added = false;
            for (int i = 0; i < sorted.size(); i++) {
                
                if (p.getMinAvailableCapacity() > sorted.get(i).getMinAvailableCapacity()) {
                    sorted.add(i, p);
                    added = true;
                    break;
                }
                
            }
            if (added == false) {
                sorted.add(sorted.size(), p);
            }
        }
        pathList.clear();
        return sorted;
    }
    

    public void mapNodesForAllPaths() {
        boolean mapSuccess = true;
        
        for (Path p : paths) {
            if(!p.mappingFunctionNode()){
                 mapSuccess = false;
                 break;
            }
               
        }
        //neu khong map duoc thanh cong voi tat ca cac paths
        //rollback lai nhung paths da duoc cap nhat tai nguyen
        if(!mapSuccess){
            this.isAccepted = false;
            this.scheduledDemandVolume = 0;
            for (Path p : paths) {
                p.rollbackResource();
                if(p.mapped)
                    p.rollbackComputingCapacityForNodes();
            }
            
        }
    }

    public void calculateMinCapacityForAllPaths() {
        for (Path p : allShortestPaths) {
            p.calculateMinAvailableCapacity();
        }
    }

    public void sortPathsDecreaseAccordingToMinCapacity() {
        allShortestPaths = _sortPathsDecreaseAccordingToMinCapacity(allShortestPaths);
    }
    
    public void sortDisjointPathsDecreaseAccordingToMinCapacity() {
        allDisjointShortestPaths = _sortPathsDecreaseAccordingToMinCapacity(allDisjointShortestPaths);
    }

    public double getUnscheduledDemandVolume() {
        return demandVolume - scheduledDemandVolume;
    }

    //scheme 1
    public boolean PathRateFit(int maxNumberShortestPaths) {
        paths.clear();
        scheduledDemandVolume=0;
        System.out.println("PathRateFit");
        System.out.println("demand volume unscheduled: " + getUnscheduledDemandVolume());
        sortDisjointPathsDecreaseAccordingToMinCapacity();
        // xem cos path nao cos cp>bandwidth(yd) gan cuoi danh sach thi gan cho d
        for (int i = allDisjointShortestPaths.size() - 1; i >= 0; i--) {
            Path p = allDisjointShortestPaths.get(i);
            double unscheduleVolume = getUnscheduledDemandVolume();
            if (p.getMinAvailableCapacity() >= unscheduleVolume) {
                p.setFlowRate(unscheduleVolume);
                scheduledDemandVolume += unscheduleVolume;
                p.updateBandwidthCapacityForLinks();
                paths.add(p);
                isAccepted = true;
                printPaths();
                return true;
            }
        }
        //khong co path nao cp>bandwidth(yd)
        //chon lan luot tu path cos cp lon nhat gan cho yd cho den het
        int numberPaths = Math.min(maxNumberShortestPaths, allDisjointShortestPaths.size());
        for (int i = 0; i < numberPaths; i++) {
            Path p = allDisjointShortestPaths.get(i);
            double mapVolume = Math.min(p.getMinAvailableCapacity(), getUnscheduledDemandVolume());
            p.setFlowRate(mapVolume);
            scheduledDemandVolume += mapVolume;
            p.updateBandwidthCapacityForLinks();
            paths.add(p);
            if (getUnscheduledDemandVolume() <= 0) {
                isAccepted = true;
                printPaths();
                return true;
            }
        }
        //neu khong du de gan cho demand thi reject demand
        if (getUnscheduledDemandVolume() > 0) {
            for (Path p : paths) {
                scheduledDemandVolume -= p.flowRate;
                p.rollbackResource();
            }
            paths.clear();
            isAccepted = false;
        }
        printPaths();
        return false;
    }

    //scheme 2
    public boolean PathRatePro(int maxNumberShortestPaths) {
        paths.clear();
        scheduledDemandVolume=0;
        sortDisjointPathsDecreaseAccordingToMinCapacity();
//         System.out.println("Number of shortestPaths : " + allDisjointShortestPaths.size());
//         for (int i = 0; i < allDisjointShortestPaths.size(); i++) {
//          System.out.println("Number of shortestPaths : " +allDisjointShortestPaths.get(i).getMinAvailableCapacity());
//        }
        int numberPaths = Math.min(maxNumberShortestPaths, allDisjointShortestPaths.size());
        double totalAvailable = 0;
        double unscheduledDemendVolume = getUnscheduledDemandVolume();
        int numberSmallestPaths = 0;

        for (int i = 0; i < numberPaths; i++) {
            Path p = allDisjointShortestPaths.get(i);
            totalAvailable += p.getMinAvailableCapacity();
            if(totalAvailable >= unscheduledDemendVolume)
                numberSmallestPaths = i+1;
        }
        System.out.println("PathRatePro");
        System.out.println("totalAvailable: " + totalAvailable);
        System.out.println("demand volume unscheduled: " + getUnscheduledDemandVolume());
        System.out.println("numberSmallestPaths " + numberSmallestPaths);
        System.out.println("numberPaths " + numberPaths);

        //k thoa man cung cap demand
        if (totalAvailable < unscheduledDemendVolume) {
//            printPaths();
            isAccepted = false;
            return false;
        }

        //thoa man cung cap demand
        //lay ra so duong it nhat ma tong bandwidth lon hon demand volume
        for (int i = 0; i < numberSmallestPaths; i++) {
            Path p = allDisjointShortestPaths.get(i);
            double mapVolume = p.getMinAvailableCapacity() * unscheduledDemendVolume / totalAvailable;
            
//            if (i == numberPaths - 1) {
//                mapVolume = unscheduledDemendVolume - _scheduleDemandVolume;
//            }
            //tim cach thoa man xp nguyen
            if(p.getMinAvailableCapacity() - Math.ceil(mapVolume) >=0 ){
                mapVolume = Math.ceil(mapVolume);
            }else{
                mapVolume = Math.floor(mapVolume);
            }
            mapVolume = Math.min(mapVolume,getUnscheduledDemandVolume());
            p.setFlowRate(mapVolume);
            scheduledDemandVolume += mapVolume;
            p.updateBandwidthCapacityForLinks();
            paths.add(p);
            if (getUnscheduledDemandVolume() <= 0) {
                isAccepted = true;
                printPaths();
                return true;
            }
        }
        //neu khong du de gan cho demand thi reject demand
        if (getUnscheduledDemandVolume() > 0) {
            for (Path p : paths) {
                scheduledDemandVolume -= p.flowRate;
                p.rollbackResource();
            }
            paths.clear();
            isAccepted = false;
        }
        printPaths();
        return false;
    }
    
    public void updateResource(){
        if(!this.isAccepted) return;
        for(Path p : paths){
            p.updateBandwidthCapacityForLinks();
            p.updateComputingCapacityForNodes();
        }
    }
}
