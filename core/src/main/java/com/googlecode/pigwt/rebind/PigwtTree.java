package com.googlecode.pigwt.rebind;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.googlecode.pigwt.client.PigwtInjectable;
import com.googlecode.pigwt.rebind.gin.GinjectorProxy;

import java.util.*;

class PigwtTree {
    Node root;
    final List<JClassType> injectables = new ArrayList<JClassType>();
    GinjectorProxy ginjectorProxy = null;

    static class Node {
        String name;
        JClassType activityClass;
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
        ginjectorProxy = context.findGinjectorInPackage(rootPackageName);

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
        root = createPigwtTreeNode(rootPackageName, packages, context, null);
    }

    private PigwtTree.Node createPigwtTreeNode(
            final String rootPackageName,
            final List<JPackage> packages,
            final PigwtGeneratorContext context,
            final Node parent) throws NotFoundException {
        if (packages.isEmpty()) {
            return null;
        }
        JPackage p = packages.get(0);
        final String thisPackageName = p.getName();
        final String rootPattern = "^" + rootPackageName.replace(".", "\\.") + "\\..*";
        final String thisPackageMatch = thisPackageName + ".";
        if (!thisPackageMatch.matches(rootPattern)) {
            return null;
        }
        packages.remove(p);

        final PigwtTree.Node node = new PigwtTree.Node();
        node.name = thisPackageName.substring(rootPackageName.length()).replace(".", "");
        node.activityClass = context.findActivityInPackage(p);
        node.shellClass = context.findShellInPackage(p);
        node.children = new ArrayList<PigwtTree.Node>();
        node.parent = parent;

        for (JClassType t : p.getTypes()) {
            if (t.getAnnotation(PigwtInjectable.class) != null) {
                injectables.add(t);
            }
        }

        PigwtTree.Node childNode = createPigwtTreeNode(thisPackageName, packages, context, node);
        while (childNode != null) {
            node.children.add(childNode);
            childNode = createPigwtTreeNode(thisPackageName, packages, context, node);
        }

        return node;
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
}
