package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

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
        //@ assert Utils.is_nat(ignorePattern_1);
        {
            opVoid(2L);
            IO.println("Before breaking pre condition");

            {
                Number ignorePattern_2 = id(-1L);

                //@ assert Utils.is_int(ignorePattern_2);

                /* skip */
            }

            IO.println("After breaking pre condition");

            return 0L;
        }
    }

    //@ requires pre_opRet(a,St);
    public static Number opRet(final Number a) {
        //@ assert Utils.is_nat(a);

        //@ assert St != null;
        St.set_x(a.longValue() + 1L);

        Number ret_1 = St.get_x();

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    //@ requires pre_opVoid(a,St);
    public static void opVoid(final Number a) {
        //@ assert Utils.is_nat(a);

        //@ assert St != null;
        St.set_x(a.longValue() + 1L);
    }

    //@ requires pre_id(a);
    /*@ pure @*/
    public static Number id(final Number a) {
        //@ assert Utils.is_int(a);
        Number ret_2 = a;

        //@ assert Utils.is_int(ret_2);
        return ret_2;
    }

    /*@ pure @*/
    public static Boolean pre_opRet(final Number a,
        final project.Entrytypes.St St) {
        //@ assert Utils.is_nat(a);

        //@ assert Utils.is_(St,project.Entrytypes.St.class);
        Boolean ret_3 = St.get_x().longValue() > 0L;

        //@ assert Utils.is_bool(ret_3);
        return ret_3;
    }

    /*@ pure @*/
    public static Boolean pre_opVoid(final Number a,
        final project.Entrytypes.St St) {
        //@ assert Utils.is_nat(a);

        //@ assert Utils.is_(St,project.Entrytypes.St.class);
        Boolean ret_4 = St.get_x().longValue() > 0L;

        //@ assert Utils.is_bool(ret_4);
        return ret_4;
    }

    /*@ pure @*/
    public static Boolean pre_id(final Number a) {
        //@ assert Utils.is_int(a);
        Boolean ret_5 = a.longValue() > 0L;

        //@ assert Utils.is_bool(ret_5);
        return ret_5;
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }
}
