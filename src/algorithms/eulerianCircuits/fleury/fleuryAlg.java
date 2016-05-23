package algorithms.eulerianCircuits.fleury;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static java.lang.Byte.parseByte;

/**
 * Created by Nick Panagopoulos on 23/5/2016.
 */
public class fleuryAlg {
    public static void main(String[] args) {

        boolean[][] adj;
        int vertices;

        //DATA INPUT
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("inputEul.txt"))){
            vertices = Character.getNumericValue((char) bufferedInputStream.read());

            adj = new boolean[vertices][vertices];

            byte newLine = (byte) '\n';

            int i = 0;
            int j = 0;
            char readChar;

            while(i < vertices){
                readChar = ((char) bufferedInputStream.read());
                if (readChar != '0' && readChar != '1'){
                    continue;
                }
                else {
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


    }
}
