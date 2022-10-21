package tutorial;

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
import org.junit.Test;

import static com.javax0.jdsl.analyzers.terminals.IdentifierAnalyzer.identifier;
import static com.javax0.jdsl.analyzers.terminals.NumberAnalyzer.number;

public class SimpleInterpreter {
    private static final Analyzer myGrammar = new GrammarDefinition() {
        @Override
        protected final Analyzer define() {
            ReporterFactory.setReporter(new LogReporter());
            skipSpaces();
            // expression definition is recursive, so has to be "defined later"
            // and we also defined it "later"
            final Define expression = later();
            // a tag at the lowest place is either a number or a variable (identifier)
            final Rule tag = or(number(), one(new VariableExecutor.Factory(),identifier()), list(kw("("), expression, kw(")")));
            // a 'product' is either a tag, or tag*tag or tag/tag
            final Rule product = or(list(new ProductExecutor.Factory(), tag, or(kw_t("*"), kw_t("/")), tag), tag);
            // it is a sum of two products, or a product
            expression.define(or(list(new ExpressionExecutor.Factory(), product, or(kw_t("+"), kw_t("-")), product), product));
            // a command is "print expression" or identifier '=' expression
            final Rule command = or(list(new PrintExecutor.Factory(),kw("print"), expression), list(new AssignmentExecutor.Factory(), identifier(), kw("="), expression));
            // the program is zero or more commands
            return many(command);
        }
    };

    @Test
    public void simpleInterpreterTutorial() {

        SimpleInterpreterContext context = executeProgram("a = (2+1)*3\nprint a");
        System.out.println(context.get("a"));
    }

    private static SimpleInterpreterContext executeProgram(final String program) {
        SourceCode source = new StringSourceCode(program);
        AnalysisResult result = myGrammar.analyze(source);
        Executor executor = result.getExecutor();
        SimpleInterpreterContext context = new SimpleInterpreterContext();
        final var calcResult = executor.execute(context);
        return context;
    }

}
