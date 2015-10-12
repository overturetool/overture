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
        Character ignorePattern_1 = idC('b');

        //@ assert (Utils.is_char(ignorePattern_1) && inv_Entry_C(ignorePattern_1));
        Character ignorePattern_2 = idC('a');

        //@ assert (Utils.is_char(ignorePattern_2) && inv_Entry_C(ignorePattern_2));
        Object ignorePattern_3 = idA(null);

        //@ assert ((ignorePattern_3 == null) || ((ignorePattern_3 == null) || (Utils.is_real(ignorePattern_3) && inv_Entry_B(ignorePattern_3)) || (Utils.is_char(ignorePattern_3) && inv_Entry_C(ignorePattern_3))) && inv_Entry_A(ignorePattern_3));
        Object ignorePattern_4 = idA(2.1);

        //@ assert ((ignorePattern_4 == null) || ((ignorePattern_4 == null) || (Utils.is_real(ignorePattern_4) && inv_Entry_B(ignorePattern_4)) || (Utils.is_char(ignorePattern_4) && inv_Entry_C(ignorePattern_4))) && inv_Entry_A(ignorePattern_4));
        Object ignorePattern_5 = constFunc();
        //@ assert ((ignorePattern_5 == null) || ((ignorePattern_5 == null) || (Utils.is_real(ignorePattern_5) && inv_Entry_B(ignorePattern_5)) || (Utils.is_char(ignorePattern_5) && inv_Entry_C(ignorePattern_5))) && inv_Entry_A(ignorePattern_5));
        {
            IO.println("Breaking named type invariant for return value");

            {
                Object ignorePattern_6 = idA('b');

                //@ assert ((ignorePattern_6 == null) || ((ignorePattern_6 == null) || (Utils.is_real(ignorePattern_6) && inv_Entry_B(ignorePattern_6)) || (Utils.is_char(ignorePattern_6) && inv_Entry_C(ignorePattern_6))) && inv_Entry_A(ignorePattern_6));

                /* skip */
            }

            return 0L;
        }
    }

    /*@ pure @*/
    public static Character idC(final Character c) {
        //@ assert (Utils.is_char(c) && inv_Entry_C(c));
        Character ret_1 = c;

        //@ assert (Utils.is_char(ret_1) && inv_Entry_C(ret_1));
        return ret_1;
    }

    /*@ pure @*/
    public static Object idA(final Object a) {
        //@ assert ((a == null) || ((a == null) || (Utils.is_real(a) && inv_Entry_B(a)) || (Utils.is_char(a) && inv_Entry_C(a))) && inv_Entry_A(a));
        Object ret_2 = a;

        //@ assert ((ret_2 == null) || ((ret_2 == null) || (Utils.is_real(ret_2) && inv_Entry_B(ret_2)) || (Utils.is_char(ret_2) && inv_Entry_C(ret_2))) && inv_Entry_A(ret_2));
        return ret_2;
    }

    /*@ pure @*/
    public static Object constFunc() {
        Object ret_3 = 'a';

        //@ assert ((ret_3 == null) || ((ret_3 == null) || (Utils.is_real(ret_3) && inv_Entry_B(ret_3)) || (Utils.is_char(ret_3) && inv_Entry_C(ret_3))) && inv_Entry_A(ret_3));
        return ret_3;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_A(final Object check_c) {
        Object c = ((Object) check_c);

        Boolean orResult_1 = false;

        if (!(Utils.is_char(c))) {
            orResult_1 = true;
        } else {
            orResult_1 = Utils.equals(c, 'a');
        }

        return orResult_1;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_B(final Object check_elem) {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_C(final Object check_c) {
        Character c = ((Character) check_c);

        Boolean orResult_2 = false;

        if (Utils.equals(c, 'a')) {
            orResult_2 = true;
        } else {
            orResult_2 = Utils.equals(c, 'b');
        }

        return orResult_2;
    }
}
