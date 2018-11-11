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
public class Matrix4f {
	
	private float[][] m_m;
	
	/**
	 * Constructor of a basic 4 by 4 matrix.
	 */
	public Matrix4f()
	{
		m_m = new float[4][4];
	}
	
	/**
	 * Raw matrix constructor.
	 * @param m matrix
	 */
	public Matrix4f(float[][] m) {
		if(m.length != 4 || m[0].length != 4) {
			System.err.println("Invalid 4x4 matrix");
			new Exception().printStackTrace();
			System.exit(1);
		}
		this.m_m = m;
	}

	/**
	 * Start it with a reduced matrix.
	 * @return Reduced matrix
	 */
	public Matrix4f initIdentity()
	{
		m_m[0][0] = 1;	m_m[0][1] = 0;	m_m[0][2] = 0;	m_m[0][3] = 0;
		m_m[1][0] = 0;	m_m[1][1] = 1;	m_m[1][2] = 0;	m_m[1][3] = 0;
		m_m[2][0] = 0;	m_m[2][1] = 0;	m_m[2][2] = 1;	m_m[2][3] = 0;
		m_m[3][0] = 0;	m_m[3][1] = 0;	m_m[3][2] = 0;	m_m[3][3] = 1;
		
		return this;
	}
	
	/**
	 * Starts the translation of a object in the 3D space.
	 * @param x axis.
	 * @param y axis.
	 * @param z axis.
	 * @return Translation for the matrix.
	 */
	public Matrix4f initTranslation(float x, float y, float z)
	{
		m_m[0][0] = 1;	m_m[0][1] = 0;	m_m[0][2] = 0;	m_m[0][3] = x;
		m_m[1][0] = 0;	m_m[1][1] = 1;	m_m[1][2] = 0;	m_m[1][3] = y;
		m_m[2][0] = 0;	m_m[2][1] = 0;	m_m[2][2] = 1;	m_m[2][3] = z;
		m_m[3][0] = 0;	m_m[3][1] = 0;	m_m[3][2] = 0;	m_m[3][3] = 1;
		
		return this;
	}
	
	/**
	 * Starts the rotation of the matrix data in the 3D space environment.
	 * @param x axis.
	 * @param y axis.
	 * @param z axis.
	 * @return Rotating matrix.
	 */
	public Matrix4f initRotation(float x, float y, float z)
	{
		Matrix4f rx = new Matrix4f();
		Matrix4f ry = new Matrix4f();
		Matrix4f rz = new Matrix4f();
		
		x = (float)Math.toRadians(x);
		y = (float)Math.toRadians(y);
		z = (float)Math.toRadians(z);
		
		rz.m_m[0][0] = (float)Math.cos(z);rz.m_m[0][1] = -(float)Math.sin(z);rz.m_m[0][2] = 0;				rz.m_m[0][3] = 0;
		rz.m_m[1][0] = (float)Math.sin(z);rz.m_m[1][1] = (float)Math.cos(z);rz.m_m[1][2] = 0;					rz.m_m[1][3] = 0;
		rz.m_m[2][0] = 0;					rz.m_m[2][1] = 0;					rz.m_m[2][2] = 1;					rz.m_m[2][3] = 0;
		rz.m_m[3][0] = 0;					rz.m_m[3][1] = 0;					rz.m_m[3][2] = 0;					rz.m_m[3][3] = 1;
		
		rx.m_m[0][0] = 1;					rx.m_m[0][1] = 0;					rx.m_m[0][2] = 0;					rx.m_m[0][3] = 0;
		rx.m_m[1][0] = 0;					rx.m_m[1][1] = (float)Math.cos(x);rx.m_m[1][2] = -(float)Math.sin(x);rx.m_m[1][3] = 0;
		rx.m_m[2][0] = 0;					rx.m_m[2][1] = (float)Math.sin(x);rx.m_m[2][2] = (float)Math.cos(x);rx.m_m[2][3] = 0;
		rx.m_m[3][0] = 0;					rx.m_m[3][1] = 0;					rx.m_m[3][2] = 0;					rx.m_m[3][3] = 1;
		
		ry.m_m[0][0] = (float)Math.cos(y);ry.m_m[0][1] = 0;					ry.m_m[0][2] = -(float)Math.sin(y);ry.m_m[0][3] = 0;
		ry.m_m[1][0] = 0;					ry.m_m[1][1] = 1;					ry.m_m[1][2] = 0;					ry.m_m[1][3] = 0;
		ry.m_m[2][0] = (float)Math.sin(y);ry.m_m[2][1] = 0;					ry.m_m[2][2] = (float)Math.cos(y);ry.m_m[2][3] = 0;
		ry.m_m[3][0] = 0;					ry.m_m[3][1] = 0;					ry.m_m[3][2] = 0;					ry.m_m[3][3] = 1;
		
		m_m = rz.mul(ry.mul(rx)).getM();
		
		return this;
	}
	
