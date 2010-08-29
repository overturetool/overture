package org.overture.ide.plugins.poviewer.actions;


import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.vdmsl.core.IVdmSlCoreConstants;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.pog.ProofObligationList;

public class ViewPosActionSl extends ViewPosAction {

	@Override
	protected String getNature() {
		return IVdmSlCoreConstants.NATURE;
	}

	@Override
	protected ProofObligationList getProofObligations(IVdmModel root) throws NotAllowedException {
		ModuleList cl = new ModuleList();
		if(!root.isTypeCorrect()){
			return null;
		}
		for (Object definition : root.getModuleList()) {
			if (definition instanceof Module)
				if (!((Module) definition).getName().equals("DEFAULT") && skipElement(((Module) definition).name.location.file))
					continue;
				else
					cl.add((Module) definition);
		}

		final ProofObligationList pos = cl.getProofObligations();
		return pos;
	}
}
