package algorithms.edmondsKarp;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Νίκος Παναγόπουλος 2393 npanagop@csd.auth.gr
 *
 * Υλοποίηση του αλγορίθμου βραχύτατων μονοπατιών Edmonds-Karp.
 *
 * Ως είσοδο διαβάζει ένα δίκτυο με ορισμένη πηγή, δεξαμενή και χωρητικότητα από το αρχείο inputEdmondsKarp.txt .
 *
 * Η μέγιστη ροή που υπολογίζεται αποθηκεύεται στο αρχείο outputEdmondsKarp.txt .
 */
public class EdmondsKarpAlg {
    public static void main(String[] args) {
        //Είσοδος δεδομένων
        int[][] adjFlow; //πίνακας γειτνίασης με χωρητικότητα
        int vertices;    //πλήθος κορυφών
        int s;           //πηγή του δικτύου
        int t;           //δεξαμενή του δικτύου

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("inputEdmondsKarp.txt"))) {
            String[] lineSplit = bufferedReader.readLine().trim().split(" ");
            vertices = Integer.valueOf(lineSplit[0]);

            lineSplit = bufferedReader.readLine().trim().split(" ");
            s = Integer.valueOf(lineSplit[0]);
            t = Integer.valueOf(lineSplit[1]);

            adjFlow = new int[vertices][vertices];

            int i = 0;
            while (i < vertices) {
                lineSplit = bufferedReader.readLine().trim().split(" ");

                for (int j = 0; j < vertices; j++) {
                    adjFlow[i][j] = Integer.valueOf(lineSplit[j]);
                }

                i++;
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        //**************************************************
        //Αρχικοποίηση δομών

        //Η μέγιστη ροή που θα υπολογιστεί
        int maxFlow = 0;
        //Πίνακας που περιέχει το γονιό κάθε κορυφής
        int parent[] = new int[vertices];

        //Αλγόριθμος EdmondsKarp

        //Όσο υπάρχει μονοπάτι επαύξησης
        while (BFS(adjFlow, s, t, parent)){
            int pathFlow = Integer.MAX_VALUE;
            int u;
            //Βρές τη μέγιστη ροή του μονοπατιού που ακολούθησε ο BFS
            for (int i = t; i != s; i=parent[i]) {
                u = parent[i];
                pathFlow = Math.min(pathFlow, adjFlow[u][i]);
            }

            //Ενημέρωσε την υπολειπόμενη χωρητικότητα των ακμών του μονοπατιού και αντέστρεψε τις ακμές
            for (int i = t; i != s ; i = parent[i]) {
                u = parent[i];
                adjFlow[u][i] -= pathFlow;
                adjFlow[i][u] += pathFlow;
            }
            //Πρόσθεσε τη ροή του μονοπατιού στη μέγιστη ροή
            maxFlow += pathFlow;
        }

        //Αποθήκευση αποτελέσματος
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputEdmondsKarp.txt"), "utf-8"))){
            writer.write(Integer.toString(maxFlow));
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }

    }

    /**
     * Μέθοδος που εφαρμόζει τον αλγόριθμο Breadth-First-Search, προσαρμοσμένο ώστε να σταματάει όταν φτάσει στη
     * δεξαμενή και να αποθηκεύει τη διαδρομή που ακολουθεί.
     * @param adjFlow Πίνακας γειτνίασης του δικτύου με χωρητικότητες.
     * @param s Πηγή του δικτύου.
     * @param t Δεξαμενή του δικτύου.
     * @param parent Πίνακας με τον γονέα κάθε κορυφής που επισκέπτεται ο αλγόριθμος.
     * @return Επιστρέφει true αν υπάρχει μονοπάτι από τη πηγή ως τη δεξαμενή.
     */
    private static boolean BFS(int[][] adjFlow, int s, int t, int parent[]){
        //Πλήθος κορυφών
        int n = adjFlow.length;
        //Πίνακας που δείχνει ποιές κορυφές έχει επισκεφτεί ο αλγόριθμος
        boolean visited[] = new boolean[n];
        for (int i = 0; i < n; i++) {
            visited[i] = false;
        }

        //Δημιουργία ουράς για το BFS
        Queue<Integer> queue = new LinkedList<>();
        //Προσθήκη της πηγής στην ουρά
        queue.add(s);
        visited[s] = true;
        parent[s] = -1;

        //η βασική επανάληψη του BFS
        while (!queue.isEmpty()){
            int currentVertex = queue.poll();

            for (int i = 0; i < n; i++) {
                if (!visited[i] && adjFlow[currentVertex][i] > 0){
                    queue.add(i);
                    parent[i] = currentVertex;
                    visited[i] = true;
                    //Αν έφτασε στη δεξαμενή υπάρχει το μονοπάτι
                    if (i == t){
                        return true;
                    }
                }
            }
        }
        //Δεν έφτασε στη δεξαμενή άρα δεν υπάρχει το μονοπάτι
        return false;
    }
}
