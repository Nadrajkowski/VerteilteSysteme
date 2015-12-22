package ue1;

import java.util.ArrayList;

/**
 * Created by kasper on 13.10.15.
 *
 * @name TelSearch.java
 *
 * This Class creates a thread that searches through the the list of names and numbers by a given number and puts it in another list
 */
public class TelNrSuche extends Thread {

    ArrayList<String[]> liste = new ArrayList();

    public void run(){
        for(String[] n : main.telefonliste) {
            if (main.tel.equals(n[1])) {
                liste.add(n);
            }
        }
        if(liste.isEmpty()){

        }else {
            for (String[] elem : liste) {
                main.suchergebnisse.add(elem);
            }
        }
    }
}

