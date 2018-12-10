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
package engine.core;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Quaternion {

    private float x;
    private float y;
    private float z;
    private float w;

    /**
     * Constructor of a QUATERNION calculation.
     * @param x axis.
     * @param y axis.
     * @param z axis.
     * @param w axis.
     */
    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    /**
     * Constructor of a QUATERNION by some direction.
     * @param direction of the QUATERNION
     */
    public Quaternion(Vector3f direction) {
		Quaternion res = new Matrix4f().initRotation(direction, Vector3f.UP).toQuaternion().normalized();
		x = res.getX();
		y = res.getY();
		z = res.getZ();
		w = res.getW();
	}
	
    /**
     * Constructor of a QUATERNION from a vector movement
     * @param from vector point
     * @param to vector point
     */
	public Quaternion(Vector3f from, Vector3f to) {
	     Vector3f H = from.add(to).normalized();

	     w = from.dot(H);
	     x = from.getY()*H.getZ() - from.getZ()*H.getY();
	     y = from.getZ()*H.getX() - from.getX()*H.getZ();
	     z = from.getX()*H.getY() - from.getY()*H.getX();
	}
	
	/**
	 * Constructor of a QUATERNION from an axis by some
	 * angle.
	 * @param axis to QUATERNION
	 * @param angle to axis
	 */
	public Quaternion(Vector3f axis, float angle) {
		float halfAngle = (float)Math.toRadians(angle / 2);
		axis = axis.normalized();
		
		float sin = (float)Math.sin(halfAngle);
		float cos = (float)Math.cos(halfAngle);
		
		x = axis.getX() * sin;
		y = axis.getY() * sin;
		z = axis.getZ() * sin;
		w = cos;
	}
	
	//From Ken Shoemake's "Quaternion Calculus and Fast Animation" article
	/**
	 * Fast QUATERNION rotation from Ken Shoemake's "QUATERNION Calculus 
	 * and Fast Animation" article.
	 * @param rot Rotation.
	 */
	public Quaternion(Matrix4f rot) {
		float trace = rot.get(0, 0) + rot.get(1, 1) + rot.get(2, 2);

		if(trace > 0) {
			float s = 0.5f / (float)Math.sqrt(trace+ 1.0f);
			w = 0.25f / s;
			x = (rot.get(1, 2) - rot.get(2, 1)) * s;
			y = (rot.get(2, 0) - rot.get(0, 2)) * s;
			z = (rot.get(0, 1) - rot.get(1, 0)) * s;
		} else {
			if(rot.get(0, 0) > rot.get(1, 1) && rot.get(0, 0) > rot.get(2, 2)) {
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(0, 0) - rot.get(1, 1) - rot.get(2, 2));
				w = (rot.get(1, 2) - rot.get(2, 1)) / s;
				x = 0.25f * s;
				y = (rot.get(1, 0) + rot.get(0, 1)) / s;
				z = (rot.get(2, 0) + rot.get(0, 2)) / s;
			} else if(rot.get(1, 1) > rot.get(2, 2)) {
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(1, 1) - rot.get(0, 0) - rot.get(2, 2));
				w = (rot.get(2, 0) - rot.get(0, 2)) / s;
				x = (rot.get(1, 0) + rot.get(0, 1)) / s;
				y = 0.25f * s;
				z = (rot.get(2, 1) + rot.get(1, 2)) / s;
			} else {
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(2, 2) - rot.get(0, 0) - rot.get(1, 1));
				w = (rot.get(0, 1) - rot.get(1, 0) ) / s;
				x = (rot.get(2, 0) + rot.get(0, 2) ) / s;
				y = (rot.get(1, 2) + rot.get(2, 1) ) / s;
				z = 0.25f * s;
			}
		}

		float length = (float)Math.sqrt(x * x + y * y + z * z + w * w);
		x /= length;
		y /= length;
		z /= length;
		w /= length;
	}

    /**
     * Returns the length of the QUATERNION.
     * @return Length.
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    /**
     * Normalizes the QUATERNION data.
     * @return QUATERNION data normalized.
     */
    public Quaternion normalized() {
        float length = length();

        return new Quaternion(x / length, y / length, z / length, w / length);
    }

    /**
     * CONJUGATES the QUATERNION axis.
     * @return CONJUGATED.
     */
    public Quaternion conjugated() {
        return new Quaternion(-x, -y, -z, w);
    }
    
    /**
     * Multiply the QUATERNION by a floating point value.
     * @param r multiplier.
     * @return Multiplied QUATERNION.
     */
    public Quaternion mul(float r) {
		return new Quaternion(x * r, y * r, z * r, w * r);
	}

    /**
	 * Multiply the QUATERNION by other QUATERNION.
	 * @param r multiplier.
	 * @return Multiplied QUATERNION.
	 */
    public Quaternion mul(Quaternion r) {
        float w_ = w * r.getW() - x * r.getX() - y * r.getY() - z * r.getZ();
        float x_ = x * r.getW() + w * r.getX() + y * r.getZ() - z * r.getY();
        float y_ = y * r.getW() + w * r.getY() + z * r.getX() - x * r.getZ();
        float z_ = z * r.getW() + w * r.getZ() + x * r.getY() - y * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }

    /**
	 * Multiply the QUATERNION by a vector.
	 * @param r multiplier.
	 * @return Multiplied QUATERNION.
	 */
    public Quaternion mul(Vector3f r) {
        float w_ = -x * r.getX() - y * r.getY() - z * r.getZ();
        float x_ = w * r.getX() + y * r.getZ() - z * r.getY();
        float y_ = w * r.getY() + z * r.getX() - x * r.getZ();
        float z_ = w * r.getZ() + x * r.getY() - y * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }
    
    /**
     * Subtracts the QUATERNION by some QUATERNION.
     * @param r to subtract
     * @return subtracted QUATERNION
     */
    public Quaternion Sub(Quaternion r) {
		return new Quaternion(x - r.getX(), y - r.getY(), z - r.getZ(), w - r.getW());
	}

    /**
     * Adds the QUATERNION by some QUATERNION.
     * @param r to subtract
     * @return added QUATERNION
     */
	public Quaternion Add(Quaternion r) {
		return new Quaternion(x + r.getX(), y + r.getY(), z + r.getZ(), w + r.getW());
	}

	/**
	 * Returns the QUATERNION to a matrix.
	 * @return QUATERNION to matrix.
	 */
	public Matrix4f ToRotationMatrix() {
		Vector3f forward =  new Vector3f(2.0f * (x * z - w * y), 2.0f * (y * z + w * x), 1.0f - 2.0f * (x * x + y * y));
		Vector3f up = new Vector3f(2.0f * (x * y + w * z), 1.0f - 2.0f * (x * x + z * z), 2.0f * (y * z - w * x));
		Vector3f right = new Vector3f(1.0f - 2.0f * (y * y + z * z), 2.0f * (x * y - w * z), 2.0f * (x * z + w * y));

		return new Matrix4f().initRotation(forward, up, right);
	}

	/**
	 * Returns the dot product of a QUATERNION.	
	 * @param r dot QUATERNION.
	 * @return Dot product.
	 */
	public float Dot(Quaternion r) {
		return x * r.getX() + y * r.getY() + z * r.getZ() + w * r.getW();
	}

	/**
	 * LERP function of the QUATERNION. 
	 * @param dest QUATERNION.
	 * @param lerpFactor of the LERP.
	 * @param shortest boolean.
	 * @return LERP QUATERNION.
	 */
	public Quaternion NLerp(Quaternion dest, float lerpFactor, boolean shortest) {
		Quaternion correctedDest = dest;

		if(shortest && this.Dot(dest) < 0)
			correctedDest = new Quaternion(-dest.getX(), -dest.getY(), -dest.getZ(), -dest.getW());

		return correctedDest.Sub(this).mul(lerpFactor).Add(this).normalized();
	}

	/**
	 * LERP function of the QUATERNION. 
	 * @param dest QUATERNION.
	 * @param lerpFactor of the LERP.
	 * @param shortest boolean.
	 * @return LERP QUATERNION.
	 */
	public Quaternion SLerp(Quaternion dest, float lerpFactor, boolean shortest) {
		final float EPSILON = 1e3f;

		float cos = this.Dot(dest);
		Quaternion correctedDest = dest;

		if(shortest && cos < 0) {
			cos = -cos;
			correctedDest = new Quaternion(-dest.getX(), -dest.getY(), -dest.getZ(), -dest.getW());
		}

		if(Math.abs(cos) >= 1 - EPSILON)
			return NLerp(correctedDest, lerpFactor, false);

		float sin = (float)Math.sqrt(1.0f - cos * cos);
		float angle = (float)Math.atan2(sin, cos);
		float invSin =  1.0f/sin;

		float srcFactor = (float)Math.sin((1.0f - lerpFactor) * angle) * invSin;
		float destFactor = (float)Math.sin((lerpFactor) * angle) * invSin;

		return this.mul(srcFactor).Add(correctedDest.mul(destFactor));
	}

	//Setters
	public Quaternion Set(float x, float y, float z, float w) { this.x = x; this.y = y; this.z = z; this.w = w; return this; }
	public Quaternion Set(Quaternion r) { Set(r.getX(), r.getY(), r.getZ(), r.getW()); return this; }
	
	/**
	 * Returns the euler angle of the quaternion.
	 * @return euler angle
	 */
	public Vector3f getEulerAngles() {
		float sqx = x * x;
		float sqy = y * y;
		float sqz = z * z;
		float sqw = w * w;
		float unit = sqx + sqy + sqz + sqw;
		float test = x * y + z * w;
		
		float heading = 0;
		float attitude = 0;
		float bank = 0;
		
		if (test > 0.499f * unit)
		{ // singularity at north pole
			heading = 2.0f * (float)Math.atan2(x, w);
			attitude = (float)Math.PI / 2.0f;
			bank = 0.0f;
		}
		else if (test < -0.499 * unit)
		{ // singularity at south pole
			heading = -2.0f * (float)Math.atan2(x, w);
			attitude = -(float)Math.PI / 2.0f;
			bank = 0.0f;
		}
		else
		{
			heading = (float)Math.atan2(2.0f * y * w - 2.0f * x * z, sqx - sqy - sqz + sqw);
			attitude = (float)Math.asin(2.0f * test / unit);
			bank = (float)Math.atan2(2.0f * x * w - 2.0f * y * z, -sqx + sqy - sqz + sqw);
		}
		
		return new Vector3f((float)Math.toDegrees(bank), (float)Math.toDegrees(heading), (float)Math.toDegrees(attitude));
	}
	
	/**
	 * Returns the rotation matrix of the quaternion.
	 * @return rotation matrix
	 */
	public Matrix4f getRotationMatrix() {
		Vector3f right = getRight();
		Vector3f up = getUp();
		Vector3f forward = getForward();
		
		float[][] m = new float[][] {{right.getX(), right.getY(), right.getZ(), 0.0f},
									   {up.getX(), up.getY(), up.getZ(), 0.0f},
									   {forward.getX(), forward.getY(), forward.getZ(), 0.0f},
									   {0.0f, 0.0f, 0.0f, 1.0f}};
		return new Matrix4f(m);
	}
	
	/**
	 * Returns the forward vector.
	 * @return forward vector
	 */
	public Vector3f getForward() {
		return new Vector3f(2.0f * (x*z - w*y), 2.0f * (y * z + w * x), 1.0f - 2.0f * (x * x + y * y));
	}
	
	/**
	 * Returns the back vector.
	 * @return back vector
	 */
	public Vector3f getBack() {
		return new Vector3f(-2.0f * (x*z - w*y), -2.0f * (y * z + w * x), -(1.0f - 2.0f * (x * x + y * y)));
	}
	
	/**
	 * Returns the up vector.
	 * @return up vector
	 */
	public Vector3f getUp() {
		return new Vector3f(2.0f * (x*y + w*z), 1.0f - 2.0f * (x*x + z*z), 2.0f * (y*z - w*x));
	}
		
	/**
	 * Returns the down vector.
	 * @return down vector
	 */
	public Vector3f getDown() {
		return new Vector3f(-2.0f * (x*y + w*z), -(1.0f - 2.0f * (x*x + z*z)), -2.0f * (y*z - w*x));
	}
	
	/**
	 * Returns the right vector.
	 * @return right vector
	 */
	public Vector3f getRight() {
		return new Vector3f(1.0f - 2.0f * (y*y + z*z), 2.0f * (x*y - w*z), 2.0f * (x*z + w*y));
	}
	
	/**
	 * Returns the left vector.
	 * @return left vector
	 */
	public Vector3f getLeft() {
		return new Vector3f(-(1.0f - 2.0f * (y*y + z*z)), -2.0f * (x*y - w*z), -2.0f * (x*z + w*y));
	}

    /**
     * Returns the x axis.
     * @return x axis.
     */
    public float getX() {return x;}

    /**
     * Sets data to x axis.
     * @param x data.
     */
    public void setX(float x) {this.x = x;}

    /**
     * Returns the y axis.
     * @return y axis.
     */
    public float getY() {return y;}

    /**
     * Sets data to y axis.
     * @param y data.
     */
    public void setY(float y) {this.y = y;}

    /**
     * Returns the z axis.
     * @return z axis.
     */
    public float getZ() {return z;}

    /**
     * Sets data to z axis.
     * @param z data.
     */
    public void setZ(float z) {this.z = z;}

    /**
     * Returns the w axis.
     * @return w axis.
     */
    public float getW() {return w;}

    /**
     * Sets data to w axis.
     * @param w data.
     */
    public void setW(float w) {this.w = w;}
    
}
