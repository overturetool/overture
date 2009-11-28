package org.overture.ide.plugins.poviewer.actions;

import org.overture.ide.ast.RootNode;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.pog.ProofObligationList;

public class ViewPosActionSl extends ViewPosAction {

	@Override
	protected String getNature() {
		return VdmSlProjectNature.VDM_SL_NATURE;
	}

	@Override
	protected ProofObligationList getProofObligations(RootNode root) {
		ModuleList cl = new ModuleList();
		for (Object definition : root.getRootElementList()) {
			if (definition instanceof Module)
				if (skipElement(((Module) definition).name.location.file))
					continue;
				else
					cl.add((Module) definition);
		}

		final ProofObligationList pos = cl.getProofObligations();
		return pos;
	}
}
