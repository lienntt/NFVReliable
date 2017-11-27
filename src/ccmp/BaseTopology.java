/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.util.ArrayList;

/**
 *
 * @author lien
 */
public class BaseTopology {
  

    public ArrayList<Link> sortLinksDecreaseAccordingToBandwidth(ArrayList<Link> linkList) {
        ArrayList<Link> sorted = new ArrayList<Link>();
        for (Link e : linkList) {
            boolean added = false;
            for (int i = 0; i < sorted.size(); i++) {
                if (e.bandwidthCapacity > sorted.get(i).bandwidthCapacity) {
                    sorted.add(i, e);
                    added = true;
                    break;
                }
            }
            if (added == false) {
                sorted.add(sorted.size(), e);
            }
        }
        linkList.clear();
        return sorted;
    }

    public ArrayList<Link> sortLinksDecreaseAccordingToVisitedNumber(ArrayList<Link> linkList) {
        ArrayList<Link> sorted = new ArrayList<Link>();
        for (Link e : linkList) {
            boolean added = false;
            for (int i = 0; i < sorted.size(); i++) {
                int sortedlink = sorted.get(i).visitedNumber + sorted.get(i).reverseLink.visitedNumber;
                int currentlink = e.visitedNumber + e.reverseLink.visitedNumber;
                if (currentlink > sortedlink) {
                    sorted.add(i, e);
//                    sorted.add(i, e.reverseLink);
                    added = true;
                    break;
                }
            }
            if (added == false) {
                sorted.add(sorted.size(), e);
//                sorted.add(sorted.size(), e.reverseLink);
            }
        }
        linkList.clear();
        return sorted;
    }

    public ArrayList<Link> sortLinksIncreaseAccordingToBandwidth(ArrayList<Link> linkList) {
        ArrayList<Link> sorted = new ArrayList<Link>();
        for (Link e : linkList) {
            boolean added = false;
            for (int i = 0; i < sorted.size(); i++) {
                if (e.bandwidthCapacity < sorted.get(i).bandwidthCapacity) {
                    sorted.add(i, e);
                    added = true;
                    break;
                }
            }
            if (added == false) {
                sorted.add(sorted.size(), e);
            }
        }
        linkList.clear();
        return sorted;
    }

   

    public ArrayList<Path> sortPathsDecreaseAccordingToMinCapacity(ArrayList<Path> pathList) {
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
    public ArrayList<Demand> sortDemandsDecreaseAccordingToVolume(ArrayList<Demand> demandList) {
        ArrayList<Demand> sorted = new ArrayList<Demand>();
        for (Demand d : demandList) {
            boolean added = false;
            for (int i = 0; i < sorted.size(); i++) {
                if (d.getVolume() > sorted.get(i).getVolume() || 
                        //neu bang thi sap xep theo so duong shortestpath giam dan
                        (d.getVolume() == sorted.get(i).getVolume())&& (d.getAllShortestPaths().size()<sorted.get(i).getAllShortestPaths().size())) {
                    sorted.add(i, d);
                    added = true;
                    break;
                }
            }
            if (added == false) {
                sorted.add(sorted.size(), d);
            }
        }
        demandList.clear();
        return sorted;
    }
    
    public ArrayList<Path> removePath(ArrayList<Path> paths, Path p) {
        ArrayList<Path> newpaths = new ArrayList<Path>();
        if (paths.contains(p)) {
            paths.remove(p);
            return paths;
        } else {
            for (Path pi : paths) {
                if (!p.equals(pi)) {
                    newpaths.add(pi);
                }
            }
        }
        paths.clear();
        return newpaths;
    }

    public boolean foundAllPaths(ArrayList<Path> paths, Node destNode) {
        for (Path p : paths) {
            if (!p.getNodes().contains(destNode)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Path> clonePaths(ArrayList<Path> ps) {
        ArrayList<Path> paths = new ArrayList<Path>();
        for (Path p : ps) {
            Path newp = new Path(p);
            paths.add(newp);
        }
        return paths;
    }

}
