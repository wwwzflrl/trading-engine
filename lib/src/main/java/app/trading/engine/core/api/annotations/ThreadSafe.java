package app.trading.engine.core.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * While it is usually reflected by name, this annotation clarifies on this fact for less obvious cases.
 * When annotated at API level, it as well sets the implementation contract
 */
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ThreadSafe {
}
