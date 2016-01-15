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

            //@ assert Utils.is_nat(ignorePattern_1);

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    /*@ pure @*/
    public static Number f() {
        Tuple tuplePattern_1 = Tuple.mk_(1L, 2L);

        //@ assert (V2J.isTup(tuplePattern_1,2) && Utils.is_nat1(V2J.field(tuplePattern_1,0)) && Utils.is_nat1(V2J.field(tuplePattern_1,1)));
        Boolean success_1 = tuplePattern_1.compatible(Number.class, Number.class);

        //@ assert Utils.is_bool(success_1);
        Number a = null;

        Number b = null;

        if (success_1) {
            a = ((Number) tuplePattern_1.get(0));
            //@ assert Utils.is_nat1(a);
            b = ((Number) tuplePattern_1.get(1));

            //@ assert Utils.is_nat1(b);
        }

        if (!(success_1)) {
            throw new RuntimeException("Tuple pattern match failed");
        }

        Number ret_1 = a.longValue() + b.longValue();

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
