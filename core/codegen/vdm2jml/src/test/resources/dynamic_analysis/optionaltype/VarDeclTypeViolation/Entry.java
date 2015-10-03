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
        IO.println("Before VALID initialisation");

        {
            Number bOkay = natOne();

            //@ assert bOkay != null;

            /* skip */
        }

        IO.println("After VALID initialisation");
        IO.println("Before INVALID initialisation");

        {
            Number bError = natNil();

            //@ assert bError != null;

            /* skip */
        }

        IO.println("After INVALID initialisation");

        return true;
    }

    /*@ pure @*/
    public static Number natNil() {
        return null;
    }

    /*@ pure @*/
    public static Number natOne() {
        return 1L;
    }

    public String toString() {
        return "Entry{}";
    }
}
