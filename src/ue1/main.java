package ue1; /**
 * Created by kasper on 07.10.15.
 *
 * @name ue1.main.java
 *
 * This is the ue1.main class of this program. It contains the UI, creates test data and makes all the outputs on the console.
 *
 */

import java.util.ArrayList;
import java.util.Scanner;

public class main {

    static ArrayList<String[]> telefonliste;
    static ArrayList<String[]> suchergebnisse = new ArrayList();
    static Boolean exit = false;
    static String name="";
    static String tel="";


    public static void main(String[] args){

        erstelleTestdaten();

        while(!exit){
            Boolean innerExit = false;
            while(!innerExit){

                System.out.println("Wählen Sie einen Suchmodus aus (name, telefonnummer, beides)");
                Scanner modusIn = new Scanner(System.in);
                String modus = modusIn.nextLine();

                switch(modus){
                    case "name": namenSuche(); innerExit=true; break;
                    case "telefonnummer": telSuche(); innerExit=true; break;
                    case "beides": beideSuche(); innerExit=true; break;
                    default: System.out.println("Modus nicht bekannt. Bitte nochmal eingeben.");
                }
            }
            printArray(suchergebnisse);
            System.out.println("\neneut suchen? (ja/nein)");
            suchergebnisse.clear();
            Scanner erneutIn = new Scanner(System.in);
            String erneut = erneutIn.nextLine();
            if(erneut.equals("nein")){
                exit=true;
                System.out.println("Ende");
            }
        }
    }

    public static void namenSuche(){
        Boolean leer = true;
        while(leer){

            System.out.println("Nach welchem Namen soll gesucht werden?");
            Scanner nameIn = new Scanner(System.in);
            name = nameIn.nextLine();

            if(name==null||name.trim().equals("")){
                System.out.println("Bitte geben sie mindestens ein Zeichen ein.");
            }else {
                leer=false;
                NameSearch ns = new NameSearch();
                ns.start();
                try {
                    ns.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void telSuche(){
        Boolean leer = true;
        while(leer){

            System.out.println("Nach welcher Telefonnummer soll gesucht werden?");
            Scanner nameIn = new Scanner(System.in);
            tel = nameIn.nextLine();

            if(tel==null||tel.trim().equals("")){
                System.out.println("Bitte geben sie mindestens ein Zeichen ein.");
            }else {
                leer=false;
                TelNrSuche tns = new TelNrSuche();
                tns.start();
                try {
                    tns.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * this method
     */
    public static void beideSuche(){
        Boolean leer = true;
        Boolean telLeer = true;
        Boolean nameLeer = true;

        while(leer){

            while(nameLeer) {
                System.out.println("Nach welchem Namen soll gesucht werden?");
                Scanner nameIn = new Scanner(System.in);                        //read name input
                name = nameIn.nextLine();
                if(name==null||name.trim().equals("")){
                    System.out.println("Bitte geben sie mindestens ein Zeichen ein.");
                }else {
                    nameLeer=false;
                }
            }

            while(telLeer) {
                System.out.println("Nach welcher Nummer soll gesucht werden?");
                Scanner telIn = new Scanner(System.in);                        //read name input
                tel = telIn.nextLine();
                if(tel==null||tel.trim().equals("")){
                    System.out.println("Bitte geben sie mindestens ein Zeichen ein.");
                }else {
                    telLeer=false;
                }
            }


            leer=false;
            NameSearch ns = new NameSearch();   //creates threads
            TelNrSuche tns = new TelNrSuche();
            ns.start();                         //starts threads
            tns.start();
            try {
                ns.join();
                tns.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * @param
     * @return
     *
     * This method simply fills a list with data
     */
    public static void erstelleTestdaten(){
        telefonliste = new ArrayList<String[]>();

        String[] sa1 = {"Meier", "1111"};
        String[] sa2 = {"Meier", "2222"};
        String[] sa3 = {"Müller", "1111"};
        String[] sa4 = {"von Berghausen", "3333"};
        telefonliste.add(sa1);
        telefonliste.add(sa2);
        telefonliste.add(sa3);
        telefonliste.add(sa4);
    }

    public static void printArray(ArrayList<String[]> a){
        if(a.isEmpty()){
            System.out.println("Die Suche nach "+name+" "+tel+" ergab keine Ergebnisse.");
        }else {
            System.out.println("\n=========================");
            System.out.println("Name:\t\tTel.Nr\n");
            for (String[] elem : a) {
                System.out.println(elem[0] + "\t\t" + elem[1]);
            }
            System.out.println("=========================");
        }
        name="";
        tel="";
    }
}

