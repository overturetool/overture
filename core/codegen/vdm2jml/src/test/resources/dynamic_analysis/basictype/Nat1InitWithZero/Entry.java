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
        Number n = 1L;
        //@ assert Utils.is_nat1(n);
        IO.println("Before valid use.");
        n = 1L;
        //@ assert Utils.is_nat1(n);
        IO.println("After valid use.");
        IO.println("Before invalid use.");

        {
            Number n1 = 0L;

            //@ assert Utils.is_nat1(n1);

            /* skip */
        }

        IO.println("After invalid use.");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
