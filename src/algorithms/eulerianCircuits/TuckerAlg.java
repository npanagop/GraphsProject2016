package algorithms.eulerianCircuits;

import java.io.*;
import java.util.*;

/**
 * Νίκος Παναγόπουλος 2393 npanagop@csd.auth.gr
 *
 * Υλοποίηση του αλγορίθμου Tucker για την εύρεση Eulerian κυκλωμάτων σε έναν συνδεδμένο γράφο με κορυφές άρτιου βαθμού.
 *
 * Τα δεδομένα βρίσκονται στο αρχείο inputEul.txt σε μορφή πίνακα γειτνίασης.
 *
 * Στο αρχείο outputEulTucker.txt αποθηκεύεται ο νέος γράφος που δημιουργείται από την διάσπαση των κορυφών και το
 * Eulerian κύκλωμα του.
 */
public class TuckerAlg {
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
        //**************************************************

        //Διάσπαση κορυφών

        //Πίνακας που περιέχει τον βαθμό κάθε κορυφής
        int[] vertexDegree = getVertexDegree(adj, vertices);
        //Πίνακας που περιέχει τους γείτονες μίας κορυφής
        ArrayList<Integer> neighbors;

        int neighbor1, neighbor2;
        //Για κάθε κορυφή
        for (int i = 0; i < vertices; i++) {
            //Αν η κορυφή έχει βαθμό μεγαλύτερο του 2
            if (vertexDegree[i]>2){
                //Δημιουργία νέου πίνακα που να περιλαμβάνει τη νέα κορυφή που θα δημιουργηθεί
                boolean[][] newAdj = new boolean[vertices+1][vertices+1];
                for (int j = 0; j < vertices; j++) {
                    for (int k = 0; k < vertices; k++) {
                        newAdj[j][k] = adj[j][k];
                    }
                }

                //Βρες δύο γείτονες της κορυφής για να γίνει η διάσπαση
                neighbors = getNeighbors(adj, i);
                neighbor1 = neighbors.get(0);
                neighbor2 = neighbors.get(1);

                //Διέγραψε τις ακμές μεταξύ των δύο γειτόνων και της κορυφής
                newAdj[i][neighbor1] = false;
                newAdj[neighbor1][i] = false;

                newAdj[i][neighbor2] = false;
                newAdj[neighbor2][i] = false;

                //Η νέα κορυφή που δημιουργείται
                int newVertex = vertices;

                //Σύνδεση των γειτόνων με την νέα κορυφή
                newAdj[newVertex][neighbor1] = true;
                newAdj[neighbor1][newVertex] = true;

                newAdj[newVertex][neighbor2] = true;
                newAdj[neighbor2][newVertex] = true;

                //Αντικατάσταση του παλιού πίνακα γειτνίασης με τον καινούργιο
                adj = newAdj;
                vertices++;
                //Ενημέρωσει πίνακα των βαθμών των κορυφών για να περιλαμβάνει και τη νέα κορυφή
                vertexDegree = getVertexDegree(adj, vertices);
            }
        }

        //Εύρεση συνιστωσών του γράφου

        //Πίνακας που δείχνει ποιές κορυφές έχει επισκεφτεί ο αλγόριθμος DFS
        boolean visited[] = new boolean[vertices];
        for (int i = 0; i < vertices; i++) {
            visited[i] = false;
        }

        //Λίστα με τις συνιστώσες που βρέθηκαν
        ArrayList<ArrayList<Integer>> components = new ArrayList<>();
        //Αρχικοποίηση πρώτης συνιστώσας, αφού θα βρεθεί τουλάχιστον μία
        components.add(new ArrayList<>());
        //Εκτέλεση Depth-First-Search για να βρεθούν οι κύκλοι του γράφου
        DFS(components, 0,adj, visited, vertices, 0);

        //Ένωση συνιστωσών

