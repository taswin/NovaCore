package nova.core.render.model;

import nova.core.render.texture.Texture;
import nova.core.util.transform.Matrix4x4;
import nova.core.util.transform.MatrixStack;
import nova.core.util.transform.Quaternion;
import nova.core.util.transform.Vector2d;
import nova.core.util.transform.Vector3;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A model is capable of containing multiple faces.
 * @author Calclavia
 */
public class Model implements Iterable<Model>, Cloneable {

	//The name of the model
	public final String name;
	/**
	 * A list of all the shapes drawn.
	 */
	public final Set<Face> faces = new HashSet<>();
	public final Set<Model> children = new HashSet<>();

	public Matrix4x4 matrix = Matrix4x4.IDENTITY;

	public Vector2d textureOffset = Vector2d.zero;

	public Model(String name) {
		this.name = Objects.requireNonNull(name, "Model name cannot be null!");
	}

	public Model() {
		this("");
	}

	/**
	 * Binds all the faces and all child models with this texture.
	 * @param texture The texture
	 */
	public void bind(Texture texture) {
		faces.forEach(f -> f.texture = Optional.of(texture));
	}

	/**
	 * Binds the texture to the model, and all its children.
	 * @param texture
	 */
	public void bindAll(Texture texture) {
		bind(texture);
		children.forEach(m -> m.bindAll(texture));
	}

	/**
	 * Starts drawing, by returning an Face for the Model to work on.
	 * Add vertices to this Face and finish it by calling drawFace()
	 * @return new {@link Face}
	 */
	public Face createFace() {
		return new Face();
	}

	/**
	 * Finish drawing the Face by adding it into the list of Faces.
	 * @param Face - The finished masterpiece.
	 */
	public void drawFace(Face Face) {
		faces.add(Face);
	}

	public Set<Model> flatten() {
		return flatten(new MatrixStack());
	}

	public Model translate(Vector3<?> vec) {
		return translate(vec.xd(), vec.yd(), vec.zd());
	}

	public Model translate(double x, double y, double z) {
		matrix = new MatrixStack().loadMatrix(matrix).translate(x, y, z).getMatrix();
		return this;
	}

	public Model scale(Vector3<?> vec) {
		return scale(vec.xd(), vec.yd(), vec.zd());
	}

	public Model scale(double x, double y, double z) {
		matrix = new MatrixStack().loadMatrix(matrix).scale(x, y, z).getMatrix();
		return this;
	}

	public Model rotate(Quaternion quat) {
		matrix = new MatrixStack().loadMatrix(matrix).rotate(quat).getMatrix();
		return this;
	}

	public Model rotate(Vector3<?> vec, double angle) {
		matrix = new MatrixStack().loadMatrix(matrix).rotate(vec, angle).getMatrix();
		return this;
	}

	/**
	 * Combines child models with names into one model with its children being the children selected.
	 * @param newModelName The new name for the model
	 * @param names The names of the child models
	 * @return The new model containing all the children.
	 */
	public Model combineChildren(String newModelName, String... names) {
		return combineChildren(newModelName, m -> Arrays.asList(names).contains(m.name));
	}

	/**
	 * Combines child models with names into one model with its children being the children selected.
	 * @param newModelName The new name for the model
	 * @param predicate The condition to select children
	 * @return The new model containing all the children.
	 */
	public Model combineChildren(String newModelName, Predicate<Model> predicate) {
		Model newModel = new Model(newModelName);

		Set<Model> combineChildren = children
			.stream()
			.filter(predicate)
			.collect(Collectors.toSet());

		newModel.children.addAll(combineChildren);
		children.removeAll(combineChildren);
		children.add(newModel);
		return newModel;
	}

	/**
	 * Flattens the model into a set of models with no additional transformations,
	 * applying all the transformations into the individual vertices.
	 * @param matrixStack transformation matrix.
	 * @return Resulting set of models
	 */
	public Set<Model> flatten(MatrixStack matrixStack) {
		Set<Model> models = new HashSet<>();

		matrixStack.pushMatrix();
		matrixStack.transform(matrix);
		//Create a new model with transformation applied.
		Model transformedModel = clone();
		transformedModel.faces.stream().forEach(f -> {
				f.normal = f.normal; //TODO normal matrix;
				f.vertices.forEach(v -> v.vec = matrixStack.transform(v.vec));
			}
		);

		models.add(transformedModel);
		//Flatten child models
		models.addAll(children.stream().flatMap(m -> m.flatten(matrixStack).stream()).collect(Collectors.toSet()));
		matrixStack.popMatrix();
		return models;
	}

	@Override
	protected Model clone() {
		Model model = new Model(name);
		model.faces.addAll(faces.stream().map(Face::clone).collect(Collectors.toSet()));
		model.children.addAll(children.stream().map(Model::clone).collect(Collectors.toSet()));
		model.matrix = matrix;
		model.textureOffset = textureOffset;
		return model;
	}

	@Override
	public Iterator<Model> iterator() {
		return children.iterator();
	}

	@Override
	public Spliterator<Model> spliterator() {
		return children.spliterator();
	}

	@Override
	public String toString() {
		return "Model['" + name + "', " + faces.size() + " faces, " + children.size() + " children]";
	}
}
