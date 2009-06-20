/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.evaluation.types.UnknownType;
import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.ISourceModuleContext;
import org.eclipse.dltk.ti.InstanceContext;
import org.eclipse.dltk.ti.goals.ExpressionTypeGoal;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.dltk.ti.goals.MethodReturnTypeGoal;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ASTUtils;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureSelfReference;

public class MethodCallTypeEvaluator extends OvertureMixinGoalEvaluator {

	private final static int STATE_INIT = 0;

	private final static int STATE_WAITING_RECEIVER = 1;

	private final static int STATE_GOT_RECEIVER = 2;

	private final static int STATE_WAITING_ARGUMENT_0 = 3;

	private final static int STATE_WAITING_ARGUMENT_LAST = 9999;

	private final static int STATE_ARGS_DONE = 10000;

	private final static int STATE_WAITING_METHOD = 10001;

	private final static int STATE_UNKNOWN = -1;

	private final static int STATE_DONE = -2;

	private int state = STATE_INIT;

	private IEvaluatedType receiverType;

	private IEvaluatedType[] arguments;

	private IEvaluatedType result;

	public MethodCallTypeEvaluator(ExpressionTypeGoal goal) {
		super(goal);
	}

	private IGoal produceNextSubgoal(IGoal previousGoal, Object previousResult) {
		if (state == STATE_INIT) {
			ExpressionTypeGoal typedGoal = (ExpressionTypeGoal) goal;
			CallExpression expression = (CallExpression) typedGoal
					.getExpression();
			ASTNode receiver = expression.getReceiver();
			if (receiver == null || receiver instanceof OvertureSelfReference) {
				// handling SelfReference here just for simplicity, could be
				// left to the TI engine as well
				IEvaluatedType scopeType = OvertureTypeInferencingUtils
						.determineSelfClass(mixinModel, goal.getContext(),
								expression.sourceStart());
				if ((scopeType != null)
						&& (!(scopeType instanceof OvertureClassType) || !("Object" //$NON-NLS-1$
						.equals(((OvertureClassType) scopeType).getTypeName())))) {
					receiverType = scopeType;
					state = STATE_GOT_RECEIVER;
				} else { // ssanders: Allow other evaluators to narrow type
					ASTNode scopeNode = receiver;
					if (scopeNode == null) {
						scopeNode = ASTUtils.findMinimalNode(
								((ISourceModuleContext) goal.getContext())
										.getRootNode(), expression
										.sourceStart(), expression
										.sourceStart());
						ASTNode[] wayToNode = ASTUtils.restoreWayToNode(
								((ISourceModuleContext) goal.getContext())
										.getRootNode(), scopeNode);
						for (int cnt = (wayToNode.length - 2); cnt >= 0; cnt--) {
							if ((wayToNode[cnt] instanceof TypeDeclaration)
									|| (wayToNode[cnt] instanceof ModuleDeclaration)) {
								scopeNode = wayToNode[cnt];

								break;
							}
						}
					}
					state = STATE_WAITING_RECEIVER;
					return new ExpressionTypeGoal(goal.getContext(), scopeNode);
				}
			} else {
				state = STATE_WAITING_RECEIVER;
				return new ExpressionTypeGoal(goal.getContext(), receiver);
			}
		}
		if (state == STATE_WAITING_RECEIVER) {
			receiverType = (IEvaluatedType) previousResult;
			if (receiverType == null) {
				state = STATE_UNKNOWN;
				return null;
			}
			state = STATE_GOT_RECEIVER;
		}
		if (state == STATE_GOT_RECEIVER) {
			ExpressionTypeGoal typedGoal = (ExpressionTypeGoal) goal;
			CallExpression expression = (CallExpression) typedGoal
					.getExpression();
			List arguments = expression.getArgs().getChilds();
			this.arguments = new IEvaluatedType[arguments.size()];
		}
		if (state >= STATE_WAITING_ARGUMENT_0
				&& state <= STATE_WAITING_ARGUMENT_LAST) {
			arguments[state - STATE_WAITING_ARGUMENT_0] = (IEvaluatedType) previousResult;
		}
		if (state == STATE_GOT_RECEIVER || state >= STATE_WAITING_ARGUMENT_0
				&& state <= STATE_WAITING_ARGUMENT_LAST) {
			int nextArg = (state == STATE_GOT_RECEIVER ? 0 : state
					- STATE_WAITING_ARGUMENT_0 + 1);
			ExpressionTypeGoal typedGoal = (ExpressionTypeGoal) goal;
			CallExpression expression = (CallExpression) typedGoal
					.getExpression();
			List arguments = expression.getArgs().getChilds();
			if (nextArg < arguments.size()) {
				state = STATE_WAITING_ARGUMENT_0 + nextArg;
				return new ExpressionTypeGoal(goal.getContext(),
						(ASTNode) arguments.get(nextArg));
			} else {
				state = STATE_ARGS_DONE;
			}
		}
		if (state == STATE_ARGS_DONE) {
			ExpressionTypeGoal typedGoal = (ExpressionTypeGoal) goal;
			CallExpression expression = (CallExpression) typedGoal
					.getExpression();
			state = STATE_WAITING_METHOD;
			if (receiverType == UnknownType.INSTANCE)
				receiverType = null;
			return new MethodReturnTypeGoal(new InstanceContext(
					(ISourceModuleContext) goal.getContext(), receiverType),
					expression.getName(), arguments);
		}
		if (state == STATE_WAITING_METHOD) {
			result = (IEvaluatedType) previousResult;
			state = STATE_DONE;
		}
		return null;
	}

	public Object produceResult() {
		if (state == STATE_UNKNOWN)
			return null;
		else
			return result;
	}

	public IGoal[] init() {
		IGoal goal = produceNextSubgoal(null, null);
		if (goal != null)
			return new IGoal[] { goal };
		return IGoal.NO_GOALS;
	}

	public IGoal[] subGoalDone(IGoal subgoal, Object result, GoalState state) {
		IGoal goal = produceNextSubgoal(subgoal, result);
		if (goal != null)
			return new IGoal[] { goal };
		return IGoal.NO_GOALS;
	}

}
