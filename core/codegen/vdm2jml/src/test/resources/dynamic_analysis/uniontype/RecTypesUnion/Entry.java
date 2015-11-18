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
        Object r = new project.Entrytypes.R1(1L);
        //@ assert (Utils.is_(r,project.Entrytypes.R1.class) || Utils.is_(r,project.Entrytypes.R2.class));
        IO.println("Before valid use");

        if (r instanceof project.Entrytypes.R1) {
            //@ assert r != null;
            ((project.Entrytypes.R1) r).set_x(5L);
        } else if (r instanceof project.Entrytypes.R2) {
            //@ assert r != null;
            ((project.Entrytypes.R2) r).set_x(5L);
        } else {
            throw new RuntimeException("Missing member: x");
        }

        IO.println("After valid use");
        IO.println("Before illegal use");

        if (r instanceof project.Entrytypes.R1) {
            //@ assert r != null;
            ((project.Entrytypes.R1) r).set_x(-5L);
        } else if (r instanceof project.Entrytypes.R2) {
            //@ assert r != null;
            ((project.Entrytypes.R2) r).set_x(-5L);
        } else {
            throw new RuntimeException("Missing member: x");
        }

        IO.println("After illegal use");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
