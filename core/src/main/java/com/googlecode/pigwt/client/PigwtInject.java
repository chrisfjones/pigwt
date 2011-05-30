package com.googlecode.pigwt.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that signifies you would like pigwt to inject a singleton instance of this class into any Activities
 * that pigwt creates. This is best used as a starting point to better injection patterns (like ClientFactory or gin).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface PigwtInject {
}
