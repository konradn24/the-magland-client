package konradn24.tml.graphics.renderer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import konradn24.tml.display.Display;
import konradn24.tml.graphics.shaders.ShaderUtils;

public class InstanceRenderer {
	private static final int VERTEX_COUNT = 6;
	
	private static int defaultShaderProgram, shaderProgram;
	private static int vaoID;
	private static int vertexVboID;
	private static int instanceVboID;

	private static class InstanceData {
		public Texture texture;
		public Matrix4f modelMatrix;
		
		public InstanceData(Texture texture, Matrix4f modelMatrix) {
			this.texture = texture;
			this.modelMatrix = modelMatrix;
		}
	}
	
	private static List<InstanceData> instances;
	
	public static void init() throws IOException {
		defaultShaderProgram = ShaderUtils.createProgram("res/shaders/instance_vertex_shader.glsl", "res/shaders/instance_fragment_shader.glsl");
		shaderProgram = defaultShaderProgram;

		float[] quadVertices = {
			// pos 		// uv
			0f, 0f, 	0f, 0f, 
			1f, 0f, 	1f, 0f, 
			1f, 1f, 	1f, 1f, 
			1f, 1f, 	1f, 1f, 
			0f, 1f, 	0f, 1f, 
			0f, 0f, 	0f, 0f, 
		};

		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		vertexVboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexVboID);

		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(quadVertices.length);
		vertexBuffer.put(quadVertices).flip();
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

		int vertexStride = 4 * Float.BYTES;
		
		// pos (location = 0)
		glVertexAttribPointer(0, 2, GL_FLOAT, false, vertexStride, 0);
		glEnableVertexAttribArray(0);

		// texCoord (location = 1)
		glVertexAttribPointer(1, 2, GL_FLOAT, false, vertexStride, 2 * Float.BYTES);
		glEnableVertexAttribArray(1);
		
		instanceVboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, instanceVboID);

		int vec4Size = 4 * Float.BYTES;
		int instanceStride = 20 * Float.BYTES;
		
		// Instance modelMatrix (4 vec4 = Matrix4f) (location = 2,3,4,5)
		for(int i = 0; i < 4; i++) {
			int loc = 2 + i;
			glEnableVertexAttribArray(loc);
			glVertexAttribPointer(loc, 4, GL_FLOAT, false, instanceStride, i * vec4Size);
			glVertexAttribDivisor(loc, 1);
		}
		
		// Instance texOffset (location = 6)
		glEnableVertexAttribArray(6);
		glVertexAttribPointer(6, 2, GL_FLOAT, false, instanceStride, 4 * vec4Size);
		glVertexAttribDivisor(6, 1);
		
		// Instance texScale (location = 7)
		glEnableVertexAttribArray(7);
		glVertexAttribPointer(7, 2, GL_FLOAT, false, instanceStride, 4 * vec4Size + 2 * Float.BYTES);
		glVertexAttribDivisor(7, 1);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		instances = new ArrayList<>();
	}

	public static void render(Texture texture, Matrix4f modelMatrix) {
		instances.add(new InstanceData(texture, modelMatrix));
	}
	
	public static void renderInstances(Matrix4f view) {
		if(instances.isEmpty()) {
			return;
		}
		
		glUseProgram(shaderProgram);
		
		// Projection and view matrices
		try(MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(16);
			glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "projection"), false, Display.PROJECTION.get(buf));
			glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "view"), false, view.get(buf));
		}
		
		glBindVertexArray(vaoID);
		
		// Render all instances by batches of the same texture atlas
		SpriteSheet currentSheet = instances.get(0).texture.sheet;
		List<InstanceData> batch = new ArrayList<>();
		
		for(InstanceData instance : instances) {
			if(instance.texture.sheet != currentSheet) {
				InstanceRenderer.renderBatch(batch, currentSheet);
				batch.clear();
				currentSheet = instance.texture.sheet;
			}
			
			batch.add(instance);
		}
		
		if(!batch.isEmpty()) {
			InstanceRenderer.renderBatch(batch, currentSheet);
		}
		
		glBindVertexArray(0);
		glUseProgram(0);
		
		if(shaderProgram != defaultShaderProgram) {
			shaderProgram = defaultShaderProgram;
		}
	}
	
	private static void renderBatch(List<InstanceData> batch, SpriteSheet sheet) {
		if(batch.isEmpty()) {
			return;
		}
		
		// Bind texture
		glActiveTexture(GL_TEXTURE0);
		sheet.bind();
		glUniform1i(glGetUniformLocation(shaderProgram, "tex"), 0); // TODO
		
		// Bind instance buffer
		FloatBuffer instanceBuf = BufferUtils.createFloatBuffer(20 * batch.size());
		for(InstanceData instance : batch) {
			instance.modelMatrix.get(instanceBuf);
			instanceBuf.put(instance.texture.u0).put(instance.texture.v0);
			instanceBuf.put(instance.texture.u1 - instance.texture.u0).put(instance.texture.v1 - instance.texture.v0);
		}
		
		instanceBuf.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, instanceVboID);
		glBufferData(GL_ARRAY_BUFFER, instanceBuf, GL_DYNAMIC_DRAW);
		
		glDrawArraysInstanced(GL_TRIANGLES, 0, VERTEX_COUNT, batch.size());
	}

	public static void clearInstances() {
		instances.clear();
	}
	
	public static void cleanup() {
		instances.clear();
		
		glDeleteBuffers(vertexVboID);
		glDeleteBuffers(instanceVboID);
		glDeleteVertexArrays(vaoID);
		glDeleteProgram(shaderProgram);
	}

	public static void setShaderProgram(int shaderProgram) {
		InstanceRenderer.shaderProgram = shaderProgram;
	}
}