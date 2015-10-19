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
            Tuple ignorePattern_2 = TupNil();

            //@ assert (V2J.isTup(ignorePattern_2,2) && Utils.is_nat(V2J.field(ignorePattern_2,0)) && Utils.is_bool(V2J.field(ignorePattern_2,1)));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static Tuple TupNil() {
        Tuple ret_1 = null;

        //@ assert ((ret_1 == null) || (V2J.isTup(ret_1,2) && Utils.is_nat1(V2J.field(ret_1,0)) && Utils.is_bool(V2J.field(ret_1,1))));
        return Utils.copy(ret_1);
    }

    /*@ pure @*/
    public static Tuple TupVal() {
        Tuple ret_2 = Tuple.mk_(1L, false);

        //@ assert ((ret_2 == null) || (V2J.isTup(ret_2,2) && Utils.is_nat1(V2J.field(ret_2,0)) && Utils.is_bool(V2J.field(ret_2,1))));
        return Utils.copy(ret_2);
    }

    public String toString() {
        return "Entry{}";
    }
}
