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
public class Node {

    int id;
    double distance;
    double computingCapacity;
    ArrayList<Link> inLinks;
    ArrayList<Link> outLinks;
    ArrayList<Link> outPaths;
    double usedComputingCapacity = 0;
    ArrayList<Function> functions;
    static int count = 0;
    Hashtable<Function, Double> compUsedForFunction;//used computing resource for Function
    boolean enabled = true;

    public Node() {
        computingCapacity = 0;
        distance = 0;
        inLinks = new ArrayList<Link>();
        outLinks = new ArrayList<Link>();
        outPaths = new ArrayList<Link>();
        functions = new ArrayList<Function>();
        compUsedForFunction = new Hashtable<Function, Double>();
        usedComputingCapacity = 0;
        id = count;
        count++;
    }

    public Node(Node v) {
        computingCapacity = v.getComputingCapacity();
        distance = v.getDistance();
        inLinks = new ArrayList<Link>();
        outLinks = new ArrayList<Link>();
        outPaths = new ArrayList<Link>();
        functions = new ArrayList<Function>();
        compUsedForFunction = new Hashtable<Function, Double>();
        usedComputingCapacity = 0;
        for (Link e : v.getInLinks()) {
            Link ie = new Link(e);
            inLinks.add(ie);
        }
        for (Link e : v.getOutLinks()) {
            Link ie = new Link(e);
            outLinks.add(ie);
        }
        for (Link e : v.getOutPaths()) {
            Link ie = new Link(e);
            outPaths.add(ie);
        }
        id = count;
        count++;

    }

    public void reset() {
        usedComputingCapacity = 0;
        enabled = true;
        outPaths.clear();
        compUsedForFunction.clear();
    }

    public void setId(int i) {
        id = i;
    }

    public void setEnabled(boolean e) {
        enabled = e;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setDistance(double d) {
        distance = d;
    }

    public void setComputingCapacity(double c) {
        computingCapacity = c;
    }
     public void setUsedComputingCapacity(double c) {
        usedComputingCapacity = c;
    }

    public int getNumInLinks() {
        return inLinks.size();
    }

    public int getNumOutLinks() {
        return outLinks.size();
    }

    public double getDistance() {
        return distance;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Link> getInLinks() {
        return inLinks;
    }

    public ArrayList<Link> getOutLinks() {
        return outLinks;
    }

    public ArrayList<Link> getOutPaths() {
        return outPaths;
    }

    public ArrayList getFunctions() {
        return functions;
    }

    public double getComputingCapacity() {
        return computingCapacity;
    }
    
    public double getUsedComputingCapacity() {
        return usedComputingCapacity;
    }

    public double getAvailableComputingCapacity() {
        return computingCapacity - usedComputingCapacity;
    }

    public boolean isOverLoad() {
        if (this.getAvailableComputingCapacity() < 0) {
            return true;
        }
        return false;
    }
    
    public void printInLinks(){
        System.out.println(" Inlinks: ");
        for(Link e : inLinks){
            System.out.print(" " + e.getSrcNode().getId());
        }
        System.out.println();
    }
     public void printOutLinks(){
         System.out.println(" Outlinks: ");
        for(Link e : outLinks){
            System.out.print(" " + e.getDestNode().getId());
        }
        System.out.println();
    }
     
}
