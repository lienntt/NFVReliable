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
public class Link {

    static int count = 0;
    int id;
    double bandwidthCapacity;
    double usedCapacity = 0;

    Node destNode;
    Node srcNode;
    Link reverseLink;
    double weight;
    boolean enabled = true;
    boolean overload = false;
    int visitedNumber = 0;
    //<demand, rate  >>
    private Hashtable<Demand, Double> totalFlowRate;

    public Link(Node src, Node dest, double bCapacity) {
        destNode = dest;
        srcNode = src;
        weight = 0;
        bandwidthCapacity = bCapacity;
        usedCapacity = 0;
        count++;
        id = count;
    }

    public Link(Link e) {
        destNode = new Node(e.getDestNode());
        srcNode = new Node(e.getSrcNode());
        weight = e.getWeight();
        bandwidthCapacity = e.getBandwidthCapacity();
        usedCapacity = 0;
        count++;
        id = count;
    }

    public void setReverseLink(Link e) {
        reverseLink = e;
        return;
    }

    public Link getReverseLink() {
        return reverseLink;
    }

    public void reset() {
        enabled = true;
        overload = false;
        usedCapacity = 0;
    }

    public void setDestNode(Node dest) {
        destNode = dest;
    }

    public void setSrcNode(Node src) {
        srcNode = src;

    }

    public void setWeight(double w) {
        weight = w;
    }

    public void setEnabled(boolean e) {
        enabled = e;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setBandwidthCapacity(double c) {
        bandwidthCapacity = c;
    }
    public void setUsedBandwidthCapacity(double c) {
        usedCapacity = c;
    }

    public Node getDestNode() {
        return destNode;
    }

    public Node getSrcNode() {
        return srcNode;
    }

    public double getWeight() {
        return weight;
    }

    public double getBandwidthCapacity() {
        return bandwidthCapacity;
    }
     public double getUsedBandwidthCapacity() {
        return usedCapacity;
    }

    public double getAvailableCapacity() {

        return bandwidthCapacity - usedCapacity;// - getReverseLink().usedCapacity;
    }
    
    public boolean isOverLoad() {
        if (this.getAvailableCapacity() < 0) {
            return true;
        }
        return false;
    }

    public Hashtable gettotalFlowRate() {
        return totalFlowRate;
    }
    //Lep

    public int isOnPath(Path p) {
        return p.hasLink(this);
    }

    public boolean isReverse(Link e) {
        if (this.getDestNode() == e.getSrcNode() && this.getSrcNode() == e.getDestNode()) {
            return true;
        }
        return false;
    }
}
