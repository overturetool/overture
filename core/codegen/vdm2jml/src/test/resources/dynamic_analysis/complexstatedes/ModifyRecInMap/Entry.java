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
        project.Entrytypes.A a = new project.Entrytypes.A(MapUtil.map(
                    new Maplet(1L, new project.Entrytypes.B(2L))));
        VDMMap stateDes_1 = a.get_m();

        project.Entrytypes.B stateDes_2 = ((project.Entrytypes.B) Utils.get(stateDes_1,
                1L));

        stateDes_2.set_x(2L);

        //@ assert a.valid();
        return 0L;
    }

    public static Number useNotOk() {
        project.Entrytypes.A a = new project.Entrytypes.A(MapUtil.map(
                    new Maplet(1L, new project.Entrytypes.B(2L))));
        VDMMap stateDes_3 = a.get_m();

        project.Entrytypes.B stateDes_4 = ((project.Entrytypes.B) Utils.get(stateDes_3,
                1L));

        stateDes_4.set_x(1L);

        //@ assert a.valid();
        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
