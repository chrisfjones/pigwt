package com.googlecode.pigwt.client;

import java.util.Stack;

//@Singleton
//public class PageLoader extends BaseBindsToEventBus implements NavStateChangeEvent.Handler {
public class PageLoader {
    private PageMap map;
    private Stack<PageFlyweight> flyweightStack = new Stack<PageFlyweight>();

//    @Inject
    public PageLoader(
            final PageMap map) {
        this.map = map;
    }

    protected void enactNavStateChange(NavState navState) {
        String token = navState.getToken();
        PageFlyweight flyweight = map.get(token);
        if (flyweight == null) {
            return;
        }

        while (!flyweightStack.isEmpty()) {
            unloadCurrentFlyweight();
        }
        
        flyweightStack.push(flyweight);
        flyweight.load(token, navState.getParams());
    }

    public void unloadCurrentFlyweight() {
        PageFlyweight flyweight = flyweightStack.pop();
		flyweight.unload();
    }

    public Page getCurrentPage() {
        return flyweightStack.peek().page;
    }
}
