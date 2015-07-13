package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(0L);

    private Entry() {
    }

    public static Object Run() {
        Number ignorePattern_1 = opRet(1L);

        Number ignorePattern_2 = f(3L);
        opVoid();
        IO.println("Before breaking post condition");

        {
            Number ignorePattern_3 = opRet(4L);

            //Skip;
        }

        IO.println("After breaking post condition");

        return 0L;
    }

    //@ ensures post_opVoid(\old(St.copy()),St);
    public static void opVoid() {
        St.x = St.x.longValue() + 1L;
    }

    //@ ensures post_opRet(a,\result,\old(St.copy()),St);
    public static Number opRet(final Number a) {
        St.x = St.x.longValue() + 1L;

        return St.x;
    }

    //@ ensures post_f(a,\result);
    /*@ pure @*/
    public static Number f(final Number a) {
        if (Utils.equals(Utils.mod(a.longValue(), 2L), 0L)) {
            return a.longValue() + 2L;
        } else {
            return a.longValue() + 1L;
        }
    }

    /*@ pure @*/
    public static Boolean post_opVoid(final project.Entrytypes.St _St,
        final project.Entrytypes.St St) {
        return Utils.equals(St.x, _St.x.longValue() + 1L);
    }

    /*@ pure @*/
    public static Boolean post_opRet(final Number a, final Number RESULT,
        final project.Entrytypes.St _St, final project.Entrytypes.St St) {
        Boolean andResult_1 = false;

        if (Utils.equals(St.x, _St.x.longValue() + 1L)) {
            if (Utils.equals(RESULT, a)) {
                andResult_1 = true;
            }
        }

        return andResult_1;
    }

    /*@ pure @*/
    public static Boolean post_f(final Number a, final Number RESULT) {
        return Utils.equals(Utils.mod(RESULT.longValue(), 2L), 0L);
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }
}
