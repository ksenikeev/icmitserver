package icmit.errors;

import java.lang.reflect.Field;

public final class EnumUtils {

	private EnumUtils() {
	}
	
	/**
	 * @param value the enum value
	 * @return описание
	 */
	public static String enumDescription(Enum<?> value) {
		if (value != null) {
			try {
				Field f = value.getClass().getField(value.name());
				Description d = f.getAnnotation(Description.class);
				return d != null ? d.value() : value.name();
			} catch (NoSuchFieldException e) {
				return value.name();
			}
		}
		return null;
	}

	/**
	 * @param value the enum value
	 * @return код
	 */
	public static String enumCode(Enum<?> value) {
		if (value != null) {
			try {
				Field f = value.getClass().getField(value.name());
				Code c = f.getAnnotation(Code.class);
				return c != null ? c.value() : value.name();
			} catch (NoSuchFieldException e) {
				return value.name();
			}
		}
		return null;
	}
}
