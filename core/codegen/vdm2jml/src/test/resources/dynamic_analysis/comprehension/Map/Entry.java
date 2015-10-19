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

        VDMMap mapCompResult_1 = MapUtil.map();

        //@ assert (V2J.isMap(mapCompResult_1) && (\forall int i; 0 <= i && i < V2J.size(mapCompResult_1); Utils.is_nat1(V2J.getDom(mapCompResult_1,i)) && Utils.is_nat1(V2J.getRng(mapCompResult_1,i))));
        VDMSet set_1 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_1) && (\forall int i; 0 <= i && i < V2J.size(set_1); Utils.is_nat1(V2J.get(set_1,i))));
        for (Iterator iterator_1 = set_1.iterator(); iterator_1.hasNext();) {
            Number x = ((Number) iterator_1.next());

            //@ assert Utils.is_nat1(x);
            if (x.longValue() > 0L) {
                mapCompResult_1 = MapUtil.munion(Utils.copy(mapCompResult_1),
                        MapUtil.map(new Maplet(x, x)));

                //@ assert (V2J.isMap(mapCompResult_1) && (\forall int i; 0 <= i && i < V2J.size(mapCompResult_1); Utils.is_nat1(V2J.getDom(mapCompResult_1,i)) && Utils.is_nat1(V2J.getRng(mapCompResult_1,i))));
            }
        }

        {
            VDMMap ignorePattern_1 = Utils.copy(mapCompResult_1);

            //@ assert (V2J.isInjMap(ignorePattern_1) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_1); Utils.is_nat(V2J.getDom(ignorePattern_1,i)) && Utils.is_nat(V2J.getRng(ignorePattern_1,i))));

            /* skip */
        }

        VDMMap mapCompResult_2 = MapUtil.map();

        //@ assert (V2J.isMap(mapCompResult_2) && (\forall int i; 0 <= i && i < V2J.size(mapCompResult_2); Utils.is_nat1(V2J.getDom(mapCompResult_2,i)) && Utils.is_nat1(V2J.getRng(mapCompResult_2,i))));
        VDMSet set_2 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_2) && (\forall int i; 0 <= i && i < V2J.size(set_2); Utils.is_nat1(V2J.get(set_2,i))));
        for (Iterator iterator_2 = set_2.iterator(); iterator_2.hasNext();) {
            Number x = ((Number) iterator_2.next());
            //@ assert Utils.is_nat1(x);
            mapCompResult_2 = MapUtil.munion(Utils.copy(mapCompResult_2),
                    MapUtil.map(new Maplet(x, x)));

            //@ assert (V2J.isMap(mapCompResult_2) && (\forall int i; 0 <= i && i < V2J.size(mapCompResult_2); Utils.is_nat1(V2J.getDom(mapCompResult_2,i)) && Utils.is_nat1(V2J.getRng(mapCompResult_2,i))));
        }

        {
            VDMMap ignorePattern_2 = Utils.copy(mapCompResult_2);

            //@ assert (V2J.isInjMap(ignorePattern_2) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_2); Utils.is_nat(V2J.getDom(ignorePattern_2,i)) && Utils.is_nat(V2J.getRng(ignorePattern_2,i))));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before violations");

        VDMMap mapCompResult_3 = MapUtil.map();

        //@ assert (V2J.isMap(mapCompResult_3) && (\forall int i; 0 <= i && i < V2J.size(mapCompResult_3); Utils.is_nat1(V2J.getDom(mapCompResult_3,i)) && Utils.is_nat1(V2J.getRng(mapCompResult_3,i))));
        VDMSet set_3 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_3) && (\forall int i; 0 <= i && i < V2J.size(set_3); Utils.is_nat1(V2J.get(set_3,i))));
        for (Iterator iterator_3 = set_3.iterator(); iterator_3.hasNext();) {
            Number x = ((Number) iterator_3.next());

            //@ assert Utils.is_nat1(x);
            if (x.longValue() > 1L) {
                mapCompResult_3 = MapUtil.munion(Utils.copy(mapCompResult_3),
                        MapUtil.map(new Maplet(x, 2L)));

                //@ assert (V2J.isMap(mapCompResult_3) && (\forall int i; 0 <= i && i < V2J.size(mapCompResult_3); Utils.is_nat1(V2J.getDom(mapCompResult_3,i)) && Utils.is_nat1(V2J.getRng(mapCompResult_3,i))));
            }
        }

        {
            VDMMap ignorePattern_3 = Utils.copy(mapCompResult_3);

            //@ assert (V2J.isInjMap(ignorePattern_3) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_3); Utils.is_nat(V2J.getDom(ignorePattern_3,i)) && Utils.is_nat(V2J.getRng(ignorePattern_3,i))));

            /* skip */
        }

        VDMMap mapCompResult_4 = MapUtil.map();

        //@ assert (V2J.isMap(mapCompResult_4) && (\forall int i; 0 <= i && i < V2J.size(mapCompResult_4); Utils.is_nat1(V2J.getDom(mapCompResult_4,i)) && Utils.is_nat1(V2J.getRng(mapCompResult_4,i))));
        VDMSet set_4 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_4) && (\forall int i; 0 <= i && i < V2J.size(set_4); Utils.is_nat1(V2J.get(set_4,i))));
        for (Iterator iterator_4 = set_4.iterator(); iterator_4.hasNext();) {
            Number x = ((Number) iterator_4.next());
            //@ assert Utils.is_nat1(x);
            mapCompResult_4 = MapUtil.munion(Utils.copy(mapCompResult_4),
                    MapUtil.map(new Maplet(x, 2L)));

            //@ assert (V2J.isMap(mapCompResult_4) && (\forall int i; 0 <= i && i < V2J.size(mapCompResult_4); Utils.is_nat1(V2J.getDom(mapCompResult_4,i)) && Utils.is_nat1(V2J.getRng(mapCompResult_4,i))));
        }

        {
            VDMMap ignorePattern_4 = Utils.copy(mapCompResult_4);

            //@ assert (V2J.isInjMap(ignorePattern_4) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_4); Utils.is_nat(V2J.getDom(ignorePattern_4,i)) && Utils.is_nat(V2J.getRng(ignorePattern_4,i))));

            /* skip */
        }

        IO.println("After violations");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
