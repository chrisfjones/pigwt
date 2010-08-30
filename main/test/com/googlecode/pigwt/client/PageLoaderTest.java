package com.googlecode.pigwt.client;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PageLoaderTest {
    private PageLoader loader;

    @Before
    public void setup() {
        loader = new PageLoader(null);
    }

    @Test
    public void enactStateChange_null() {
        loader.enactNavStateChange(new NavState(""));
        loader.tree = new PageTree(null);
        loader.enactNavStateChange(new NavState(""));
    }

    @Test
    public void enactStateChange_rootWithGroupFresh() {
        final PageTree.Node root = new PageTree.Node("");
        final PageFlyweight groupFlyweight = mock(PageFlyweight.class);
        final PageGroup group = mock(PageGroup.class);
        when(groupFlyweight.getPage()).thenReturn(group);
        root.setGroupFlyweight(groupFlyweight);
        
        final PageFlyweight pageFlyweight = mock(PageFlyweight.class);
        root.setPageFlyweight(pageFlyweight);
        
        loader.tree = new PageTree(root);
        final NavState navState = new NavState("");
        loader.enactNavStateChange(navState);

        final InOrder order = inOrder(groupFlyweight, pageFlyweight);
        order.verify(groupFlyweight).load(navState.getToken(), navState.getParams(), loader.rootPageGroup);
        order.verify(pageFlyweight).load(navState.getToken(), navState.getParams(), group);
    }

    @Test
    public void enactStateChange_rootNoGroupWithLoaded() {
        final PageTree.Node root = new PageTree.Node("");
        final PageFlyweight pageFlyweight = mock(PageFlyweight.class);
        root.setPageFlyweight(pageFlyweight);

        final PageFlyweight loaded1 = mock(PageFlyweight.class, "loaded1");
        final PageFlyweight loaded2 = mock(PageFlyweight.class, "loaded2");
        loader.loadedFlyweights.add(loaded1);
        loader.loadedFlyweights.add(loaded2);

        loader.tree = new PageTree(root);
        final NavState navState = new NavState("");
        loader.enactNavStateChange(navState);

        final InOrder order = inOrder(loaded1, loaded2, pageFlyweight);
        order.verify(loaded2).unload(loader.rootPageGroup);
        order.verify(loaded1).unload(loader.rootPageGroup);
        order.verify(pageFlyweight).load(navState.getToken(), navState.getParams(), loader.rootPageGroup);
    }

    @Test
    public void enactStateChange_rootWithLoadedGroup() {
        final PageTree.Node root = new PageTree.Node("");
        final PageFlyweight pageFlyweight = mock(PageFlyweight.class);
        root.setPageFlyweight(pageFlyweight);

        final PageFlyweight loadedGroupFlyweight = mock(PageFlyweight.class, "loadedGroup");
        final PageGroup loadedGroup = mock(PageGroup.class);
        when(loadedGroupFlyweight.getPage()).thenReturn(loadedGroup);
        final PageFlyweight loadedPage = mock(PageFlyweight.class, "loadedPage");
        loader.loadedFlyweights.add(loadedGroupFlyweight);
        loader.loadedFlyweights.add(loadedPage);

        root.setGroupFlyweight(loadedGroupFlyweight);

        loader.tree = new PageTree(root);
        final NavState navState = new NavState("");
        loader.enactNavStateChange(navState);

        final InOrder order = inOrder(loadedPage, pageFlyweight);
        order.verify(loadedPage).unload(loadedGroup);
        order.verify(pageFlyweight).load(navState.getToken(), navState.getParams(), loadedGroup);
        // loadedGroup is not loaded or unloaded because it is in common with target
        verify(loadedGroupFlyweight, never()).unload(any(PageGroup.class));
        verify(loadedGroupFlyweight, never()).load(anyString(), anyMap(), any(PageGroup.class));
    }

    @Test
    public void enactStateChange_leafWithGroupFresh() {
        // todo
    }

    @Test
    public void enactStateChange_leafNoGroupFresh() {
        // todo
    }

    @Test
    public void enactStateChange_leafCommonGroupLoaded() {
        // todo
    }

    @Test
    public void enactStateChange_rootLeafToLeafNoGroups() {
        // todo
    }

    @Test
    public void enactStateChange_leafToRootLeafNoGroups() {
        // todo
    }

    @Test
    public void chopLoadedFlyweightsDownToSize_0() {
        assertEquals(0, loader.loadedFlyweights.size());
        loader.chopLoadedFlyweightsDownToSize(0);
        assertEquals(0, loader.loadedFlyweights.size());
        loader.chopLoadedFlyweightsDownToSize(1);
        assertEquals(0, loader.loadedFlyweights.size());
        loader.chopLoadedFlyweightsDownToSize(-1);
        assertEquals(0, loader.loadedFlyweights.size());
    }

    @Test
    public void chopLoadedFlyweightsDownToSize() {
        loader.loadedFlyweights.add(mock(PageFlyweight.class));
        loader.loadedFlyweights.add(mock(PageFlyweight.class));
        loader.loadedFlyweights.add(mock(PageFlyweight.class));
        assertEquals(3, loader.loadedFlyweights.size());
        loader.chopLoadedFlyweightsDownToSize(2);
        assertEquals(2, loader.loadedFlyweights.size());
        loader.chopLoadedFlyweightsDownToSize(0);
        assertEquals(0, loader.loadedFlyweights.size());
    }

    @Test
    public void loadFlyweight() {
        final NavState navState = new NavState("token");
        final PageFlyweight pageFlyweight = mock(PageFlyweight.class);
        loader.loadFlyweight(navState, pageFlyweight, null);
        verify(pageFlyweight).load(navState.getToken(), navState.getParams(), null);
    }

    @Test
    public void loadFlyweight_null() {
        final NavState navState = new NavState("token");
        loader.loadFlyweight(navState, null, null);
    }
}
