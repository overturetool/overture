package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        IO.println("Before legal use");

        {
            VDMSeq ignorePattern_1 = SeqUtil.seq(1L, true, 2L, false, 3L);

            //@ assert ((V2J.isSeq(ignorePattern_1) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_1); (Utils.is_bool(V2J.get(ignorePattern_1,i)) || Utils.is_nat1(V2J.get(ignorePattern_1,i))))) && inv_Entry_SeqNat1Bool(ignorePattern_1));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            VDMSeq ignorePattern_2 = SeqUtil.seq(1L, true, 2L, false, minusOne());

            //@ assert ((V2J.isSeq(ignorePattern_2) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_2); (Utils.is_bool(V2J.get(ignorePattern_2,i)) || Utils.is_nat1(V2J.get(ignorePattern_2,i))))) && inv_Entry_SeqNat1Bool(ignorePattern_2));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static Number minusOne() {
        Number ret_1 = -1L;

        //@ assert Utils.is_int(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_SeqNat1Bool(final Object check_elem) {
        return true;
    }
}
