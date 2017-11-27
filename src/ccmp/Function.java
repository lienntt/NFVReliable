/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author lien
 */
public class Function {

    int id;
//    ArrayList<Node> nodes;
    int requiredResource = 1;

    public Function() {
        id = -1;
//        nodes = new ArrayList<Node>();
    }
    public Function(Function f) {
        id = f.getId();
//        nodes = new ArrayList<Node>();
    }
    public Function(int fid) {
        id = fid;
//        nodes = new ArrayList<Node>();
    }
    public int getRequireResource(){
        return requiredResource;
    }
    public void setRequireResource(int rs){
         requiredResource =rs;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    

}
