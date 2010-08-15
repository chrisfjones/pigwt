package com.googlecode.pigwt.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;

//@Singleton
public class NavStateGuy implements ValueChangeHandler<String> {
    private PageLoader pageLoader;

    //    @Inject
    public NavStateGuy(final PageLoader pageLoader) {
        this.pageLoader = pageLoader;
        History.addValueChangeHandler(this);
    }

    public void onValueChange(ValueChangeEvent<String> event) {
        enactNavStateChange(event.getValue());
    }

    private void enactNavStateChange(String historyValue) {
        pageLoader.enactNavStateChange(parseNavState(historyValue));
    }

    protected NavState parseNavState(String token) {
        if (token == null) {
            return new NavState(null);
        }
        int paramIndex = token.indexOf("?");
        if (paramIndex <= 0) {
            return new NavState(token);
        }
        String paramsPart = token.substring(paramIndex + 1, token.length());

        NavState navState = new NavState(URL.decode(token.substring(0, paramIndex)));

        String[] paramChunks = paramsPart.split("&");
        for (String chunk : paramChunks) {
            int equalsIndex = chunk.indexOf("=");
            if (equalsIndex < 0) {
                break;
            }
            String key = chunk.substring(0, equalsIndex);
            String value = chunk.substring(equalsIndex + 1, chunk.length());
            key = key != null ? URL.decode(key) : key;
            value = value != null ? URL.decode(value) : value;
            navState.addParam(key, value);
        }
        return navState;
    }

    public void fireCurrentNavState() {
        History.fireCurrentHistoryState();
    }

    public String getCurrentNavStateToken() {
        return History.getToken();
    }

//    public void got(NavStateChangeRequestEvent event) {
//        boolean unbind = false;
//
//        if (event.isModifyHistory()) {
//            String navStateString = event.getNavState().toString();
//            History.newItem(navStateString, false);
//
//            if (!event.getNavState().getToken().equals(HistoryTokens.INTRO)) {
//                unbind = true;
//            }
//        }
//
//        enactNavStateChange(event.getNavState().toInternalString(), unbind);
//    }
//
}