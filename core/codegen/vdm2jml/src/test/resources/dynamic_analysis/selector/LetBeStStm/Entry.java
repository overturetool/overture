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
            Number ignorePattern_1 = null;

            Boolean success_1 = false;

            //@ assert Utils.is_bool(success_1);
            VDMSet set_1 = SetUtil.set(1L, 2L, 3L);

            //@ assert (V2J.isSet(set_1) && (\forall int i; 0 <= i && i < V2J.size(set_1); Utils.is_nat1(V2J.get(set_1,i))));
            for (Iterator iterator_1 = set_1.iterator();
                    iterator_1.hasNext() && !(success_1);) {
                ignorePattern_1 = ((Number) iterator_1.next());
                success_1 = true;

                //@ assert Utils.is_bool(success_1);
            }

            if (!(success_1)) {
                throw new RuntimeException(
                    "Let Be St found no applicable bindings");
            }

            /* skip */
        }

        {
            Number x = null;

            Boolean success_2 = false;

            //@ assert Utils.is_bool(success_2);
            VDMSet set_2 = SetUtil.set(1L, 2L, 3L);

            //@ assert (V2J.isSet(set_2) && (\forall int i; 0 <= i && i < V2J.size(set_2); Utils.is_nat1(V2J.get(set_2,i))));
            for (Iterator iterator_2 = set_2.iterator();
                    iterator_2.hasNext() && !(success_2);) {
                x = ((Number) iterator_2.next());
                success_2 = x.longValue() > 1L;

                //@ assert Utils.is_bool(success_2);
            }

            if (!(success_2)) {
                throw new RuntimeException(
                    "Let Be St found no applicable bindings");
            }

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
