package com.googlecode.pigwt.rebind;

import com.googlecode.pigwt.client.PageTree;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PageMapGeneratorTest {
    @Test
    public void buildTree() {
        final List<String> pList = Arrays.asList("", "a", "a.b", "a.c", "b", "c", "b.a", "b.a.b", "b.a.c");
        Collections.sort(pList);

        final PageTree tree = PageMapGenerator.buildTree(pList);
        assertNotNull(tree);
        final PageTree.Node root = tree.getRoot();
        assertNotNull(root);
        assertEquals("", root.getToken());
        assertEquals(3, root.getChildren().size());
        final PageTree.Node a = root.getChild("a");
        assertEquals("a", a.getToken());
        assertEquals(2, a.getChildren().size());
        final PageTree.Node b = root.getChild("b");
        assertEquals("b", b.getToken());
        assertEquals(1, b.getChildren().size());
        final PageTree.Node ba = b.getChild("b.a");
        assertEquals("b.a", ba.getToken());
        assertEquals(2, ba.getChildren().size());
    }

    @Test
    public void dots() {
        assertEquals(3, PageMapGenerator.dotsInString("a.b.c.d"));
        assertEquals(2, PageMapGenerator.dotsInString("a.b.c"));
        assertEquals(1, PageMapGenerator.dotsInString("adsfljfdslk.dlsfkjlsdfk"));
        assertEquals(0, PageMapGenerator.dotsInString("adsfljfdslk"));
    }
}
