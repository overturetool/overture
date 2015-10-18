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

        VDMSet setCompResult_1 = SetUtil.set();

        //@ assert (V2J.isSet(setCompResult_1) && (\forall int i; 0 <= i && i < V2J.size(setCompResult_1); Utils.is_nat1(V2J.get(setCompResult_1,i))));
        VDMSet set_1 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_1) && (\forall int i; 0 <= i && i < V2J.size(set_1); Utils.is_nat1(V2J.get(set_1,i))));
        for (Iterator iterator_1 = set_1.iterator(); iterator_1.hasNext();) {
            Number x = ((Number) iterator_1.next());

            //@ assert Utils.is_nat1(x);
            if (x.longValue() > 0L) {
                setCompResult_1 = SetUtil.union(Utils.copy(setCompResult_1),
                        SetUtil.set(x));

                //@ assert (V2J.isSet(setCompResult_1) && (\forall int i; 0 <= i && i < V2J.size(setCompResult_1); Utils.is_nat1(V2J.get(setCompResult_1,i))));
            }
        }

        {
            VDMSet ignorePattern_1 = Utils.copy(setCompResult_1);

            //@ assert (V2J.isSet(ignorePattern_1) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_1); Utils.is_nat1(V2J.get(ignorePattern_1,i))));

            /* skip */
        }

        VDMSet setCompResult_2 = SetUtil.set();

        //@ assert (V2J.isSet(setCompResult_2) && (\forall int i; 0 <= i && i < V2J.size(setCompResult_2); Utils.is_nat1(V2J.get(setCompResult_2,i))));
        VDMSet set_2 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_2) && (\forall int i; 0 <= i && i < V2J.size(set_2); Utils.is_nat1(V2J.get(set_2,i))));
        for (Iterator iterator_2 = set_2.iterator(); iterator_2.hasNext();) {
            Number x = ((Number) iterator_2.next());
            //@ assert Utils.is_nat1(x);
            setCompResult_2 = SetUtil.union(Utils.copy(setCompResult_2),
                    SetUtil.set(x));

            //@ assert (V2J.isSet(setCompResult_2) && (\forall int i; 0 <= i && i < V2J.size(setCompResult_2); Utils.is_nat1(V2J.get(setCompResult_2,i))));
        }

        {
            VDMSet ignorePattern_2 = Utils.copy(setCompResult_2);

            //@ assert (V2J.isSet(ignorePattern_2) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_2); Utils.is_nat1(V2J.get(ignorePattern_2,i))));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before violations");

        VDMSet setCompResult_3 = SetUtil.set();

        //@ assert (V2J.isSet(setCompResult_3) && (\forall int i; 0 <= i && i < V2J.size(setCompResult_3); Utils.is_nat(V2J.get(setCompResult_3,i))));
        VDMSet set_3 = SetUtil.set(0L, 1L, 2L);

        //@ assert (V2J.isSet(set_3) && (\forall int i; 0 <= i && i < V2J.size(set_3); Utils.is_nat(V2J.get(set_3,i))));
        for (Iterator iterator_3 = set_3.iterator(); iterator_3.hasNext();) {
            Number x = ((Number) iterator_3.next());

            //@ assert Utils.is_nat(x);
            if (x.longValue() > -1L) {
                setCompResult_3 = SetUtil.union(Utils.copy(setCompResult_3),
                        SetUtil.set(x));

                //@ assert (V2J.isSet(setCompResult_3) && (\forall int i; 0 <= i && i < V2J.size(setCompResult_3); Utils.is_nat(V2J.get(setCompResult_3,i))));
            }
        }

        {
            VDMSet ignorePattern_3 = Utils.copy(setCompResult_3);

            //@ assert (V2J.isSet(ignorePattern_3) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_3); Utils.is_nat1(V2J.get(ignorePattern_3,i))));

            /* skip */
        }

        VDMSet setCompResult_4 = SetUtil.set();

        //@ assert (V2J.isSet(setCompResult_4) && (\forall int i; 0 <= i && i < V2J.size(setCompResult_4); Utils.is_nat(V2J.get(setCompResult_4,i))));
        VDMSet set_4 = SetUtil.set(0L, 1L, 2L);

        //@ assert (V2J.isSet(set_4) && (\forall int i; 0 <= i && i < V2J.size(set_4); Utils.is_nat(V2J.get(set_4,i))));
        for (Iterator iterator_4 = set_4.iterator(); iterator_4.hasNext();) {
            Number x = ((Number) iterator_4.next());
            //@ assert Utils.is_nat(x);
            setCompResult_4 = SetUtil.union(Utils.copy(setCompResult_4),
                    SetUtil.set(x));

            //@ assert (V2J.isSet(setCompResult_4) && (\forall int i; 0 <= i && i < V2J.size(setCompResult_4); Utils.is_nat(V2J.get(setCompResult_4,i))));
        }

        {
            VDMSet ignorePattern_4 = Utils.copy(setCompResult_4);

            //@ assert (V2J.isSet(ignorePattern_4) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_4); Utils.is_nat1(V2J.get(ignorePattern_4,i))));

            /* skip */
        }

        IO.println("After violations");

        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }
}
