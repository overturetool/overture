package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(1L);

    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static void op1() {
        Object p = null;
        //@ assert (((((p == null) || Utils.is_nat(p)) && inv_Entry_PossiblyOne(p)) || (Utils.is_bool(p) && inv_Entry_True(p))) && inv_Entry_PT(p));
        p = 1L;
        //@ assert (((((p == null) || Utils.is_nat(p)) && inv_Entry_PossiblyOne(p)) || (Utils.is_bool(p) && inv_Entry_True(p))) && inv_Entry_PT(p));
        p = true;
        //@ assert (((((p == null) || Utils.is_nat(p)) && inv_Entry_PossiblyOne(p)) || (Utils.is_bool(p) && inv_Entry_True(p))) && inv_Entry_PT(p));

        //@ assert St != null;
        St.set_x(null);

        //@ assert St != null;
        St.set_x(1L);

        //@ assert St != null;
        St.set_x(true);

        IO.println("Breaking named type invariant (assigning record field)");
        //@ assert St != null;
        St.set_x(false);
    }

    public static void op2() {
        Object p1 = null;
        //@ assert (((((p1 == null) || Utils.is_nat(p1)) && inv_Entry_PossiblyOne(p1)) || (Utils.is_bool(p1) && inv_Entry_True(p1))) && inv_Entry_PT(p1));

        //@ assert St != null;
        St.set_x(true);

        IO.println("Breaking named type invariant (assigning local variable)");
        p1 = false;

        //@ assert (((((p1 == null) || Utils.is_nat(p1)) && inv_Entry_PossiblyOne(p1)) || (Utils.is_bool(p1) && inv_Entry_True(p1))) && inv_Entry_PT(p1));
    }

    public static Object Run() {
        op1();
        op2();

        return 0L;
    }

    public String toString() {
        return "Entry{" + "St := " + Utils.toString(St) + "}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_PT(final Object check_elem) {
        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_PossiblyOne(final Object check_p) {
        Number p = ((Number) check_p);

        Boolean orResult_1 = false;

        if (!(!(Utils.equals(p, null)))) {
            orResult_1 = true;
        } else {
            orResult_1 = Utils.equals(p, 1L);
        }

        return orResult_1;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_True(final Object check_b) {
        Boolean b = ((Boolean) check_b);

        return b;
    }
}
