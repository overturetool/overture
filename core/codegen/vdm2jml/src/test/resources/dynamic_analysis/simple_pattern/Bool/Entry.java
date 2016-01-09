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
            Boolean ignorePattern_1 = f();

            //@ assert Utils.is_bool(ignorePattern_1);

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    /*@ pure @*/
    public static Boolean f() {
        Boolean boolPattern_1 = true;

        //@ assert Utils.is_bool(boolPattern_1);
        Boolean success_1 = Utils.equals(boolPattern_1, true);

        //@ assert Utils.is_bool(success_1);
        if (!(success_1)) {
            throw new RuntimeException("Bool pattern match failed");
        }

        Boolean ret_1 = true;

        //@ assert Utils.is_bool(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
