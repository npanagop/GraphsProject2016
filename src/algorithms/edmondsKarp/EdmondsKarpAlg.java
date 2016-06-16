package algorithms.edmondsKarp;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Nick Panagopoulos on 16/6/2016.
 */
public class EdmondsKarpAlg {
    public static void main(String[] args) {
        //Είσοδος δεδομένων
        int[][] adjFlow; //πίνακας γειτνίασης
        int vertices;    //πλήθος κορυφών
        int s; //source
        int t; //sink

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



        int parent[] = new int[vertices];

        int maxFlow = 0;

        while (BFS(adjFlow, s, t, parent)){

            int pathFlow = Integer.MAX_VALUE;

            int u;
            for (int i = t; i != s; i=parent[i]) {
                u = parent[i];
                pathFlow = Math.min(pathFlow, adjFlow[u][i]);
            }

            for (int i = t; i != s ; i = parent[i]) {
                u = parent[i];
                adjFlow[u][i] -= pathFlow;
                adjFlow[i][u] += pathFlow;
            }
            maxFlow += pathFlow;
        }

        System.out.println(maxFlow);
        //Αποθήκευση αποτελέσματος
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputEdmondsKarp.txt"), "utf-8"))){
            writer.write(maxFlow);
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }

    }

    private static boolean BFS(int[][] adjFlow, int s, int t, int parent[]){
        int n = adjFlow.length;
        boolean visited[] = new boolean[n];
        for (int i = 0; i < n; i++) {
            visited[i] = false;
        }

        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        visited[s] = true;
        parent[s] = -1;

        while (!queue.isEmpty()){
            int currentVertex = queue.poll();

            for (int i = 0; i < n; i++) {
                if (!visited[i] && adjFlow[currentVertex][i] > 0){
                    queue.add(i);
                    parent[i] = currentVertex;
                    visited[i] = true;

                    if (i == currentVertex){
                        return true;
                    }
                }
            }
        }

        return visited[t];
    }
}
