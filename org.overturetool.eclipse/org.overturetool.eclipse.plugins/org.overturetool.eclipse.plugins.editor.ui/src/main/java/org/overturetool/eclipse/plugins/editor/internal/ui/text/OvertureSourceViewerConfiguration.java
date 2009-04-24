/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.compiler.task.ITodoTaskPreferences;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.DLTKColorConstants;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.TodoTaskPreferencesOnPreferenceStore;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.dltk.ui.text.completion.ContentAssistProcessor;
import org.eclipse.dltk.ui.text.rules.CombinedWordRule;
import org.eclipse.dltk.ui.text.rules.CombinedWordRule.WordMatcher;
import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.completion.OvertureCompletionProcessor;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.completion.OvertureContentAssistPreference;
import org.overturetool.eclipse.plugins.editor.ui.text.IOverturePartitions;

public class OvertureSourceViewerConfiguration extends
		ScriptSourceViewerConfiguration {

	private OvertureTextTools fTextTools;

	private OvertureCodeScanner fCodeScanner;

	private AbstractScriptScanner fStringScanner;

	private AbstractScriptScanner fCommentScanner;

	private OvertureDocScanner fDocScanner;

	public OvertureSourceViewerConfiguration(IColorManager colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				IOverturePartitions.OVERTURE_STRING,
				IOverturePartitions.OVERTURE_COMMENT, IOverturePartitions.OVERTURE_DOC };
	}

	public String getCommentPrefix() {
		return "--";
	}

	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		// XXX: what happens here?.. why " " ?
		return new String[] { "\t", "    ", "" };
	}

	/*
	 * @see SourceViewerConfiguration#getContentFormatter(ISourceViewer)
	 */
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(
				getConfiguredDocumentPartitioning(sourceViewer),
				IDocument.DEFAULT_CONTENT_TYPE);

		formatter.setMasterStrategy(new OvertureFormattingStrategy());
		// formatter.setSlaveStrategy(new CommentFormattingStrategy(),
		// IJavaPartitions.JAVA_DOC);
		// formatter.setSlaveStrategy(new CommentFormattingStrategy(),
		// IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
