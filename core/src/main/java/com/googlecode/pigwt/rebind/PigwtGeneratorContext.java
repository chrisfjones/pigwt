package com.googlecode.pigwt.rebind;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import com.google.gwt.dev.resource.impl.PathPrefix;
import com.google.gwt.dev.resource.impl.PathPrefixSet;
import com.googlecode.pigwt.client.Shell;
import com.googlecode.pigwt.rebind.gin.GinjectorProxy;

import java.io.PrintWriter;
import java.util.Arrays;

class PigwtGeneratorContext {
    private static final String GINJECTOR_CLASS_NAME = "com.google.gwt.inject.client.Ginjector";
    private final GeneratorContext context;
    private final TreeLogger logger;
    private final TypeOracle typeOracle;
    private final JClassType activityType;
    private final JPackage modulePackage;
    private final JClassType shellType;
    private final JClassType stringType;

    public PigwtGeneratorContext(final GeneratorContext context, final TreeLogger logger) throws Exception {
        this.context = context;
        this.logger = logger;
        typeOracle = context.getTypeOracle();
        activityType = typeOracle.getType(Activity.class.getCanonicalName());
        modulePackage = typeOracle.findPackage(findRootPackage());
        shellType = typeOracle.getType(Shell.class.getCanonicalName());
        stringType = typeOracle.getType(String.class.getCanonicalName());
    }

    private String findRootPackage() throws NoSuchFieldException, IllegalAccessException {
        // todo: must be a better way to get this
        java.lang.reflect.Field privateModuleField = StandardGeneratorContext.class.getDeclaredField("module");
        privateModuleField.setAccessible(true);
        ModuleDef module = (ModuleDef) privateModuleField.get(context);

        java.lang.reflect.Field privateSourcePrefixSetField = ModuleDef.class.getDeclaredField("sourcePrefixSet");
        privateSourcePrefixSetField.setAccessible(true);
        PathPrefixSet prefixes = (PathPrefixSet) privateSourcePrefixSetField.get(module);
        final PathPrefix pathPrefix = (PathPrefix) prefixes.values().toArray()[prefixes.getSize() - 1];
        String pathString = pathPrefix.getPrefix();
        if (pathString.endsWith("/")) {
            pathString = pathString.substring(0, pathString.length() - 1);
        }
        pathString = pathString.replace("/", ".");
        return pathString;
    }

    public JPackage getModulePackage() {
        return modulePackage;
    }

    public JPackage[] getAllPackages() {
        return typeOracle.getPackages();
    }

    public JClassType findActivityInPackage(final JPackage p) {
        for (JClassType t : p.getTypes()) {
            if (t.isAbstract()) {
                continue;
            }
            if (t.equals(activityType)) {
                continue;
            }
            if (t.getFlattenedSupertypeHierarchy().contains(activityType))
                return t;
        }
        return null;
    }

    public JClassType findShellInPackage(final JPackage p) {
        JClassType found = null;
        for (JClassType t : p.getTypes()) {
            if (t.isAbstract()) {
                continue;
            }
            if (!t.isAssignableTo(shellType)) {
                continue;
            }
            if (found != null) {
                logger.log(TreeLogger.WARN,
                        "Found more than one suitable shell in your module, I'm using '" + found.getName() + "' and am ignoring '" + t.getName() + "'. " +
                                "If this isn't what you want, remove the @Shell annotation from classes (or their superclasses) that you don't want to be shells.");
            }
            found = t;
        }
        return found;
    }

    public PrintWriter createPrintWriter(final TreeLogger logger, final String packageName, final String className) {
        return context.tryCreate(logger, packageName, className);
    }

    public GeneratorContext getGeneratorContext() {
        return context;
    }

    public void commit(final PrintWriter printWriter) {
        context.commit(logger, printWriter);
    }

    public boolean isStringType(final JType type) {
        return stringType.equals(type);
    }

    public boolean isGinAvailable() {
        return typeOracle.findType(GINJECTOR_CLASS_NAME) != null;
    }

    public JClassType findType(final Class clazz) {
        return typeOracle.findType(clazz.getCanonicalName());
    }

    public GinjectorProxy findGinjectorInPackage(final String pkg) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (!isGinAvailable()) {
            return null;
        }
        final GinjectorProxy ginjectorProxy = (GinjectorProxy)
                Class.forName("com.googlecode.pigwt.rebind.gin.GinjectorProxyImpl").newInstance();
        final JPackage p = typeOracle.findPackage(pkg);
        for (JClassType t : p.getTypes()) {
            if (t.isInterface() == null) {
                continue;
            }
            if (!Arrays.asList(t.getImplementedInterfaces()).contains(typeOracle.findType(GINJECTOR_CLASS_NAME))) {
                continue;
            }
            if (!ginjectorProxy.hasGinModulesAnnotation(t)) {
                continue;
            }
            ginjectorProxy.setGinjectorType(t);
            return ginjectorProxy;
        }
        return null;
    }
}
