/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.ISourceModuleContext;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class ConstantReferenceEvaluator extends OvertureMixinGoalEvaluator {

	private IEvaluatedType result;

	private IGoal helperGoal;

	public ConstantReferenceEvaluator(IGoal goal) {
		super(goal);
	}

	private ConstantTypeGoal getTypedGoal() {
		return (ConstantTypeGoal) DefaultOvertureEvaluatorFactory.translateGoal(goal);
	}

	private ISourceModuleContext getTypedContext() {
		return (ISourceModuleContext) goal.getContext();
	}

	public Object produceResult() {
		return result;
	}

	public IGoal[] init() {
		helperGoal = null;
		ISourceModuleContext typedContext = getTypedContext();
		ConstantTypeGoal typedGoal = getTypedGoal();
		String constantName = typedGoal.getName();
		int calculationOffset = typedGoal.getOffset();

		String elementKey = OvertureTypeInferencingUtils.searchConstantElement(
				mixinModel.getRawModel(), typedContext.getRootNode(),
				calculationOffset, constantName);

		IMixinElement constantElement = null;

		if (elementKey != null)
			constantElement = mixinModel.getRawModel().get(elementKey);

		if (constantElement == null)
			return IGoal.NO_GOALS;

		Object[] realObjs = constantElement.getAllObjects();
		for (int i = 0; i < realObjs.length; i++) {
			OvertureMixinElementInfo realObj = (OvertureMixinElementInfo) realObjs[i];
			if (realObj == null)
				continue;
			if (realObj.getKind() == OvertureMixinElementInfo.K_CLASS
					|| realObj.getKind() == OvertureMixinElementInfo.K_MODULE) {
				result = new OvertureClassType(constantElement.getKey());
				break;
			} else if (realObj.getKind() == OvertureMixinElementInfo.K_VARIABLE) {
				// String parent = constantElement.getParent().getKey();
				// String name = constantElement.getLastKeySegment();
				// helperGoal = new VariableTypeGoal (goal.getContext(), name,
				// parent, RubyVariableKind.CONSTANT);
				// Object[] allObjects = constantElement.getAllObjects();
				helperGoal = new NonTypeConstantTypeGoal(goal.getContext(),
						constantElement);
				break;
			}
		}
		if (helperGoal != null) {
			return new IGoal[] { helperGoal };
		}
		return IGoal.NO_GOALS;
	}

	public IGoal[] subGoalDone(IGoal subgoal, Object result, GoalState state) {
		this.result = (IEvaluatedType) result;
		return IGoal.NO_GOALS;
	}

}
