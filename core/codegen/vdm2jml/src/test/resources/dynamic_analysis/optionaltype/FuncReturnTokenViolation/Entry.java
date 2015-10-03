package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        IO.println("Before evaluating ok()");

        {
            Token ignorePattern_1 = ok();

            //@ assert ignorePattern_1 != null;

            /* skip */
        }

        IO.println("After evaluating ok()");
        IO.println("Before evaluating error()");

        {
            Token ignorePattern_2 = err();

            //@ assert ignorePattern_2 != null;

            /* skip */
        }

        IO.println("After evaluating error()");

        return true;
    }

    /*@ pure @*/
    public static Token ok() {
        Token aOpt = new Token("");

        Token ret_1 = aOpt;

        //@ assert ret_1 != null;
        return ret_1;
    }

    /*@ pure @*/
    public static Token err() {
        Token aOpt = null;

        Token ret_2 = aOpt;

        //@ assert ret_2 != null;
        return ret_2;
    }

    public String toString() {
        return "Entry{}";
    }
}
