package project;

import org.overture.codegen.runtime.*;
import org.overture.codegen.vdm2jml.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
final public class Entry {
    /*@ public ghost static boolean invChecksOn = true; @*/
    private Entry() {
    }

    public static Object Run() {
        {
            Character ignorePattern_1 = f();

            //@ assert Utils.is_char(ignorePattern_1);

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    /*@ pure @*/
    public static Character f() {
        Character letBeStExp_1 = null;
        Character charPattern_1 = null;

        Boolean success_1 = false;

        //@ assert Utils.is_bool(success_1);
        VDMSet set_1 = SetUtil.set('a');

        //@ assert (V2J.isSet(set_1) && (\forall int i; 0 <= i && i < V2J.size(set_1); Utils.is_char(V2J.get(set_1,i))));
        for (Iterator iterator_1 = set_1.iterator();
                iterator_1.hasNext() && !(success_1);) {
            charPattern_1 = ((Character) iterator_1.next());
            //@ assert Utils.is_char(charPattern_1);
            success_1 = Utils.equals(charPattern_1, 'a');

            //@ assert Utils.is_bool(success_1);
            if (!(success_1)) {
                continue;
            }

            success_1 = true;

            //@ assert Utils.is_bool(success_1);
        }

        if (!(success_1)) {
            throw new RuntimeException("Let Be St found no applicable bindings");
        }

        letBeStExp_1 = 'a';

        //@ assert Utils.is_char(letBeStExp_1);
        Character ret_1 = letBeStExp_1;

        //@ assert Utils.is_char(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
