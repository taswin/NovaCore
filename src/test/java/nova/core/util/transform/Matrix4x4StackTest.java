package nova.core.util.transform;

import org.junit.Before;
import org.junit.Test;

import java.util.EmptyStackException;

import static org.junit.Assert.*;

public class Matrix4x4StackTest {
	MatrixStack ms;
	@Before
	public void setUp() {
		ms = new MatrixStack();
	}

	@Test(expected = EmptyStackException.class)
	public void testThrowsOnEmpty() {
		ms.popMatrix();
	}

	@Test
	public void testStack() {
		Matrix4x4 one = MatrixHelper.translationMatrix(1,0,0);
		Matrix4x4 two = MatrixHelper.translationMatrix(0,1,0);
		Matrix4x4 three = MatrixHelper.translationMatrix(0,0,1);
		ms.loadMatrix(one);
		ms.pushMatrix();
		ms.loadMatrix(two);
		ms.pushMatrix();
		ms.loadIdentity();
		ms.pushMatrix();
		ms.loadMatrix(three);
		ms.pushMatrix();
		ms.loadIdentity();

		assertEquals(Matrix4x4.IDENTITY,ms.getMatrix());
		ms.popMatrix();
		assertEquals(three, ms.getMatrix());
		ms.popMatrix();
		assertEquals(Matrix4x4.IDENTITY, ms.getMatrix());
		ms.popMatrix();
		assertEquals(two,ms.getMatrix());
		ms.popMatrix();
		assertEquals(one,ms.getMatrix());
	}
	@Test
	public void testTransforms() {
		ms.translate(Vector3d.one);
		ms.scale(Vector3d.one.multiply(2));
		ms.pushMatrix();
		ms.rotate(Vector3d.yAxis, Math.PI/2);
		assertEquals(new Vector3d(-1,1,1),ms.transform(Vector3d.zAxis));

		ms.popMatrix();
		ms.transform(MatrixHelper.rotationMatrix(Vector3d.yAxis, Math.PI / 2));
		assertEquals(new Vector3d(-1,1,1),ms.transform(Vector3d.zAxis));

		assertEquals(ms.getMatrix().transform(Vector3d.one), ms.transform(Vector3d.one));

	}

}