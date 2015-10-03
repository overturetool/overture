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
        project.quotes.AQuote aOpt = null;

        project.quotes.AQuote a = project.quotes.AQuote.getInstance();

        {
            IO.println("Before passing LEGAL value");
            op(a);
            IO.println("After passing LEGAL value");
            IO.println("Before passing ILLEGAL value");
            op(aOpt);
            IO.println("After passing ILLEGAL value");

            return true;
        }
    }

    public static void op(final project.quotes.AQuote ignorePattern_1) {
        //@ assert ignorePattern_1 != null;

        /* skip */
    }

    public String toString() {
        return "Entry{}";
    }
}
