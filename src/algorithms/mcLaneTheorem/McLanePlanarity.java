package algorithms.mcLaneTheorem;

import algorithms.Pair;

import java.io.*;
import java.util.ArrayList;

/**
 * Νίκος Παναγόπουλος 2393 npanagop@csd.auth.gr
 *
 * Υλοποίηση του αλγορίθμου που εφαρμόζει το θεώρημα του McLane για τον έλεγχο της επιπεδικότητας ενός γράφου.
 *
 * Τα δεδομένα βρίσκονται στο αρχείο inputMcLane.txt σε μορφή πίνακα γειτνίασης.
 *
 * Το αποτέλεσμα αποθηκεύεται στο αρχείο outputMcLane.txt αναφέροντας αν ο γράφος είναι ή δεν είναι επίπεδος.
 */
public class McLanePlanarity {
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

        //Αρχικοποίηση δομών

        //Πίνακας που δείχνει ποιές κορυφές έχει επισκεφτεί ο αλγόριθμος DFS
        boolean visited[] = new boolean[vertices];
        for (int i = 0; i < vertices; i++) {
            visited[i] = false;
        }
        //Πίνακας που δείχνει την διαδρομή που ακολούθησε ο DFS, χρησιμοποιείται για να καθοριστεί ποιός είναι ο κύκλος
        //που βρέθηκε
        ArrayList<Integer> path = new ArrayList<>();

        //Λίστα με τους κύκλους που βρέθηκαν
        ArrayList<ArrayList<Integer>> cyclesList = new ArrayList<>();
        //Εκτέλεση Depth-First-Search για να βρεθούν οι κύκλοι του γράφου
        DFS(path, cyclesList, adj, visited, vertices, 0, -1, false);

        //Λίστα με τις ακμές του γράφου που σχηματίζουν κύκλους
        ArrayList<Pair<Integer, Integer>> listOfEdgesInCycles = new ArrayList<>();

        int current, previous;
        //Εύρεση των ακμών του γράφου που σχηματίζουν κύκλους
        for (ArrayList<Integer> cycle : cyclesList){
            current = -1;
            for (int vertex : cycle){
                previous = current;
                current = vertex;
                if (previous != -1){
                    listOfEdgesInCycles.add(new Pair<>(previous, current));
                }
                //αν είναι το τελευταίο στοιχείο ένωσε το με το πρώτο ως μία ακμή
                if (cycle.indexOf(vertex) == cycle.size()-1){
                    listOfEdgesInCycles.add(new Pair<>(current, cycle.get(0)));
                }
            }
        }

        //Έλεγχος αν είναι επίπεδος ο γράφος
        //Δηλαδή αν υπάρχει ακμή του που να συμμετέχει σε πάνω από δύο κύκλους
        boolean isPlanar = true;
        //Πόσες φορές εμφανίζεται μια ακμή στους κύκλους
        int occurrences;
        for (Pair<Integer, Integer> edge : listOfEdgesInCycles){
            occurrences = 0;
            for (Pair<Integer, Integer> otherEdges : listOfEdgesInCycles){
                if (edge.equals(otherEdges)){
                    occurrences++;
                }
            }
            if (occurrences>2){
                isPlanar = false;
                break;
            }
        }

        //Αποθήκευση αποτελέσματος
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputMcLane.txt"), "utf-8"))){
            if (isPlanar){
                writer.write("Ο γράφος είναι επίπεδος.");
            }
            else{
                writer.write("Ο γράφος δεν είναι επίπεδος.");
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }

    }

    /**
     * Υλοποίηση του αλγορίθμου DFS, τροποποιημένος ώστε να αποθηκεύει τη διαδρομή που ακολουθεί και να βρίσκει τους
     * κύκλους ενός γράφου.
     * @param path Η διαδρομή που ακολούθησε ο αλγόριθμος.
     * @param cyclesList Πίνακας όπου ο αλγόριθμος αποθηκεύει τους κύκλους που εντωπίζει.
     * @param adj Πίνακας γειτνίασης του γράφου.
     * @param visited Πίνακας με τις κορυφές που έχει ήδη επισκεφτεί ο αλγόριθμος.
     * @param vertices Το πλήθος των κορυφών του γράφου.
     * @param currentVertex Η κορυφή στην οποία βρίσκεται ο αλγόριθμος.
     * @param parent Ο γονιός της κορυφής στην οποία βρίσκεται ο αλγόριθμος.
     * @param backtrack Μεταβλητή για τον έλεγχο της οπισθοδρόμησης του αλγορίθμου με σκοπό την σωστή καταγραφή της
     *                  διαδρομής του.
     */
    private static void DFS(ArrayList<Integer> path, ArrayList<ArrayList<Integer>> cyclesList,
                            boolean[][] adj, boolean[] visited, int vertices,int currentVertex,
                            int parent, boolean backtrack){
        //Σημείωσε ότι επισκέφτηκες αυτή τη κορυφή
        visited[currentVertex] = true;
        //Έλεγχος αν οπισθοδρόμησε ο αλγόριθμος και προσαρμογή του path
        if (backtrack){
            path.add(parent);
            backtrack = false;
        }
        //Προσθήκη της κορυφής στη διαδρομή του αλγορίθμου.
        path.add(currentVertex);

        //Εύρεση των γειτόνων της κορυφής
        ArrayList<Integer> neighbors = getNeighbors(adj, currentVertex);
        //Έλεγχος για κύκλο
        for (int neighbor : neighbors){
            //Αν έχει ακμή με κορυφή που δεν είναι ούτε γονιός της ούτε παιδί της τότε η ακμή δημιουργεί κύκλο
            if (visited[neighbor] && neighbor != parent){
                path.indexOf(neighbor);
                cyclesList.add(new ArrayList<>(path.subList(path.indexOf(neighbor), path.size())));
            }
        }

        //Βασική αναδρομή του DFS
        for (int i = 0; i < vertices; i++) {
            if (!visited[i] && adj[currentVertex][i]){
                DFS(path, cyclesList, adj, visited, vertices, i, currentVertex, backtrack);
                backtrack = true; //swsth 8esh toy backtrack??
            }
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

}
