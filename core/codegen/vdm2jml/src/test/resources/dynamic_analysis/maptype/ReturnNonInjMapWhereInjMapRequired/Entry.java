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
        IO.println("Before legal use");

        {
            VDMMap ignorePattern_1 = consInjMap();

            //@ assert (V2J.isInjMap(ignorePattern_1) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_1); Utils.is_nat(V2J.getDom(ignorePattern_1,i)) && Utils.is_(V2J.getRng(ignorePattern_1,i),project.Entrytypes.V2.class)));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            VDMMap ignorePattern_2 = consInjMapErr();

            //@ assert (V2J.isInjMap(ignorePattern_2) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_2); Utils.is_nat(V2J.getDom(ignorePattern_2,i)) && Utils.is_(V2J.getRng(ignorePattern_2,i),project.Entrytypes.V2.class)));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static VDMMap consInjMap() {
        VDMMap ret_1 = MapUtil.map(new Maplet(1L,
                    new project.Entrytypes.V2(1L, 2L)),
                new Maplet(2L, new project.Entrytypes.V2(2L, 1L)));

        //@ assert (V2J.isInjMap(ret_1) && (\forall int i; 0 <= i && i < V2J.size(ret_1); Utils.is_nat(V2J.getDom(ret_1,i)) && Utils.is_(V2J.getRng(ret_1,i),project.Entrytypes.V2.class)));
        return Utils.copy(ret_1);
    }

    /*@ pure @*/
    public static VDMMap consInjMapErr() {
        VDMMap ret_2 = MapUtil.map(new Maplet(1L,
                    new project.Entrytypes.V2(1L, 2L)),
                new Maplet(2L, new project.Entrytypes.V2(2L, 1L)),
                new Maplet(3L, new project.Entrytypes.V2(1L, 2L)));

        //@ assert (V2J.isInjMap(ret_2) && (\forall int i; 0 <= i && i < V2J.size(ret_2); Utils.is_nat(V2J.getDom(ret_2,i)) && Utils.is_(V2J.getRng(ret_2,i),project.Entrytypes.V2.class)));
        return Utils.copy(ret_2);
    }

    public String toString() {
        return "Entry{}";
    }
}
