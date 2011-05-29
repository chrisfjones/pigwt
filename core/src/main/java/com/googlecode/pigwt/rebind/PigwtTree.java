package com.googlecode.pigwt.rebind;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.googlecode.pigwt.client.PigwtInjectable;

import java.util.*;

class PigwtTree {
    Node root;
    final List<JClassType> injectables = new ArrayList<JClassType>();

    static class Node {
        String name;
        JClassType activityClass;
        JClassType shellClass;
        List<Node> children;

        @Override
        public String toString() {
            return name + "(" + children.size() + ")";
        }
    }

    public PigwtTree(final PigwtGeneratorContext context) throws NotFoundException {
        final String rootPackageName = context.getModulePackage().getName();
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
        root = createPigwtTreeNode(rootPackageName, packages, context);
    }

    private PigwtTree.Node createPigwtTreeNode(
            final String rootPackageName,
            final List<JPackage> packages,
            final PigwtGeneratorContext context) throws NotFoundException {
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

        for (JClassType t : p.getTypes()) {
            if (t.getAnnotation(PigwtInjectable.class) != null) {
                injectables.add(t);
            }
        }

        PigwtTree.Node childNode = createPigwtTreeNode(thisPackageName, packages, context);
        while (childNode != null) {
            node.children.add(childNode);
            childNode = createPigwtTreeNode(thisPackageName, packages, context);
        }

        return node;
    }

    public synchronized List<String> walkNames() {
        List<String> names = new ArrayList<String>();
        walkNames(names, root.name, root.children);
        return names;
    }

    private synchronized void walkNames(final List<String> names, final String prefix, final List<Node> nodes) {
        for (Node node : nodes) {
            names.add(prefix + node.name);
            walkNames(names, prefix + node.name + ".", node.children);
        }
    }

    public synchronized Map<String, JClassType> walkPrefixesToShells() {
        final HashMap<String, JClassType> prefixesToShells = new HashMap<String, JClassType>();
        walkPrefixesToShells(prefixesToShells, root.name, root.children);
        return prefixesToShells;
    }

    private synchronized void walkPrefixesToShells(final HashMap<String, JClassType> prefixesToShells, final String prefix, final List<Node> nodes) {
        for (Node node : nodes) {
            if (node.shellClass != null) {
                prefixesToShells.put(prefix + node.name, node.shellClass);
            }
            walkPrefixesToShells(prefixesToShells, prefix + node.name + ".", node.children);
        }
    }
}
