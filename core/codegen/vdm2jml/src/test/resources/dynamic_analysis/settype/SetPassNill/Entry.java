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
            VDMSet r = idSet(SetUtil.set(true, false));

            //@ assert (V2J.isSet(r) && (\forall int i; 0 <= i && i < V2J.size(r); Utils.is_bool(V2J.get(r,i))));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            VDMSet xs = null;

            //@ assert ((xs == null) || (V2J.isSet(xs) && (\forall int i; 0 <= i && i < V2J.size(xs); Utils.is_bool(V2J.get(xs,i)))));
            VDMSet r = idSet(Utils.copy(xs));

            //@ assert (V2J.isSet(r) && (\forall int i; 0 <= i && i < V2J.size(r); Utils.is_bool(V2J.get(r,i))));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static VDMSet idSet(final VDMSet xs) {
        //@ assert (V2J.isSet(xs) && (\forall int i; 0 <= i && i < V2J.size(xs); Utils.is_bool(V2J.get(xs,i))));
        VDMSet ret_1 = Utils.copy(xs);

        //@ assert (V2J.isSet(ret_1) && (\forall int i; 0 <= i && i < V2J.size(ret_1); Utils.is_bool(V2J.get(ret_1,i))));
        return Utils.copy(ret_1);
    }

    public String toString() {
        return "Entry{}";
    }
}
