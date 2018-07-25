/*
 * Copyright 2017 Carlos Rodriguez.
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
package engine.physics;

import engine.core.Vector2f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class PhysicsUtil {

	/**
	 * Do a collide when a object hits other object or a wall and tries to pass it.
	 * @param oldPos Objects' old position.
	 * @param newPos Objects' new position.
	 * @param size1 Object A's  size.
	 * @param pos2 Object A's position.
	 * @param size2 Object B's  size.
	 * @return Collision.
	public static Vector2f rectCollide(Vector2f oldPos, Vector2f newPos, Vector2f size1, Vector2f pos2, Vector2f size2) {
		Vector2f result = new Vector2f(1,1);
		
		if(!(newPos.getX() + size1.getX() < pos2.getX() || 
			newPos.getX() - size1.getX() > (pos2.getX()/size2.getX() + size2.getX()) * size2.getX() ||
			oldPos.getY() + size1.getY() < pos2.getY() || 
			oldPos.getY() - size1.getY() > (pos2.getY()/size2.getY() + size2.getY()) * size2.getY()))
				result.setX(0);
		
		if(!(oldPos.getX() + size1.getX() < pos2.getX() || 
			oldPos.getX() - size1.getX() > (pos2.getX()/size2.getX() + size2.getX())  * size2.getX() ||
			newPos.getY() + size1.getY() < pos2.getY() || 
			newPos.getY() - size1.getY() > (pos2.getY()/size2.getY() + size2.getY()) * size2.getY()))
				result.setY(0);
			
		return result;
	}
	*/

	/**
	 * Do a collide when a object hits other object or a wall and tries to pass it.
	 * @param oldPos Objects' old position.
	 * @param newPos Objects' new position.
	 * @param size1 Object A's  size.
	 * @param pos2 Object A's position.
	 * @param size2 Object B's  size.
	 * @return Collision.
	 */
    public static Vector2f rectCollide(Vector2f oldPos, Vector2f newPos, Vector2f size1, Vector2f pos2, Vector2f size2) {
        Vector2f result = new Vector2f(1, 1);

        if (!(newPos.getX() + size1.getX() < pos2.getX()
                || newPos.getX() - size1.getX() > pos2.getX() + (size2.getX() * size2.getX())
                || oldPos.getY() + size1.getY() < pos2.getY()
                || oldPos.getY() - size1.getY() > pos2.getY() + (size2.getY() * size2.getY()))) {
            result.setX(0);
        }

        if (!(oldPos.getX() + size1.getX() < pos2.getX()
                || oldPos.getX() - size1.getX() > pos2.getX() + (size2.getX() * size2.getX())
                || newPos.getY() + size1.getY() < pos2.getY()
                || newPos.getY() - size1.getY() > pos2.getY() + (size2.getY() * size2.getY()))) {
            result.setY(0);
        }

        return result;
    }

    /**
     * Generates a line of intersection and checks if something hits something else.
     * @param a1 Line A start.
     * @param a2 Line B start.
     * @param b1 Line A end.
     * @param b2 Line B end.
     * @return Data to access.
     */
    public static Vector2f lineIntersect(Vector2f a1, Vector2f a2, Vector2f b1, Vector2f b2) {
        Vector2f line1 = a2.sub(a1);
        Vector2f line2 = b2.sub(b1);

        float cross = line1.cross(line2);

        Vector2f pointDistance = b1.sub(a1);

        if (cross == 0) {
            return null;
        }

        float crossFactor1 = pointDistance.cross(line2) / cross;
        float crossFactor2 = pointDistance.cross(line1) / cross;

        if (0.0f < crossFactor1 && crossFactor1 < 1.0f && 0.0f < crossFactor2 && crossFactor2 < 1.0f) {
            return a1.add(line1.mul(crossFactor1));
        }

        return null;
    }

    /**
	Vector2f collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, wallBottom, wallBottom.add(wallX));
	
	if(collision != null && (nearestIntersect == null || 
			nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
		nearestIntersect = collision;
	
	collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, wallBottom, wallBottom.add(wallY));
	
	if(collision != null && (nearestIntersect == null || 
			nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
		nearestIntersect = collision;
	
	collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, wallBottom.add(wallX), wallTop);
	
	if(collision != null && (nearestIntersect == null || 
			nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
		nearestIntersect = collision;
	
	collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, wallBottom.add(wallY), wallTop);
	
	if(collision != null && (nearestIntersect == null || 
			nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
		nearestIntersect = collision;
	*/
    
    /**
     * Continuation of the lineIntersect method, it starts to take the 3D space and check the points of
     * possible collision and collide them if needed.
     * @see lineIntersect();
     * @param lineStart Start point of the line.
     * @param lineEnd End point of the line.
     * @param rectStart Position in the 3D space.
     * @param rectSize Object's size.
     * @return Collision.
     */
    public static Vector2f lineIntersectRect(Vector2f lineStart, Vector2f lineEnd, Vector2f rectStart, Vector2f rectSize) {
        Vector2f result = null;

        Vector2f collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, rectStart, new Vector2f(rectStart.getX() + rectSize.getX(), rectStart.getY()));

        if (collision != null && (result == null
                || result.sub(lineStart).length() > collision.sub(lineStart).length())) {
            result = collision;
        }

        collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, rectStart, new Vector2f(rectStart.getX(), rectStart.getY() + rectSize.getY()));

        if (collision != null && (result == null
                || result.sub(lineStart).length() > collision.sub(lineStart).length())) {
            result = collision;
        }

        collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, new Vector2f(rectStart.getX() + rectSize.getX(), rectStart.getY()), rectStart.add(rectSize));

        if (collision != null && (result == null
                || result.sub(lineStart).length() > collision.sub(lineStart).length())) {
            result = collision;
        }

        collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, new Vector2f(rectStart.getX(), rectStart.getY() + rectSize.getY()), rectStart.add(rectSize));

        if (collision != null && (result == null
                || result.sub(lineStart).length() > collision.sub(lineStart).length())) {
            result = collision;
        }

        return result;
    }
}