        //Όσο υπάρχουν πολλές συνιστώσες
        while(components.size() > 1){

            //Η μία κορυφή της κοινής ακμής δύο συνιστωσ'ων
            int commonVertex = -1;
            //αν είναι η ίδια συνιστώσα
            boolean areSame = true;
            //δείκτες θέσεις των συνιστώσων στο πίνακα με συνιστώσες
            int indexComponent1 = -1;
            int indexComponent2 = -1;
            //δείκτης θέσης της άλλης κορυφής της κοινής ακμής δύο συνιστωσών
            int indexCommonVertex2 = -1;
            //αν μια κορυφή μιας συνιστώσας έχει γείτονα μια κορυφή μιάς άλλης συνιστώσας
            boolean containsNeighbor = false;

            //για κάθε συνιστώσα
            for (ArrayList<Integer> component1 : components){
                indexComponent1 = components.indexOf(component1);
                //συγκρινέ την με όλες τις άλλες
                for (ArrayList<Integer> component2 : components){
                    indexComponent2 = components.indexOf(component2);
                    //προσπέρασε την ίδια συνιστώσα
                    if (components.indexOf(component1) == indexComponent2){
                        areSame = false;
                        continue;
                    }
                    //για κάθε κορυφή της συνιστώσας
                    for (Integer vertex1 : component1){
                        //βρες τους γείτονες της
                        neighbors = getNeighbors(adj, vertex1);
                        for (Integer vertex2 : component2){
                            for (int tempVertex : neighbors){
                                if (tempVertex == vertex2){
                                    containsNeighbor = true;
                                    break;
                                }
                            }
                            //αν μια κορυφή μιας συνιστώσας έχει γείτονα μια κορυφή μιάς άλλης συνιστώσας
                            if (containsNeighbor){
                                commonVertex = vertex1;
                                indexCommonVertex2 = component2.indexOf(vertex2);
                                containsNeighbor = false;
                            }
                            else{
                                areSame = false;
                            }
                        }
                    }
                    //αν είναι η ίδια συνιστώσα σε διαφορετικό πίνακα
                    if (areSame){
                        components.remove(indexComponent2);
                        break;
                    }
                    else if (commonVertex != -1){
                        //αν η κοινή μεταβλητή βρίσκεται στην αρχή της συνιστώσας
                        if (component1.indexOf(commonVertex) == 0) {
                            //αναποδογύρισμα της πρώτης συνιστώσας για να ενωθεί σωστά με την δεύτερη
                            ArrayList<Integer> tempComponent = new ArrayList<>();
                            for (int i = component1.size() - 1; i >= 0; i--) {
                                tempComponent.add(component1.get(i));
                            }
                            component1 = tempComponent;
                        }

                        int counter = 0;
                        int tempSize = component1.size();
                        //προσθήκη των κορυφών της δεύτερης συνιστώσας στην πρώτη
                        for (int i = indexCommonVertex2; i <= component2.size()-1; i++) {
                            component1.add(tempSize+counter, component2.get(i));
                            counter++;
                        }
                        for (int i = 0; i < indexCommonVertex2; i++) {
                            component1.add(tempSize + counter, component2.get(i));
                        }
                        components.set(indexComponent1, component1);
                        //αφαίρεση της δεύτερης συνιστώσας από τον πίνακα συνιστώσων
                        components.remove(indexComponent2);
                        break;
                    }
                }
                //αν ενώθηκαν συνιστώσες μπες στην επανάληψη από την αρχή
                if (areSame || commonVertex != -1){
                    break;
                }
            }
        }

        //Αποθήκευση αποτελέσματος
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputEulTucker.txt"), "utf-8"))){
            writer.write(vertices+" "+edges+"\n");
            for (int i = 0; i < vertices; i++) {
                for (int j = 0; j < vertices; j++) {
                    if (adj[i][j]){
                        writer.write("1 ");
                    }
                    else{
                        writer.write("0 ");
                    }
                }
                writer.write("\n");
            }
            writer.write("\nEulerian κύκλωμα: ");
            for (int vertex : components.get(0)){
                writer.write(vertex+" ");
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }


    }

    /**
     * Υλοποίηση του αλγορίθμου DFS, τροποποιημένος ώστε να βρίσκει τις συνιστώσες ενός γράφου.
     * @param components Πίνακας που περιέχει τις συνιστώσες που βρέθηκαν.
     * @param componentsCounter Μετρητής για τον πίνακα συνιστωσών.
     * @param adj Πίνακας γειτνίασης του γράφου.
     * @param visited Πίνακας με τις κορυφές που έχει ήδη επισκεφτεί ο αλγόριθμος.
     * @param vertices Το πλήθος των κορυφών του γράφου.
     * @param currentVertex Η κορυφή στην οποία βρίσκεται ο αλγόριθμος.
     */
    private static void DFS(ArrayList<ArrayList<Integer>> components, int componentsCounter,
                            boolean[][] adj, boolean[] visited, int vertices,int currentVertex){
        //Σημείωσε ότι επισκέφτηκες αυτή τη κορυφή
        visited[currentVertex] = true;
        //Αν βρέθηκε και νέα συνιστώσα τότε πρόσθεσε πίνακα στον πίνακα συνιστωσών
        if (components.size() == componentsCounter){
            components.add(new ArrayList<>());
        }
        components.get(componentsCounter).add(currentVertex);

        //Βασική αναδρομή του DFS
        for (int i = 0; i < vertices; i++) {
            if (!visited[i] && adj[currentVertex][i]){
                DFS(components, componentsCounter, adj, visited, vertices, i);
                componentsCounter++;
            }
        }
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
