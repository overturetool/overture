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
        {
            Number ignorePattern_1 = f();

            //@ assert ((ignorePattern_1 == null) || Utils.is_nat(ignorePattern_1));

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    /*@ pure @*/
    public static Number f() {
        Object letBeStExp_1 = null;
        Object nullPattern_1 = null;

        Boolean success_1 = false;

        //@ assert Utils.is_bool(success_1);
        VDMSet set_1 = SetUtil.set(null);

        //@ assert (V2J.isSet(set_1) && (\forall int i; 0 <= i && i < V2J.size(set_1); true));
        for (Iterator iterator_1 = set_1.iterator();
                iterator_1.hasNext() && !(success_1);) {
            nullPattern_1 = ((Object) iterator_1.next());
            success_1 = Utils.equals(nullPattern_1, null);

            //@ assert Utils.is_bool(success_1);
            if (!(success_1)) {
                continue;
            }

            success_1 = true;

            //@ assert Utils.is_bool(success_1);
        }

        if (!(success_1)) {
            throw new RuntimeException("Let Be St found no applicable bindings");
        }

        letBeStExp_1 = null;

        Number ret_1 = ((Number) letBeStExp_1);

        //@ assert ((ret_1 == null) || Utils.is_nat(ret_1));
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
