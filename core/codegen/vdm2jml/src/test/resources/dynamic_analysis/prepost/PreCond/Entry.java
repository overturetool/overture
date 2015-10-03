package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(5L);

    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        Number ignorePattern_1 = opRet(1L);
        //@ assert ignorePattern_1 != null;
        {
            opVoid(2L);
            IO.println("Before breaking pre condition");

            {
                Number ignorePattern_2 = id(-1L);

                //@ assert ignorePattern_2 != null;

                /* skip */
            }

            IO.println("After breaking pre condition");

            return 0L;
        }
    }

    //@ requires pre_opRet(a,St);
    public static Number opRet(final Number a) {
        //@ assert a != null;

        //@ assert St != null;
        St.set_x(a.longValue() + 1L);

        Number ret_1 = St.get_x();

        //@ assert ret_1 != null;
        return ret_1;
    }

    //@ requires pre_opVoid(a,St);
    public static void opVoid(final Number a) {
        //@ assert a != null;

        //@ assert St != null;
        St.set_x(a.longValue() + 1L);
    }

    //@ requires pre_id(a);
    /*@ pure @*/
    public static Number id(final Number a) {
        //@ assert a != null;
        Number ret_2 = a;

        //@ assert ret_2 != null;
        return ret_2;
    }

    /*@ pure @*/
    public static Boolean pre_opRet(final Number a,
        final project.Entrytypes.St St) {
        //@ assert a != null;

        //@ assert St != null;
        Boolean ret_3 = St.get_x().longValue() > 0L;

        //@ assert ret_3 != null;
        return ret_3;
    }

    /*@ pure @*/
    public static Boolean pre_opVoid(final Number a,
        final project.Entrytypes.St St) {
        //@ assert a != null;

        //@ assert St != null;
        Boolean ret_4 = St.get_x().longValue() > 0L;

        //@ assert ret_4 != null;
        return ret_4;
    }

    /*@ pure @*/
    public static Boolean pre_id(final Number a) {
        //@ assert a != null;
        Boolean ret_5 = a.longValue() > 0L;

        //@ assert ret_5 != null;
        return ret_5;
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }
}
