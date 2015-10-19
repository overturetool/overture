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
        recInvOk();
        IO.println("Before breaking record invariant");
        recInvBreak();
        IO.println("After breaking record invariant");

        return 0L;
    }

    public static void recInvOk() {
        project.Entrytypes.Rec ignorePattern_1 = new project.Entrytypes.Rec(1L,
                2L);

        //@ assert Utils.is_(ignorePattern_1,project.Entrytypes.Rec.class);

        /* skip */
    }

    public static void recInvBreak() {
        project.Entrytypes.Rec ignorePattern_2 = new project.Entrytypes.Rec(1L,
                -2L);

        //@ assert Utils.is_(ignorePattern_2,project.Entrytypes.Rec.class);

        /* skip */
    }

    public String toString() {
        return "Entry{}";
    }
}
