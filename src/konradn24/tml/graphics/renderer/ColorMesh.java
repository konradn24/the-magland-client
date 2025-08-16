package konradn24.tml.graphics.renderer;

import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import konradn24.tml.display.Display;

public class ColorMesh {
	private int vaoID;
	private int vboID;
	private int vertexCount;
	
	private int shaderProgram;
	private Matrix4f modelMatrix;
	
	public ColorMesh(float[] vertices, int shaderProgram, Matrix4f modelMatrix) {
		this.shaderProgram = shaderProgram;
		this.modelMatrix = modelMatrix;
		
		vertexCount = vertices.length / 5;
		
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		// VBO
		FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
		buffer.put(vertices).flip();
		
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		
		// Attribute 0: position (2 floats)
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 5 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);
		
		// Attribute 1: color (3 floats)
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 5 * Float.BYTES, 2 * Float.BYTES);
		glEnableVertexAttribArray(1);
		
		// Unbind
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public void render(Matrix4f viewMatrix) {
		glUseProgram(shaderProgram);
		
		int locProjection = glGetUniformLocation(shaderProgram, "projection");
		
		if(locProjection != -1) {
			FloatBuffer buf = BufferUtils.createFloatBuffer(16);
			Display.PROJECTION.get(buf);
			glUniformMatrix4fv(locProjection, false, buf);
		}
		
		int locView = glGetUniformLocation(shaderProgram, "view");
		if(locView != -1) {
			FloatBuffer viewBuf = BufferUtils.createFloatBuffer(16);
			viewMatrix.get(viewBuf);
			glUniformMatrix4fv(locView, false, viewBuf);
		}
		
		int locModel = glGetUniformLocation(shaderProgram, "model");
		if(locModel != -1) {
			FloatBuffer buf = BufferUtils.createFloatBuffer(16);
			modelMatrix.get(buf);
			glUniformMatrix4fv(locModel, false, buf);
		}
		
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);

		glBindVertexArray(0);
		glUseProgram(0);
	}
	
	public void cleanup() {
		glDeleteBuffers(vboID);
		glDeleteVertexArrays(vaoID);
	}
}

