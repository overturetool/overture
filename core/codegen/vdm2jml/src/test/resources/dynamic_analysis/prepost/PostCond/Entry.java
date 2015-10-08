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

        //@ assert Utils.is_nat(ignorePattern_1);
        Number ignorePattern_2 = f(3L);
        //@ assert Utils.is_nat(ignorePattern_2);
        {
            opVoid();
            IO.println("Before breaking post condition");

            {
                Number ignorePattern_3 = opRet(4L);

                //@ assert Utils.is_nat(ignorePattern_3);

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
        //@ assert Utils.is_nat(a);

        //@ assert St != null;
        St.set_x(St.get_x().longValue() + 1L);

        Number ret_1 = St.get_x();

        //@ assert Utils.is_nat(ret_1);
        return ret_1;
    }

    //@ ensures post_f(a,\result);
    /*@ pure @*/
    public static Number f(final Number a) {
        //@ assert Utils.is_nat(a);
        if (Utils.equals(Utils.mod(a.longValue(), 2L), 0L)) {
            Number ret_2 = a.longValue() + 2L;

            //@ assert Utils.is_nat(ret_2);
            return ret_2;
        } else {
            Number ret_3 = a.longValue() + 1L;

            //@ assert Utils.is_nat(ret_3);
            return ret_3;
        }
    }

    /*@ pure @*/
    public static Boolean post_opVoid(final project.Entrytypes.St _St,
        final project.Entrytypes.St St) {
        //@ assert Utils.is_(_St,project.Entrytypes.St.class);

        //@ assert Utils.is_(St,project.Entrytypes.St.class);
        Boolean ret_4 = Utils.equals(St.get_x(), _St.get_x().longValue() + 1L);

        //@ assert Utils.is_bool(ret_4);
        return ret_4;
    }

    /*@ pure @*/
    public static Boolean post_opRet(final Number a, final Number RESULT,
        final project.Entrytypes.St _St, final project.Entrytypes.St St) {
        //@ assert Utils.is_nat(a);

        //@ assert Utils.is_nat(RESULT);

        //@ assert Utils.is_(_St,project.Entrytypes.St.class);

        //@ assert Utils.is_(St,project.Entrytypes.St.class);
        Boolean andResult_1 = false;

        //@ assert Utils.is_bool(andResult_1);
        if (Utils.equals(St.get_x(), _St.get_x().longValue() + 1L)) {
            if (Utils.equals(RESULT, a)) {
                andResult_1 = true;

                //@ assert Utils.is_bool(andResult_1);
            }
        }

        Boolean ret_5 = andResult_1;

        //@ assert Utils.is_bool(ret_5);
        return ret_5;
    }

    /*@ pure @*/
    public static Boolean post_f(final Number a, final Number RESULT) {
        //@ assert Utils.is_nat(a);

        //@ assert Utils.is_nat(RESULT);
        Boolean ret_6 = Utils.equals(Utils.mod(RESULT.longValue(), 2L), 0L);

        //@ assert Utils.is_bool(ret_6);
        return ret_6;
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }
}
