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
            VDMSet ignorePattern_1 = SetUtil.set(2L, 4L, 6L);

            //@ assert (V2J.isSet(ignorePattern_1) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_1); Utils.is_nat(V2J.get(ignorePattern_1,i))));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            VDMSet ignorePattern_2 = setOfNat();

            //@ assert (V2J.isSet(ignorePattern_2) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_2); Utils.is_nat(V2J.get(ignorePattern_2,i))));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static VDMSet setOfNat() {
        VDMSet ret_1 = null;

        //@ assert ((ret_1 == null) || (V2J.isSet(ret_1) && (\forall int i; 0 <= i && i < V2J.size(ret_1); Utils.is_nat(V2J.get(ret_1,i)))));
        return Utils.copy(ret_1);
    }

    public String toString() {
        return "Entry{}";
    }
}
