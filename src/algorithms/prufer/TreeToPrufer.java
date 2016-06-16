package algorithms.prufer;

import java.io.*;
import java.util.ArrayList;

/**
 * Νίκος Παναγόπουλος 2393 npanagop@csd.auth.gr
 *
 * Υλοποίηση αλγορίθμου για την κωδικοποίηση Prufer ενός δένδρου.
 *
 * Τα δεδομένα βρίσκονται στο αρχείο inputPruferTree.txt σε μορφή πίνακα γειτνίασης.
 *
 * Ο κώδικας Prufer που προκύπτει αποθηκεύεται στο αρχείο outputTreeToPrufer.txt .
 */
public class TreeToPrufer {
    public static void main(String[] args) {
        //Είσοδος δεδομένων
        boolean[][] adj; //πίνακας γειτνίασης
        int vertices;    //πλήθος κορυφών

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("inputPruferTree.txt"))){
            vertices = Character.getNumericValue((char) bufferedInputStream.read());

            adj = new boolean[vertices][vertices];

            int i = 0;
            int j = 0;
            char readChar;

            while(i < vertices){
                readChar = ((char) bufferedInputStream.read());
                if (readChar == '0' || readChar == '1'){
                    adj[i][j] = readChar == '1';
                    j++;
                    if (j == vertices) {
                        j = 0;
                        i++;
                    }
                }
            }

        }
        catch (IOException e){
            System.err.println(e.getMessage());
            return;
        }

        //Αρχικοποίηση δομών

        ArrayList<Integer> pruferCode = new ArrayList<>(vertices-2);
        //πίνακας με τον βαθμό κάθε κορυφής
        int vertexDegree[];
        int counter = 0;

        //Κωδικοποίηση Prufer

        //Μέχρι να τρέξει ο αλγόριθμος V-2 φορές
        while (counter<vertices-2){
            //υπολόγισε τον βαθμό κάθε κορυφής
            vertexDegree = getVertexDegree(adj, vertices);
            //Διέτρεξε τις κορυφές του γράφου
            for (int i = 0; i < vertices; i++) {
                //αν η κορυφή είναι φύλλο με τη μικρότερη επιγραφή
                if (vertexDegree[i]==1){
                    int neighbor = getNeighbors(adj, i).get(0);
                    //βάλε στον κώδικα Prufer την επιγραφή του γείτονα της (+1 γιατί είναι σε μορφή 0...ν-1)
                    pruferCode.add(neighbor+1);
                    //διέγραψε το φύλλο
                    adj[i][neighbor] = false;
                    adj[neighbor][i] = false;
                    break;
                }
            }
            counter++;
        }

        //Αποθήκευση αποτελέσματος
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputTreeToPrufer.txt"), "utf-8"))){
            for (int i : pruferCode) {
                writer.write(i+" ");
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }

    }

    /**
     * Μέθοδος για την έβρεση των γειτονικών κορυφών μια κορυφής.
     * @param adj Πίνακας γειτνίασης του γράφου.
     * @param currentVertex Κορυφή για την οποία ψάχνουμε τους γείτονες της.
     * @return Πίνακας με τους γείτονες της κορυφής.
     */
    private static ArrayList<Integer> getNeighbors(boolean[][] adj, int currentVertex){
        ArrayList<Integer> neighbors = new ArrayList<>();
        for (int i = 0; i < adj.length; i++) {
            if (adj[currentVertex][i]) {
                neighbors.add(i);
            }
        }
        return neighbors;
    }

    /**
     * Μέθοδος για τον υπολογισμό του βαθμού κάθε κορυφής σε έναν πίνακα γειτνίασης.
     * @param adj Πίνακας γειτνίασης του γράφου.
     * @param vertices Το πλήθος των κορυφών.
     * @return Πίνακα με τους βαθμούς κάθε κορυφής του γράφου.
     */
    private static int[] getVertexDegree(boolean[][] adj, int vertices){
        int vertexDegree[] = new int[vertices];

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                if (adj[i][j]){
                    vertexDegree[i]++;
                }
            }
        }
        return vertexDegree;
    }
}