	/**
	 * Starts the scaling for the matrix in the 3D space.
	 * @param x axis.
	 * @param y axis.
	 * @param z axis.
	 * @return Scaled matrix.
	 */
	public Matrix4f initScale(float x, float y, float z)
	{
		m_m[0][0] = x;	m_m[0][1] = 0;	m_m[0][2] = 0;	m_m[0][3] = 0;
		m_m[1][0] = 0;	m_m[1][1] = y;	m_m[1][2] = 0;	m_m[1][3] = 0;
		m_m[2][0] = 0;	m_m[2][1] = 0;	m_m[2][2] = z;	m_m[2][3] = 0;
		m_m[3][0] = 0;	m_m[3][1] = 0;	m_m[3][2] = 0;	m_m[3][3] = 1;
		
		return this;
	}
	
	/**
	 * Starts the projection of the matrix data with a perspective.
	 * @param fov Field of view.
	 * @param aspect of the ratio.
	 * @param zNear Near Z axis space.
	 * @param zFar Far Z axis space.
	 * @return projected matrix.
	 */
	public Matrix4f initPerspective(float fov, float aspect, float zNear, float zFar)
	{
		float tanHalfFOV = (float)Math.tan(fov / 2);
		float zRange = zNear - zFar;
		
		m_m[0][0] = 1.0f / (tanHalfFOV * aspect);	m_m[0][1] = 0;					m_m[0][2] = 0;	m_m[0][3] = 0;
		m_m[1][0] = 0;								m_m[1][1] = 1.0f / tanHalfFOV;	m_m[1][2] = 0;	m_m[1][3] = 0;
		m_m[2][0] = 0;								m_m[2][1] = 0;					m_m[2][2] = (-zNear -zFar)/zRange;	m_m[2][3] = 2 * zFar * zNear / zRange;
		m_m[3][0] = 0;								m_m[3][1] = 0;					m_m[3][2] = 1;	m_m[3][3] = 0;
		
		return this;
	}
	
	/**
	 * Starts the projection of the matrix data without a perspective.
	 * @param left side.
	 * @param right side.
	 * @param top side.
	 * @param bottom side.
	 * @param near space.
	 * @param far space.
	 * @return projected matrix.
	 */
	public Matrix4f initOrthographic(float left, float right, float top, float bottom, float near, float far)
	{
		m_m[0][0] = 2/(right - left);					m_m[0][1] = 0;								m_m[0][2] = 0;					m_m[0][3] = 0;
		m_m[1][0] = 0;								m_m[1][1] = 2/(top - bottom);					m_m[1][2] = 0;					m_m[1][3] = 0;
		m_m[2][0] = 0;								m_m[2][1] = 0;								m_m[2][2] = -1/(far - near);		m_m[2][3] = 0;
		m_m[3][0] = -(right + left)/(right - left);	m_m[3][1] = -(top + bottom)/(top - bottom);	m_m[3][2] = -near/(far - near);	m_m[3][3] = 1;
		
		return this;
	}
	
	/**
	 * Starts the rotation of the matrix data in the 3D space environment.
	 * @param forward vector.
	 * @param up vector.
	 * @return Rotation.
	 */
	public Matrix4f initRotation(Vector3f forward, Vector3f up) {
		Vector3f f = forward.normalized();
		
		Vector3f r = up.normalized();
		r = r.cross(f);
		
		Vector3f u = f.cross(r);

		return initRotation(f, u, r);
	}

	/**
	 * Starts the rotation of the matrix data in the 3D space environment.
	 * @param forward vector.
	 * @param up vector.
	 * @param right vector.
	 * @return Rotation
	 */
	public Matrix4f initRotation(Vector3f forward, Vector3f up, Vector3f right) {
		Vector3f f = forward;
		Vector3f r = right;
		Vector3f u = up;

		m_m[0][0] = r.getX();	m_m[0][1] = r.getY();	m_m[0][2] = r.getZ();	m_m[0][3] = 0;
		m_m[1][0] = u.getX();	m_m[1][1] = u.getY();	m_m[1][2] = u.getZ();	m_m[1][3] = 0;
		m_m[2][0] = f.getX();	m_m[2][1] = f.getY();	m_m[2][2] = f.getZ();	m_m[2][3] = 0;
		m_m[3][0] = 0;		m_m[3][1] = 0;		m_m[3][2] = 0;		m_m[3][3] = 1;

		return this;
	}
	
