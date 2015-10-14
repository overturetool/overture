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
        VDMSeq s = SeqUtil.seq(10L, 11L, 12L);
        //@ assert ((V2J.isSeq(s) && (\forall int i; 0 <= i && i < V2J.size(s); true)) && inv_Entry_S(s));

        //@ assert s != null;
        Utils.mapSeqUpdate(s, 1L, 'a');
        //@ assert ((V2J.isSeq(s) && (\forall int i; 0 <= i && i < V2J.size(s); true)) && inv_Entry_S(s));

        //@ assert s != null;
        Utils.mapSeqUpdate(s, 2L, null);
        //@ assert ((V2J.isSeq(s) && (\forall int i; 0 <= i && i < V2J.size(s); true)) && inv_Entry_S(s));
        IO.println("Breaking named type invariant for sequence");
        //@ assert s != null;
        Utils.mapSeqUpdate(s, 3L, 4L);

        //@ assert ((V2J.isSeq(s) && (\forall int i; 0 <= i && i < V2J.size(s); true)) && inv_Entry_S(s));
        return 0L;
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_S(final Object check_s) {
        VDMSeq s = ((VDMSeq) check_s);

        Boolean forAllExpResult_1 = true;
        VDMSet set_1 = SeqUtil.elems(Utils.copy(s));

        for (Iterator iterator_1 = set_1.iterator();
                iterator_1.hasNext() && forAllExpResult_1;) {
            Object x = ((Object) iterator_1.next());
            Boolean orResult_1 = false;

            if (!(Utils.is_nat(x))) {
                orResult_1 = true;
            } else {
                orResult_1 = ((Number) x).doubleValue() > 5L;
            }

            forAllExpResult_1 = orResult_1;
        }

        return forAllExpResult_1;
    }
}
