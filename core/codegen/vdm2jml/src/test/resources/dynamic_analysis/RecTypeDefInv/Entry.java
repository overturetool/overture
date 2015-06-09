package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
public class Entry {
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

        //Skip;
    }

    public static void recInvBreak() {
        project.Entrytypes.Rec ignorePattern_2 = new project.Entrytypes.Rec(1L,
                -2L);

        //Skip;
    }

    public String toString() {
        return "Entry{}";
    }
}
