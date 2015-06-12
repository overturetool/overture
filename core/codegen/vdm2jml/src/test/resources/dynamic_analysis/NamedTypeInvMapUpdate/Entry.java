package project;

import org.overture.codegen.runtime.*;

import java.util.*;


//@ nullable_by_default
@SuppressWarnings("all")
public class Entry {
    public static Object Run() {
        VDMMap m = MapUtil.map(new Maplet('a', 1L), new Maplet(1L, 2L));
        //@ assert inv_Entry_M(m);
        Utils.mapSeqUpdate(m, 'a', 2L);
        //@ assert inv_Entry_M(m);
        Utils.mapSeqUpdate(m, 1L, 2L);
        //@ assert inv_Entry_M(m);
        IO.println("Breaking named type invariant for sequence");
        Utils.mapSeqUpdate(m, 2L, 10L);

        //@ assert inv_Entry_M(m);
        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_M(final Object check_m) {
        if ((Utils.equals(check_m, null)) ||
                !(Utils.is_(check_m, VDMMap.class))) {
            return false;
        }

        VDMMap m = ((VDMMap) check_m);

        Boolean forAllExpResult_1 = true;
        VDMSet set_1 = MapUtil.dom(Utils.copy(m));

        for (Iterator iterator_1 = set_1.iterator();
                iterator_1.hasNext() && forAllExpResult_1;) {
            Object x = ((Object) iterator_1.next());
            Boolean orResult_1 = false;

            Boolean andResult_1 = false;

            if (Utils.is_nat(x)) {
                if (Utils.is_nat(Utils.get(m, x))) {
                    andResult_1 = true;
                }
            }

            if (!(andResult_1)) {
                orResult_1 = true;
            } else {
                orResult_1 = Utils.equals(((Number) x).doubleValue() + 1L,
                        Utils.get(m, x));
            }

            forAllExpResult_1 = orResult_1;
        }

        return forAllExpResult_1;
    }
}
