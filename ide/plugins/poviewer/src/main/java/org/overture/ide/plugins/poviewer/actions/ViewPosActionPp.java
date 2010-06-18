package org.overture.ide.plugins.poviewer.actions;


import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.vdmpp.core.IVdmPpCoreConstants;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.pog.ProofObligationList;

public class ViewPosActionPp extends ViewPosAction {

	@Override
	protected String getNature() {
		return IVdmPpCoreConstants.NATURE;
	}

	@Override
	protected ProofObligationList getProofObligations(IVdmModel model) throws NotAllowedException {
		if(!model.isTypeCorrect()){
			return null;
		}
		ClassList cl = new ClassList();
		for (Object definition : model.getClassList()) {
			if (definition instanceof ClassDefinition) {
				if (skipElement(((ClassDefinition) definition).location.file))
					continue;
				else
					cl.add((ClassDefinition) definition);
			}
		}

		final ProofObligationList pos = cl.getProofObligations();
		return pos;
	}
}
