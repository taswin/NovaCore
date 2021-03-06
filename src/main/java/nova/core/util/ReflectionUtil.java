package nova.core.util;

import nova.core.util.exception.NovaException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ReflectionUtil {
	private ReflectionUtil() {
	}

	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new NovaException();
		}
	}

	/**
	 * Invokes an action on each field annotated with specified annotation of given object
	 * @param <T> Annotation type
	 * @param annotation Annotation type
	 * @param clazz Class to scan
	 * @param action Action to invoke
	 */
	public static <T extends Annotation> void forEachAnnotatedField(Class<? extends T> annotation, Class clazz, BiConsumer<Field, T> action) {
		Arrays.stream(clazz.getDeclaredFields())
			.filter(f -> f.isAnnotationPresent(annotation))
			.forEachOrdered(f -> action.accept(f, f.getAnnotation(annotation)));
	}

	/**
	 * Gets all the annotated fields of this class, including all the parents classes in the order of hierarchy.
	 * @return An ordered map of annotated fields and their annotations from the order of the most sub class to the most super class.
	 */
	public static <T extends Annotation> Map<Field, T> getAnnotatedFields(Class<T> annotation, Class clazz) {
		Map<Field, T> fields = new LinkedHashMap<>();
		forEachRecursiveAnnotatedField(annotation, clazz, fields::put);
		return fields;
	}

	//TODO: Cache this?
	public static <T extends Annotation> void forEachRecursiveAnnotatedField(Class<T> annotation, Class clazz, BiConsumer<Field, T> action) {
		Arrays.stream(clazz.getDeclaredFields())
			.filter(f -> f.isAnnotationPresent(annotation))
			.forEachOrdered(f -> action.accept(f, f.getAnnotation(annotation)));

		Class superClass = clazz.getSuperclass();

		if (superClass != null) {
			forEachRecursiveAnnotatedField(annotation, superClass, action);
		}
	}
}
