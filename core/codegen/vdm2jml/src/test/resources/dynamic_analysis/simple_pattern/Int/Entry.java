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
            Number ignorePattern_1 = f(1L);

            //@ assert Utils.is_nat(ignorePattern_1);

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    /*@ pure @*/
    public static Number f(final Number intPattern_1) {
        //@ assert Utils.is_nat(intPattern_1);
        Boolean success_1 = Utils.equals(intPattern_1, 1L);

        //@ assert Utils.is_bool(success_1);
        if (!(success_1)) {
            throw new RuntimeException("Integer pattern match failed");
        }

        Number ret_1 = 2L;

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
