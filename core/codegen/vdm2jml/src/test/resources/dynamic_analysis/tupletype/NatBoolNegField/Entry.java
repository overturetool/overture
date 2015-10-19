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
            Tuple ignorePattern_1 = Tuple.mk_(1L, true);

            //@ assert (V2J.isTup(ignorePattern_1,2) && Utils.is_nat(V2J.field(ignorePattern_1,0)) && Utils.is_bool(V2J.field(ignorePattern_1,1)));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            Tuple ignorePattern_2 = Tuple.mk_(negInt(), true);

            //@ assert (V2J.isTup(ignorePattern_2,2) && Utils.is_nat(V2J.field(ignorePattern_2,0)) && Utils.is_bool(V2J.field(ignorePattern_2,1)));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static Number negInt() {
        Number ret_1 = -1L;

        //@ assert Utils.is_int(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
