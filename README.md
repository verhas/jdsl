jdsl
====

jdsl stands for Java Domain Specific Language, and it is a software library to aid the creation of domain specific languages.

jdsl is a library, it is not a framework and not a preprocessor, code generator. You can use it just as any other library. There is no special grammar file to write and there is no need to modify your usual build process. Jdsl will not generate any JVM code or Java code for you. All it does is that it gives you nice and handy API to define your language, to analyze source code and to execute the code defined in your DSL. The features:

* Simple and easy to read, fluent-api supported DSL definition
* Java execution of the DSL code using your executors or executors provided off the shelf. (Executors are simple instances of classes implementing the `Executor` interface.)
* Excessive and expressive logging to analyze your grammar in case something works different than expected.

Using jdsl you define the grammar in Java using fluent API. When you have your grammar all you should do is invoke `analyze()` to get your source code analyzed and an executor to be created as part of the analysis process. After that you can call `execute()` on the generated executor.

You can get a DSL in five minutes. Here comes an example:

EXAMPLE
-------

---
* We want to have some numbers defined. The user can provide a pure decimal number or can give a conditional statement specifying a number. For example `1` is an appropriate 'expression'. `if(1){1}` is also an appropriate expression and is equivalent to the previous one. `if(0){2}else{1}` is also appropriate. The vague definition of the grammar will look like this:

  * a number as an `expression`,
  * an `if(expression){expression}`
  * or `if(expression){expression}else{expression}`

---

The easiest and recommended way defining a grammar for the above is to create an instance of the abtract class `GrammarDefinition` (you can see the whole source code in the unit test directory):

```java
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
  

The class `GrammarDefinition` is abstract with the abstract method `define()`. The methods defined in the class `GrammarDefinition` can be used as in the example above to describe the grammar that the analyzer will analyze.

The first call `skipSpaces()` instructs the analyzer that spaces that separate keywords, numbers and so on are not important. The next call defines an analyzer, which is defined later. The method `later()` creates such an analyzer. This is needed because the definition of the grammar is recursive. We need the analyzer `expression` but we just can not define it yet.

The analyzer `ifStatement` is a list of analyzers. When it matches some source code then it creates an `IfExecutor` class instance using the provided `IfExecutorFactory` and the elements of the list will be used by the executor. The elements are also analyzers. `kw()` creates a terminal symbol analyzer that is a keyword or some string that has to be present in the source code as it is defined in the argument of the method `kw(keyword)`.

The definition of `ifStatement` is a list of the keywords `if`, the symbol `(`, then an expression, the symbol `)` then `}` and optionally `else{ expression }` at the end.

The method `optional()` creates an analyzer that matches the underlying analyzers passed as arguments optionally. This means that this is not an error if there is no `else{expression}` part at the end of an `if` statement in our grammar.

Now, after we have created the analyzer for the variable `ifStatement` we finally have to define the analyzer stored in the variable `expression`. This can be done because this analyzer is a special type `Define`, which has the method `define()` to define the meaning of the analyzer after it had been created. (Note that you can and have to define such an analyzer exactly once before the method `GrammarDefinition.define()` method finishes.

Finally the created grammar is returned.

The code using the method above can use the returned grammar:

```java
		final Analyzer myGrammar = ...
		
		...
		
		AnalysisResult result = myGrammar.analyze(new StringSourceCode(
				"if(1){55}else{33}"));
		Assert.assertTrue(result.wasSuccessful());
		LOG.debug(result.getExecutor().toString());
		Long res = (Long) result.getExecutor().execute(null);
		Assert.assertEquals((Long) 55L, res);
```

The source code for the analyzers should be provided in the form of an instance of `SourceCode`. This is a simple interface that can deliver characters one after the other. A readily implementation backed up by a String is `StringSourceCode`.

The method `analyze()` analyzes the source code and returns the result of the analysis. The analysis can be successful or not. This can be checked calling the method `wasSuccessful()`. If the analysis fails, it means that the source code was not matching the grammar. In that case there is no executor created by the analysis. If the analysis was successful then an executor is created and can be invoked (one or more times) to execute the code.

The executor provides a method `execute()` that accept a single argument, the execution context. The executors are created by analysis process using factories. In case of keywords, numbers and so on the executors are simple and are created without any factory off-the-shelf. For example the executor of a "number" will just return the number itself. The excutor of a list will `execute()` the executors returned by the elements of the list during analysis. Optional will just execute the executor that was matched. In case of domain specific behaviour a factory have to be provided, like the `IfExecutorFactory` in the example above. The factory itself is very simple:

```java
        private static class IfExecutorFactory implements Factory<ListExecutor> {

                @Override
                public ListExecutor get() {
                        return new IfExecutor();
                }

        }
```

The `IfExecutor` implements the subinterface `ListExecutor` of the root `Executor` interface since this executor gets two or three underlying executors (expressions in this case) that it can executed based on its own algorithm. The `ifExecutor` class from the test example looks like the following:

```java
private static class IfExecutor implements ListExecutor {

                @Override
                public Object execute(Context context) {
                        final Object condition = executorList.get(0).execute(context);
                        final Long one = (Long) condition;
                        if (one != 0) {
                                if (executorList.size() > 1) {
                                        return executorList.get(1).execute(context);
                                } else {
                                        return null;
                                }
                        } else {
                                if (executorList.size() > 2) {
                                        return executorList.get(2).execute(context);
                                } else {
                                        return null;
                                }
                        }
                }

                private List<Executor> executorList;

                @Override
                public void setList(final List<Executor> executorList) {
                        this.executorList = executorList;
                }

                ...
        }
```        

The class implements two methods: `execute()` and `setList()`. The firs one is needed to execute the code. `setList()` is called during analysis to set the underlying executors that result the values for the expressions that play the role of the condition and the return values. (The method `toString()` was removed from the example for brevity.)

The argument `Context context` in this example is not used. The actual interface `Context` is empty. The built-in executors do not use it for anything except passing on to the underlying executors in the hierarchy. The domain specific executors can use it. When the execution starts in your code you can pass any object of a class implementing the interface `Context`. This can be used to manage variables of your domain specifi language, execution environmental objects and so on.

The executor provides a method `execute()` that accept a single argument, which is an execution context. The execution context is an empty interface defined by the library and is not used by itself. You can pass any object as an execution context to the executors you implement.

For further information and more detailed documentation visit the project [site](http://verhas.github.io/jdsl/index.html "Project site on GitHub")
