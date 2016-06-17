package algorithms.prufer;

import java.io.*;
import java.util.ArrayList;

/**
 * Νίκος Παναγόπουλος 2393 npanagop@csd.auth.gr
 *
 * Υλοποίηση του αλγορίθμου για την δημιουργία δένδρου από κώδικα Prufer.
 *
 * Ο κώδικας Prufer βρίσκεται στο αρχείο inputPruferCode.txt .
 *
 * Το δένδρο που προκύπτει αποθηκεύεται στο αρχείο outputPruferToTree.txt σε μορφή πίνακα γειτνίασης.
 */
public class PruferToTree {
    public static void main(String[] args) {
        //Είσοδος δεδομένων

        //Ο κώδικας Prufer
        ArrayList<Integer> pruferCode = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("inputPruferCode.txt"))){
            String[] lineSplit = bufferedReader.readLine().trim().split(" ");

            for (String value : lineSplit){
                pruferCode.add(Integer.valueOf(value));
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
            return;
        }
        //**************************************************

        //Αρχικοποίηση δομών

        //Το πλήθος των κορυφών
        int n = pruferCode.size()+2;

        //Πίνακας που περιλαμβάνει κορυφές 1 εώς n
        ArrayList<Integer> verticesList = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            verticesList.add(i);
        }

        //Πίνακας με τις κορυφές που δεν υπάρχουν στον κώδικα Prufer
        ArrayList<Integer> verticesNotInP = getVerticesNotInPrufer(pruferCode, verticesList);

        //Πίνακας γειτνίασης όπου θα δημιουργηθεί το δένδρο.
        boolean adj[][] = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adj[i][j] = false;
            }
        }

        //Μετρητής του πλήθους των ακμών που δημιουργήθηκαν
        int edges = 0;
        //Επανέλαβε μέχρι να υπάρχουν μόνο δύο κορυφές στη λίστα
        while(verticesList.size()>2){
            //ένωσε με ακμή τη μικρότερη κορυφή από τη λίστα που δεν υπάρχει στον κώδικα Prufer και τη πρώτη κορυφή από
            //το κώδικα Prufer
            adj[verticesNotInP.get(0)-1][pruferCode.get(0)-1] = true;
            adj[pruferCode.get(0)-1][verticesNotInP.get(0)-1] = true;
            edges++;
            //αφαίρεσε τους αριθμούς που χρησιμοποίησες από τις λίστες τους
            pruferCode.remove(0);
            verticesList.remove(verticesList.indexOf(verticesNotInP.get(0)));
            //ενημέρωσε τις κορυφές που δεν υπάρχουν στον κώδικα Prufer
            verticesNotInP = getVerticesNotInPrufer(pruferCode, verticesList);
        }
        //ένωσε με ακμή τις δύο τελευταίες κορυφές
        adj[verticesList.get(0)-1][verticesList.get(1)-1] = true;
        adj[verticesList.get(1)-1][verticesList.get(0)-1] = true;
        edges++;

        //Αποθήκευση αποτελέσματος
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputPruferToTree.txt"), "utf-8"))){
            writer.write(n+" "+edges+"\n");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (adj[i][j]){
                        writer.write("1 ");
                    }
                    else{
                        writer.write("0 ");
                    }
                }
                writer.write("\n");
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Μέθοδος για να βρεθεί ποιοί αριθμοί από μια λίστα δεν ανήκουν σε μια άλλη.
     * @param pruferCode Η πρώτη λίστα στην οποία ελέγχουμε να μην υπάρχουν τα στοιχεία της δεύτερης.
     * @param verticesList Η δεύτερη λίστα.
     * @return Πίνακας με τους αριθμούς της δεύτερης λίστας που δεν ανήκουν στην πρώτη.
     */
    private static ArrayList<Integer> getVerticesNotInPrufer(ArrayList<Integer> pruferCode, ArrayList<Integer> verticesList){
        boolean exists;
        ArrayList<Integer> verticesNotInP = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            exists = false;
            for (int j = 0; j < pruferCode.size(); j++) {
                if (verticesList.get(i) == pruferCode.get(j)){
                    exists = true;
                    break;
                }
            }
            if (!exists){
                verticesNotInP.add(verticesList.get(i));
            }
        }
        return verticesNotInP;
    }
}
