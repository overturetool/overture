/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.mixin.MixinModel;
import org.eclipse.dltk.ti.BasicContext;
import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.goals.ExpressionTypeGoal;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.dltk.ti.goals.MethodCallVerificationGoal;
import org.eclipse.dltk.ti.goals.PossiblePosition;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ASTUtils;

public class MethodCallVerificator extends OvertureMixinGoalEvaluator {

	private static final int INIT = 0;
	private static final int RECEIVER_WAIT = 1;

	private int state = INIT;
	private OvertureMethodReference result = null;
	private IEvaluatedType receiverType;
	private PossiblePosition position;
	private boolean topLevelMethod;

	public MethodCallVerificator(IGoal goal) {
		super(goal);
	}

	private MethodCallVerificationGoal getTypedGoal() {
		return (MethodCallVerificationGoal) this.getGoal();
	}

	public IGoal[] init() {
		topLevelMethod = false;
		MethodCallVerificationGoal goal2 = getTypedGoal();
		position = goal2.getPosition();
		IResource resource = position.getResource();

		IModelElement element = DLTKCore.create(resource);
		if (element instanceof ISourceModule) {
			ModuleDeclaration decl = ASTUtils.getAST((ISourceModule) element);
			ASTNode node = position.getNode();
			if (node == null) {
				node = ASTUtils.findMinimalNode(decl, position.getOffset(),
						position.getOffset() + position.getLength() - 1);
			}
			if (node instanceof CallExpression) {
				receiverType = null;
				CallExpression call = (CallExpression) node;
				ASTNode receiver = call.getReceiver();
				if (receiver != null) {
					ExpressionTypeGoal rgoal = new ExpressionTypeGoal(
							new BasicContext((ISourceModule) element, decl),
							receiver);
					state = RECEIVER_WAIT;
					return new IGoal[] { rgoal };
				} else {
					// ASTNode[] nodes =
					// RubyTypeInferencingUtils.getAllStaticScopes(decl,
					// node.sourceStart());
					receiverType = OvertureTypeInferencingUtils.determineSelfClass(
							mixinModel, (ISourceModule) element, decl, node
									.sourceStart());
				}
			}
		}

		//				
		return null;
	}

	public Object produceResult() { // TODO: add partial results support
		if (!(receiverType instanceof OvertureClassType))
			return null;

		MethodCallVerificationGoal goal2 = getTypedGoal();
		OvertureClassType type = (OvertureClassType) receiverType;
		String parentModelKey = goal2.getGoal().getParentModelKey();
		String name = goal2.getGoal().getName();
		String requiredKey = ((parentModelKey != null) ? (parentModelKey + MixinModel.SEPARATOR)
				: "") + name; //$NON-NLS-1$		
		OvertureMixinClass rclass = mixinModel.createOvertureClass(type);
		OvertureMixinMethod method = null;
		if (topLevelMethod) {
			method = (OvertureMixinMethod) mixinModel.createOvertureElement(name);
		} else if (rclass != null) {
			method = rclass.getMethod(name);
		}
		if (method != null) {
			String key = method.getKey();
			if (key.equals(requiredKey)
					|| (parentModelKey.equals("Object") && key.equals(name))) { //$NON-NLS-1$
				result = new OvertureMethodReference(name, parentModelKey,
						position, OvertureMethodReference.ACCURATE);
				if (position.getNode() instanceof CallExpression) {
					result.setNode((CallExpression) position.getNode());
				}
			}
		}

		return result;
	}

	public IGoal[] subGoalDone(IGoal subgoal, Object result, GoalState state) {
		if (this.state == RECEIVER_WAIT) {
			receiverType = (IEvaluatedType) result;
		}
		return null;
	}

}
