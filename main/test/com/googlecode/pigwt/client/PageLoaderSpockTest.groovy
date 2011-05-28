package com.googlecode.pigwt.client

import org.mockito.InOrder
import spock.lang.Specification
import static org.mockito.Mockito.*

class PageLoaderSpockTest extends Specification  {
    PageLoader loader = new PageLoader(null);
    
    def "enactStateChange - null"() {
        given:
        loader.enactNavStateChange(new NavState(""));
        loader.tree = new PageTree(null);
        loader.enactNavStateChange(new NavState(""));
    }

    def "enactStateChange - root with group, fresh"() {
        given:
        PageLoader loader = new PageLoader(null);
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


        expect:
        final InOrder order = inOrder(groupFlyweight, pageFlyweight);
        order.verify(groupFlyweight).load(navState.getToken(), navState.getParams(), loader.rootPageGroup);
        order.verify(pageFlyweight).load(navState.getToken(), navState.getParams(), group);
	}

    def "enactStateChange - root No Group With Loaded"() {
        given:
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

        expect:
        final InOrder order = inOrder(loaded1, loaded2, pageFlyweight);
        order.verify(loaded2).unload(loader.rootPageGroup);
        order.verify(loaded1).unload(loader.rootPageGroup);
        order.verify(pageFlyweight).load(navState.getToken(), navState.getParams(), loader.rootPageGroup);
    }

    def "enactStateChange - root with loaded group"() {
        given:
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

        expect:
        final InOrder order = inOrder(loadedPage, pageFlyweight);
        order.verify(loadedPage).unload(loadedGroup);
        order.verify(pageFlyweight).load(navState.getToken(), navState.getParams(), loadedGroup);
        // loadedGroup is not loaded or unloaded because it is in common with target
        verify(loadedGroupFlyweight, never()).unload(any(PageGroup.class));
        verify(loadedGroupFlyweight, never()).load(anyString(), anyMap(), any(PageGroup.class));
    }

    def "enactStateChange leafWithGroupFresh"() {
        // todo
    }

    def "enactStateChange leafNoGroupFresh"() {
        // todo
    }

    def "enactStateChange_leafCommonGroupLoaded"() {
        // todo
    }

    def "enactStateChange_rootLeafToLeafNoGroups"() {
        // todo
    }

    def "enactStateChange_leafToRootLeafNoGroups"() {
        // todo
    }

    def "chopLoadedFlyweightsDownToSize_0"() {
        assertEquals(0, loader.loadedFlyweights.size());
        loader.chopLoadedFlyweightsDownToSize(0);
        assertEquals(0, loader.loadedFlyweights.size());
        loader.chopLoadedFlyweightsDownToSize(1);
        assertEquals(0, loader.loadedFlyweights.size());
        loader.chopLoadedFlyweightsDownToSize(-1);
        assertEquals(0, loader.loadedFlyweights.size());
    }

    def "chopLoadedFlyweightsDownToSize"() {
        given:
        loader.loadedFlyweights.add(mock(PageFlyweight.class));
        loader.loadedFlyweights.add(mock(PageFlyweight.class));
        loader.loadedFlyweights.add(mock(PageFlyweight.class));

        expect:
        3 == loader.loadedFlyweights.size();

        loader.chopLoadedFlyweightsDownToSize(2);
        2 == loader.loadedFlyweights.size();

        loader.chopLoadedFlyweightsDownToSize(0);
        0 == loader.loadedFlyweights.size();
    }

    def "loadFlyweight"() {
        final NavState navState = new NavState("token");
        final PageFlyweight pageFlyweight = mock(PageFlyweight.class);
        loader.loadFlyweight(navState, pageFlyweight, null);
        verify(pageFlyweight).load(navState.getToken(), navState.getParams(), null);
    }

    def "loadFlyweight_null"() {
        final NavState navState = new NavState("token");
        loader.loadFlyweight(navState, null, null);
    }
}
