package org.overture.ide.plugins.poviewer.actions;

import org.overture.ide.ast.RootNode;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.pog.ProofObligationList;

public class ViewPosActionPp extends ViewPosAction {

	@Override
	protected String getNature() {
		return VdmPpProjectNature.VDM_PP_NATURE;
	}

	@Override
	protected ProofObligationList getProofObligations(RootNode root) {
		ClassList cl = new ClassList();
		for (Object definition : root.getRootElementList()) {
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
