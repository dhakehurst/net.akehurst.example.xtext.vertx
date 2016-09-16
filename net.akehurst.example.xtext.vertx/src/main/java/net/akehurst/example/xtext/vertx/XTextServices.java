package net.akehurst.example.xtext.vertx;

import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import com.google.inject.Inject;

public class XTextServices {

	@Inject
	ISemanticHighlightingCalculator highlightingCalculator;

}
