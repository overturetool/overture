package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ spec_public @*/
    private static project.Entrytypes.St St = new project.Entrytypes.St(1L);

    private Entry() {
    }

    public static void op1() {
        Object p = null;
        //@ assert inv_Entry_PT(p) && (p == null || inv_Entry_PossiblyOne(p) || inv_Entry_True(p));
        p = 1L;
        //@ assert inv_Entry_PT(p) && (p == null || inv_Entry_PossiblyOne(p) || inv_Entry_True(p));
        p = true;
        //@ assert inv_Entry_PT(p) && (p == null || inv_Entry_PossiblyOne(p) || inv_Entry_True(p));
        St.x = null;
        St.x = 1L;
        St.x = true;
        IO.println("Breaking named type invariant (assigning record field)");
        St.x = false;
    }

    public static void op2() {
        Object p1 = null;
        //@ assert inv_Entry_PT(p1) && (p1 == null || inv_Entry_PossiblyOne(p1) || inv_Entry_True(p1));
        St.x = true;
        IO.println("Breaking named type invariant (assigning local variable)");
        p1 = false;

        //@ assert inv_Entry_PT(p1) && (p1 == null || inv_Entry_PossiblyOne(p1) || inv_Entry_True(p1));
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
        if (!(Utils.equals(check_elem, null)) &&
                !(Utils.is_nat(check_elem) || Utils.is_bool(check_elem))) {
            return false;
        }

        return true;
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_PossiblyOne(final Object check_p) {
        if (!(Utils.equals(check_p, null)) && !(Utils.is_nat(check_p))) {
            return false;
        }

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
        if ((Utils.equals(check_b, null)) || !(Utils.is_bool(check_b))) {
            return false;
        }

        Boolean b = ((Boolean) check_b);

        return b;
    }
}
