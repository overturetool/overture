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
            Boolean ignorePattern_1 = f();

            //@ assert Utils.is_bool(ignorePattern_1);

            /* skip */
        }

        IO.println("Done! Expected no violations");

        return 0L;
    }

    /*@ pure @*/
    public static Boolean f() {
        Boolean letBeStExp_1 = null;
        project.Entrytypes.R recordPattern_1 = null;

        Boolean success_1 = false;

        //@ assert Utils.is_bool(success_1);
        VDMSet set_1 = SetUtil.set(new project.Entrytypes.R(false),
                new project.Entrytypes.R(true));

        //@ assert (V2J.isSet(set_1) && (\forall int i; 0 <= i && i < V2J.size(set_1); Utils.is_(V2J.get(set_1,i),project.Entrytypes.R.class)));
        for (Iterator iterator_1 = set_1.iterator();
                iterator_1.hasNext() && !(success_1);) {
            recordPattern_1 = ((project.Entrytypes.R) iterator_1.next());
            //@ assert Utils.is_(recordPattern_1,project.Entrytypes.R.class);
            success_1 = true;

            //@ assert Utils.is_bool(success_1);
            Boolean boolPattern_1 = recordPattern_1.get_b();
            //@ assert Utils.is_bool(boolPattern_1);
            success_1 = Utils.equals(boolPattern_1, true);

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

        letBeStExp_1 = true;

        //@ assert Utils.is_bool(letBeStExp_1);
        Boolean ret_1 = letBeStExp_1;

        //@ assert Utils.is_bool(ret_1);
        return ret_1;
    }

    public String toString() {
        return "Entry{}";
    }
}
