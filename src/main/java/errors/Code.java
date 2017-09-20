package errors;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Добавляет код
 *
 */
@Target( { FIELD })
@Retention(RUNTIME)
public @interface Code {

	/**
	 * код элемента.
	 */
	String value();
}
