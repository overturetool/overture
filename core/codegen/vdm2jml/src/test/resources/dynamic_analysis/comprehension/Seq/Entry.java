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

        VDMSeq seqCompResult_1 = SeqUtil.seq();

        //@ assert (V2J.isSeq(seqCompResult_1) && (\forall int i; 0 <= i && i < V2J.size(seqCompResult_1); Utils.is_nat1(V2J.get(seqCompResult_1,i))));
        VDMSet set_1 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_1) && (\forall int i; 0 <= i && i < V2J.size(set_1); Utils.is_nat1(V2J.get(set_1,i))));
        for (Iterator iterator_1 = set_1.iterator(); iterator_1.hasNext();) {
            Number x = ((Number) iterator_1.next());

            //@ assert Utils.is_nat1(x);
            if (x.longValue() > 0L) {
                seqCompResult_1 = SeqUtil.conc(Utils.copy(seqCompResult_1),
                        SeqUtil.seq(x));

                //@ assert (V2J.isSeq(seqCompResult_1) && (\forall int i; 0 <= i && i < V2J.size(seqCompResult_1); Utils.is_nat1(V2J.get(seqCompResult_1,i))));
            }
        }

        {
            VDMSeq ignorePattern_1 = Utils.copy(seqCompResult_1);

            //@ assert (V2J.isSeq1(ignorePattern_1) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_1); Utils.is_nat(V2J.get(ignorePattern_1,i))));

            /* skip */
        }

        VDMSeq seqCompResult_2 = SeqUtil.seq();

        //@ assert (V2J.isSeq(seqCompResult_2) && (\forall int i; 0 <= i && i < V2J.size(seqCompResult_2); Utils.is_nat1(V2J.get(seqCompResult_2,i))));
        VDMSet set_2 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_2) && (\forall int i; 0 <= i && i < V2J.size(set_2); Utils.is_nat1(V2J.get(set_2,i))));
        for (Iterator iterator_2 = set_2.iterator(); iterator_2.hasNext();) {
            Number x = ((Number) iterator_2.next());
            //@ assert Utils.is_nat1(x);
            seqCompResult_2 = SeqUtil.conc(Utils.copy(seqCompResult_2),
                    SeqUtil.seq(x));

            //@ assert (V2J.isSeq(seqCompResult_2) && (\forall int i; 0 <= i && i < V2J.size(seqCompResult_2); Utils.is_nat1(V2J.get(seqCompResult_2,i))));
        }

        {
            VDMSeq ignorePattern_2 = Utils.copy(seqCompResult_2);

            //@ assert (V2J.isSeq1(ignorePattern_2) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_2); Utils.is_nat(V2J.get(ignorePattern_2,i))));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before violations");

        VDMSeq seqCompResult_3 = SeqUtil.seq();

        //@ assert (V2J.isSeq(seqCompResult_3) && (\forall int i; 0 <= i && i < V2J.size(seqCompResult_3); Utils.is_nat1(V2J.get(seqCompResult_3,i))));
        VDMSet set_3 = SetUtil.set(1L, 2L, 3L);

        //@ assert (V2J.isSet(set_3) && (\forall int i; 0 <= i && i < V2J.size(set_3); Utils.is_nat1(V2J.get(set_3,i))));
        for (Iterator iterator_3 = set_3.iterator(); iterator_3.hasNext();) {
            Number x = ((Number) iterator_3.next());

            //@ assert Utils.is_nat1(x);
            if (x.longValue() > 4L) {
                seqCompResult_3 = SeqUtil.conc(Utils.copy(seqCompResult_3),
                        SeqUtil.seq(x));

                //@ assert (V2J.isSeq(seqCompResult_3) && (\forall int i; 0 <= i && i < V2J.size(seqCompResult_3); Utils.is_nat1(V2J.get(seqCompResult_3,i))));
            }
        }

        {
            VDMSeq ignorePattern_3 = Utils.copy(seqCompResult_3);

            //@ assert (V2J.isSeq1(ignorePattern_3) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_3); Utils.is_nat(V2J.get(ignorePattern_3,i))));

            /* skip */
        }

        VDMSeq seqCompResult_4 = SeqUtil.seq();

        //@ assert (V2J.isSeq(seqCompResult_4) && (\forall int i; 0 <= i && i < V2J.size(seqCompResult_4); Utils.is_nat(V2J.get(seqCompResult_4,i))));
        VDMSet set_4 = Entry.xs();

        //@ assert (V2J.isSet(set_4) && (\forall int i; 0 <= i && i < V2J.size(set_4); Utils.is_nat(V2J.get(set_4,i))));
        for (Iterator iterator_4 = set_4.iterator(); iterator_4.hasNext();) {
            Number x = ((Number) iterator_4.next());
            //@ assert Utils.is_nat(x);
            seqCompResult_4 = SeqUtil.conc(Utils.copy(seqCompResult_4),
                    SeqUtil.seq(x));

            //@ assert (V2J.isSeq(seqCompResult_4) && (\forall int i; 0 <= i && i < V2J.size(seqCompResult_4); Utils.is_nat(V2J.get(seqCompResult_4,i))));
        }

        {
            VDMSeq ignorePattern_4 = Utils.copy(seqCompResult_4);

            //@ assert (V2J.isSeq1(ignorePattern_4) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_4); Utils.is_nat(V2J.get(ignorePattern_4,i))));

            /* skip */
        }

        IO.println("After violations");

        return 0L;
    }

    /*@ pure @*/
    public static VDMSet xs() {
        VDMSet ret_1 = SetUtil.set();

        //@ assert (V2J.isSet(ret_1) && (\forall int i; 0 <= i && i < V2J.size(ret_1); Utils.is_nat(V2J.get(ret_1,i))));
        return Utils.copy(ret_1);
    }

    public String toString() {
        return "Entry{}";
    }
}
