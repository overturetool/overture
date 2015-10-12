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
        Number n1 = 2L;

        //@ assert Utils.is_nat1(n1);
        Number n2 = 3L;
        //@ assert Utils.is_nat1(n2);
        {
            {
                Number ignorePattern_1 = op(n1, 5L, n1);

                //@ assert (Utils.is_nat(ignorePattern_1) && inv_Entry_Even(ignorePattern_1));

                /* skip */
            }

            IO.println("Breaking named type invariant for method parameter");

            {
                Number ignorePattern_2 = op(n1, 6L, n2);

                //@ assert (Utils.is_nat(ignorePattern_2) && inv_Entry_Even(ignorePattern_2));

                /* skip */
            }

            return 0L;
        }
    }

    public static Number op(final Number a, final Number b, final Number c) {
        //@ assert (Utils.is_nat(a) && inv_Entry_Even(a));

        //@ assert Utils.is_nat(b);

        //@ assert (Utils.is_nat(c) && inv_Entry_Even(c));
        Number ret_1 = b.longValue() * (a.longValue() + c.longValue());

        //@ assert (Utils.is_nat(ret_1) && inv_Entry_Even(ret_1));
        return ret_1;
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
