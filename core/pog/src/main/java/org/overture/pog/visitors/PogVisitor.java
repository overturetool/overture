package org.overture.pog.visitors;

import org.overture.pog.contexts.POContextStack;
import org.overture.pog.obligation.ProofObligationList;

/**
 * This is the proof obligation visitor climbs through the AST and builds the list of proof obligations the given
 * program exhibits. References: [1] http://wiki.overturetool.org/images/9/95/VDM10_lang_man.pdf for BNF definitions.
 * This work is based on previous work by Nick Battle in the VDMJ package.
 * 
 * @author Overture team
 * @since 1.0
 */
public class PogVisitor extends
		PogParamVisitor<POContextStack, ProofObligationList>
{

}
