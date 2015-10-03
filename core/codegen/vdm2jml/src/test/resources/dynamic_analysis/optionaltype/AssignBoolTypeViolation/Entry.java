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
        Boolean b = true;

        Boolean bOpt = null;

        IO.println("Before doing valid assignments");
        bOpt = true;
        b = bOpt;
        //@ assert b != null;
        bOpt = null;
        IO.println("After doing valid assignments");
        IO.println("Before doing illegal assignments");
        b = bOpt;
        //@ assert b != null;
        IO.println("After doing illegal assignments");

        return true;
    }

    public String toString() {
        return "Entry{}";
    }
}
