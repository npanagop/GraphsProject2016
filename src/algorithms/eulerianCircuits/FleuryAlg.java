package algorithms.eulerianCircuits;

import algorithms.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Νίκος Παναγόπουλος 2393 npanagop@csd.auth.gr
 *
 * Υλοποίηση του αλγορίθμου Fleury για την εύρεση Eulerian κυκλωμάτων σε έναν συνδεδμένο γράφο με κορυφές άρτιου βαθμού.
 *
 * Τα δεδομένα βρίσκονται στο αρχείο inputEul.txt σε μορφή πίνακα γειτνίασης.
 *
 * Το αποτέλεσμα αποθηκεύεται στο αρχείο outputEulFleury.txt με την μορφή "0 1 2 3... 0" που δείχνει το κύκλωμα που
 * βρήκε ο αλγόριθμος.
 */
public class FleuryAlg {
    public static void main(String[] args) {

        //Είσοδος δεδομένων
        boolean[][] adj; //πίνακας γειτνίασης
        int vertices;    //πλήθος κορυφών
        int edges;       //πλήθος ακμών

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("inputEul.txt"))){
            vertices = Character.getNumericValue((char) bufferedInputStream.read());
            bufferedInputStream.read();
            edges = Character.getNumericValue((char) bufferedInputStream.read());

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

        //κύκλωμα που θα βρεθεί
        Stack<Integer> eulerianCircuit = new Stack<>();

        int currentVertex = 0;
        int steps = 0;

        //πίνακας που περιέχει τις ακμές που είναι γέφυρες του γράφου
        ArrayList<Pair<Integer, Integer>> bridges;
        //πίνακας που περιέχει δείκτες θέσης για τον προηγούμενο πίνακα, για κάθε ακμή που περιλαμβάνει την
        //κορυφή στην οποία βρίσκεται ο αλγόριθμος
        ArrayList<Integer> indexCurrentBridges;

        //πίνακας με τις γειτονικές κορυφές της κορυφής που βρίσκεται ο αλγόριθμος
        ArrayList<Integer> neighbors;

        //Αλγόριθμος Fleury
        while (steps<=edges) { //μέχρι να περάσεις κάθε ακμή

            neighbors = new ArrayList<>();
            indexCurrentBridges = new ArrayList<>();
            //η επόμενη κορυφή που θα πάει ο αλγόριθμος
            int nextVertex = -1;

            //βάλε αυτή τη κορυφή στο κύκλωμα
            eulerianCircuit.add(currentVertex);

            //βρες τις γέφυρες του γράφου
            bridges = findBridges(adj);
            boolean currentVertexIsOnBridge = false;
            for (Pair<Integer, Integer> bridge : bridges){
                if (bridge.first == currentVertex || bridge.second == currentVertex){
                    indexCurrentBridges.add(bridges.indexOf(bridge));
                    currentVertexIsOnBridge = true;
                }
            }

            //βρες τους γείτονες της κορυφής
            for (int i = 0; i < vertices; i++) {
                if (adj[currentVertex][i]) {
                    neighbors.add(i);
                }
            }

            //αν είναι η τελευταία κορυφή βγες από το while
            if (neighbors.isEmpty()){
                break;
            }

            //αν η κορυφή έχει περισσότερους από έναν γείτονες και βρίσκεται πάνω σε γέφυρα
            if (neighbors.size() > 1 && currentVertexIsOnBridge) {
                for (int i : neighbors) {
                    for (int j : indexCurrentBridges) {
                        //διάλεξε για επόμενη κορυφή αυτή που δέν βρίσκεται σε γέφυρα
                        if (bridges.get(j).first != i && bridges.get(j).second != i){
                            nextVertex = i;
                            break;
                        }
                    }
                }
            }
            else{
                nextVertex = neighbors.get(0);
            }

            //σβήσε την ακμή των δύο κορυφών
            adj[currentVertex][nextVertex] = false;
            adj[nextVertex][currentVertex] = false;

            //πάνε στον επόμενο γείτονα
            currentVertex = nextVertex;

            steps++;
        }

        //Αποθήκευση αποτελέσματος
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputEulFleury.txt"), "utf-8"))){
            for (int i : eulerianCircuit) {
                writer.write(i+" ");
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Συνάρτηση που χρησιμοποιεί DFS για να βρεί γέφυρες σε έναν γράφο.
     * @param adj Πίνακας γειτνίασης του γράφου.
     * @return Πίνακας που περιέχει τις ακμές που είναι γέφυρες του γράφου.
     */
    private static ArrayList<Pair<Integer, Integer>> findBridges(boolean[][] adj){
        //Αρχικοποίηση
        int vertices = adj.length;
        //αρχικοποίηση "χρόνου" για τον υπολογισμό της σειράς προσπέλασης κορυφών
        int time = 0;
        boolean visited[] = new boolean[vertices];
        int disc[] = new int[vertices];
        int low[] = new int[vertices];
        int parent[] = new int[vertices];

        for (int i = 0; i < vertices; i++) {
            parent[i] = -1;
            visited[i] = false;
        }
        ArrayList<Pair<Integer, Integer>> bridges = new ArrayList<>();
        //Εκτέλεση DFS
        for (int i = 0; i < vertices; i++) {
            if (!visited[i]){
                bridgeDFS(adj, bridges, i, visited, disc, low, parent, time);
            }
        }
        return bridges;
    }

    /**
     * Αναδρομική συνάρτηση που χρησιμοποιεί DFS για να βρεί γέφυρες σε έναν γράφο.
     * Βασίζεται στο γεγονός ότι μια ακμή (u,v), όπου u πρόγονος του v, είναι γέφυρα αν δεν υπάρχει τρόπος να πάμε στο u
     * ή πρόγονο του u από έναν υποδένδρο με ρίζα το v.
     * @param adj Πίνακας γειτνίασης του γράφου.
     * @param bridges Πίνακας που περιέχει τις ακμές που είναι γέφυρες του γράφου.
     * @param u Η επόμενη κορυφή που θα διασχίσει ο αλγόριθμος.
     * @param visited Πίνακας με τις κορυφές που έχει ηδη επισκεφτεί ο αλγόριθμος.
     * @param disc Η χρόνοι επίσκεψεις για κάθε κορυφή που έχει επισκεφτεί ο αλγόριθμος.
     * @param low Πίνακας με την πιο παλιά κορυφή που μπορούμε να φτάσουμε από το υποδένδρο της κορυφής που αντιστοιχεί
     *            στο δείκτη του πίνακα.
     * @param parent Πίνακας με τις κορυφές προγόνους.
     * @param time Ο "χρόνος" του αλγορίθμου.
     */
    private static void bridgeDFS(boolean[][] adj, ArrayList<Pair<Integer, Integer>> bridges,int u,
                                  boolean visited[], int disc[], int low[], int parent[], int time){

        visited[u] = true;
        disc[u] = low[u] = ++time;

        //Για κάθε γείτονα της u
        for (int i = 0; i < adj.length; i++) {
            if (adj[u][i]){
                if (!visited[i]){

                    parent[i] = u;
                    bridgeDFS(adj, bridges, i, visited, disc, low, parent, time);

                    low[u] = Math.min(low[u], low[i]);

                    //Αν η πιο παλιά κορυφή που φτάνουμε από το v είναι πιο παλιά απο τη u, τότε η ακμή u-v είναι γέφυρα
                    if (low[i] > disc[u]){
                        bridges.add(new Pair<>(u,i));
                    }
                }
                else if (i != parent[u]){
                    low[u] = Math.min(low[u], disc[i]);
                }
            }
        }
    }
}
