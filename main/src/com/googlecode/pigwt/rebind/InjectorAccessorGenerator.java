package com.googlecode.pigwt.rebind;

import com.google.gwt.core.ext.*;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;

public class InjectorAccessorGenerator extends Generator {

    private String packageName;
    private String className;
    private static final TreeLogger.Type logType = TreeLogger.DEBUG;

    @Override
    public String generate(
            TreeLogger logger,
            GeneratorContext context,
            String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();

        try {
            JClassType classType = typeOracle.getType(typeName);
            packageName = classType.getPackage().getName();
            className = classType.getSimpleSourceName() + "Generated";
            // Generate class source code
            generateClass(logger, context);
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR, "Failed to generate InjectorGuy", e);
        }
        return packageName + "." + className;
    }

    private void generateClass(TreeLogger logger, GeneratorContext context) throws NotFoundException, BadPropertyValueException, UnableToCompleteException {
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, packageName, className);
        if (printWriter == null) return;
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
//        composer.addImplementedInterface(InjectorAccessor.class.getCanonicalName());
        composer.addImplementedInterface("InjectorAccessor");
        composer.addImport("com.google.gwt.core.client.GWT");

        SourceWriter sourceWriter = null;
        sourceWriter = composer.createSourceWriter(context, printWriter);
        generateGetInjector(sourceWriter, logger, context);
        sourceWriter.outdent();
        sourceWriter.println("}");
        context.commit(logger, printWriter);

    }

    private void generateGetInjector(SourceWriter sourceWriter, TreeLogger logger, GeneratorContext context) throws BadPropertyValueException {
//        sourceWriter.println("public " + CoreInjector.class.getCanonicalName() + " createInjector() { ");
        sourceWriter.println("public " + "CoreInjector" + " createInjector() { ");
        sourceWriter.indent();

        String injectorClassName = context.getPropertyOracle().getConfigurationProperty("injector").getValues().get(0);
        logger.log(logType, "InjectorAccessorGenerator using injector className = " + injectorClassName);
        sourceWriter.println("return GWT.create(" + injectorClassName + ".class);");

        sourceWriter.outdent();
        sourceWriter.println("}");
    }
}