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
            Number ignorePattern_1 = f(2L);

            //@ assert Utils.is_nat(ignorePattern_1);

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    /*@ pure @*/
    public static Number f(final Number a) {
        //@ assert Utils.is_nat(a);
        Number casesExpResult_1 = null;

        Number intPattern_1 = a;

        //@ assert Utils.is_nat(intPattern_1);
        Boolean success_1 = Utils.equals(intPattern_1, 1L);

        //@ assert Utils.is_bool(success_1);
        if (!(success_1)) {
            Number intPattern_2 = a;
            //@ assert Utils.is_nat(intPattern_2);
            success_1 = Utils.equals(intPattern_2, 2L);

            //@ assert Utils.is_bool(success_1);
            if (success_1) {
                casesExpResult_1 = 8L;

                //@ assert Utils.is_nat1(casesExpResult_1);
            } else {
                casesExpResult_1 = 2L;

                //@ assert Utils.is_nat1(casesExpResult_1);
            }
        } else {
            casesExpResult_1 = 4L;

            //@ assert Utils.is_nat1(casesExpResult_1);
        }

        Number ret_1 = casesExpResult_1;

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
