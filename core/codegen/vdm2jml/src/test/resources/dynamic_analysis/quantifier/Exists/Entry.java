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
        Boolean ignorePattern_1 = f();
        //@ assert Utils.is_bool(ignorePattern_1);
        {
            IO.println("Done! Expected no errors");

            return 0L;
        }
    }

    /*@ pure @*/
    public static Boolean f() {
        Boolean existsExpResult_1 = false;

        //@ assert Utils.is_bool(existsExpResult_1);
        VDMSet set_1 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_1) && (\forall int i; 0 <= i && i < V2J.size(set_1); Utils.is_nat1(V2J.get(set_1,i))));
        for (Iterator iterator_1 = set_1.iterator();
                iterator_1.hasNext() && !(existsExpResult_1);) {
            Number x = ((Number) iterator_1.next());
            //@ assert Utils.is_nat1(x);
            existsExpResult_1 = x.longValue() > 0L;

            //@ assert Utils.is_bool(existsExpResult_1);
        }

        Boolean ret_1 = existsExpResult_1;

        //@ assert Utils.is_bool(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
