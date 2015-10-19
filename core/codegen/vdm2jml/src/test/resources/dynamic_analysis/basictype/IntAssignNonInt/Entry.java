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
        Number i = -1L;
        //@ assert Utils.is_int(i);
        IO.println("Before valid use.");
        i = 1L;
        //@ assert Utils.is_int(i);
        IO.println("After valid use.");
        IO.println("Before invalid use.");
        i = i.longValue() + 0.5;
        //@ assert Utils.is_int(i);
        IO.println("After invalid use.");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
