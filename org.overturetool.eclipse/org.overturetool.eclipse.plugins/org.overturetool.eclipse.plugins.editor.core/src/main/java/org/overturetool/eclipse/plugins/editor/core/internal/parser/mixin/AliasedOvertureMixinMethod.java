package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.internal.core.ModelElement;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureAliasExpression;
import org.overturetool.eclipse.plugins.editor.core.model.FakeMethod;

public class AliasedOvertureMixinMethod extends OvertureMixinMethod {

	private final OvertureMixinAlias alias;

	public AliasedOvertureMixinMethod(OvertureMixinModel model, OvertureMixinAlias alias) {
		super(model, alias.getKey());
		this.alias = alias;
		final OvertureAliasExpression node = alias.getAlias();
		final int length = node.sourceEnd() - node.sourceStart();
		final IMethod sourceMethod = findSourceMethod(model, alias);
		final ModelElement fakeMethodParent;
		if (sourceMethod != null
				&& sourceMethod.getParent() instanceof ModelElement) {
			fakeMethodParent = (ModelElement) sourceMethod.getParent();
		} else {
			fakeMethodParent = (ModelElement) alias.getSourceModule();
		}
		FakeMethod fakeMethod = new FakeMethod(fakeMethodParent, node
				.getNewValue(), node.sourceStart(), length, node.sourceStart(),
				length);
		if (sourceMethod != null) {
			try {
				fakeMethod.setFlags(sourceMethod.getFlags());
				fakeMethod.setParameters(sourceMethod.getParameters());
				fakeMethod.setParameterInitializers(sourceMethod
						.getParameterInitializers());
			} catch (ModelException e) {
			}
		}
		this.setSourceMethods(new IMethod[] { fakeMethod });
	}

	private static IMethod findSourceMethod(OvertureMixinModel model,
			OvertureMixinAlias alias) {
		final IMethod[] sourceMethods = OvertureMixinMethod.getSourceMethods(model,
				alias.getOldKey());
		if (sourceMethods.length == 1 && sourceMethods[0] != null) {
			return sourceMethods[0];
		} else {
			return null;
		}
	}

	public String getName() {
		return alias.getNewName();
	}

	public OvertureMixinVariable[] getVariables() {
		List result = new ArrayList();
		IMixinElement mixinElement = model.getRawModel().get(alias.getOldKey());
		IMixinElement[] children = mixinElement.getChildren();
		for (int i = 0; i < children.length; i++) {
			IOvertureMixinElement element = model.createOvertureElement(children[i]);
			if (element instanceof OvertureMixinVariable)
				result.add(element);
		}
		return (OvertureMixinVariable[]) result
				.toArray(new OvertureMixinVariable[result.size()]);
	}

}
