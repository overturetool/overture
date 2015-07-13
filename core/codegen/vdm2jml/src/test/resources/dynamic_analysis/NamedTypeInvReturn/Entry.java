package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    private Entry() {
    }

    public static Object Run() {
        Character ignorePattern_1 = idC('b');

        //@ assert inv_Entry_C(ignorePattern_1);
        Character ignorePattern_2 = idC('a');

        //@ assert inv_Entry_C(ignorePattern_2);
        Object ignorePattern_3 = idA(null);

        //@ assert inv_Entry_A(ignorePattern_3) && (ignorePattern_3 == null || inv_Entry_B(ignorePattern_3) || inv_Entry_C(ignorePattern_3));
        Object ignorePattern_4 = idA(2.1);

        //@ assert inv_Entry_A(ignorePattern_4) && (ignorePattern_4 == null || inv_Entry_B(ignorePattern_4) || inv_Entry_C(ignorePattern_4));
        Object ignorePattern_5 = constFunc();
        //@ assert inv_Entry_A(ignorePattern_5) && (ignorePattern_5 == null || inv_Entry_B(ignorePattern_5) || inv_Entry_C(ignorePattern_5));
        {
            IO.println("Breaking named type invariant for return value");

            {
                Object ignorePattern_6 = idA('b');

                //@ assert inv_Entry_A(ignorePattern_6) && (ignorePattern_6 == null || inv_Entry_B(ignorePattern_6) || inv_Entry_C(ignorePattern_6));

                //Skip;
            }

            return 0L;
        }
    }

    /*@ pure @*/
    public static Character idC(final Character c) {
        //@ assert inv_Entry_C(c);
        return c;
    }

    /*@ pure @*/
    public static Object idA(final Object a) {
        //@ assert inv_Entry_A(a) && (a == null || inv_Entry_B(a) || inv_Entry_C(a));
        return a;
    }

    /*@ pure @*/
    public static Object constFunc() {
        Object ret_2 = 'a';

        //@ assert inv_Entry_A(ret_2) && (ret_2 == null || inv_Entry_B(ret_2) || inv_Entry_C(ret_2));
        return ret_2;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_A(final Object check_c) {
        if (!(Utils.equals(check_c, null)) &&
                !(Utils.is_real(check_c) || Utils.is_char(check_c))) {
            return false;
        }

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
        if ((Utils.equals(check_elem, null)) || !(Utils.is_real(check_elem))) {
            return false;
        }

        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_C(final Object check_c) {
        if ((Utils.equals(check_c, null)) || !(Utils.is_char(check_c))) {
            return false;
        }

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
