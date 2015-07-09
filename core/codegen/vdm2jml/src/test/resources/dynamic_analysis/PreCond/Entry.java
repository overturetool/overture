package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(5L);

    public static Object Run() {
        Number ignorePattern_1 = opRet(1L);
        opVoid(2L);
        IO.println("Before breaking pre condition");

        {
            Number ignorePattern_2 = id(-1L);

            //Skip;
        }

        IO.println("After breaking pre condition");

        return 0L;
    }

    //@ requires pre_opRet(a,St);
    public static Number opRet(final Number a) {
        St.x = a.longValue() + 1L;

        return St.x;
    }

    //@ requires pre_opVoid(a,St);
    public static void opVoid(final Number a) {
        St.x = a.longValue() + 1L;
    }

    //@ requires pre_id(a);
    /*@ pure @*/
    public static Number id(final Number a) {
        return a;
    }

    /*@ pure @*/
    public static Boolean pre_opRet(final Number a,
        final project.Entrytypes.St St) {
        return St.x.longValue() > 0L;
    }

    /*@ pure @*/
    public static Boolean pre_opVoid(final Number a,
        final project.Entrytypes.St St) {
        return St.x.longValue() > 0L;
    }

    /*@ pure @*/
    public static Boolean pre_id(final Number a) {
        return a.longValue() > 0L;
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }
}
