package nova.core.recipes;

/**
 * A recipe added event is fired when a recipe of the right type has been added
 * to the RecipeManager.
 *
 * @param <T> recipe type
 * @author Stan Hebben
 */
public class RecipeAddedEvent<T extends Recipe> {
	private final T recipe;

	public RecipeAddedEvent(T recipe) {
		this.recipe = recipe;
	}

	public T getRecipe() {
		return recipe;
	}
}
