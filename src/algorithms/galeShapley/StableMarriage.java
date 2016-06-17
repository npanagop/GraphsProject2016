package algorithms.galeShapley;


import java.io.*;
import java.util.ArrayList;

/**
 * Νίκος Παναγόπουλος 2393 npanagop@csd.auth.gr
 *
 * Υλοποίηση του αλγορίθμου Gale-Shapley για το πρόβλημα των σταθερών γάμων.
 *
 * Τα δεδομένα βρίσκονται στο αρχείο inputStableMarriage.txt. Το πρώτο στοιχείο δείχνει πόσα ζευγάρια υπάρχουν. Στη συνέχεια
 * υπάρχουν σειρές της μορφής "άντρας γυναίκα προτίμηση" και όταν τελειώσουν οι άντρες, της μορφής "γυναίκα άντρας
 * προτίμηση". Το κάθε άτομο ταυτοποιείται με έναν αριθμό 0..Ν-1 , όπου Ν το πλήθος των ζευγαριών. Οι προτιμήσεις
 * εκφράζονται με τον ίδιο τρόπο, με το μεγαλύτερο νούμερο να εκφράζει μεγαλύτερη προτίμηση.
 *
 * Ο αλγόριθμος υποθέτει ότι οι άντρες κάνουν τις προτάσεις γάμου.
 *
 * Το αποτέλεσμα αποθηκεύεται στο αρχείο outputStableMarriage.txt με την μορφή "άντρας γυναίκα" που δείχνει το ζευγάρι.
 */
public class StableMarriage {
    public static void main(String[] args) {
        //Είσοδος δεδομένων

        ArrayList<ArrayList<Integer>> menPreferences = new ArrayList<>();
        ArrayList<ArrayList<Integer>> womenPreferences = new ArrayList<>();

        int pairs; //το πλήθος τον ζευγαριών

        //Ανάγνωση από το αρχείο
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("inputStableMarriage.txt"))){
            pairs = Integer.valueOf(bufferedReader.readLine().trim());
            int separate = pairs * pairs;
            //αρχικοποίηση πινάκων
            for (int i = 0; i < pairs; i++) {
                menPreferences.add(new ArrayList<>(pairs));
                womenPreferences.add(new ArrayList<>(pairs));

                for (int j = 0; j < pairs; j++) {
                    menPreferences.get(i).add(-1);
                    womenPreferences.get(i).add(-1);
                }
            }

            String[] lineSplit;
            int counter = 0;
            int personA, personB, preference;
            while(counter < 2*separate){
                lineSplit = bufferedReader.readLine().trim().split(" ");
                personA = Integer.valueOf(lineSplit[0]);
                personB = Integer.valueOf(lineSplit[1]);
                preference = Integer.valueOf(lineSplit[2]);
                if (counter<separate){ //αν τα δεδομένα αφορούν τους άντρες
                    menPreferences.get(personA).set(preference-1, personB);
                }
                else{
                    womenPreferences.get(personA).set(preference-1, personB);
                }
                counter++;
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
            return;
        }
        //**************************************************

        //Αρχικοποίηση δομών

        //πίνακας με τα ζευγάρια που παντρεύονται
        int[][] marriages = new int[pairs][2];
        int marriageCounter = 0;

        //true = έχει ζευγάρι
        ArrayList<Boolean> menMarried = new ArrayList<>();
        ArrayList<Boolean> womenMarried = new ArrayList<>();

        //μετρητής μόνο για τους άντρες αφού αυτοί κάνουν προτάσεις
        ArrayList<Integer> menPrefCounter = new ArrayList<>();

        for (int i = 0; i < pairs; i++) {
            menMarried.add(false);
            womenMarried.add(false);

            menPrefCounter.add(0);
        }

        int man = -1;
        int manVs = -1; //ο άντρας που είναι ήδη ζευγάρι με μια γυναίκα
        int woman = -1;

        int previousMarriageIndex = -1;
        int counter = 0;

        //Αλγόριθμος Gale-Shapley

        while (menMarried.contains(false)){ //όσο υπάρχουν ελεύθεροι άντρες
            do{                             //βρες τον επόμενο ελεύθερο
                man++;
                if (man>=pairs){
                    man = 0;
                }
            }while(menMarried.get(man));

            //βρες την επόμενη γυναίκα που προτιμά ο άντρας και δεν της έχει κάνει ακόμη πρόταση
            woman = menPreferences.get(man).get(menPrefCounter.get(man));
            menPrefCounter.set(man, menPrefCounter.get(man)+1);

            //αν είναι ελέυθερη, πάντρεψε τους
            if (!womenMarried.get(woman)){
                menMarried.set(man, true);
                womenMarried.set(woman, true);
                marriages[marriageCounter][0] = man;
                marriages[marriageCounter][1] = woman;
                marriageCounter++;
            }
            else{
                //αλλιώς βρες με ποιόν είναι ήδη ζευγάρι
                for (int i = 0; i < marriageCounter; i++) {
                    if (marriages[i][1] == woman){
                        manVs = marriages[i][0];
                        previousMarriageIndex = i;
                        break;
                    }
                }
                //και βρες ποιόν προτιμάει
                while(counter < womenPreferences.get(woman).size()){
                    if (womenPreferences.get(woman).get(counter) == man){
                        //αν προτιμάει αυτόν που κάνει την πρόταση κάνε ζευγάρι με αυτόν
                        menMarried.set(man, true);
                        menMarried.set(manVs, false);
                        womenMarried.set(woman, true);
                        marriages[previousMarriageIndex][0] = man;
                        break;
                    }
                    else if (womenPreferences.get(woman).get(counter) == manVs){
                        break;
                    }
                    counter++;
                }
            }
        }

        //Αποθήκευση αποτελέσματος
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputStableMarriage.txt"), "utf-8"))){
            for (int i = 0; i < pairs; i++) {
                writer.write(marriages[i][0]+" "+marriages[i][1]+"\n");
            }
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