	/**
	 * Turns a 4*4 matrix to QUATERNION.
	 * @return QUATERNION
	 */
	public Quaternion toQuaternion() {
		float trace = m_m[0][0] + m_m[1][1] + m_m[2][2];
		
		float w = 1;
		float x = 0;
		float y = 0;
		float z = 0;
		  if( trace > 0 ) 
		  {// I changed M_EPSILON to 0
		    float s = 0.5f / (float)Math.sqrt(trace+ 1.0f);
		    w = 0.25f / s;
		    x = ( m_m[1][2] - m_m[2][1] ) * s;
		    y = ( m_m[2][0] - m_m[0][2] ) * s;
		    z = ( m_m[0][1] - m_m[1][0] ) * s;
		  } 
		  else 
		  {
		    if ( m_m[0][0] > m_m[1][1] && m_m[0][0] > m_m[2][2] ) 
		    {
		      float s = 2.0f * (float)Math.sqrt( 1.0f + m_m[0][0] - m_m[1][1] - m_m[2][2]);
		      w = (m_m[1][2] - m_m[2][1] ) / s;
		      x = 0.25f * s;
		      y = (m_m[1][0] + m_m[0][1] ) / s;
		     z = (m_m[2][0] + m_m[0][2] ) / s;
		    } 
		    else if (m_m[1][1] > m_m[2][2]) 
		    {
		      float s = 2.0f * (float)Math.sqrt( 1.0f + m_m[1][1] - m_m[0][0] - m_m[2][2]);
		      w = (m_m[2][0] - m_m[0][2] ) / s;
		      x = (m_m[1][0] + m_m[0][1] ) / s;
		      y = 0.25f * s;
		      z = (m_m[2][1] + m_m[1][2] ) / s;
		    } 
		    else 
		    {
		      float s = 2.0f * (float)Math.sqrt( 1.0f + m_m[2][2] - m_m[0][0] - m_m[1][1] );
		      w = (m_m[0][1] - m_m[1][0] ) / s;
		      x = (m_m[2][0] + m_m[0][2] ) / s;
		      y = (m_m[1][2] + m_m[2][1] ) / s;
		      z = 0.25f * s;
		    }
		  }
		  
		  return new Quaternion(x,y,z,w);
	}
	
	public Quaternion toQuaternionBasic()
	{
		float w = (float)(Math.sqrt(1.0 + m_m[0][0] + m_m[1][1] + m_m[2][2]) / 2.0);
		float w4 = (4.0f * w);
		float x = (m_m[1][2] - m_m[2][1]) / w4 ;
		float y = (m_m[2][0] - m_m[0][2]) / w4 ;
		float z = (m_m[0][1] - m_m[1][0]) / w4 ;
		
		return new Quaternion(x,y,z,w);
	}

	/**
	 * Sets a vector of three transform.
	 * @param r transform
	 * @return Transform.
	 */
	public Vector3f transform(Vector3f r) {
		return new Vector3f(m_m[0][0] * r.getX() + m_m[0][1] * r.getY() + m_m[0][2] * r.getZ() + m_m[0][3],
		                    m_m[1][0] * r.getX() + m_m[1][1] * r.getY() + m_m[1][2] * r.getZ() + m_m[1][3],
		                    m_m[2][0] * r.getX() + m_m[2][1] * r.getY() + m_m[2][2] * r.getZ() + m_m[2][3]);
	}
	
	/**
	 * Starts "camera" data to cross-product the visible part.
	 * @param forward coordinates.
	 * @param up coordinates.
	 * @return What data render.
	 */
	public Matrix4f initCamera(Vector3f forward, Vector3f up) {
		Vector3f f = forward.normalized();
		
		Vector3f r = up.normalized();
		r = r.cross(f);
		
		Vector3f u = f.cross(r);
		
		m_m[0][0] = r.getX();	m_m[0][1] = r.getY();	m_m[0][2] = r.getZ();	m_m[0][3] = 0;
		m_m[1][0] = u.getX();	m_m[1][1] = u.getY();	m_m[1][2] = u.getZ();	m_m[1][3] = 0;
		m_m[2][0] = f.getX();	m_m[2][1] = f.getY();	m_m[2][2] = f.getZ();	m_m[2][3] = 0;
		m_m[3][0] = 0;		m_m[3][1] = 0;		m_m[3][2] = 0;		m_m[3][3] = 1;
		
		return this;
	}
	
	/**
	 * Multiply the matrix by other matrix.
	 * @param r multiplier.
	 * @return Multiplied matrix.
	 */
	public Matrix4f mul(Matrix4f r) {
		Matrix4f res = new Matrix4f();
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				res.set(i, j, m_m[i][0] * r.get(0, j) +
							  m_m[i][1] * r.get(1, j) +
							  m_m[i][2] * r.get(2, j) +
							  m_m[i][3] * r.get(3, j));
			}
		}
		
		return res;
	}
	
	/**
	 * Returns all the matrix data.
	 * @return Matrix data.
	 */
	public float[][] getM() {
		float[][] res = new float[4][4];
		
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				res[i][j] = m_m[i][j];
		
		return res;
	}
	
	/**
	 * Returns the x and y components of the matrix.
	 * @param x component.
	 * @param y component.
	 * @return x and y component of the matrix.
	 */
	public float get(int x, int y) {return m_m[x][y];}

	/**
	 * Sets a new matrix for the main matrix.
	 * @param m new matrix.
	 */
	public void setM(float[][] m) {this.m_m = m;}
	
	/**
	 * Sets the x and y part of matrix with a value.
	 * @param x index of the matrix.
	 * @param y index of the matrix.
	 * @param value to set.
	 */
	public void set(int x, int y, float value) {m_m[x][y] = value;}
	
}
