package engine.physics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import engine.core.Vector2f;

class PhysicsUtilTest {

    @Test
    void lineIntersectFindsCrossingPoint() {
        Vector2f a1 = new Vector2f(0, 0);
        Vector2f a2 = new Vector2f(10, 10);
        Vector2f b1 = new Vector2f(0, 10);
        Vector2f b2 = new Vector2f(10, 0);

        Vector2f result = PhysicsUtil.lineIntersect(a1, a2, b1, b2);

        assertNotNull(result);
        assertEquals(5.0f, result.getX(), 1e-4f);
        assertEquals(5.0f, result.getY(), 1e-4f);
    }

    @Test
    void lineIntersectReturnsNullForParallelLines() {
        Vector2f a1 = new Vector2f(0, 0);
        Vector2f a2 = new Vector2f(10, 0);
        Vector2f b1 = new Vector2f(0, 5);
        Vector2f b2 = new Vector2f(10, 5);

        assertNull(PhysicsUtil.lineIntersect(a1, a2, b1, b2));
    }

    @Test
    void lineIntersectRectHitsNearestEdge() {
        Vector2f lineStart = new Vector2f(-5, 1);
        Vector2f lineEnd = new Vector2f(5, 1);
        Vector2f rectStart = new Vector2f(0, 0);
        Vector2f rectSize = new Vector2f(2, 2);

        Vector2f result = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, rectStart, rectSize);

        assertNotNull(result);
        assertEquals(0.0f, result.getX(), 1e-4f);
        assertEquals(1.0f, result.getY(), 1e-4f);
    }
}