//		formatter.setSlaveStrategy(new CommentFormattingStrategy(), IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

		return formatter;
	}

	/*
	 * @seeorg.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration#
	 * alterContentAssistant
	 * (org.eclipse.jface.text.contentassist.ContentAssistant)
	 */
	protected void alterContentAssistant(ContentAssistant assistant) {
		IContentAssistProcessor scriptProcessor = new OvertureCompletionProcessor( getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(scriptProcessor, IDocument.DEFAULT_CONTENT_TYPE);

		ContentAssistProcessor singleLineProcessor = new OvertureCompletionProcessor(getEditor(), assistant, IOverturePartitions.OVERTURE_COMMENT);
		assistant.setContentAssistProcessor(singleLineProcessor, IOverturePartitions.OVERTURE_COMMENT);

		ContentAssistProcessor stringProcessor = new OvertureCompletionProcessor(getEditor(), assistant, IOverturePartitions.OVERTURE_STRING);
		assistant.setContentAssistProcessor(stringProcessor, IOverturePartitions.OVERTURE_STRING);

		// TODO OVERTURE DOC completion??
		ContentAssistProcessor vdmProcessor = new OvertureCompletionProcessor(getEditor(), assistant, IOverturePartitions.OVERTURE_DOC);
		assistant.setContentAssistProcessor(vdmProcessor, IOverturePartitions.OVERTURE_DOC);

	}

	/*
	 * @seeorg.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration#
	 * getContentAssistPreference()
	 */
	protected ContentAssistPreference getContentAssistPreference() {
		return OvertureContentAssistPreference.getDefault();
	}

	/*
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getTabWidth(org
	 * .eclipse.jface.text.source.ISourceViewer)
	 */
	public int getTabWidth(ISourceViewer sourceViewer) {
		if (fPreferenceStore == null)
			return super.getTabWidth(sourceViewer);
		return fPreferenceStore.getInt(CodeFormatterConstants.FORMATTER_TAB_SIZE);
	}

	protected void initializeScanners() {
		Assert.isTrue(isNewSetup());
		fCodeScanner = new OvertureCodeScanner(getColorManager(), fPreferenceStore);
		fStringScanner = new OvertureStringScanner(getColorManager(), fPreferenceStore);

		fCommentScanner = new ScriptCommentScanner(getColorManager(),
				fPreferenceStore,
				OvertureColorConstants.OVERTURE_SINGLE_LINE_COMMENT,
				OvertureColorConstants.OVERTURE_TODO_TAG,
				new TodoTaskPreferencesOnPreferenceStore(fPreferenceStore));
		fDocScanner = new OvertureDocScanner(getColorManager(), fPreferenceStore);
	}

	/**
	 * @return <code>true</code> iff the new setup without text tools is in use.
	 */
	private boolean isNewSetup() {
		return fTextTools == null;
	}

	/**
	 * 
	 * Returns the Overture string scanner for this configuration.
	 * 
	 * @return the Overture string scanner
	 */
	protected RuleBasedScanner getStringScanner() {
		return fStringScanner;
	}

	/**
	 * Returns the Overture comment scanner for this configuration.
	 * 
	 * @return the Overture comment scanner
	 */
	protected RuleBasedScanner getCommentScanner() {
		return fCommentScanner;
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		ScriptPresentationReconciler reconciler = new ScriptPresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(this.fCodeScanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IOverturePartitions.OVERTURE_STRING);
		reconciler.setRepairer(dr, IOverturePartitions.OVERTURE_STRING);

		dr = new DefaultDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, IOverturePartitions.OVERTURE_COMMENT);
		reconciler.setRepairer(dr, IOverturePartitions.OVERTURE_COMMENT);

		dr = new DefaultDamagerRepairer(fDocScanner);
		reconciler.setDamager(dr, IOverturePartitions.OVERTURE_DOC);
		reconciler.setRepairer(dr, IOverturePartitions.OVERTURE_DOC);

		return reconciler;
	}

	/**
	 * Adapts the behavior of the contained components to the change encoded in
	 * the given event.
	 * <p>
	 * Clients are not allowed to call this method if the old setup with text
	 * tools is in use.
	 * </p>
	 * 
	 * @param event
	 *            the event to which to adapt
	 * @see OvertureSourceViewerConfiguration#ScriptSourceViewerConfiguration(IColorManager,
	 *      IPreferenceStore, ITextEditor, String)
	 */
	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		Assert.isTrue(isNewSetup());
		if (fCodeScanner.affectsBehavior(event))
			fCodeScanner.adaptToPreferenceChange(event);
		if (fStringScanner.affectsBehavior(event))
			fStringScanner.adaptToPreferenceChange(event);
	}

	/**
	 * Determines whether the preference change encoded by the given event
	 * changes the behavior of one of its contained components.
	 * 
	 * @param event
	 *            the event to be investigated
	 * @return <code>true</code> if event causes a behavioral change
	 * 
	 */
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return fCodeScanner.affectsBehavior(event)
				|| fStringScanner.affectsBehavior(event);
	}

	public IInformationPresenter getHierarchyPresenter(
			ScriptSourceViewer viewer, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	//TODO: 
	/*
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		// TODO: check contentType. think, do we really need it? :)
		String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
		return new IAutoEditStrategy[] { new OvertureAutoEditStrategy(
				partitioning, null) };
	}*/

	public IInformationControlCreator getInformationControlCreator(
			ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE,
						new HTMLTextPresenter(true));
			}
		};
	}

	protected IInformationControlCreator getOutlinePresenterControlCreator(
			ISourceViewer sourceViewer, final String commandId) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle = SWT.RESIZE;
				int treeStyle = SWT.V_SCROLL | SWT.H_SCROLL;
				return new OvertureOutlineInformationControl(parent,
						shellStyle, treeStyle, commandId);
			}
		};
	}
}

class ScriptCommentScanner extends AbstractScriptScanner {

	private static final char[] COMMENT_CHAR = "--".toCharArray();

	private final String[] fProperties;
	private final String fDefaultTokenProperty;

	private TaskTagMatcher fTaskTagMatcher;

