/*
 * Copyright 2026 Carlos Rodriguez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package engine.rendering;

import engine.core.Matrix4f;
import engine.core.Vector3f;

/**
 * The camera's view frustum, extracted from a view-projection matrix using
 * the standard Gribb-Hartmann method (each clip plane is a signed
 * combination of the matrix's rows). Used to skip rendering entities that
 * can't possibly be on screen.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class Frustum {

	private static final int PLANE_COUNT = 6;

	private float[][] planes = new float[PLANE_COUNT][4];

	/**
	 * Recomputes the 6 frustum planes from a view-projection matrix.
	 * @param viewProjection the camera's combined view-projection matrix.
	 */
	public void update(Matrix4f viewProjection) {
		float[] row0 = row(viewProjection, 0);
		float[] row1 = row(viewProjection, 1);
		float[] row2 = row(viewProjection, 2);
		float[] row3 = row(viewProjection, 3);

		setPlane(0, add(row3, row0)); // left
		setPlane(1, sub(row3, row0)); // right
		setPlane(2, add(row3, row1)); // bottom
		setPlane(3, sub(row3, row1)); // top
		setPlane(4, add(row3, row2)); // near
		setPlane(5, sub(row3, row2)); // far
	}

	/**
	 * Gets if a sphere could be at least partially visible - false only
	 * when it's entirely outside one of the 6 planes, so this can't
	 * produce a false "not visible" for anything actually on screen.
	 * @param center of the sphere.
	 * @param radius of the sphere.
	 * @return visibility state.
	 */
	public boolean sphereInFrustum(Vector3f center, float radius) {
		for (float[] plane : planes) {
			float distance = plane[0] * center.getX() + plane[1] * center.getY() + plane[2] * center.getZ() + plane[3];
			if (distance < -radius)
				return false;
		}
		return true;
	}

	private void setPlane(int index, float[] p) {
		float length = (float) Math.sqrt(p[0] * p[0] + p[1] * p[1] + p[2] * p[2]);
		planes[index] = new float[] {p[0] / length, p[1] / length, p[2] / length, p[3] / length};
	}

	private static float[] row(Matrix4f m, int i) {
		return new float[] {m.get(i, 0), m.get(i, 1), m.get(i, 2), m.get(i, 3)};
	}

	private static float[] add(float[] a, float[] b) {
		return new float[] {a[0] + b[0], a[1] + b[1], a[2] + b[2], a[3] + b[3]};
	}

	private static float[] sub(float[] a, float[] b) {
		return new float[] {a[0] - b[0], a[1] - b[1], a[2] - b[2], a[3] - b[3]};
	}

}
