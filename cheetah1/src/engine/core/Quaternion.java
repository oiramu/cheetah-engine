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
 * @version 1.0
 * @since 2017
 */
public class Quaternion {

    private float m_x;
    private float m_y;
    private float m_z;
    private float m_w;

    /**
     * Constructor of a QUATERNION calculation.
     * @param x axis.
     * @param y axis.
     * @param z axis.
     * @param w axis.
     */
    public Quaternion(float x, float y, float z, float w) {
        this.m_x = x;
        this.m_y = y;
        this.m_z = z;
        this.m_w = w;
    }

    /**
     * Returns the length of the QUATERNION.
     * @return Length.
     */
    public float length() {
        return (float) Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z + m_w * m_w);
    }

    /**
     * Normalizes the QUATERNION data.
     * @return QUATERNION data normalized.
     */
    public Quaternion normalized() {
        float length = length();

        return new Quaternion(m_x / length, m_y / length, m_z / length, m_w / length);
    }

    /**
     * CONJUGATES the QUATERNION axis.
     * @return CONJUGATED.
     */
    public Quaternion conjugated() {
        return new Quaternion(-m_x, -m_y, -m_z, m_w);
    }
    
    /**
     * Multiply the QUATERNION by a floating point value.
     * @param r multiplier.
     * @return Multiplied QUATERNION.
     */
    public Quaternion mul(float r) {
		return new Quaternion(m_x * r, m_y * r, m_z * r, m_w * r);
	}

    /**
	 * Multiply the QUATERNION by other QUATERNION.
	 * @param r multiplier.
	 * @return Multiplied QUATERNION.
	 */
    public Quaternion mul(Quaternion r) {
        float w_ = m_w * r.getW() - m_x * r.getX() - m_y * r.getY() - m_z * r.getZ();
        float x_ = m_x * r.getW() + m_w * r.getX() + m_y * r.getZ() - m_z * r.getY();
        float y_ = m_y * r.getW() + m_w * r.getY() + m_z * r.getX() - m_x * r.getZ();
        float z_ = m_z * r.getW() + m_w * r.getZ() + m_x * r.getY() - m_y * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }

    /**
	 * Multiply the QUATERNION by a vector.
	 * @param r multiplier.
	 * @return Multiplied QUATERNION.
	 */
    public Quaternion mul(Vector3f r) {
        float w_ = -m_x * r.getX() - m_y * r.getY() - m_z * r.getZ();
        float x_ = m_w * r.getX() + m_y * r.getZ() - m_z * r.getY();
        float y_ = m_w * r.getY() + m_z * r.getX() - m_x * r.getZ();
        float z_ = m_w * r.getZ() + m_x * r.getY() - m_y * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }
    
    public Quaternion Sub(Quaternion r) {
		return new Quaternion(m_x - r.getX(), m_y - r.getY(), m_z - r.getZ(), m_w - r.getW());
	}

	public Quaternion Add(Quaternion r) {
		return new Quaternion(m_x + r.getX(), m_y + r.getY(), m_z + r.getZ(), m_w + r.getW());
	}

	public Matrix4f ToRotationMatrix() {
		Vector3f forward =  new Vector3f(2.0f * (m_x * m_z - m_w * m_y), 2.0f * (m_y * m_z + m_w * m_x), 1.0f - 2.0f * (m_x * m_x + m_y * m_y));
		Vector3f up = new Vector3f(2.0f * (m_x * m_y + m_w * m_z), 1.0f - 2.0f * (m_x * m_x + m_z * m_z), 2.0f * (m_y * m_z - m_w * m_x));
		Vector3f right = new Vector3f(1.0f - 2.0f * (m_y * m_y + m_z * m_z), 2.0f * (m_x * m_y - m_w * m_z), 2.0f * (m_x * m_z + m_w * m_y));

		return new Matrix4f().initRotation(forward, up, right);
	}

	/**
	 * Returns the dot product of a QUATERNION.	
	 * @param r dot QUATERNION.
	 * @return Dot product.
	 */
	public float Dot(Quaternion r) {
		return m_x * r.getX() + m_y * r.getY() + m_z * r.getZ() + m_w * r.getW();
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
			m_w = 0.25f / s;
			m_x = (rot.get(1, 2) - rot.get(2, 1)) * s;
			m_y = (rot.get(2, 0) - rot.get(0, 2)) * s;
			m_z = (rot.get(0, 1) - rot.get(1, 0)) * s;
		} else {
			if(rot.get(0, 0) > rot.get(1, 1) && rot.get(0, 0) > rot.get(2, 2)) {
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(0, 0) - rot.get(1, 1) - rot.get(2, 2));
				m_w = (rot.get(1, 2) - rot.get(2, 1)) / s;
				m_x = 0.25f * s;
				m_y = (rot.get(1, 0) + rot.get(0, 1)) / s;
				m_z = (rot.get(2, 0) + rot.get(0, 2)) / s;
			} else if(rot.get(1, 1) > rot.get(2, 2)) {
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(1, 1) - rot.get(0, 0) - rot.get(2, 2));
				m_w = (rot.get(2, 0) - rot.get(0, 2)) / s;
				m_x = (rot.get(1, 0) + rot.get(0, 1)) / s;
				m_y = 0.25f * s;
				m_z = (rot.get(2, 1) + rot.get(1, 2)) / s;
			} else {
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(2, 2) - rot.get(0, 0) - rot.get(1, 1));
				m_w = (rot.get(0, 1) - rot.get(1, 0) ) / s;
				m_x = (rot.get(2, 0) + rot.get(0, 2) ) / s;
				m_y = (rot.get(1, 2) + rot.get(2, 1) ) / s;
				m_z = 0.25f * s;
			}
		}

		float length = (float)Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z + m_w * m_w);
		m_x /= length;
		m_y /= length;
		m_z /= length;
		m_w /= length;
	}

	//Setters
	public Quaternion Set(float x, float y, float z, float w) { this.m_x = x; this.m_y = y; this.m_z = z; this.m_w = w; return this; }
	public Quaternion Set(Quaternion r) { Set(r.getX(), r.getY(), r.getZ(), r.getW()); return this; }

    /**
     * Returns the x axis.
     * @return x axis.
     */
    public float getX() {return m_x;}

    /**
     * Sets data to x axis.
     * @param x data.
     */
    public void setX(float x) {this.m_x = x;}

    /**
     * Returns the y axis.
     * @return y axis.
     */
    public float getY() {return m_y;}

    /**
     * Sets data to y axis.
     * @param y data.
     */
    public void setY(float y) {this.m_y = y;}

    /**
     * Returns the z axis.
     * @return z axis.
     */
    public float getZ() {return m_z;}

    /**
     * Sets data to z axis.
     * @param z data.
     */
    public void setZ(float z) {this.m_z = z;}

    /**
     * Returns the w axis.
     * @return w axis.
     */
    public float getW() {return m_w;}

    /**
     * Sets data to w axis.
     * @param w data.
     */
    public void setW(float w) {this.m_w = w;}
    
}
