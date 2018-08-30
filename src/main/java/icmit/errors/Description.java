package icmit.errors;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Добавляет описание филдам.
 *
  */
@Target( { FIELD })
@Retention(RUNTIME)
public @interface Description {

	/**
	 * Срока описания.
	 */
	String value();
}
