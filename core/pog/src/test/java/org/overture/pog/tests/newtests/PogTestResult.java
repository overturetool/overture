package org.overture.pog.tests.newtests;

import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by ldc on 25/05/16.
 */
public class PogTestResult extends HashSet<String> {

    public static PogTestResult convert (IProofObligationList pol){

        PogTestResult r = new PogTestResult();
        pol.renumber();
        for (IProofObligation po : pol)
        {
            r.add(po.getKindString() + " obligation "
                    +po.getLocation().toString()+": "
                    + po.getFullPredString());
        }
        return r;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = this.iterator();
        sb.append("{");
        while (iter.hasNext()){
            sb.append(iter.next());
            if (iter.hasNext()){
                sb.append(", \n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
