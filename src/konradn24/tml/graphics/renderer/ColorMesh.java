package konradn24.tml.graphics.renderer;

import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import konradn24.tml.display.Display;
import konradn24.tml.graphics.shaders.Shader;

public class ColorMesh {
	private int vaoID;
	private int vboID;
	private int vertexCount;
	
	private Shader shader;
	private Matrix4f modelMatrix;
	
	public ColorMesh(float[] vertices, Shader shader, Matrix4f modelMatrix) {
		this.shader = shader;
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
		shader.use();
		
		shader.uploadMat4f("projection", Display.PROJECTION);
		shader.uploadMat4f("view", viewMatrix);
		shader.uploadMat4f("model", modelMatrix);
		
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);

		glBindVertexArray(0);
		shader.detach();
	}
	
	public void cleanup() {
		glDeleteBuffers(vboID);
		glDeleteVertexArrays(vaoID);
	}
}

