package com.googlecode.pigwt.rebind;

import com.google.gwt.core.ext.*;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.googlecode.pigwt.client.PageMap;
import com.googlecode.pigwt.client.PageTree;

import java.io.PrintWriter;
import java.util.*;

public class PageMapGenerator extends Generator {
    private static final String PAGE_MAP_CLASS_QNAME = PageMap.class.getCanonicalName();

    private String packageName;
    private String className;
    private static final TreeLogger.Type logType = TreeLogger.DEBUG;
    private static final String PAGE_QNAME = "com.googlecode.pigwt.client.Page";
    private static final String GROUP_QNAME = "com.googlecode.pigwt.client.PageGroup";
    private static final String ROOT_PACKAGE_VARIABLE_NAME = "pigwt.rootPackage";

    @Override
    public String generate(
            TreeLogger logger,
            GeneratorContext context,
            String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();

        try {
            JClassType classType = typeOracle.getType(typeName);
            packageName = classType.getPackage().getName();
            className = classType.getSimpleSourceName() + "Wrapper";
            // Generate class source code
            generateClass(logger, context);
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR, "Failed to generate PageMap", e);
        }
        return packageName + "." + className;
    }

    private void generateClass(TreeLogger logger, GeneratorContext context) throws NotFoundException, BadPropertyValueException, UnableToCompleteException {
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, packageName, className);
        if (printWriter == null) return;
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.setSuperclass(PAGE_MAP_CLASS_QNAME);
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.addImport("java.util.Map");
        composer.addImport("com.google.gwt.core.client.RunAsyncCallback");

        SourceWriter sourceWriter = null;
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
        packages.put("", typeOracle.getPackage(rootPackage));

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

        final ArrayList<String> pList = new ArrayList<String>(packages.keySet());
        Collections.sort(pList);

        PageTree tree = buildTree(pList);

        for (String key : packages.keySet()) {
            JPackage p1 = packages.get(key);
            JClassType rootGroup = findTypeInPackage(groupType, p1, logger);
            JClassType rootPage = findTypeInPackage(pageType, p1, logger);
            if (rootPage != null) {
                body.append(generateFlyweight(key, rootPage, logger, p1));
            }
        }

        // todo: this would be nicer
//        JMethod method = new JMethod(null, className);
//        method.getReadableDeclaration(false, false, false, false, true)
        
        writeMethod(sourceWriter, "public " + className + "()", body.toString());

//        sourceWriter.commit(logger);
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


    private String generateFlyweight(String token, JClassType pageType, TreeLogger logger, JPackage jPackage) throws UnableToCompleteException, NotFoundException {
        String pageName = jPackage.getName() + "." + pageType.getName();
        logger.branch(logType, "generating flyweight for page: " + pageName);
        String code =
                "new PageFlyweight<" + pageName + ">(){\n" +
                        "    public void load(final String token, final Map<String, String> params) {\n" +
                        "        GWT.runAsync(new RunAsyncCallback() {\n" +
                        "            public void onFailure(Throwable reason) {}\n" +
                        "            public void onSuccess() {\n" +
                        "                if (page == null) {\n" +
//                        "                    page = GWT.create(" + pageName + ");\n" + todo: why can't I do a GWT.create here?
                        "                    page = new " + pageName + "();\n" +
                        "                }\n" +
                        "                page.show(null, params);\n" +
                        "            }\n" +
                        "        });\n" +
                        "    }\n" +
                        "}\n";

        return "put(\"" + token + "\", " + code + ");";
    }
}
