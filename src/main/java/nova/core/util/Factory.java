package nova.core.util;

import java.util.function.Supplier;

/**
 * @param <T> Type of produced object
 * @author Calclavia
 */
public class Factory<T extends Identifiable> implements Identifiable {
	protected final Supplier<T> constructor;
	protected final T dummy;

	public Factory(Supplier<T> constructor) {
		this.constructor = constructor;
		this.dummy = constructor.get();
	}

	public T getDummy() {
		return dummy;
	}

	public String getID() {
		return dummy.getID();
	}
}
