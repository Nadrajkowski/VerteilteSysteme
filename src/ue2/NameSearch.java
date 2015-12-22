package ue2;



import java.util.ArrayList;

/**
 * Created by kasper on 13.10.15.
 *
 * @name ue1.NameSearch.java
 *
 * This Class creates a thread that searches through the the list of names and numbers by a given name and puts it in another list
 */
public class NameSearch extends Thread {

    ArrayList<String[]> liste = new ArrayList();

    public void run(){
        for(String[] n : Request.telefonliste) {
            if (Request.aGET.equals(n[0])) {
                liste.add(n);
            }
        }
        if(liste.isEmpty()){

        }else {
            for (String[] elem : liste) {
                Request.suchergebnisse.add(elem);
            }
        }
    }
}