	private final ITodoTaskPreferences preferences;

	private static class ScriptIdentifierDetector implements IWordDetector {

		public boolean isWordStart(char c) {
			return Character.isJavaIdentifierStart(c);
		}

		public boolean isWordPart(char c) {
			return Character.isJavaIdentifierPart(c);
		}
	}

	private class TaskTagMatcher extends CombinedWordRule.WordMatcher {

		private IToken fToken;
		/**
		 * Uppercase words
		 * 
		 * @since 3.0
		 */
		private Map fUppercaseWords = new HashMap();
		/**
		 * <code>true</code> if task tag detection is case-sensitive.
		 * 
		 * @since 3.0
		 */
		private boolean fCaseSensitive = true;
		/**
		 * Buffer for uppercase word
		 * 
		 * @since 3.0
		 */
		private CombinedWordRule.CharacterBuffer fBuffer = new CombinedWordRule.CharacterBuffer(16);

		public TaskTagMatcher(IToken token) {
			fToken = token;
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#clearWords
		 * ()
		 * 
		 * @since 3.0
		 */
		public synchronized void clearWords() {
			super.clearWords();
			fUppercaseWords.clear();
		}

		public synchronized void addTaskTags(String[] tasks) {
			for (int i = 0; i < tasks.length; i++) {
				if (tasks[i].length() > 0) {
					addWord(tasks[i], fToken);
				}
			}
		}

		public synchronized void addTaskTags(String value) {
			String[] tasks = split(value, ","); //$NON-NLS-1$
			addTaskTags(tasks);
		}

		private String[] split(String value, String delimiters) {
			StringTokenizer tokenizer = new StringTokenizer(value, delimiters);
			int size = tokenizer.countTokens();
			String[] tokens = new String[size];
			int i = 0;
			while (i < size)
				tokens[i++] = tokenizer.nextToken();
			return tokens;
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#addWord
		 * (java.lang.String, org.eclipse.jface.text.rules.IToken)
		 * 
		 * @since 3.0
		 */
		public synchronized void addWord(String word, IToken token) {
			Assert.isNotNull(word);
			Assert.isNotNull(token);

			super.addWord(word, token);
			fUppercaseWords.put(new CombinedWordRule.CharacterBuffer(word.toUpperCase()), token);
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#evaluate
		 * (org.eclipse.jface.text.rules.ICharacterScanner,
		 * org.eclipse.jdt.internal.ui.text.CombinedWordRule.CharacterBuffer)
		 * 
		 * @since 3.0
		 */
		public synchronized IToken evaluate(ICharacterScanner scanner, CombinedWordRule.CharacterBuffer word) {
			if (fCaseSensitive)
				return super.evaluate(scanner, word);

			fBuffer.clear();
			for (int i = 0, n = word.length(); i < n; i++)
				fBuffer.append(Character.toUpperCase(word.charAt(i)));

			IToken token = (IToken) fUppercaseWords.get(fBuffer);
			if (token != null)
				return token;
			return Token.UNDEFINED;
		}

		/**
		 * Is task tag detection case-sensitive?
		 * 
		 * @return <code>true</code> iff task tag detection is case-sensitive
		 * @since 3.0
		 */
		public boolean isCaseSensitive() {
			return fCaseSensitive;
		}

		/**
		 * Enables/disables the case-sensitivity of the task tag detection.
		 * 
		 * @param caseSensitive
		 *            <code>true</code> iff case-sensitivity should be enabled
		 * @since 3.0
		 */
		public void setCaseSensitive(boolean caseSensitive) {
			fCaseSensitive = caseSensitive;
		}
	}

	public ScriptCommentScanner(IColorManager manager, IPreferenceStore store,
			String comment, String todoTag, ITodoTaskPreferences preferences) {
		super(manager, store);

		fProperties = new String[] { comment, todoTag };
		fDefaultTokenProperty = comment;

		this.preferences = preferences;
		initialize();
	}

	protected String[] getTokenProperties() {
		return fProperties;
	}

	protected List createRules() {
		IToken defaultToken = getToken(fDefaultTokenProperty);
		setDefaultReturnToken(defaultToken);

		List<IRule> list = new ArrayList<IRule>();
		list.add(createTodoRule());

		return list;
	}

	protected IRule createTodoRule() {
		CombinedWordRule combinedWordRule = new CombinedWordRule(
				new ScriptIdentifierDetector(), Token.UNDEFINED);

		List matchers = createMatchers();
		if (matchers.size() > 0) {
			for (int i = 0, n = matchers.size(); i < n; i++) {
				combinedWordRule.addWordMatcher((WordMatcher) matchers.get(i));
			}
		}

		return combinedWordRule;
	}

	/**
	 * Creates a list of word matchers.
	 * 
	 * @return the list of word matchers
	 */
	@SuppressWarnings("unchecked")
	protected List createMatchers() {
		List list = new ArrayList();

		boolean isCaseSensitive = preferences.isCaseSensitive();
		String[] tasks = preferences.getTagNames();

		if (tasks != null) {
			fTaskTagMatcher = new TaskTagMatcher(getToken(DLTKColorConstants.TASK_TAG));
			fTaskTagMatcher.addTaskTags(tasks);
			fTaskTagMatcher.setCaseSensitive(isCaseSensitive);
			list.add(fTaskTagMatcher);
		}
		return list;
	}

	/**
	 * Returns the character used to identifiy a comment.
	 * 
	 * <p>
	 * Default implementation returns <code>#</code>. Clients may override if
	 * their languange uses a different identifier.
	 * </p>
	 */
	protected char[] getCommentChar() {
		return COMMENT_CHAR;
	}

	public void setRange(IDocument document, int offset, int length) {
		super.setRange(document, offset, length);
		state = STATE_START;
	}

	public void adaptToPreferenceChange(PropertyChangeEvent event) {
		if (fTaskTagMatcher != null
				&& event.getProperty().equals(ITodoTaskPreferences.TAGS)) {

			Object value = event.getNewValue();
			if (value instanceof String) {
				synchronized (fTaskTagMatcher) {
					fTaskTagMatcher.clearWords();
					fTaskTagMatcher.addTaskTags(preferences.getTagNames());
				}
			}
		} else if (fTaskTagMatcher != null
				&& event.getProperty().equals(
						ITodoTaskPreferences.CASE_SENSITIVE)) {
			Object value = event.getNewValue();
			if (value instanceof String) {
				boolean caseSensitive = Boolean.valueOf((String) value)
						.booleanValue();
				fTaskTagMatcher.setCaseSensitive(caseSensitive);
			}
		} else {
			super.adaptToPreferenceChange(event);
		}
	}

	public boolean affectsBehavior(PropertyChangeEvent event) {
		if (event.getProperty().equals(ITodoTaskPreferences.TAGS)) {
			return true;
		}

		if (event.getProperty().equals(ITodoTaskPreferences.CASE_SENSITIVE)) {
			return true;
		}

		return super.affectsBehavior(event);
	}

	private int state = STATE_START;

	private static final int STATE_START = 0;
	private static final int STATE_STARTED = 1;
	private static final int STATE_BODY = 2;

	/*
	 * We overload nextToken() because of the way task parsing is implemented:
	 * the TO-DO tasks are recognized only at the beginning of the comment
	 */
	public IToken nextToken() {
		char[] commentChar = getCommentChar();

		fTokenOffset = fOffset;
		fColumn = UNDEFINED;
		if (state == STATE_START) {
			state = STATE_STARTED;
			int count = 0;
			int c = read();
			while (count < commentChar.length) {
				if (c == commentChar[count]) {
					c = read();
					++count;
				} else {
					break;
				}
			}
			while (c != EOF && Character.isWhitespace((char) c)) {
				c = read();
				++count;
			}
			unread();
			if (count > 0) {
				return fDefaultReturnToken;
			} else if (c == EOF) {
				return Token.EOF;
			}
		}
		if (state == STATE_STARTED) {
			state = STATE_BODY;
			final IToken token = fRules[0].evaluate(this);
			if (!token.isUndefined()) {
				return token;
			}
		}
		return read() != EOF ? fDefaultReturnToken : Token.EOF;
	}
}
