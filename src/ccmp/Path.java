/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.io.File;
import java.util.Hashtable;
import java.util.Scanner;

/**
 *
 * @author lien
 */
public class Path {

    ArrayList<Node> nodes;
    ArrayList<Link> links;
    double prevFlowRate;
    double originalFlowRate;
    double flowRate;
    Demand demand;
    double weight = -1;
    boolean getDest = false;
    double minCapacity;
    boolean mapped = false;
    boolean foundBetterMappingFunctionNode = false;
    //<Node, <Function, ratio of required computing of function>>
//    Hashtable<Node, list of function on node>> mappingFunctionNode;
    Hashtable<Node, ArrayList<Function>> mappingFunctionNode;
    Hashtable<Node, Double> usedComputing = new Hashtable<Node, Double>();

    public Path() {
        nodes = new ArrayList<Node>();
        links = new ArrayList<Link>();
        mappingFunctionNode = new Hashtable<Node, ArrayList<Function>>();
        flowRate = 0;
        originalFlowRate = 0;
        prevFlowRate = 0;
        minCapacity = 0;
    }

    public Path(Path p) {
        flowRate = p.getFlowRate();
        originalFlowRate = flowRate;
        prevFlowRate = flowRate;
        nodes = new ArrayList<Node>();
        links = new ArrayList<Link>();
        mappingFunctionNode = new Hashtable<Node, ArrayList<Function>>();
        demand = p.demand;
        minCapacity = p.minCapacity;
        for (Node v : p.getNodes()) {
//            Node vp = new Node(v);
            nodes.add(v);
            ArrayList<Function> fs = new ArrayList<Function>();
            if(p.mappingFunctionNode.containsKey(v)){
                for(Function f : p.mappingFunctionNode.get(v)){
                    fs.add(f);
                }
                mappingFunctionNode.put(v,fs );
            }
        }
        for (Link e : p.getLinks()) {
//            Link ep  = new Link(e);
            links.add(e);
        }

    }

    public int hasNode(Node v) {
        if (nodes.contains(v)) {
            return 1;
        }
        return 0;
    }
    //Jdp

    public int servesDemand(Demand d) {
        if (demand == d) {
            return 1;
        }
        return 0;
    }
    //Lep

    public int hasLink(Link e) {
        if (links.contains(e)) {
            return 1;
        }
        return 0;
    }

    public double getFlowRate() {
        return flowRate;
    }

    public double getPrevFlowRate() {
        return prevFlowRate;
    }

    public double getOriginalFlowRate() {
        return originalFlowRate;
    }

    public void setFlowRate(double fr) {
        // prevFlowRate = flowRate;
        flowRate = fr;
    }

//    public void updateUsedCapacityForLinks() {
//        for (Link e : links) {
//            e.usedCapacity += this.flowRate - this.prevFlowRate;
//        }
//    }
//
//    public void updateUsedComputingCapacityForNodes() {
//        for (Node v : this.mappingFunctionNode.keySet()) {
//            for (Function f : this.mappingFunctionNode.get(v).keySet()) {
//                v.usedComputingCapactiy += this.mappingFunctionNode.get(v).get(f) * (this.flowRate - this.prevFlowRate);
//            }
//        }
//    }
//
//    public void updateUsedCapacity() {
//        updateUsedCapacityForLinks();
//        updateUsedComputingCapacityForNodes();
//    }
//
//    public void increaseFlowRate() {
//        double increaseFlowRate = flowRate + Math.abs(flowRate - prevFlowRate);
//        prevFlowRate = flowRate;
//        flowRate = increaseFlowRate;
//        updateUsedCapacity();
//    }
//
//    public void decreaseFlowRate() {
//        double decreaseFlowRate = flowRate - Math.abs(flowRate - prevFlowRate) / 2;
//        prevFlowRate = flowRate;
//        flowRate = decreaseFlowRate;
//        updateUsedCapacity();
//    }
    public boolean hasOverLoad() {
        boolean hasoverload = false;
        for (Link e : links) {
            if (e.isOverLoad()) {
                hasoverload = true;
            }
        }
        for (Node v : nodes) {
            if (v.isOverLoad()) {
                hasoverload = true;
            }
        }
        return hasoverload;
    }

    public void setOriginalFlowRate(double fr) {
        originalFlowRate = fr;
    }

