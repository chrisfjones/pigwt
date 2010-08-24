package com.googlecode.pigwt.rebind;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.*;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.googlecode.pigwt.client.Page;
import com.googlecode.pigwt.client.PageGroup;
import com.googlecode.pigwt.client.PageTree;

import java.io.PrintWriter;
import java.util.*;

public class PageTreeGenerator extends Generator {
    private static final TreeLogger.Type logType = TreeLogger.DEBUG;
    private static final String PAGE_TREE_CLASS_QNAME = PageTree.class.getCanonicalName();
    private static final String PAGE_QNAME = Page.class.getCanonicalName();
    private static final String GROUP_QNAME = PageGroup.class.getCanonicalName();
    private static final String ROOT_PACKAGE_VARIABLE_NAME = "pigwt.rootPackage";
    private static final String PAGE_INJECTOR_VARIABLE_NAME = "pigwt.pageInjector";
    private static final String GINJECTOR_CLASS_QNAME = "com.google.gwt.inject.client.Ginjector";

    private String packageName;
    private String className;

    @Override
    public String generate(
            TreeLogger logger,
            GeneratorContext context,
            String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();

        try {
            JClassType classType = typeOracle.getType(typeName);
            packageName = classType.getPackage().getName();
            className = classType.getSimpleSourceName() + "_generated";
            // Generate class source code
            generateClass(logger, context);
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR, "Failed to generate PageTree", e);
        }
        return packageName + "." + className;
    }

    private void generateClass(TreeLogger logger, GeneratorContext context) throws NotFoundException, BadPropertyValueException, UnableToCompleteException {
        PrintWriter printWriter;
        printWriter = context.tryCreate(logger, packageName, className);
        if (printWriter == null) return;
        ClassSourceFileComposerFactory composer;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.setSuperclass(PAGE_TREE_CLASS_QNAME);
        composer.addImport(GWT.class.getCanonicalName());
        composer.addImport(RunAsyncCallback.class.getCanonicalName());
        composer.addImport(Map.class.getCanonicalName());
        
        SourceWriter sourceWriter;
        sourceWriter = composer.createSourceWriter(context, printWriter);
        generateConstructor(sourceWriter, logger, context);
        sourceWriter.outdent();
        sourceWriter.println("}");
        context.commit(logger, printWriter);

    }

    private void generateConstructor(SourceWriter sourceWriter, TreeLogger logger, GeneratorContext context) throws NotFoundException, BadPropertyValueException, UnableToCompleteException {
        StringBuffer body = new StringBuffer();
        body.append("super();\n");

        TypeOracle typeOracle = context.getTypeOracle();
        JClassType pageType = typeOracle.getType(PAGE_QNAME);
        JClassType groupType = typeOracle.getType(GROUP_QNAME);

        Map<String, JPackage> packages = new HashMap<String, JPackage>();

        String rootPackage = context.getPropertyOracle().getConfigurationProperty(ROOT_PACKAGE_VARIABLE_NAME).getValues().get(0);
        if (rootPackage == null) {
            logger.log(TreeLogger.ERROR, "Configuration property '" + ROOT_PACKAGE_VARIABLE_NAME + "' not found, pigwt can't build its PageTree without it.");
            throw new UnableToCompleteException();
        }
        packages.put("", typeOracle.getPackage(rootPackage));

        String pageInjector = context.getPropertyOracle().getConfigurationProperty(PAGE_INJECTOR_VARIABLE_NAME).getValues().get(0);
        if (pageInjector == null && isUsingGin(typeOracle)) {
            logger.log(TreeLogger.WARN, "Looks like gin is on your module's classpath. If you'd like pigwt to use injected pages instead of creating new ones,\n" +
                    "set the '" + PAGE_INJECTOR_VARIABLE_NAME + "' configuration property to the fully qualified class name of your page injector.");
        }

        JPackage[] jPackages = typeOracle.getPackages();
        for (JPackage jPackage : jPackages) {
            String pName = jPackage.getName();
            if (!pName.startsWith(rootPackage)) {
                continue;
            }
            if (pName.equals(rootPackage)) {
                packages.put("", jPackage);
                continue;
            }
            logger.branch(logType, "checking package " + jPackage.getName());
            final String chopped = pName.substring(rootPackage.length() + 1);
            packages.put(chopped, jPackage);
        }

        // construct the tree backwards via tail-recursion
        writeNode("", packages, body, pageType, groupType, logger);
        body.append("root = node_;");

//        System.err.println(body.toString());

        writeMethod(sourceWriter, "public " + className + "()", body.toString());
    }

    private boolean isUsingGin(TypeOracle typeOracle) {
        try {
            return typeOracle.getType(GINJECTOR_CLASS_QNAME) != null;
        } catch (NotFoundException e) {
            return false;
        }
    }

    private String writeNode(String token, Map<String, JPackage> packages, StringBuffer body,
                             JClassType pageType, JClassType groupType, TreeLogger logger) throws UnableToCompleteException, NotFoundException {
        int rootDots = "".equals(token) ? -1 : dotsInString(token);
        Stack<String> nodesToAttachToThis = new Stack<String>();
        for (String p : packages.keySet()) {
            if (p.equals(token)) continue;
            if (!p.startsWith(token)) continue;
            int pDots = dotsInString(p);
            if (rootDots + 1 == pDots) {
                nodesToAttachToThis.push(writeNode(p, packages, body, pageType, groupType, logger));
            }
        }
        String nodeName = "node_" + token.replaceAll("\\.", "_");
        body.append("PageTree.Node ").append(nodeName).append(" = new PageTree.Node(\"").append(token).append("\");\n");

        final JPackage thisPackage = packages.get(token);

        JClassType groupFlyweightType = findTypeInPackage(groupType, thisPackage, logger);
        if (groupFlyweightType != null) {
            body.append(nodeName).append(".setGroupFlyweight(").
                    append(generateFlyweight(groupFlyweightType, logger, thisPackage)).append(");\n");
        }

        JClassType pageFlyweightType = findTypeInPackage(pageType, thisPackage, logger);
        if (pageFlyweightType != null) {
            body.append(nodeName).append(".setPageFlyweight(").
                    append(generateFlyweight(pageFlyweightType, logger, thisPackage)).append(");\n");
        }

        while (!nodesToAttachToThis.isEmpty()) {
            body.append(nodeName).append(".addChild(").append(nodesToAttachToThis.pop()).append(");\n");
        }
        return nodeName;
    }

    public void writeMethod(SourceWriter writer, String signature, String body) {
      writer.println(signature + " {");
      writer.indent();
      writer.println(body);
      writer.outdent();
      writer.println("}");
      writer.println();
    }

    private JClassType findTypeInPackage(JClassType type, JPackage p, TreeLogger logger) {
        for (JClassType t : p.getTypes()) {
            if (t.isAbstract()) {
                continue;
            }
            if (t.equals(type)) {
                continue;
            }
            logger.log(logType, "check type " + t);
            final JClassType[] interfaces = t.getImplementedInterfaces();
            for (JClassType i : interfaces) {
                if (i.equals(type)) {
                    return t;
                }
            }
        }
        return null;
    }

    protected static PageTree buildTree(List<String> pList) {
        return new PageTree(buildNode("", pList));
    }

    private static PageTree.Node buildNode(String rootToken, List<String> pList) {
        PageTree.Node node = new PageTree.Node(rootToken);
        int rootDots = "".equals(rootToken) ? -1 : dotsInString(rootToken);
        for (String p : pList) {
            if (p.equals(rootToken)) continue;
            if (!p.startsWith(rootToken)) continue;
            int pDots = dotsInString(p);
            if (rootDots + 1 == pDots) {
                node.addChild(buildNode(p, pList));
            }
        }
        return node;
    }

    public static int dotsInString(String string) {
        int dots = 0;
        int dotIndex = string.indexOf(".");
        while (dotIndex > 0) {
            dots++;
            dotIndex = string.indexOf(".", dotIndex + 1);
        }
        return dots;
    }

    private String generateFlyweight(JClassType pageType, TreeLogger logger, JPackage jPackage) throws UnableToCompleteException, NotFoundException {
        String pageName = jPackage.getName() + "." + pageType.getName();
        logger.branch(logType, "generating flyweight for page: " + pageName);

        return "new PageFlyweight<" + pageName + ">(\"" + pageName + "\"){\n" +
                "    public void load(final String token, final Map<String, String> params, final PageGroup parent) {\n" +
                "        GWT.runAsync(new RunAsyncCallback() {\n" +
                "            public void onFailure(Throwable reason) {}\n" +
                "            public void onSuccess() {\n" +
                "                if (page == null) {\n" +
//                        "                    page = GWT.create(" + pageName + ");\n" + todo: why can't I do a GWT.create here? QQ
                "                    page = new " + pageName + "();\n" +
                "                }\n" +
                "                if (page instanceof Bindable) {\n" +
                "                    ((Bindable)page).bind();\n" +
                "                }\n" +
                "                page.show(parent, params);\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "}";
    }
}
