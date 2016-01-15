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

    public static Object Run() {
        IO.println("Before legal use");

        {
            Object ignorePattern_1 = charA();

            //@ assert ((Utils.is_nat(ignorePattern_1) && inv_Entry_Even(ignorePattern_1)) || Utils.is_char(ignorePattern_1));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            Object ignorePattern_2 = charNil();

            //@ assert ((Utils.is_nat(ignorePattern_2) && inv_Entry_Even(ignorePattern_2)) || Utils.is_char(ignorePattern_2));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static Character charA() {
        Character ret_1 = 'a';

        //@ assert Utils.is_char(ret_1);
        return ret_1;
    }

    /*@ pure @*/
    public static Character charNil() {
        Character ret_2 = null;

        //@ assert ((ret_2 == null) || Utils.is_char(ret_2));
        return ret_2;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_Even(final Object check_n) {
        Number n = ((Number) check_n);

        return Utils.equals(Utils.mod(n.longValue(), 2L), 0L);
    }
}
