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
        IO.println("Before useOk");

        {
            Number ignorePattern_1 = useOk();

            /* skip */
        }

        IO.println("After useOk");
        IO.println("Before useNotOk");

        {
            Number ignorePattern_2 = useNotOk();

            /* skip */
        }

        IO.println("After useNotOk");

        return 0L;
    }

    public static Number useOk() {
        project.Entrytypes.A a = new project.Entrytypes.A(MapUtil.map());
        a.set_m(MapUtil.munion(Utils.copy(a.get_m()),
                MapUtil.map(new Maplet(1L, new project.Entrytypes.B(2L)))));

        return 0L;
    }

    public static Number useNotOk() {
        project.Entrytypes.A a = new project.Entrytypes.A(MapUtil.map());
        a.set_m(MapUtil.munion(Utils.copy(a.get_m()),
                MapUtil.map(new Maplet(1L, new project.Entrytypes.B(1L)))));

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
