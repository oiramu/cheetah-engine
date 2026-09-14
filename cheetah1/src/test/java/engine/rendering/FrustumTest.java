package engine.rendering;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import engine.core.Matrix4f;
import engine.core.Vector3f;

/**
 * Frustum's plane extraction can't be visually verified on this machine
 * (no working GL context here), so it's validated purely mathematically:
 * build a view-projection matrix with the same engine.core.Matrix4f calls
 * Camera.getViewProjection() uses, then check known points that must be
 * in/out of a simple forward-facing frustum.
 */
class FrustumTest {

	private static final float FOV = (float) Math.toRadians(70);
	private static final float ASPECT = 16f / 9f;
	private static final float Z_NEAR = 0.1f;
	private static final float Z_FAR = 100f;

	private static Matrix4f viewProjection(Vector3f camPos, Vector3f forward, Vector3f up) {
		Matrix4f projection = new Matrix4f().initPerspective(FOV, ASPECT, Z_NEAR, Z_FAR);
		Matrix4f rotation = new Matrix4f().initCamera(forward, up);
		Matrix4f translation = new Matrix4f().initTranslation(-camPos.getX(), -camPos.getY(), -camPos.getZ());

		return projection.mul(rotation.mul(translation));
	}

	private static Frustum forwardFacingFrustumAtOrigin() {
		Frustum frustum = new Frustum();
		frustum.update(viewProjection(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, 1, 0)));
		return frustum;
	}

	@Test
	void pointStraightAheadIsVisible() {
		Frustum frustum = forwardFacingFrustumAtOrigin();

		assertTrue(frustum.sphereInFrustum(new Vector3f(0, 0, 10), 0f));
	}

	@Test
	void pointBehindCameraIsNotVisible() {
		Frustum frustum = forwardFacingFrustumAtOrigin();

		assertFalse(frustum.sphereInFrustum(new Vector3f(0, 0, -10), 0f));
	}

	@Test
	void pointBeyondFarPlaneIsNotVisible() {
		Frustum frustum = forwardFacingFrustumAtOrigin();

		assertFalse(frustum.sphereInFrustum(new Vector3f(0, 0, Z_FAR + 50), 0f));
	}

	@Test
	void pointCloserThanNearPlaneIsNotVisible() {
		Frustum frustum = forwardFacingFrustumAtOrigin();

		assertFalse(frustum.sphereInFrustum(new Vector3f(0, 0, Z_NEAR / 2), 0f));
	}

	@Test
	void pointFarOffToTheSideIsNotVisible() {
		Frustum frustum = forwardFacingFrustumAtOrigin();

		assertFalse(frustum.sphereInFrustum(new Vector3f(1000, 0, 10), 0f));
	}

	@Test
	void pointSlightlyOffAxisWithinFovIsVisible() {
		Frustum frustum = forwardFacingFrustumAtOrigin();

		assertTrue(frustum.sphereInFrustum(new Vector3f(1, 0, 10), 0f));
	}

	@Test
	void sphereRadiusRescuesAPointJustOutsideThePlane() {
		Frustum frustum = forwardFacingFrustumAtOrigin();
		Vector3f justBehindNear = new Vector3f(0, 0, Z_NEAR - 0.05f);

		assertFalse(frustum.sphereInFrustum(justBehindNear, 0f));
		assertTrue(frustum.sphereInFrustum(justBehindNear, 1f));
	}

	@Test
	void frustumFollowsCameraPositionNotJustOrientation() {
		Frustum frustum = new Frustum();
		frustum.update(viewProjection(new Vector3f(0, 0, 50), new Vector3f(0, 0, 1), new Vector3f(0, 1, 0)));

		assertFalse(frustum.sphereInFrustum(new Vector3f(0, 0, 10), 0f));
		assertTrue(frustum.sphereInFrustum(new Vector3f(0, 0, 60), 0f));
	}

	@Test
	void frustumFollowsCameraOrientation() {
		Frustum frustum = new Frustum();
		frustum.update(viewProjection(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), new Vector3f(0, 1, 0)));

		assertTrue(frustum.sphereInFrustum(new Vector3f(10, 0, 0), 0f));
		assertFalse(frustum.sphereInFrustum(new Vector3f(0, 0, 10), 0f));
	}
}
