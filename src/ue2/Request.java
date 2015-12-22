package ue2;


import java.io.*;      // Fuer den Reader
import java.net.*;     // Fuer den Socket
import java.util.ArrayList;

public class Request {

    public static String aGET              = null;
    public static String bGET              = null;
    public static String dGET              = null;
    public static ArrayList<String[]> telefonliste;
    public static ArrayList<String[]> suchergebnisse = new ArrayList();
    public static ServerSocket ss       = null;  // Fuer das accept()
    public static Socket cs             = null;  // Fuer die Requests
    public static InputStream is        = null;  // Aus dem Socket lesen
    public static InputStreamReader isr = null;
    public static BufferedReader br     = null;
    public static OutputStream os       = null;  // In den Socket schreiben
    public static PrintWriter pw        = null;
    public static int port;
    public static String host;



    public static void main(String[] args) throws Exception {

        // Vereinbarungen
        // ---------------------------------------------------------
        String zeile          = null; // Eine Zeile aus dem Socket
        port              = Integer.parseInt(args[0]);     // Der lokale Port

        // Programmstart und Portbelegung
        // ---------------------------------------------------------
        host = InetAddress.getLocalHost().getHostName();

        System.out.println("Server startet auf "+host+" an "+port);

        // ServerSocket einrichten und in einer Schleife auf
        // Requests warten.
        // ---------------------------------------------------------

        erstelleTestdaten();

        ss = new ServerSocket(port);
        while(true) {
            System.out.println("Warte im accept()");
            cs = ss.accept();               // <== Auf Requests warten

            // Den Request lesen (Hier nur erste Zeile)
            // -------------------------------------------------------
            is    = cs.getInputStream();
            isr   = new InputStreamReader(is);
            br    = new BufferedReader(isr);
            zeile = br.readLine();
            System.out.println("Kontrollausgabe: "+zeile);

            try {
                String[] nachLeer = zeile.split(" ");
                String[] nachFrageZ = nachLeer[1].split("\\?");
                String[] nachKU = nachFrageZ[1].split("&");

                String[] aNachGleich = nachKU[0].split("=");
                try {
                    aGET = aNachGleich[1].replace("+", " ");
                    aGET = aGET.replace("%FC", "ü");
                } catch (Exception e) {
                    System.out.println("Keine Angabe für Name");
                }

                String[] bNachGleich = nachKU[1].split("=");
                try {
                    bGET = bNachGleich[1].replace("+", " ");
                } catch (Exception e) {
                    System.out.println("Keine Angabe für Nummer");
                }

                String[] dNachGleich = nachKU[2].split("=");
                try {
                    dGET = dNachGleich[1];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Request wird bearbeitet");
                os  = cs.getOutputStream();
                pw  = new PrintWriter(os);

                pw.println("HTTP/1.1 200 OK");
                pw.println("<meta charset='utf-8'>");// Der Header
                pw.println("Content-Type: text/html");
                pw.println();
                pw.println("<html>");
                pw.println("<body>");
                pw.println("<h2 align=center>Telefonverzeichnis</h2>");
                pw.println("<h3>Sie k&oumlnnen nach Name oder nach Telefonnummer oder nach beiden (nebenl&aumlufig) suchen.</h3>");
                pw.println("<form method=get action='http://"+host+":"+port+"'>");
                pw.println("<table>");
                pw.println("<tr> <td valign=top>Name:</td>    <td><input name=A></td>    <td></td> </tr>");
                pw.println("<tr> <td valign=top>Nummer:</td> <td><input name=B></td>    <td></td> </tr>");
                pw.println("<tr> <td valign=top><input type=submit name=C value=Suchen></td>");
                pw.println("<td><input type=reset></td>");
                pw.println("<td><input type=submit name=D value='Server beenden' ></td> </tr>");
                pw.println("</table>");
                pw.println("</form>");
                pw.println("</body>");
                pw.println("</html>");
                pw.println();
                pw.flush();
                pw.close();
                br.close();
            }

            //Wenn Suchen oder Server beenden gedrückt wird
            if(dGET!=null){
                if(dGET.equals("Server+beenden")){
                    serverBeenden();
                }else if(dGET.equals("Suchen")){
                    if(aGET!=null&&bGET!=null){
                        beideSuche();
                    }else if(aGET!=null){
                        System.out.println("Nach "+aGET+" suchen");
                        namenSuche();
                    }else if(bGET!=null){
                        System.out.println("Nach "+bGET+" suchen");
                        telSuche();
                    }
                    printArray(suchergebnisse);
                }
            }


            // Favicon-Requests nicht bearbeiten
            // -------------------------------------------------------
            if(zeile.startsWith("GET /favicon")) {
                System.out.println("Favicon-Request");
                br.close();
                continue;                       // Zum naechsten Request
            }

            aGET=null;
            bGET=null;
            suchergebnisse.clear();

            // Den Request bearbeiten (Hier: nur zuruecksenden)
            // -------------------------------------------------------

        }  // end while
    }  // end main()


    public static void serverBeenden(){
        System.out.println("Server wird beendet");
        try {
            os  = cs.getOutputStream();
        } catch (IOException e) {

        }
        pw  = new PrintWriter(os);
        pw.println("HTTP/1.1 200 OK");
        pw.println("<meta charset='utf-8'>");// Der Header
        pw.println("Content-Type: text/html");
        pw.println();
        pw.println("<html>");
        pw.println("<body>");
        pw.println("<h2 align=center>Der Server wurde beendet</h2>");
        pw.println("</body>");
        pw.println("</html>");
        pw.println();
        pw.flush();
        pw.close();
        try {
            br.close();
        } catch (IOException e) {

        }
        System.exit(2);
    }

    public static void beideSuche(){
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

    public static void namenSuche(){
        NameSearch ns = new NameSearch();   //creates thread
        ns.start();                         //starts thread
        try {
            ns.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void telSuche(){
        TelNrSuche tns = new TelNrSuche();   //creates thread
        tns.start();                         //starts thread
        try {
           tns.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printArray(ArrayList<String[]> a) {

        if(aGET!=null){
            System.out.println("aGET: "+aGET);
            if(aGET.trim().equals("")){
                aGET=null;
            }
        }

        if(bGET!=null){
            System.out.println("bGET: "+bGET);
            if(bGET.trim().equals("")){
                bGET=null;
            }
        }

        try {
            os = cs.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw = new PrintWriter(os);
        pw.println("HTTP/1.1 200 OK");
        pw.println("<meta charset='utf-8'>");// Der Header
        pw.println("Content-Type: text/html");
        pw.println();
        pw.println("<html>");
        pw.println("<body>");
        pw.println("<h2>Suchergebnisse:</h2>");
        if(aGET==null&&bGET==null) {
            pw.println("<span>Bitte machen Sie mindestens eine Eingabe</span>");
        }
        else if (a.isEmpty()) {
            pw.println("<span>Die Suche ergab keine Ergebnisse</span>");
        }else{
            for (String[] elem : a) {
                String n = elem[0];
                String nu = elem[1];
                n = n.replace("ü", "&uuml");
                {
                    pw.println("<span>" + n + " " + nu + "</span>");
                    pw.println("<br>");
                }

            }
        }
            pw.println("<form method=get action='http://" + host + ":" + port + "'>");
            pw.println("<input type='submit' value='erneute Suche'>");
            pw.println("</form>");
            pw.println("</body>");
            pw.println("</body>");
            pw.println("</html>");
            pw.println();
            pw.flush();
            pw.close();
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}  // end class// Datei: Request.java