package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object tNat() {
        Object ret_1 = 1L;

        //@ assert ((Utils.is_nat(ret_1) || (V2J.isSeq(ret_1) && (\forall int i; 0 <= i && i < V2J.size(ret_1); false))) && inv_Entry_T(ret_1));
        return ret_1;
    }

    public static Object tSeq() {
        Object ret_2 = SeqUtil.seq(SeqUtil.seq(SeqUtil.seq(1L)));

        //@ assert ((Utils.is_nat(ret_2) || (V2J.isSeq(ret_2) && (\forall int i; 0 <= i && i < V2J.size(ret_2); false))) && inv_Entry_T(ret_2));
        return ret_2;
    }

    public static Object t1Nat() {
        Object ret_3 = 1L;

        //@ assert (((V2J.isTup(ret_3,2) && Utils.is_nat(V2J.field(ret_3,0)) && false) || Utils.is_nat(ret_3)) && inv_Entry_T1(ret_3));
        return ret_3;
    }

    public static Object t1Tup() {
        Object ret_4 = Tuple.mk_(1L, 2L);

        //@ assert (((V2J.isTup(ret_4,2) && Utils.is_nat(V2J.field(ret_4,0)) && false) || Utils.is_nat(ret_4)) && inv_Entry_T1(ret_4));
        return ret_4;
    }

    public static Object Run() {
        IO.println("Before legal use");

        {
            Object ignorePattern_1 = tNat();

            //@ assert ((Utils.is_nat(ignorePattern_1) || (V2J.isSeq(ignorePattern_1) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_1); false))) && inv_Entry_T(ignorePattern_1));

            /* skip */
        }

        {
            Object ignorePattern_2 = t1Nat();

            //@ assert (((V2J.isTup(ignorePattern_2,2) && Utils.is_nat(V2J.field(ignorePattern_2,0)) && false) || Utils.is_nat(ignorePattern_2)) && inv_Entry_T1(ignorePattern_2));

            /* skip */
        }

        IO.println("Before legal use");
        IO.println("Before illegal use");

        {
            Object ignorePattern_3 = tSeq();

            //@ assert ((Utils.is_nat(ignorePattern_3) || (V2J.isSeq(ignorePattern_3) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_3); false))) && inv_Entry_T(ignorePattern_3));

            /* skip */
        }

        {
            Object ignorePattern_4 = t1Tup();

            //@ assert (((V2J.isTup(ignorePattern_4,2) && Utils.is_nat(V2J.field(ignorePattern_4,0)) && false) || Utils.is_nat(ignorePattern_4)) && inv_Entry_T1(ignorePattern_4));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_T(final Object check_elem) {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_T1(final Object check_elem) {
        return true;
    }
}
