package nova.core.entity.components;

import nova.core.util.Identifiable;

/**
 * Applied to entities that can take damage.
 *
 * @author Calclavia
 */
public interface Damageable {

	default void damage(double amount) {
		damage(amount, DamageType.generic);
	}

	void damage(double amount, DamageType type);

	public static class DamageType implements Identifiable {

		public static final DamageType generic = new DamageType("generic");

		public final String name;

		public DamageType(String name) {
			this.name = name;
		}

		@Override
		public String getID() {
			return name;
		}
	}
}
