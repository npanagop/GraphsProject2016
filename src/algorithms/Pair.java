package algorithms;

import java.util.Objects;

/**
 * Νίκος Παναγόπουλος 2393 npanagop@csd.auth.gr
 *
 * Κλάση που υλοποιεί ένα απλό ζευγάρι τιμών.
 * Δημιουργήθηκε για βοηθητική χρήση στην υλοποίηση των αλγορίθμων.
 */
public class Pair<X, Y> extends Object{

    public final X first;
    public final Y second;

    public Pair(X first, Y second){
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString(){
        return "(" + first + "," + second + ")";
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Pair)){
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) o;
        return Objects.equals(first, p.first) && Objects.equals(second, p.second);
    }
}
