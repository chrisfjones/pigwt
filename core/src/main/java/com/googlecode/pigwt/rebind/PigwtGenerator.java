package com.googlecode.pigwt.rebind;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.impl.AbstractPlaceHistoryMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.googlecode.pigwt.client.DefaultShell;
import com.googlecode.pigwt.client.Param;
import com.googlecode.pigwt.client.PigwtPlace;
import com.googlecode.pigwt.client.TokenizerUtil;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public class PigwtGenerator extends Generator {
    private String packageName = "com.googlecode.pigwt.client";
    private String className = "Pigwt_gen";
    private static final String ABSTRACT_PIGWT = "com.googlecode.pigwt.client.Pigwt";
    private PigwtGeneratorContext context;
    private TreeLogger logger;
    private PigwtTree tree;

    @Override
    public String generate(
            TreeLogger logger,
            GeneratorContext generatorContext,
            String typeName) throws UnableToCompleteException {
        this.logger = logger;

        try {
            context = new PigwtGeneratorContext(generatorContext, logger);
            packageName = context.getModulePackage().getName();
            tree = new PigwtTree(context);

            List<String> placePrefixes = tree.walkNames();
            placePrefixes.remove("");
            for (String s : placePrefixes) {
                generatePlaceClass(s);
                generateTokenizerClass(s);
            }
            String ginjectorName = null;
            if (context.isGinAvailable()) {
                ginjectorName = generateGinjectorInterface();
            }
            generatePlaceHistoryMapperClass(placePrefixes);
            generatePigwtClass(ginjectorName);

        } catch (Exception e) {
            logger.log(TreeLogger.ERROR, "Failed to generate Pigwt support classes", e);
        }
        return packageName + "." + className;
    }

    private String generateGinjectorInterface() {
        final JClassType clientGinjectorType = tree.ginjector;
        final String pigwtGinjectorClassName = clientGinjectorType.getName() + "_PigwtGinjector";

        PrintWriter printWriter = context.createPrintWriter(logger, packageName, pigwtGinjectorClassName);
        if (printWriter == null) return null;
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, pigwtGinjectorClassName);
        composer.makeInterface();
        composer.addImplementedInterface(clientGinjectorType.getQualifiedSourceName());
        SourceWriter sourceWriter = composer.createSourceWriter(context.getGeneratorContext(), printWriter);

        for (JClassType activityType : tree.walkActivityTypes()) {
            writeGinjectorActivityGetter(sourceWriter, activityType);
        }
        for (JClassType shellType : tree.walkPrefixesToShells().values()) {
            writeGinjectorActivityGetter(sourceWriter, shellType);
        }

        sourceWriter.outdent();
        sourceWriter.println("}");
        context.commit(printWriter);
        return pigwtGinjectorClassName;
    }

    private void writeGinjectorActivityGetter(final SourceWriter sourceWriter, final JClassType activityType) {
        sourceWriter.println(format("{0} {1}();",
                activityType.getQualifiedSourceName(),
                buildGetterFor(activityType)));
    }

    private String buildGetterFor(final JClassType type) {
        return "get_" + type.getQualifiedSourceName().replaceAll("\\.", "_");
    }

    private void generatePigwtClass(final String ginjectorName) throws Exception {
        PrintWriter printWriter;
        printWriter = context.createPrintWriter(logger, packageName, className);
        if (printWriter == null) return;
        ClassSourceFileComposerFactory composer;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.setSuperclass(ABSTRACT_PIGWT);
        addImports(composer);

        SourceWriter sourceWriter = composer.createSourceWriter(context.getGeneratorContext(), printWriter);

        if (ginjectorName != null) {
            sourceWriter.println(format("private final {0} injector = GWT.create({0}.class);", ginjectorName));
        }
        generateInjectableFields(sourceWriter, tree.injectables);
        generateGetShellMethods(sourceWriter, tree);
        generateGetPlaceHistoryMapperMethod(sourceWriter);
        generateGetActivityMethod(tree, sourceWriter);

        sourceWriter.outdent();
        sourceWriter.println("}");
        context.commit(printWriter);
    }

    private void generateInjectableFields(final SourceWriter sourceWriter, final List<JClassType> injectables) {
        for (JClassType injectable : injectables) {
            final String injectableFieldName = getInjectableFieldName(injectable);
            sourceWriter.println(format("private final {0} {1} = ({0})GWT.create({0}.class);\n",
                    injectable.getQualifiedSourceName(), injectableFieldName));
        }
    }

    private String getInjectableFieldName(final JType injectable) {
        return injectable.getQualifiedSourceName().replace(".", "_") + "_injectable";
    }

    // sadly, this is a rewrite of the PlaceHistoryMapperGenerator because I couldn't figure out how to get
    // GWT.create(MyPlaceHistoryMapper.class) to work with generated tokenizers in the @WithTokenizers annotation
    private void generatePlaceHistoryMapperClass(List<String> placePrefixes) {
        PrintWriter printWriter = context.createPrintWriter(logger, packageName, "PlaceHistoryMapper_gen");
        if (printWriter == null) return;
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, "PlaceHistoryMapper_gen");
        composer.setSuperclass(format("{0}<Void>", AbstractPlaceHistoryMapper.class.getSimpleName()));

        composer.addImport("com.googlecode.pigwt.client.*");
        composer.addImport(AbstractPlaceHistoryMapper.class.getCanonicalName());
        composer.addImport(Place.class.getCanonicalName());
        composer.addImport(PlaceTokenizer.class.getCanonicalName());
        composer.addImport(GWT.class.getCanonicalName());

        SourceWriter sourceWriter = composer.createSourceWriter(context.getGeneratorContext(), printWriter);
        sourceWriter.println();

        writeHistoryMapperGetPrefixAndToken(sourceWriter, placePrefixes);
        sourceWriter.println();

        writeHistoryMapperGetTokenizer(sourceWriter, placePrefixes);
        sourceWriter.println();

        sourceWriter.outdent();
        sourceWriter.println("}");
        context.commit(printWriter);
    }

    private void writeHistoryMapperGetTokenizer(final SourceWriter sourceWriter, List<String> placePrefixes) {
        sourceWriter.println("protected PlaceTokenizer<?> getTokenizer(String prefix) {");
        sourceWriter.indent();

        sourceWriter.println("if (\"\".equals(prefix)) return GWT.create(DefaultPlace.Tokenizer.class);");

        for (String s : placePrefixes) {
            final String classPrefix = s.replace(".", "__");
            sourceWriter.println(format("if (\"{0}\".equals(prefix)) '{'", escape(s)));
            sourceWriter.indent();

            sourceWriter.println(format("return GWT.create({0}_Tokenizer.class);", classPrefix));

            sourceWriter.outdent();
            sourceWriter.println("}");
        }

        sourceWriter.println("return null;");
        sourceWriter.outdent();
        sourceWriter.println("}");
        sourceWriter.outdent();
    }

    private void writeHistoryMapperGetPrefixAndToken(final SourceWriter sourceWriter, List<String> placePrefixes) {
        sourceWriter.println("protected PrefixAndToken getPrefixAndToken(Place newPlace) {");
        sourceWriter.indent();

        sourceWriter.println("if (newPlace instanceof DefaultPlace) {");
        sourceWriter.indent();
        sourceWriter.println("DefaultPlace place = (DefaultPlace) newPlace;");

        sourceWriter.println("PlaceTokenizer<DefaultPlace> t = GWT.create(DefaultPlace.Tokenizer.class);");
        sourceWriter.println("return new PrefixAndToken(\"\", t.getToken(place));");

        sourceWriter.outdent();
        sourceWriter.println("}");

        for (String s : placePrefixes) {
            final String classPrefix = s.replace(".", "__");
            sourceWriter.println(format("if (newPlace instanceof {0}_Place) '{'", classPrefix));
            sourceWriter.indent();
            sourceWriter.println(format("{0}_Place place = ({0}_Place) newPlace;", classPrefix));

            sourceWriter.println(format("PlaceTokenizer<{0}_Place> t = GWT.create({0}_Tokenizer.class);", classPrefix));
            sourceWriter.println(format("return new PrefixAndToken(\"{0}\", t.getToken(place));", escape(s)));

            sourceWriter.outdent();
            sourceWriter.println("}");
        }

        sourceWriter.println("return null;");
        sourceWriter.outdent();
        sourceWriter.println("}");
    }

    private void addImports(final ClassSourceFileComposerFactory composer) {
        composer.addImport("com.googlecode.pigwt.client.*");
        composer.addImport(GWT.class.getCanonicalName());
        composer.addImport(RunAsyncCallback.class.getCanonicalName());
        composer.addImport(Activity.class.getCanonicalName());
        composer.addImport(GWT.class.getCanonicalName());
        composer.addImport(AcceptsOneWidget.class.getCanonicalName());
        composer.addImport(EventBus.class.getCanonicalName());
        composer.addImport(AcceptsOneWidget.class.getCanonicalName());
        composer.addImport(PlaceHistoryMapper.class.getCanonicalName());
    }

    private void generateTokenizerClass(final String placePrefix) {
        final String classPrefix = placePrefix.replace(".", "__");
        String className = classPrefix + "_Tokenizer";
        PrintWriter printWriter = context.createPrintWriter(logger, packageName, className);
        if (printWriter == null) return;
        ClassSourceFileComposerFactory composer;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport(TokenizerUtil.class.getCanonicalName());
        composer.addImplementedInterface(format("{0}<{1}_Place>", PlaceTokenizer.class.getCanonicalName(), classPrefix));
        composer.addAnnotationDeclaration(format("@{0}(\"{1}\")", Prefix.class.getCanonicalName(), placePrefix));

        SourceWriter sourceWriter = composer.createSourceWriter(printWriter);

        writeMethod(sourceWriter,
                format("public {0}_Place getPlace(final String token)", classPrefix),
                format("return new {0}_Place(TokenizerUtil.parseParams(token));", classPrefix));
        writeMethod(sourceWriter,
                format("public String getToken(final {0}_Place place)", classPrefix),
                format("return place.getParamString();", placePrefix));

        sourceWriter.outdent();
        sourceWriter.println("}");
        context.commit(printWriter);
    }

    private void generatePlaceClass(final String placePrefix) {
        String className = placePrefix.replace(".", "__") + "_Place";
        PrintWriter printWriter = context.createPrintWriter(logger, packageName, className);
        if (printWriter == null) return;
        ClassSourceFileComposerFactory composer;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport(Map.class.getCanonicalName());
        composer.setSuperclass(PigwtPlace.class.getCanonicalName());

        SourceWriter sourceWriter = composer.createSourceWriter(context.getGeneratorContext(), printWriter);

        writeMethod(sourceWriter,
                format("public {0}(final Map<String, String> params)", className),
                "super(params);");
        writeMethod(sourceWriter,
                "public String getPrefix()",
                format("return \"{0}\";", placePrefix));

        sourceWriter.outdent();
        sourceWriter.println("}");
        context.commit(printWriter);
    }

    private void generateGetPlaceHistoryMapperMethod(final SourceWriter sourceWriter) {
        writeMethod(sourceWriter,
                "protected PlaceHistoryMapper getPlaceHistoryMapper()",
                "return GWT.create(PlaceHistoryMapper_gen.class);");
    }

    private void generateGetShellMethods(final SourceWriter sourceWriter, PigwtTree tree) {
        writeShellDeclaration(sourceWriter, "rootShell", tree.root.shellClass != null ? tree.root.shellClass : context.findType(DefaultShell.class));

        Map<String, JClassType> prefixesToShells = tree.walkPrefixesToShells();
        prefixesToShells.remove("");
        for (Map.Entry<String, JClassType> prefixToShell : prefixesToShells.entrySet()) {
            String classPrefix = prefixToShell.getKey().replace(".", "__");
            writeShellDeclaration(sourceWriter, classPrefix + "_Shell", prefixToShell.getValue());
        }
        writeMethod(sourceWriter, "protected Shell getRootShell()", "return rootShell;");

        StringBuilder body = new StringBuilder();
        for (String prefix : prefixesToShells.keySet()) {
            String classPrefix = prefix.replace(".", "__");
            body.append(format("if (\"{0}\".equals(placePrefix)) return {1}_Shell;\n", prefix, classPrefix));
        }
        body.append("return null;");
        writeMethod(sourceWriter, "protected Shell getShell(final String placePrefix)", body.toString());
    }

    private void writeShellDeclaration(final SourceWriter sourceWriter, final String declarationName, final JClassType shellClass) {
        if (context.isGinAvailable()) {
            sourceWriter.println(format("private final Shell {0} = injector.{1}();", declarationName, buildGetterFor(shellClass)));
        } else {
            sourceWriter.println(format("private final Shell {0} = (Shell)GWT.create({1}.class);", declarationName, shellClass.getQualifiedSourceName()));
        }
    }

    private void generateGetActivityMethod(PigwtTree tree, SourceWriter sourceWriter)
            throws NotFoundException, NoSuchFieldException, IllegalAccessException {
        StringBuilder body = new StringBuilder();

        writeActivityLine(body, tree.root.name, tree.root);
        writeActivityLines(body, tree.root.name, tree.root.children);

        body.append("return null;");

        writeMethod(sourceWriter, "public Activity getActivity(final com.google.gwt.place.shared.Place p)", body.toString());
    }

    private void writeActivityLines(final StringBuilder body, final String prefix, final List<PigwtTree.Node> nodes) throws NotFoundException {
        for (PigwtTree.Node node : nodes) {
            writeActivityLine(body, prefix, node);
            writeActivityLines(body, prefix + node.name + "__", node.children);
        }
    }

    protected static final String activityLineTemplate =
            "if (p instanceof {0}) '{'\n" +
                    "  return new ActivityFlyweight((PigwtPlace)p) '{'\n" +
                    "    @Override\n" +
                    "    public void start(final AcceptsOneWidget panel, final EventBus eventBus) '{'\n" +
                    "      GWT.runAsync(new RunAsyncCallback() '{'\n" +
                    "        public void onFailure(Throwable reason) '{'\n" +
                    "          fail(\"{1}\", reason);\n" +
                    "        }\n" +
                    "        public void onSuccess() '{'\n" +
                    "          {2} activity = {3};\n" +
                    "          {4}\n" +
                    "          setProxiedActivity(activity);\n" +
                    "          getProxiedActivity().start(panel, eventBus);\n" +
                    "        '}'\n" +
                    "      '}');\n" +
                    "    '}'\n" +
                    "  '}';\n" +
                    "'}'\n";

    private void writeActivityLine(StringBuilder body, final String prefix, PigwtTree.Node node) throws NotFoundException {
        String newActivityBlock;
        if (context.isGinAvailable()) {
            newActivityBlock = format("injector.{0}()", buildGetterFor(node.activityType));
        } else {
            JConstructor constructor = findActivityConstructor(node.activityType);
            String paramString = buildParamString(constructor);
            newActivityBlock = format("new {0}({1})", node.activityType.getQualifiedSourceName(), paramString);
        }

        String paramSetters = buildParamSetters(node.activityType);

        final String classPrefix = prefix + node.name;
        final String placeClass = "".equals(classPrefix) ? "DefaultPlace" : classPrefix + "_Place";

        body.append(format(
                activityLineTemplate,
                placeClass,
                classPrefix,
                node.activityType.getQualifiedSourceName(),
                newActivityBlock,
                paramSetters));
    }

    private String buildParamString(final JConstructor constructor) {
        StringBuilder paramString = new StringBuilder("");
        int i = 0;
        String comma = "";
        for (JParameter parameter : constructor.getParameters()) {
            final JType type = parameter.getType();
            if (tree.injectables.contains(type)) {
                paramString.append(comma).append(getInjectableFieldName(type));
            } else {
                // todo: error out properly
                throw new RuntimeException("Don't know how to inject " + type.getQualifiedSourceName());
            }
            comma = ",";
        }
        return paramString.toString();
    }


    private JConstructor findActivityConstructor(JClassType activityClass) {
        JConstructor constructor = null;
        final JConstructor[] constructors = activityClass.getConstructors();

        int mostInjectables = -1;
        // find the constructor with the most injectables
        for (JConstructor c : constructors) {
            int injectables = getNumberOfInjectablesInConstructor(c);
            if (injectables > mostInjectables) {
                constructor = c;
                mostInjectables = injectables;
            }
        }
        if (constructor == null) {
            // todo: error out properly
            throw new RuntimeException("Found no suitable constructors for " + activityClass.getQualifiedSourceName() + ". " +
                    "Pigwt needs all activity class parameters to be @PigwtInjectable unless you're using gin.");
        }
        return constructor;
    }

    private int getNumberOfInjectablesInConstructor(final JConstructor constructor) {
        int i = 0;
        for (JParameter p : constructor.getParameters()) {
            if (tree.injectables.contains(p.getType())) {
                i++;
            } else {
                return -1;
            }
        }
        return i;
    }

    private String buildParamSetters(final JClassType activityClass) {
        StringBuilder paramSetters = new StringBuilder();
        String prefixSpaces = "";
        for (JMethod m : activityClass.getMethods()) {
            if (!m.getName().startsWith("set")) {
                continue;
            }
            final JParameter[] parameters = m.getParameters();
            if (parameters.length > 1) {
                continue;
            }
            JParameter parameter = parameters[0];
            final String paramClassName = parameter.getType().getQualifiedSourceName();
            boolean isInteger = paramClassName.equals(Integer.class.getCanonicalName());
            boolean isLong = paramClassName.equals(Long.class.getCanonicalName());
            boolean isString = paramClassName.equals(String.class.getCanonicalName());
            
            if (isInteger) {
                paramSetters.append(prefixSpaces).append("activity.").append(m.getName()).append("(").append("getIntParam(\"");
            } else if (isLong) {
                paramSetters.append(prefixSpaces).append("activity.").append(m.getName()).append("(").append("getLongParam(\"");
            } else if (isString) {
                paramSetters.append(prefixSpaces).append("activity.").append(m.getName()).append("(").append("getStringParam(\"");
            } else {
                continue;
            }

            String setterName = m.getName();
            setterName = setterName.substring(3, setterName.length());
            char firstChar = Character.toLowerCase(setterName.charAt(0));
            setterName = firstChar + setterName.substring(1, setterName.length());
            paramSetters.append(setterName);

            paramSetters.append("\"));\n");

            prefixSpaces = "          ";
        }
        return paramSetters.toString();
    }

    public void writeMethod(SourceWriter writer, String signature, String body) {
        writer.println(signature + " {");
        writer.indent();
        writer.println(body);
        writer.outdent();
        writer.println("}");
    }

}
