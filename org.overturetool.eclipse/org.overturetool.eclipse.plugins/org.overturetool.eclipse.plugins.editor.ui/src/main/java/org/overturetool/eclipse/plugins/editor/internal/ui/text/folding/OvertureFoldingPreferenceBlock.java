/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text.folding;

import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.text.folding.SourceCodeFoldingPreferenceBlock;
import org.eclipse.jface.preference.PreferencePage;
import org.overturetool.eclipse.plugins.editor.internal.ui.preferences.OverturePreferenceMessages;

/**
 * Overture default folding preferences.
 * 
 * 
 */
public class OvertureFoldingPreferenceBlock extends SourceCodeFoldingPreferenceBlock {

	public OvertureFoldingPreferenceBlock(OverlayPreferenceStore store, PreferencePage page) {
		super(store, page);
	}

	protected String getInitiallyFoldMethodsText() {
		return OverturePreferenceMessages.OvertureEditorPreferencePage_general;
		//TODO
		//JavascriptFoldingPreferencePage_initiallyFoldFunctions;
	}

	protected boolean supportsClassFolding() {
		return false;
	}

	
//	private OverlayPreferenceStore fOverlayStore;
//	private OverlayKey[] fKeys;
//	private Map fCheckBoxes = new HashMap();
//	private SelectionListener fCheckBoxListener = new SelectionListener() {
//		public void widgetDefaultSelected(SelectionEvent e) {
//		}
//
//		public void widgetSelected(SelectionEvent e) {
//			Button button = (Button) e.widget;
//			fOverlayStore.setValue((String) fCheckBoxes.get(button), button
//					.getSelection());
//		}
//	};
//
//	public OvertureFoldingPreferenceBlock(OverlayPreferenceStore store) {
//		fOverlayStore = store;
//		fKeys = createKeys();
//		fOverlayStore.addKeys(fKeys);
//	}
//
//	private OverlayKey[] createKeys() {
//		ArrayList<OverlayPreferenceStore.OverlayKey> overlayKeys = new ArrayList<OverlayPreferenceStore.OverlayKey>();
//
//		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(
//				OverlayPreferenceStore.BOOLEAN,
//				PreferenceConstants.EDITOR_COMMENTS_DEFAULT_FOLDED));
//		// overlayKeys.add(new
//		// OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN,
//		// PreferenceConstants.EDITOR_FOLDING_METHODS));
//		// // overlayKeys.add(new
//		// OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN,
//		// PreferenceConstants.EDITOR_FOLDING_IMPORTS));
//		// overlayKeys.add(new
//		// OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN,
//		// PreferenceConstants.EDITOR_FOLDING_HEADERS));
//
//		OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys
//				.size()];
//		overlayKeys.toArray(keys);
//		return keys;
//	}
//
//	/*
//	 * @seeorg.eclipse.dltk.internal.ui.text.folding.IScriptFoldingPreferences#
//	 * createControl(org.eclipse.swt.widgets.Group)
//	 */
//	public Control createControl(Composite composite) {
//		fOverlayStore.load();
//		fOverlayStore.start();
//
//		Composite inner = new Composite(composite, SWT.NONE);
//		GridLayout layout = new GridLayout(1, true);
//		layout.verticalSpacing = 3;
//		layout.marginWidth = 0;
//		inner.setLayout(layout);
//
//		addCheckBox(inner, "Comments",
//				PreferenceConstants.EDITOR_COMMENTS_DEFAULT_FOLDED, 0);
//		// addCheckBox(inner,
//		// FoldingMessages.DefaultFoldingPreferenceBlock_innerTypes,
//		// PreferenceConstants.EDITOR_FOLDING_INNERTYPES, 0);
//		// addCheckBox(inner,
//		// FoldingMessages.DefaultFoldingPreferenceBlock_methods,
//		// PreferenceConstants.EDITOR_FOLDING_METHODS, 0);
//		// addCheckBox(inner,
//		// FoldingMessages.DefaultFoldingPreferenceBlock_imports,
//		// PreferenceConstants.EDITOR_FOLDING_IMPORTS, 0);
//
//		return inner;
//	}
//
//	private Button addCheckBox(Composite parent, String label, String key,
//			int indentation) {
//		Button checkBox = new Button(parent, SWT.CHECK);
//		checkBox.setText(label);
//
//		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
//		gd.horizontalIndent = indentation;
//		gd.horizontalSpan = 1;
//		gd.grabExcessVerticalSpace = false;
//		checkBox.setLayoutData(gd);
//		checkBox.addSelectionListener(fCheckBoxListener);
//
//		fCheckBoxes.put(checkBox, key);
//
//		return checkBox;
//	}
//
//	private void initializeFields() {
//		Iterator it = fCheckBoxes.keySet().iterator();
//		while (it.hasNext()) {
//			Button b = (Button) it.next();
//			String key = (String) fCheckBoxes.get(b);
//			b.setSelection(fOverlayStore.getBoolean(key));
//		}
//	}
//
//	/*
//	 * @see
//	 * org.eclipse.dltk.internal.ui.text.folding.AbstractScriptFoldingPreferences
//	 * #performOk()
//	 */
//	public void performOk() {
//		fOverlayStore.propagate();
//	}
//
//	/*
//	 * @see
//	 * org.eclipse.dltk.internal.ui.text.folding.AbstractScriptFoldingPreferences
//	 * #initialize()
//	 */
//	public void initialize() {
//		initializeFields();
//	}
//
//	/*
//	 * @see
//	 * org.eclipse.dltk.internal.ui.text.folding.AbstractScriptFoldingPreferences
//	 * #performDefaults()
//	 */
//	public void performDefaults() {
//		fOverlayStore.loadDefaults();
//		initializeFields();
//	}
//
//	/*
//	 * @see
//	 * org.eclipse.dltk.internal.ui.text.folding.AbstractScriptFoldingPreferences
//	 * #dispose()
//	 */
//	public void dispose() {
//		fOverlayStore.stop();
//	}
}
