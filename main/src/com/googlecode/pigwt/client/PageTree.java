package com.googlecode.pigwt.client;

import java.util.ArrayList;
import java.util.List;

public class PageTree {
    public static class Node {
        private String token;
        private PageFlyweight<? extends PageGroup> groupFlyweight;
        private PageFlyweight<? extends Page> pageFlyweight;
        private List<Node> children = new ArrayList<Node>();

        public Node(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void addChild(Node node) {
            children.add(node);
        }

        // assumes only the sub-packages are included in children
        public Node getChild(String lastTokenBit) {
            for (Node node : children) {
                if (node.getLastTokenBit().equals(lastTokenBit)) {
                    return node;
                }
            }
            return null;
        }

        private String getLastTokenBit() {
            final int index = token.lastIndexOf(".");
            if (index < 0) {
                return token;
            }
            return token.substring(index + 1);
        }

        public PageFlyweight<? extends PageGroup> getGroupFlyweight() {
            return groupFlyweight;
        }

        public void setGroupFlyweight(PageFlyweight<? extends PageGroup> groupFlyweight) {
            this.groupFlyweight = groupFlyweight;
        }

        public PageFlyweight<? extends Page> getPageFlyweight() {
            return pageFlyweight;
        }

        public void setPageFlyweight(PageFlyweight<? extends Page> pageFlyweight) {
            this.pageFlyweight = pageFlyweight;
        }

        @Override
        public String toString() {
            return token + ":" + children;
        }
    }

    protected Node root;

    protected PageTree() {
    }
    
    public PageTree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }
}
