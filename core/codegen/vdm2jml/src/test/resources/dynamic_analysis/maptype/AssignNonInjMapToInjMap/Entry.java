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
        IO.println("Before legal use");

        {
            VDMMap ignorePattern_1 = MapUtil.map(new Maplet(1L, 2L),
                    new Maplet(3L, 4L));

            //@ assert (V2J.isMap(ignorePattern_1) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_1); Utils.is_nat(V2J.getDom(ignorePattern_1,i)) && Utils.is_nat(V2J.getRng(ignorePattern_1,i))));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            VDMMap ignorePattern_2 = MapUtil.map(new Maplet(1L, 2L),
                    new Maplet(3L, 2L));

            //@ assert (V2J.isInjMap(ignorePattern_2) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_2); Utils.is_nat(V2J.getDom(ignorePattern_2,i)) && Utils.is_nat(V2J.getRng(ignorePattern_2,i))));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
