/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.plugins.poviewer.actions;


import org.overture.ast.definitions.SClassDefinition;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.vdmpp.core.IVdmPpCoreConstants;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.visitor.PogVisitor;

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
		PogVisitor pogVisitor = new PogVisitor();
		ProofObligationList obligations = new ProofObligationList();
		for (Object definition : model.getClassList()) {
			if (definition instanceof SClassDefinition) {
				if (skipElement(((SClassDefinition) definition).getLocation().file))
					continue;
				else
				{
					ProofObligationList tmp = pogVisitor.defaultPDefinition((SClassDefinition) definition,new POContextStack());
					tmp.trivialCheck();
					obligations.addAll(tmp);
				}
			}
		}

		final ProofObligationList pos = obligations;
		return pos;
	}
}
