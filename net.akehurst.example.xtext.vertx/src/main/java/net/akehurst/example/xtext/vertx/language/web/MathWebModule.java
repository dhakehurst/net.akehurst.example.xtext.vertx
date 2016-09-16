package net.akehurst.example.xtext.vertx.language.web;

import java.util.concurrent.ExecutorService;

import org.eclipse.xtext.ide.LexerIdeBindings;
import org.eclipse.xtext.ide.editor.contentassist.FQNPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AntlrProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.IContentAssistParser;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer;
import org.eclipse.xtext.web.server.DefaultWebModule;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import net.akehurst.example.xtext.vertx.language.ide.contentassist.antlr.MathParser;
import net.akehurst.example.xtext.vertx.language.ide.contentassist.antlr.internal.InternalMathLexer;

@SuppressWarnings("all")
public class MathWebModule extends DefaultWebModule {

	public MathWebModule(final Provider<ExecutorService> executorServiceProvider) {
		super(executorServiceProvider);
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends IProposalConflictHelper> bindIProposalConflictHelper() {
		return AntlrProposalConflictHelper.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public void configureContentAssistLexer(final Binder binder) {
		binder.bind(Lexer.class).annotatedWith(Names.named(LexerIdeBindings.CONTENT_ASSIST)).to(InternalMathLexer.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends IContentAssistParser> bindIContentAssistParser() {
		return MathParser.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.exporting.QualifiedNamesFragment2
	public Class<? extends IPrefixMatcher> bindIPrefixMatcher() {
		return FQNPrefixMatcher.class;
	}

}
