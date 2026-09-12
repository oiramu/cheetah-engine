package engine.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Vector3fTest {

    private static final float EPSILON = 1e-5f;

    @Test
    void lengthOfUnitAxisIsOne() {
        assertEquals(1.0f, Vector3f.UP.length(), EPSILON);
    }

    @Test
    void addThenSubtractReturnsOriginal() {
        Vector3f a = new Vector3f(1, 2, 3);
        Vector3f b = new Vector3f(4, -5, 6);

        Vector3f result = a.add(b).sub(b);

        assertEquals(a.getX(), result.getX(), EPSILON);
        assertEquals(a.getY(), result.getY(), EPSILON);
        assertEquals(a.getZ(), result.getZ(), EPSILON);
    }

    @Test
    void normalizedVectorHasUnitLength() {
        Vector3f v = new Vector3f(3, 4, 0).normalized();

        assertEquals(1.0f, v.length(), EPSILON);
    }

    @Test
    void crossOfPerpendicularAxesIsThirdAxis() {
        Vector3f result = Vector3f.RIGHT.cross(Vector3f.UP);

        assertTrue(result.equals(Vector3f.FORWARD));
    }
}
