package com.acmerobotics.library.dashboard;

import org.atteo.classindex.IndexAnnotated;

/**
 * @author Ryan
 */

@IndexAnnotated
public @interface Config {
    String value() default "";
}
