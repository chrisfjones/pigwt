package com.googlecode.pigwt.client;

import java.util.Stack;

//@Singleton
//public class PageLoader extends BaseBindsToEventBus implements NavStateChangeEvent.Handler {
public class PageLoader {
    private PageTree tree;
    private PageGroup rootPageGroup = new RootPanelPageGroup();
    private Stack<PageFlyweight> flyweightStack = new Stack<PageFlyweight>();

//    @Inject
    public PageLoader(
            final PageTree tree) {
        this.tree = tree;
    }

    protected void enactNavStateChange(NavState navState) {
        String token = navState.getToken();
        PageTree.Node lastNode;

        PageGroup parent = rootPageGroup;
        lastNode = tree.getRoot();
        int dotIndex = token.indexOf(".");
        while (token.length() > 0) {
            final PageFlyweight<? extends PageGroup> group = lastNode.getGroupFlyweight();
            loadFlyweight(navState, group, parent);
            String firstPart;
            firstPart = dotIndex > 0 ? token.substring(0, dotIndex) : token;
            if (group != null) {
                parent = group.page;
            }
            lastNode = lastNode.getChild(firstPart);
            token = dotIndex > 0 ? token.substring(dotIndex + 1) : "";
            dotIndex = token.indexOf(".");
        }
        final PageFlyweight<? extends PageGroup> group = lastNode.getGroupFlyweight();
        loadFlyweight(navState, group, parent);
        loadFlyweight(navState, lastNode.getPageFlyweight(), group == null ? parent : group.page);
    }

    private void loadFlyweight(NavState navState, PageFlyweight pageFlyweight, PageGroup parent) {
        if (pageFlyweight == null) return;
        pageFlyweight.load(navState.getToken(), navState.getParams(), parent);
    }

    public void unloadCurrentFlyweight() {
        PageFlyweight flyweight = flyweightStack.pop();
		flyweight.unload();
    }

    public Page getCurrentPage() {
        return flyweightStack.peek().page;
    }
}
