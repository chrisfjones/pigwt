package com.googlecode.pigwt.client;

import java.util.LinkedList;
import java.util.Queue;

public class PageLoader {
    protected PageTree tree;
    protected PageGroup rootPageGroup = new RootPanelPageGroup();
    protected final LinkedList<PageFlyweight> loadedFlyweights = new LinkedList<PageFlyweight>();
    protected final Queue<PageFlyweight> flyweightsToLoad = new LinkedList<PageFlyweight>();

    public PageLoader(final PageTree tree) {
        this.tree = tree;
    }

    protected synchronized void enactNavStateChange(NavState navState) {
        if (tree == null || tree.getRoot() == null) {
            return;
        }
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
                parent = group.getPage();
            } else {
                chopLoadedFlyweightsDownToSize(level);
                if (group != null) {
                    flyweightsToLoad.add(group);
                }
            }
            String firstPart;
            firstPart = dotIndex > 0 ? token.substring(0, dotIndex) : token;
            lastNode = lastNode.getChild(firstPart);
            token = dotIndex > 0 ? token.substring(dotIndex + 1) : "";
            dotIndex = token.indexOf(".");
            level++;
        }
        final PageFlyweight<? extends PageGroup> groupFlyweight = lastNode.getGroupFlyweight();
        if (groupFlyweight != null) {
            if (loadedFlyweights.size() > level && loadedFlyweights.get(level).equals(groupFlyweight)) {
                // don't load this flyweight because its already loaded
                parent = groupFlyweight.getPage();
            } else {
                chopLoadedFlyweightsDownToSize(level);
                flyweightsToLoad.add(groupFlyweight);
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
            final Page page = pageFlyweight.getPage();
            if (page instanceof PageGroup) {
                parent = (PageGroup) page;
            }
        }
    }

    protected synchronized void chopLoadedFlyweightsDownToSize(int level) {
        level = Math.max(0, level);
        while (loadedFlyweights.size() > level) {
            final PageFlyweight unloadedFlyweight = loadedFlyweights.removeLast();
            Page parent = loadedFlyweights.size() > 0 ? loadedFlyweights.get(loadedFlyweights.size() - 1).getPage() : null;
            if (parent == null) {
                parent = rootPageGroup;
            }
            assert (parent instanceof PageGroup);
            unloadedFlyweight.unload((PageGroup) parent);
        }
        assert loadedFlyweights.size() <= level;
    }

    protected void loadFlyweight(NavState navState, PageFlyweight pageFlyweight, PageGroup parent) {
        if (pageFlyweight == null) return;
        pageFlyweight.load(navState.getToken(), navState.getParams(), parent);
    }
}
