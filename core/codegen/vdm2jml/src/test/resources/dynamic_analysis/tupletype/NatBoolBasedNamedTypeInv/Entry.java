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
            Tuple ignorePattern_1 = Tuple.mk_(1001L, true);

            //@ assert ((V2J.isTup(ignorePattern_1,2) && Utils.is_nat(V2J.field(ignorePattern_1,0)) && Utils.is_bool(V2J.field(ignorePattern_1,1))) && inv_Entry_TrueEven(ignorePattern_1));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal uses");

        {
            Tuple ignorePattern_2 = Tuple.mk_(1000L, true);

            //@ assert ((V2J.isTup(ignorePattern_2,2) && Utils.is_nat(V2J.field(ignorePattern_2,0)) && Utils.is_bool(V2J.field(ignorePattern_2,1))) && inv_Entry_TrueEven(ignorePattern_2));

            /* skip */
        }

        {
            Tuple ignorePattern_3 = Tuple.mk_(1001L, false);

            //@ assert ((V2J.isTup(ignorePattern_3,2) && Utils.is_nat(V2J.field(ignorePattern_3,0)) && Utils.is_bool(V2J.field(ignorePattern_3,1))) && inv_Entry_TrueEven(ignorePattern_3));

            /* skip */
        }

        IO.println("After illegal uses");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_TrueEven(final Object check_te) {
        Tuple te = ((Tuple) check_te);

        Boolean andResult_1 = false;

        if (((Number) te.get(0)).longValue() > 1000L) {
            if (((Boolean) te.get(1))) {
                andResult_1 = true;
            }
        }

        return andResult_1;
    }
}