    public void setPrevFlowRate() {
        prevFlowRate = flowRate;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public boolean equals(Path p) {
        if (p.getNodes().size() != nodes.size()) {
            return false;
        }
        for (int i = 1; i < nodes.size(); i++) {
            if (p.getNodes().get(i) != nodes.get(i)) {
                return false;
            }
        }
//       for(Node v: nodes){
//           if(p.hasNode(v)==0)
//               return false;
//       }
//        for(Node v: p.getNodes()){
//           if(hasNode(v)==0)
//               return false;
//       }
//        
        return true;
    }
    //calculate weight of path

    public double getWeight() {
        if (weight > 0) {
            return weight;
        }
        double pw = 0;
        for (Link e : links) {
            pw += e.getWeight();
        }
        weight = pw;
        return pw;
    }

    public void setOutPathForLink() {
        for (Link e : getLinks()) {
            if (!e.getSrcNode().getOutPaths().contains(e)) {
                e.getSrcNode().getOutPaths().add(e);
            }
        }
    }

    public double getAvailableCapacity() {
        double availableCapacity = 0;
        for (Node v : nodes) {
            availableCapacity += v.getAvailableComputingCapacity();
        }
        return availableCapacity;
    }

    public double getMinAvailableComputingCapacity() {
        if (nodes.size() < 1) {
            return 0;
        }
        double minAvailableComputingCapacity = nodes.get(0).getAvailableComputingCapacity();
        for (Node v : nodes) {
            if (v.getAvailableComputingCapacity() > minAvailableComputingCapacity) {
                minAvailableComputingCapacity = v.getAvailableComputingCapacity();
            }
        }
        return minAvailableComputingCapacity;
    }

    public void updateBandwidthCapacityForLinks() {
        for (Link e : links) {
            e.setUsedBandwidthCapacity(e.getUsedBandwidthCapacity() + this.flowRate);
        }
    }

    public void rollbackResource() {
        for (Link e : links) {
            e.setUsedBandwidthCapacity(e.getUsedBandwidthCapacity() - this.flowRate);
        }
    }

    public double getAvailableBandwidth() {
        if (links.size() < 1) {
            return Double.MAX_VALUE;
        }
        double availableBandwidth = links.get(0).getBandwidthCapacity();
        double be =0;
        for (Link e : links) {
            be = e.getAvailableCapacity();
            if (availableBandwidth > be) {
                availableBandwidth = be;
            }
        }
        return availableBandwidth;

    }

    public double getMinAvailableBandwidth() {
        return getAvailableBandwidth();
    }

    public void calculateMinAvailableCapacity() {

//        if (getMinAvailableComputingCapacity() < getMinAvailableBandwidth()) {
//             minCapacity = getMinAvailableComputingCapacity();
//        }
        minCapacity = getMinAvailableBandwidth();

    }

    public double getMinAvailableCapacity() {
        return minCapacity;
    }
//    public boolean isSatisfyForDemand(Demand d, double trafficRate) {
//
//        if (this.getAvailableCapacity() < d.getRequireResource(trafficRate)
//                || this.getAvailableBandwidth() < trafficRate) {
//            return false;
//        }
//
//        return true;
//    }

    public boolean isSatisfyNodeCapacity() {
        for (Node v : nodes) {
            if (v.isOverLoad()) {
                return false;
            }
        }

        return true;
    }

    public boolean isSatisfyLinkCapacity() {
        for (Link e : links) {
            if (e.isOverLoad()) {
                return false;
            }
        }
        return true;
    }

    public boolean isSatifyAllRequirements() {

        return this.isSatisfyLinkCapacity() && this.isSatisfyNodeCapacity();
    }

    public boolean mappingFunctionNode() {
        //tim loi giai kha dung
       return  _mappingFunctionNode();
    }

    public boolean _mappingFunctionNode() {

        findSolutionMappingForFunctionNode();

        do {
            findBetterSolutionMappingForFunctionNode();
            //tiep tuc lap neu van tim ra chuoi moi tot hon
        } while (foundBetterMappingFunctionNode == true);

        //phan bo function tren node thanh cong thi cap nhat lai capacity cua node
        if (this.mapped == true) {
            
            updateComputingCapacityForNodes();
            System.out.println("testNode");
            for (Node v : nodes) {
                System.out.println("Node " + v.id + " : " + v.getComputingCapacity() + " - " + v.getUsedComputingCapacity());
            }
            return true;
        }
        return false;
    }

    public void findSolutionMappingForFunctionNode() {
        ArrayList<Function> functionsMap = new ArrayList<Function>();
        //cac function can xu ly
        for (Function f : demand.functions) {
            functionsMap.add(f);
        }

        ArrayList<Node> nodesMap = new ArrayList<Node>();
        for (Node v : nodes) {
            nodesMap.add(v);
        }
//        Hashtable<Node, Double> usedComputing = new Hashtable<Node, Double>();
        for (Node v : nodesMap) {
            usedComputing.put(v, 0.0);
            ArrayList<Function> fs = new ArrayList<Function>();
            mappingFunctionNode.put(v, fs);
        }
        double newUsedCapa = 0;
        //neu chua het node de gan va chua het function can gan
        while (!nodesMap.isEmpty() && !functionsMap.isEmpty()) {
            //xet node tiep theo
            Node v = nodesMap.get(0);
            nodesMap.remove(v);
            
            //neu node con kha nang xu ly va chua het function can gan
            while ((v.getAvailableComputingCapacity() - usedComputing.get(v)) > 0 && !functionsMap.isEmpty()) {

                //xet function tiep theo
                Function f = functionsMap.get(0);

                //neu node con kha nang cap capacity cho function
                if ((v.getAvailableComputingCapacity() - usedComputing.get(v) - flowRate * f.requiredResource) >=0) {
                    //cap nhat cho v
                    newUsedCapa = usedComputing.get(v) + flowRate * f.requiredResource;
                    usedComputing.put(v, newUsedCapa);
                     mappingFunctionNode.get(v).add(f);
                    functionsMap.remove(f);
                    
                } else {
                    //neu khong du xy ly co function thi thoat khoi vong lap
                    //chuyen sang node tiep theo
                    break;
                }
            }
        }
        if (functionsMap.isEmpty()  ) {
            this.mapped = true;
        } else {
            this.mapped = false;
        }
    }

    public void findBetterSolutionMappingForFunctionNode() {

        foundBetterMappingFunctionNode = false;

        ArrayList<Node> nodesList = new ArrayList<Node>();
        //lau cac node dang duoc gan xu ly function
        for(Node v: nodes){
            if(!mappingFunctionNode.get(v).isEmpty())
                nodesList.add(v);
        }
        
        while(nodesList.size()>= 2){
            Node src = nodesList.get(0);
            Node dest = nodesList.get(1);
            ArrayList<Function> fs = mappingFunctionNode.get(src);
            if(_findBetterSolutionMappingForFunction(src, dest, fs)==true)
                foundBetterMappingFunctionNode = true;
            nodesList.remove(src);
            nodesList.remove(dest);
        }
    }

    //fs hien tai dang chay tren src
    public boolean _findBetterSolutionMappingForFunction(Node src, Node dest, ArrayList<Function> fs) {
        double requiredComputingCapacity = 0;
        for (Function f : fs) {
            requiredComputingCapacity += f.getRequireResource() * this.getFlowRate();
        }
        ArrayList<Node> availableNodes = new ArrayList<Node>();
        //add cac node tu src den dest
        availableNodes.add(src);
        for (Link e : links) {
            if (availableNodes.contains(e.getSrcNode()) && e.getDestNode() != dest) {
                availableNodes.add(e.getDestNode());
            }
        }
        //sort nodes tang dan theo available computing capacity
        availableNodes = sortNodesIncreaseAccordingToAvailableCapacity(availableNodes);

        for (Node v : availableNodes) {
            //tim thay node nho nhat va du kha nang chay function 
            if (findAvailableCapacityOfNodeAfterMapping(v) >= requiredComputingCapacity) {
                mappingFunctionNode.get(v).addAll(fs);
                mappingFunctionNode.get(src).removeAll(fs);
                usedComputing.put(src, usedComputing.get(src) - requiredComputingCapacity);
                usedComputing.put(v, usedComputing.get(v) + requiredComputingCapacity);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Node> sortNodesIncreaseAccordingToAvailableCapacity(ArrayList<Node> nodeList) {

        ArrayList<Node> sorted = new ArrayList<Node>();
        for (Node v : nodeList) {
            boolean added = false;
            for (int i = 0; i < sorted.size(); i++) {
                if (findAvailableCapacityOfNodeAfterMapping(v) < findAvailableCapacityOfNodeAfterMapping(sorted.get(i))) {
                    sorted.add(i, v);
                    added = true;
                    break;
                }
            }
            if (added == false) {
                sorted.add(sorted.size(), v);
            }
        }
        return sorted;
    }

    public double findAvailableCapacityOfNodeAfterMapping(Node v) {
        return v.getAvailableComputingCapacity() - usedComputing.get(v);
    }

    public void updateComputingCapacityForNodes() {
        for (Node v : nodes) {
            if(mappingFunctionNode.containsKey(v)){
                ArrayList<Function> fs = mappingFunctionNode.get(v);
                double newUsedCapa = 0;
                for (Function f : fs) {
                    newUsedCapa = v.getUsedComputingCapacity() + this.getFlowRate() * f.getRequireResource();
                    v.setUsedComputingCapacity(newUsedCapa);
                }
            }
        }
    }
    public void rollbackComputingCapacityForNodes() {
        for (Node v : nodes) {
            if(mappingFunctionNode.containsKey(v)){
                ArrayList<Function> fs = mappingFunctionNode.get(v);
                double newUsedCapa = 0;
                for (Function f : fs) {
                    newUsedCapa = v.getUsedComputingCapacity() - this.getFlowRate() * f.getRequireResource();
                    v.setUsedComputingCapacity(newUsedCapa);
//                    v.setUsedComputingCapacity(v.getUsedComputingCapacity() - this.getFlowRate() * f.getRequireResource());
                }
            }
        }
    }
}
