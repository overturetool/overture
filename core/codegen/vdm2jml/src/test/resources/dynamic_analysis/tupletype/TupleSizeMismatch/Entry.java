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
            Tuple ignorePattern_1 = ((Tuple) Tup4());

            //@ assert (V2J.isTup(ignorePattern_1,4) && Utils.is_nat(V2J.field(ignorePattern_1,0)) && Utils.is_nat(V2J.field(ignorePattern_1,1)) && Utils.is_char(V2J.field(ignorePattern_1,2)) && Utils.is_bool(V2J.field(ignorePattern_1,3)));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            Tuple ignorePattern_2 = ((Tuple) Tup3());

            //@ assert (V2J.isTup(ignorePattern_2,4) && Utils.is_nat(V2J.field(ignorePattern_2,0)) && Utils.is_nat(V2J.field(ignorePattern_2,1)) && Utils.is_char(V2J.field(ignorePattern_2,2)) && Utils.is_bool(V2J.field(ignorePattern_2,3)));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static Object Tup3() {
        Object ret_1 = Tuple.mk_(1L, 'a', true);

        //@ assert ((V2J.isTup(ret_1,3) && Utils.is_nat(V2J.field(ret_1,0)) && Utils.is_char(V2J.field(ret_1,1)) && Utils.is_bool(V2J.field(ret_1,2))) || (V2J.isTup(ret_1,4) && Utils.is_nat(V2J.field(ret_1,0)) && Utils.is_nat(V2J.field(ret_1,1)) && Utils.is_char(V2J.field(ret_1,2)) && Utils.is_bool(V2J.field(ret_1,3))));
        return ret_1;
    }

    /*@ pure @*/
    public static Object Tup4() {
        Object ret_2 = Tuple.mk_(1L, 2L, 'b', false);

        //@ assert ((V2J.isTup(ret_2,3) && Utils.is_nat(V2J.field(ret_2,0)) && Utils.is_char(V2J.field(ret_2,1)) && Utils.is_bool(V2J.field(ret_2,2))) || (V2J.isTup(ret_2,4) && Utils.is_nat(V2J.field(ret_2,0)) && Utils.is_nat(V2J.field(ret_2,1)) && Utils.is_char(V2J.field(ret_2,2)) && Utils.is_bool(V2J.field(ret_2,3))));
        return ret_2;
    }

    public String toString() {
        return "Entry{}";
    }
}
