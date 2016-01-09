package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(-5L);

    //@ public static invariant St != null ==> inv_St(St);
    private Entry() {
    }

    public static Object Run() {
        return 1L;
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_St(final project.Entrytypes.St s) {
        return s.x.longValue() > 0L;
    }
}
