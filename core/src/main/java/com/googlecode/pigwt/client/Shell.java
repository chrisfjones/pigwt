package com.googlecode.pigwt.client;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A shell in pigwt is a widget that has one main panel that you can shove stuff into. If you need nested shells, just
 * put another shell in a subpackage underneath your root. Shells are singletons in pigwt.
 */
public interface Shell extends AcceptsOneWidget, IsWidget {
}
