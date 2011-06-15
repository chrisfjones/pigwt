package com.googlecode.pigwt.rebind;

import com.google.gwt.core.ext.typeinfo.*;
import com.googlecode.pigwt.client.PigwtInjectable;

import java.util.*;

class PigwtTree {
    Node root;
    final Set<JClassType> injectables = new HashSet<JClassType>();
    final Map<JClassType, JClassType> abstractToConcreteInjectables = new HashMap<JClassType, JClassType>();
    JClassType ginjector = null;

    static class Node {
        String name;
        JClassType activityType;
        JConstructor activityConstructor;
        JClassType shellClass;
        List<Node> children;
        Node parent;

        @Override
        public String toString() {
            return name + "(" + children.size() + ")";
        }

        public String getFullName() {
            final String parentFullName = parent == null ? "" : parent.getFullName();
            return "".equals(parentFullName) ? name : (parentFullName + "." + name);
        }
    }

    public PigwtTree(final PigwtGeneratorContext context) throws Exception {
        final String rootPackageName = context.getModulePackage().getName();

        // todo: for now, just use the first injector we run across on the root classpath
        ginjector = context.findGinjectorInPackage(rootPackageName);

        List<JPackage> packages = findModuleSubpackages(context, rootPackageName);
        
        root = createPigwtTreeNode(rootPackageName, packages, context, null);

        resolveAbstractInjectables();
    }

    private void resolveAbstractInjectables() {
        for (JClassType type : injectables) {
            if (!type.isAbstract() && (type.isInterface() == null)) {
                continue;
            }
            abstractToConcreteInjectables.put(type, findConcreteTypeForAbstractType(type));
        }
    }

    private JClassType findConcreteTypeForAbstractType(final JClassType type) {
        final JClassType[] subtypes = type.getSubtypes();
        if (subtypes.length != 1) {
            // just return the abstract type so it can go through the GWT.create process in case the user has set up a deferred binding rule
            return type;
        }
        return subtypes[0];
    }

    private List<JPackage> findModuleSubpackages(final PigwtGeneratorContext context, final String rootPackageName) {
        List<JPackage> packages = new ArrayList<JPackage>();
        for (JPackage p : context.getAllPackages()) {
            String pName = p.getName();
            if (pName.startsWith(rootPackageName)) packages.add(p);
        }
        Collections.sort(packages, new Comparator<JPackage>() {
            @Override
            public int compare(final JPackage jPackage, final JPackage jPackage1) {
                return jPackage.getName().compareTo(jPackage1.getName());
            }
        });
        return packages;
    }

    private PigwtTree.Node createPigwtTreeNode(
            final String rootPackageName,
            final List<JPackage> packages,
            final PigwtGeneratorContext context,
            final Node parent) throws NotFoundException {
        if (packages.isEmpty()) {
            return null;
        }
        JPackage pkg = packages.get(0);
        final String thisPackageName = pkg.getName();
        final String rootPattern = "^" + rootPackageName.replace(".", "\\.") + "\\..*";
        final String thisPackageMatch = thisPackageName + ".";
        if (!thisPackageMatch.matches(rootPattern)) {
            return null;
        }
        packages.remove(pkg);

        final JClassType activityClass = context.findActivityInPackage(pkg);
        if (activityClass == null) {
            return null;
        }

        final PigwtTree.Node node = new PigwtTree.Node();
        node.activityType = activityClass;
        node.name = thisPackageName.substring(rootPackageName.length()).replace(".", "");
        node.shellClass = context.findShellInPackage(pkg);
        node.children = new ArrayList<PigwtTree.Node>();
        node.parent = parent;
        node.activityConstructor = findActivityConstructor(activityClass);

        // add injectables
        for (JType parameterType : node.activityConstructor.getParameterTypes()) {
            if (parameterType instanceof JClassType) {
                injectables.add((JClassType) parameterType);
            }
        }

        PigwtTree.Node childNode = createPigwtTreeNode(thisPackageName, packages, context, node);
        while (childNode != null) {
            node.children.add(childNode);
            childNode = createPigwtTreeNode(thisPackageName, packages, context, node);
        }

        return node;
    }

    private JConstructor findActivityConstructor(JClassType activityClass) {
        JConstructor constructor = null;
        final JConstructor[] constructors = activityClass.getConstructors();

        int mostParameters = -1;
        // find the constructor with the most injectables
        for (JConstructor c : constructors) {
            int parameters = c.getParameters().length;
            if (parameters > mostParameters) {
                constructor = c;
                mostParameters = parameters;
            }
        }
        if (constructor == null) {
            // todo: error out properly
            throw new RuntimeException("Found no suitable constructors for " + activityClass.getQualifiedSourceName() + ".");
        }
        return constructor;
    }

    public synchronized List<String> walkNames() {
        List<String> names = new ArrayList<String>();
        walkNames(names, root);
        return names;
    }

    private synchronized void walkNames(final List<String> names, Node node) {
        names.add(node.getFullName());
        for (Node child : node.children) {
            walkNames(names, child);
        }
    }

    public synchronized Map<String, JClassType> walkPrefixesToShells() {
        final HashMap<String, JClassType> prefixesToShells = new HashMap<String, JClassType>();
        walkPrefixesToShells(prefixesToShells, root);
        return prefixesToShells;
    }

    private synchronized void walkPrefixesToShells(final HashMap<String, JClassType> prefixesToShells, Node node) {
        if (node.shellClass != null) {
            prefixesToShells.put(node.getFullName(), node.shellClass);
        }
        for (Node child : node.children) {
            walkPrefixesToShells(prefixesToShells, child);
        }
    }

    public synchronized List<JClassType> walkActivityTypes() {
        List<JClassType> activityTypes = new ArrayList<JClassType>();
        walkActivityTypes(activityTypes, root);
        return activityTypes;
    }

    private void walkActivityTypes(final List<JClassType> activityTypes, final Node node) {
        if (node.activityType != null) {
            activityTypes.add(node.activityType);
        }
        for (Node child : node.children) {
            walkActivityTypes(activityTypes, child);
        }
    }
}
