package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(0L);

    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        Number ignorePattern_1 = opRet(1L);

        //@ assert ignorePattern_1 != null;
        Number ignorePattern_2 = f(3L);
        //@ assert ignorePattern_2 != null;
        {
            opVoid();
            IO.println("Before breaking post condition");

            {
                Number ignorePattern_3 = opRet(4L);

                //@ assert ignorePattern_3 != null;

                /* skip */
            }

            IO.println("After breaking post condition");

            return 0L;
        }
    }

    //@ ensures post_opVoid(\old(St.copy()),St);
    public static void opVoid() {
        //@ assert St != null;
        St.set_x(St.get_x().longValue() + 1L);
    }

    //@ ensures post_opRet(a,\result,\old(St.copy()),St);
    public static Number opRet(final Number a) {
        //@ assert a != null;

        //@ assert St != null;
        St.set_x(St.get_x().longValue() + 1L);

        Number ret_1 = St.get_x();

        //@ assert ret_1 != null;
        return ret_1;
    }

    //@ ensures post_f(a,\result);
    /*@ pure @*/
    public static Number f(final Number a) {
        //@ assert a != null;
        if (Utils.equals(Utils.mod(a.longValue(), 2L), 0L)) {
            Number ret_2 = a.longValue() + 2L;

            //@ assert ret_2 != null;
            return ret_2;
        } else {
            Number ret_3 = a.longValue() + 1L;

            //@ assert ret_3 != null;
            return ret_3;
        }
    }

    /*@ pure @*/
    public static Boolean post_opVoid(final project.Entrytypes.St _St,
        final project.Entrytypes.St St) {
        //@ assert _St != null;

        //@ assert St != null;
        Boolean ret_4 = Utils.equals(St.get_x(), _St.get_x().longValue() + 1L);

        //@ assert ret_4 != null;
        return ret_4;
    }

    /*@ pure @*/
    public static Boolean post_opRet(final Number a, final Number RESULT,
        final project.Entrytypes.St _St, final project.Entrytypes.St St) {
        //@ assert a != null;

        //@ assert RESULT != null;

        //@ assert _St != null;

        //@ assert St != null;
        Boolean andResult_1 = false;

        if (Utils.equals(St.get_x(), _St.get_x().longValue() + 1L)) {
            if (Utils.equals(RESULT, a)) {
                andResult_1 = true;
            }
        }

        Boolean ret_5 = andResult_1;

        //@ assert ret_5 != null;
        return ret_5;
    }

    /*@ pure @*/
    public static Boolean post_f(final Number a, final Number RESULT) {
        //@ assert a != null;

        //@ assert RESULT != null;
        Boolean ret_6 = Utils.equals(Utils.mod(RESULT.longValue(), 2L), 0L);

        //@ assert ret_6 != null;
        return ret_6;
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }
}
