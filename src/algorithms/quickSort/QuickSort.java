package algorithms.quickSort;

import java.util.ArrayList;

/**
 * Created by Nick Panagopoulos on 21/5/2016.
 *
 * Recursive quicksort algorithm for descending sorting of an ArrayList containing Integers.
 * TODO: Make generic
 */
public class QuickSort{
    private QuickSort(){}
    
    public static ArrayList<Integer> runQuickSort(ArrayList<Integer> arrayList){
        if (arrayList.size() <= 1){
            return arrayList;
        }

        int mid = (int) Math.ceil((double) arrayList.size() / 2);

        int pivot = arrayList.get(mid);

        ArrayList<Integer> small = new ArrayList<>();
        ArrayList<Integer> big = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i) >= pivot){
                if (i!=mid){
                    small.add(arrayList.get(i));
                }
            }
            else{
                big.add(arrayList.get(i));
            }
        }

        return join(runQuickSort(small), pivot, runQuickSort(big));
    }
    
    private static ArrayList<Integer> join(ArrayList<Integer> small,int pivot,ArrayList<Integer> big){
        ArrayList<Integer> newArrayList = new ArrayList<>();
        for (int small1 : small){
            newArrayList.add(small1);
        }
        newArrayList.add(pivot);
        for (int big1 : big){
            newArrayList.add(big1);
        }
        return newArrayList;
    }
}
