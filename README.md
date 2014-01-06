jdsl
====

jdsl stands for Java Domain Specific Language, and it is a software package to easily create domain specific languages to extend your java application.

jdsl is a library. You can use it just as any other library. There is no special grammar file to write and there is no need to modify your usual build process. Jdsl will not generate any JVM code or Java code for you. All it does is that it gives you nice and handy API to define your language and then to analyze and execute the code defined in your DSL.

* Simple DSL definition
* Java execution of the DSL code using your executors or executors provided off the shelf.
* Excessive and expressive logging to analyze your grammar in case something works different than expected.

Using jdsl you define the grammar in Java using fluent API. When you have your grammar all you should do is invoke `analyze()` to get your source code analyzed and then call `execute()` to execute the code. You can get a DSL in five minutes. Here comes an example.

* We want to have some numbers defined. The user of the DSL can have
  * a number as an `expression`,
  * an `if(expression){expression}`
  * or `if(expression){expression}else{expression}`

The easiest and recommended way defining a grammar for the above is 

```
		final Analyzer myGrammar = new GrammarDefinition() {
			@Override
			final Analyzer define() {
				ReporterFactory.setReporter(new NullReporter());
				skipSpaces();
				final Define expression = later();
				final Analyzer ifStatement = list(new IfExecutorFactory(),
						kw("if", "("), expression, kw(")", "{"), expression,
						kw("}"), optional(kw("else", "{"), expression, kw("}")));
				expression.define(or(ifStatement, number(),
						list(kw("{"), many(expression), kw("}"))));
				return many(expression);
			}
		};
```
  
to create a `GrammarDefinition`. The class `GrammarDefinition` is an abstract one and  creating a concrete extension of it has to define the method `define()`. The methods defined in the class `GrammarDefinition` can be used as in the example above to describe the grammar that the analyzer will analyze.

The first call `skipSpaces()` instructs the analyzer that spaces that separate keywords, numbers and so on are not important. The next call defines a `PassThroughAnalyzer`, which is defined later. The method `definedLater()` creates such an analyzer. This is needed because the definition of the grammar is recursive. We need the analyzer `expression` but we just can not define it yet.

The analyzer `ifStatement` is a list of analyzers. When it matches some source code then it creates an `IfExecutor` class instance and the elements of the list will be used by the executor. The elements are also analyzers. `kw()` creates a terminal symbol analyzer that is a keyword or some string that has to be present in the source code as it is defined in the argument of the method `kw(keyword)`.

The definition of `ifStatement` is a list of the keywords `if`, the symbol `(`, then an expression, the symbol `)` then `}` and optionally `else{ expression }` at the end.

The method `optional()` creates an analyzer that matches the underlying analyzers passed as arguments optionally. This means that this is not an error if there is no `else{expression}` part at the end of an `if` statement in our grammar.

Now, after we have created the analyzer for the variable `ifStatement` we finally have to define the analyzer stored in the variable `expression`. This can be done because this analyzer is a special type `PassThroughAnalyzer` that has a method `define()` to define the meaning of the analyzer after it had been created.

Finally the field `grammar` has to be assigned. This field is declared in the abstract class `GrammarDefinition` and it has to get a value in the method `define()`.

The code using the method above can use the returned grammar:

```
		final Analyzer myGrammar = defineMyGrammar();
		AnalysisResult result = myGrammar.analyze(new StringSourceCode(
				"if(1){55}else{33}"));
		Assert.assertTrue(result.wasSuccessful());
		LOG.debug(result.getExecutor().toString());
		Long res = (Long) result.getExecutor().execute(null);
		Assert.assertEquals((Long) 55L, res);
```

The source code is provided in a String. The readily available implementation of the interface `SourceCode` used by the analyzers implements the methods of the interface using an embedded string. The method `analyze()` analyzes the source code and returns the result of the analysis. The analysis can be successful or not. This can be checked calling the method `wasSuccessful()`. If the analysis failed, it means that the source code was not matching the grammar. In that case there is no executor created by the analysis. If the analysis was successful then an executor is created and can be invoked (one or more times) to execute the code.

The executor provides a method `execute()` that accept a single argument, which is an execution context. The execution context is an empty interface defined by the library and is not used by itself. You can pass any object as an execution context to the executors you implement.
