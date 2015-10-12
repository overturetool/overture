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

            //@ assert Utils.is_token(ignorePattern_1);

            /* skip */
        }

        IO.println("After evaluating ok()");
        IO.println("Before evaluating error()");

        {
            Token ignorePattern_2 = err();

            //@ assert Utils.is_token(ignorePattern_2);

            /* skip */
        }

        IO.println("After evaluating error()");

        return true;
    }

    /*@ pure @*/
    public static Token ok() {
        Token aOpt = new Token("");

        //@ assert ((aOpt == null) || Utils.is_token(aOpt));
        Token ret_1 = aOpt;

        //@ assert Utils.is_token(ret_1);
        return ret_1;
    }

    /*@ pure @*/
    public static Token err() {
        Token aOpt = null;

        //@ assert ((aOpt == null) || Utils.is_token(aOpt));
        Token ret_2 = aOpt;

        //@ assert Utils.is_token(ret_2);
        return ret_2;
    }

    public String toString() {
        return "Entry{}";
    }
}
