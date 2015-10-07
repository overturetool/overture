/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.pog.contexts;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPogAssistantFactory;

public class POFunctionDefinitionContext extends POContext {
	public final ILexNameToken name;
	public final AFunctionType deftype;
	public final List<List<PPattern>> paramPatternList;
	public final List<PType> argtypes;
	public final boolean addPrecond;
	public final PExp precondition;

	public POFunctionDefinitionContext(AExplicitFunctionDefinition definition,
			boolean precond) {
		this.name = definition.getName();
		this.deftype = (AFunctionType) definition.getType();
		this.paramPatternList = definition.getParamPatternList();
		this.addPrecond = precond;
		this.precondition = definition.getPrecondition();
		this.argtypes = calculateTypes(deftype,definition.getIsCurried());
	}

	private List<PType> calculateTypes(AFunctionType ftype, boolean curried) {
		List<PType> r = new LinkedList<PType>();

		for (PType t : ftype.getParameters()) {
			r.add(t.clone());
		}
		if (curried) {
			r.addAll(handleCurries(ftype.getResult()));
		}
		return r;
	}

	private Collection<? extends PType> handleCurries(PType result) {
		List<PType> r = new LinkedList<PType>();

		if (result instanceof AFunctionType) {
			AFunctionType ft = (AFunctionType) result;
			for (PType p : ft.getParameters()) {
				r.add(p.clone());
			}
			r.addAll(handleCurries(ft.getResult()));
		}

		return r;
	}

	public POFunctionDefinitionContext(AImplicitFunctionDefinition definition,
			boolean precond, IPogAssistantFactory assistantFactory) {
		this.name = definition.getName();
		this.deftype = (AFunctionType) definition.getType();
		this.addPrecond = precond;
		this.paramPatternList = assistantFactory
				.createAImplicitFunctionDefinitionAssistant()
				.getParamPatternList(definition);
		this.precondition = definition.getPrecondition();
		this.argtypes = calculateTypes(deftype, false);
	}

	@Override
	public PExp getContextNode(PExp stitch) {
		AForAllExp forAllExp = new AForAllExp();
		forAllExp.setBindList(makeBinds());
		forAllExp.setType(new ABooleanBasicType());
		if (deftype.getParameters().isEmpty()) {
			return stitch;
		}

		if (addPrecond && precondition != null) {

			AImpliesBooleanBinaryExp implies = AstExpressionFactory
					.newAImpliesBooleanBinaryExp(precondition.clone(), stitch);
			implies.setType(new ABooleanBasicType());
			
			forAllExp.setPredicate(implies);
		} else {
			forAllExp.setPredicate(stitch);
		}

		return forAllExp;
	}



	private List<PMultipleBind> makeBinds() {
		List<PMultipleBind> result = new LinkedList<PMultipleBind>();

		Iterator<PType> types = argtypes.iterator();

		for (List<PPattern> params : paramPatternList) {

			for (PPattern param : params) {
				ATypeMultipleBind typeBind = new ATypeMultipleBind();
				List<PPattern> one = new Vector<PPattern>();
				one.add(param.clone());
				typeBind.setPlist(one);
				PType type = types.next();
				typeBind.setType(type.clone());
				result.add(typeBind);
			}
		}

		return result;
	}

	@Override
	public String getContext() {
		StringBuilder sb = new StringBuilder();

		if (!deftype.getParameters().isEmpty()) {
			sb.append("forall ");
			String sep = "";
			AFunctionType ftype = deftype;

			for (List<PPattern> pl : paramPatternList) {
				Iterator<PType> types = ftype.getParameters().iterator();

				for (PPattern p : pl) {
					if (!(p instanceof AIgnorePattern)) {
						sb.append(sep);
						sb.append(p.toString());
						sb.append(":");
						sb.append(types.next());
						sep = ", ";
					}
				}

				if (ftype.getResult() instanceof AFunctionType) {
					ftype = (AFunctionType) ftype.getResult();
				} else {
					break;
				}
			}

			sb.append(" &");

			if (addPrecond && precondition != null) {
				sb.append(" ");
				sb.append(precondition);
				sb.append(" =>");
			}
		}

		return sb.toString();
	}
}
