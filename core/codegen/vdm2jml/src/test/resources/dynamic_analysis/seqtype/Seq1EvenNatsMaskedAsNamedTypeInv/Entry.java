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
            VDMSeq ignorePattern_1 = SeqUtil.seq(2L, 4L, 6L);

            //@ assert ((V2J.isSeq1(ignorePattern_1) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_1); Utils.is_nat(V2J.get(ignorePattern_1,i)))) && inv_Entry_Seq1Even(ignorePattern_1));

            /* skip */
        }

        IO.println("After legal use");
        IO.println("Before illegal use");

        {
            VDMSeq ignorePattern_2 = SeqUtil.seq(2L, 4L, 6L, 9L);

            //@ assert ((V2J.isSeq1(ignorePattern_2) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_2); Utils.is_nat(V2J.get(ignorePattern_2,i)))) && inv_Entry_Seq1Even(ignorePattern_2));

            /* skip */
        }

        {
            VDMSeq ignorePattern_3 = emptySeqOfNat();

            //@ assert ((V2J.isSeq1(ignorePattern_3) && (\forall int i; 0 <= i && i < V2J.size(ignorePattern_3); Utils.is_nat(V2J.get(ignorePattern_3,i)))) && inv_Entry_Seq1Even(ignorePattern_3));

            /* skip */
        }

        IO.println("After illegal use");

        return 0L;
    }

    /*@ pure @*/
    public static VDMSeq emptySeqOfNat() {
        VDMSeq ret_1 = SeqUtil.seq();

        //@ assert (V2J.isSeq(ret_1) && (\forall int i; 0 <= i && i < V2J.size(ret_1); Utils.is_nat(V2J.get(ret_1,i))));
        return Utils.copy(ret_1);
    }

    public String toString() {
        return "Entry{}";
    }

    /*@ pure @*/
    /*@ helper @*/
    public static Boolean inv_Entry_Seq1Even(final Object check_xs) {
        VDMSeq xs = ((VDMSeq) check_xs);

        Boolean forAllExpResult_1 = true;
        VDMSet set_1 = SeqUtil.elems(Utils.copy(xs));

        for (Iterator iterator_1 = set_1.iterator();
                iterator_1.hasNext() && forAllExpResult_1;) {
            Number x = ((Number) iterator_1.next());
            forAllExpResult_1 = Utils.equals(Utils.mod(x.longValue(), 2L), 0L);
        }

        return forAllExpResult_1;
    }
}
