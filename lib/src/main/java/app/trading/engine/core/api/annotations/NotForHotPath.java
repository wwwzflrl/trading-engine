package app.trading.engine.core.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method generates garbage
 */
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
public @interface NotForHotPath {
}
