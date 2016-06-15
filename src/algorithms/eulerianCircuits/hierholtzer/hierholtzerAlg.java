package algorithms.eulerianCircuits.hierholtzer;

import java.io.*;
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
public class hierholtzerAlg {
    public static void main(String[] args) {
        //Είσοδος δεδομένων
        boolean[][] adj; //πίνακας γειτνίασης
        int vertices;    //πλήθος κορυφών

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("inputEul.txt"))) {
            vertices = Character.getNumericValue((char) bufferedInputStream.read());

            adj = new boolean[vertices][vertices];

            int i = 0;
            int j = 0;
            char readChar;

            while (i < vertices) {
                readChar = ((char) bufferedInputStream.read());
                if (readChar == '0' || readChar == '1') {
                    adj[i][j] = readChar == '1';
                    j++;
                    if (j == vertices) {
                        j = 0;
                        i++;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        //Αρχικοποίηση δομών

        //κύκλωμα που δημιουργείται καθώς προχωράει μπροστά ο αλγόριθμος
        Stack<Integer> tempCircuit = new Stack<>();
        //κύκλωμα που δημιουργείται καθώς κάνει backtrack ο αλγόριθμος
        Stack<Integer> finalCircuit = new Stack<>();

        int currentVertex = 0;
        int nextVertex;

        Stack<Integer> neighbors = getNeighbors(adj,currentVertex);

        //Όσο υπάρχει γειτονική κορυφή
        while (!neighbors.isEmpty()) {
            //η επόμενη κορυφή που θα πάει ο αλγόριθμος
            nextVertex = neighbors.pop();
            //βάλε την κορυφή στο προσωρινό κύκλωμα
            tempCircuit.push(currentVertex);

            //διαγραφή της ακμής που θα διασχίσει
            adj[currentVertex][nextVertex] = false;
            adj[nextVertex][currentVertex] = false;

            //εύρεση των γειτόνων της νέας κορυφής
            currentVertex = nextVertex;
            neighbors = getNeighbors(adj, currentVertex);
            //αν είναι η τελευταία κορυφή που θα προσπελάσει ο αλγόριθμος, βάλτην στο προσωρινό κύκλωμα
            if (neighbors.isEmpty()){
                tempCircuit.push(currentVertex);
            }
        }

        //όσο υπάρχουν κορυφές στο προσωρινό κύκλωμα
        while (!tempCircuit.isEmpty()){
            //βγάλε τη κορυφή από το πρώτο προσωρινό κύκλωμα
            currentVertex = tempCircuit.pop();
            //και βάλτην στο τελικό κύκλωμα
            finalCircuit.push(currentVertex);

            //βρες τους γείτονες της κορυφής
            neighbors = getNeighbors(adj, currentVertex);
            //επανέλαβε το πρώτο while από τη νέα κορυφή
            while (!neighbors.isEmpty()){
                nextVertex = neighbors.pop();
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
     * @return Στοίβα με τους γείτονες της κορυφής.
     */
    private static Stack<Integer> getNeighbors(boolean[][] adj, int currentVertex){
        Stack<Integer> neighbors = new Stack<>();
        for (int i = 0; i < adj.length; i++) {
            if (adj[currentVertex][i]) {
                neighbors.push(i);
            }
        }
        return neighbors;
    }


}
