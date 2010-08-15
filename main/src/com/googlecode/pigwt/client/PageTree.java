package com.googlecode.pigwt.client;

import java.util.ArrayList;
import java.util.List;

public class PageTree {
    public static class Node {
        private String token;
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

        public Node getChild(String token) {
            for (Node node : children) {
                if (node.getToken().equals(token)) {
                    return node;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return token + ":" + children;
        }
    }

    private Node root;

    public PageTree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }
}
