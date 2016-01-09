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
        IO.println("Before valid use.");

        {
            project.quotes.AQuote aOpt = null;

            //@ assert ((aOpt == null) || Utils.is_(aOpt,project.quotes.AQuote.class));

            /* skip */
        }

        IO.println("After valid use.");
        IO.println("Before invalid use.");

        {
            project.quotes.AQuote a = Nil();

            //@ assert Utils.is_(a,project.quotes.AQuote.class);

            /* skip */
        }

        IO.println("After invalid use.");

        return 0L;
    }

    /*@ pure @*/
    public static project.quotes.AQuote Nil() {
        project.quotes.AQuote ret_1 = null;

        //@ assert ((ret_1 == null) || Utils.is_(ret_1,project.quotes.AQuote.class));
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
