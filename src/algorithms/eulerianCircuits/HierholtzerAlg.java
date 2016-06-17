package algorithms.eulerianCircuits;

import java.io.*;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.IntSummaryStatistics;
import java.util.Stack;

/**
 * Νίκος Παναγόπουλος 2393 npanagop@csd.auth.gr
 *
 * Υλοποίηση του αλγορίθμου Hierholtzer για την εύρεση Eulerian κυκλωμάτων σε έναν συνδεδμένο γράφο με κορυφές άρτιου
 * βαθμού.
 *
 * Τα δεδομένα βρίσκονται στο αρχείο inputEul.txt σε μορφή πίνακα γειτνίασης.
 *
 * Το αποτέλεσμα αποθηκεύεται στο αρχείο outputEulHierholtzer.txt με την μορφή "0 1 2 3... 0" που δείχνει το κύκλωμα που
 * βρήκε ο αλγόριθμος.
 */
public class HierholtzerAlg {
    public static void main(String[] args) {
        //Είσοδος δεδομένων
        boolean[][] adj; //πίνακας γειτνίασης
        int vertices;    //πλήθος κορυφών
        int edges;       //πλήθος ακμών

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("inputEul.txt"))) {
            String[] lineSplit = bufferedReader.readLine().trim().split(" ");
            vertices = Integer.valueOf(lineSplit[0]);
            edges = Integer.valueOf(lineSplit[1]);

            adj = new boolean[vertices][vertices];

            int i = 0;
            while (i < vertices) {
                lineSplit = bufferedReader.readLine().trim().split(" ");

                for (int j = 0; j < vertices; j++) {
                    if (Integer.valueOf(lineSplit[j]) == 1){
                        adj[i][j] = true;
                    }
                    else{
                        adj[i][j] = false;
                    }
                }

                i++;
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        //**************************************************

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                if (adj[i][j]){
                    System.out.print("1 ");
                }
                else{
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }

        //Αρχικοποίηση δομών

        //κύκλωμα που δημιουργείται καθώς προχωράει μπροστά ο αλγόριθμος
        Stack<Integer> tempCircuit = new Stack<>();
        //κύκλωμα που δημιουργείται καθώς κάνει backtrack ο αλγόριθμος
        Stack<Integer> finalCircuit = new Stack<>();

        int currentVertex = 0;
        int nextVertex;

        ArrayList<Integer> neighbors = getNeighbors(adj,currentVertex);
        System.out.println(neighbors.toString());

        //Όσο υπάρχει γειτονική κορυφή
        while (!neighbors.isEmpty()) {;
            //η επόμενη κορυφή που θα πάει ο αλγόριθμος
            nextVertex = neighbors.get(0);
            neighbors.remove(0);
            //System.out.println("next vertex : "+nextVertex);
            //βάλε την κορυφή στο προσωρινό κύκλωμα
            tempCircuit.push(currentVertex);

            //διαγραφή της ακμής που θα διασχίσει
            adj[currentVertex][nextVertex] = false;
            adj[nextVertex][currentVertex] = false;

            //εύρεση των γειτόνων της νέας κορυφής
            currentVertex = nextVertex;
            neighbors = getNeighbors(adj, currentVertex);
            System.out.println(neighbors.toString());
            //αν είναι η τελευταία κορυφή που θα προσπελάσει ο αλγόριθμος, βάλτην στο προσωρινό κύκλωμα
            if (neighbors.isEmpty()){
                tempCircuit.push(currentVertex);
            }
        }

        //όσο υπάρχουν κορυφές στο προσωρινό κύκλωμα
        while (!tempCircuit.isEmpty()){
            //βγάλε τη κορυφή από το προσωρινό κύκλωμα
            currentVertex = tempCircuit.pop();
            //και βάλτην στο τελικό κύκλωμα
            finalCircuit.push(currentVertex);

            //βρες τους γείτονες της κορυφής
            neighbors = getNeighbors(adj, currentVertex);
            //επανέλαβε το πρώτο while από τη νέα κορυφή
            while (!neighbors.isEmpty()){
                nextVertex = neighbors.get(0);
                neighbors.remove(0);
                tempCircuit.push(currentVertex);

                adj[currentVertex][nextVertex] = false;
                adj[nextVertex][currentVertex] = false;

                currentVertex = nextVertex;
                neighbors = getNeighbors(adj, currentVertex);
            }

        }

        //Αποθήκευση αποτελέσματος
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputEulHierholtzer.txt"), "utf-8"))){
            for (int i : finalCircuit) {
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
        System.out.println("Vertex: "+currentVertex);
        ArrayList<Integer> neighbors = new ArrayList<>();
        for (int i = 0; i < adj.length; i++) {
            if (adj[currentVertex][i]) {
                System.out.println("Neighbor : "+i);
                neighbors.add(i);
            }
        }
        return neighbors;
    }


}
