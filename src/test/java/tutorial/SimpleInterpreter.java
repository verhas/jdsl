package tutorial;

import static com.javax0.jdsl.analyzers.terminals.IdentifierAnalyzer.identifier;
import static com.javax0.jdsl.analyzers.terminals.NumberAnalyzer.number;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javax0.jdsl.GrammarDefinition;
import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.Define;
import com.javax0.jdsl.analyzers.Rule;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.analyzers.StringSourceCode;
import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.log.LogReporter;
import com.javax0.jdsl.log.ReporterFactory;

public class SimpleInterpreter {
	private static final Logger LOG = LoggerFactory
			.getLogger(SimpleInterpreter.class);

	@Test
	public void simpleInterpreterTutorial() {
		final Analyzer myGrammar = new GrammarDefinition() {
			@Override
			protected final Analyzer define() {
				ReporterFactory.setReporter(new LogReporter());
				skipSpaces();
				final Rule tag = or(number(), identifier());
				final Rule product = or(tag,
						list(tag, or(kw("*"), kw("/")), tag));
				final Rule sum = or(product,
						list(product, or(kw("+"), kw("-")), product));
				final Define expression = later();
				expression.define(or(sum,
						list(kw("("), expression, kw(")"))));
				final Rule command = or(list(kw("print"), expression),
						list(identifier(), kw("="), expression));
				return many(command);
			}
		};
		SourceCode source = new StringSourceCode("");
		AnalysisResult result = myGrammar.analyze(source);
		Executor executor = result.getExecutor();
		SimpleInterpreterContext context = new SimpleInterpreterContext();
		executor.execute(context);
	}

}
