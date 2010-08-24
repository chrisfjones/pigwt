package com.googlecode.pigwt.client;

import java.util.LinkedList;
import java.util.Queue;

public class PageLoader {
    private PageTree tree;
    private PageGroup rootPageGroup = new RootPanelPageGroup();
    private final LinkedList<PageFlyweight> loadedFlyweights = new LinkedList<PageFlyweight>();
    private final Queue<PageFlyweight> flyweightsToLoad = new LinkedList<PageFlyweight>();

    public PageLoader(final PageTree tree) {
        this.tree = tree;
    }

    protected synchronized void enactNavStateChange(NavState navState) {
        String token = navState.getToken();
        PageTree.Node lastNode;

        lastNode = tree.getRoot();
        int level = 0;
        int dotIndex = token.indexOf(".");
        PageGroup parent = rootPageGroup;
        while (token.length() > 0) {
            final PageFlyweight<? extends PageGroup> group = lastNode.getGroupFlyweight();
            if (loadedFlyweights.size() > level && loadedFlyweights.get(level).equals(group)) {
                // don't load this flyweight because its already loaded
                parent = group.page;
            } else {
                chopLoadedFlyweightsDownToSize(level);
                flyweightsToLoad.add(group);
            }
            String firstPart;
            firstPart = dotIndex > 0 ? token.substring(0, dotIndex) : token;
            lastNode = lastNode.getChild(firstPart);
            token = dotIndex > 0 ? token.substring(dotIndex + 1) : "";
            dotIndex = token.indexOf(".");
            level++;
        }
        final PageFlyweight<? extends PageGroup> group = lastNode.getGroupFlyweight();
        if (group != null) {
            if (loadedFlyweights.size() > level && loadedFlyweights.get(level).equals(group)) {
                // don't load this flyweight because its already loaded
                parent = group.page;
            } else {
                chopLoadedFlyweightsDownToSize(level);
                flyweightsToLoad.add(group);
            }
            level++;
        }
        chopLoadedFlyweightsDownToSize(level);
        final PageFlyweight<? extends Page> lastPageFlyweight = lastNode.getPageFlyweight();
        if (lastPageFlyweight != null) {
            flyweightsToLoad.add(lastPageFlyweight);
        }

        while (!flyweightsToLoad.isEmpty()) {
            final PageFlyweight pageFlyweight = flyweightsToLoad.remove();
            loadFlyweight(navState, pageFlyweight, parent);
            loadedFlyweights.add(pageFlyweight);
            final Page page = pageFlyweight.page;
            if (page instanceof PageGroup) {
                parent = (PageGroup) page;
            }
        }
    }

    private void chopLoadedFlyweightsDownToSize(int level) {
        while (loadedFlyweights.size() > level) {
            final PageFlyweight unloadedFlyweight = loadedFlyweights.removeLast();
            Page page = loadedFlyweights.get(loadedFlyweights.size() - 1).page;
            if (page == null) {
                page = rootPageGroup;
            }
            assert (page instanceof PageGroup);
            unloadedFlyweight.unload((PageGroup) page);
        }
        assert loadedFlyweights.size() <= level;
    }

    private void loadFlyweight(NavState navState, PageFlyweight pageFlyweight, PageGroup parent) {
        if (pageFlyweight == null) return;
        pageFlyweight.load(navState.getToken(), navState.getParams(), parent);
    }
}
