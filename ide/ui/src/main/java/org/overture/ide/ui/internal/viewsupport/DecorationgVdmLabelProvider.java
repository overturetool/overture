package org.overture.ide.ui.internal.viewsupport;


import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.ui.PlatformUI;
import org.overture.ide.ui.VdmProblemsLabelDecorator;

public class DecorationgVdmLabelProvider extends VdmColoringLabelProvider {

	/**
	 * Decorating label provider for Java. Combines a JavaUILabelProvider
	 * with problem and override indicator with the workbench decorator (label
	 * decorator extension point).
	 * @param labelProvider the label provider to decorate
	 */
	public DecorationgVdmLabelProvider(VdmUILabelProvider labelProvider) {
		this(labelProvider, true);
	}

	/**
	 * Decorating label provider for Java. Combines a JavaUILabelProvider
	 * (if enabled with problem indicator) with the workbench
	 * decorator (label decorator extension point).
	 * 	@param labelProvider the label provider to decorate
	 * @param errorTick show error ticks
	 */
	public DecorationgVdmLabelProvider(VdmUILabelProvider labelProvider, boolean errorTick) {
		this(labelProvider, errorTick, true);
	}

	/**
	 * Decorating label provider for Java. Combines a JavaUILabelProvider
	 * (if enabled with problem indicator) with the workbench
	 * decorator (label decorator extension point).
	 * 	@param labelProvider the label provider to decorate
	 * @param errorTick show error ticks
	 * @param flatPackageMode configure flat package mode
	 */
	public DecorationgVdmLabelProvider(VdmUILabelProvider labelProvider, boolean errorTick, boolean flatPackageMode) {
		super(labelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), DecorationContext.DEFAULT_CONTEXT);
		if (errorTick) {
			labelProvider.addLabelDecorator(new VdmProblemsLabelDecorator(null));
		}
		setFlatPackageMode(flatPackageMode);
	}

	/**
	 * Tells the label decorator if the view presents packages flat or hierarchical.
	 * @param enable If set, packages are presented in flat mode.
	 */
	public void setFlatPackageMode(boolean enable) {
//		if (enable) {
//			setDecorationContext(DecorationContext.DEFAULT_CONTEXT);
//		} else {
//			setDecorationContext(HierarchicalDecorationContext.getContext());
//		}
	}

	
}
