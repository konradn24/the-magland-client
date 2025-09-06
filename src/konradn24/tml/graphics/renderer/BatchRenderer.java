package konradn24.tml.graphics.renderer;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import konradn24.tml.display.Display;
import konradn24.tml.entities.Entity;
import konradn24.tml.graphics.shaders.Shader;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class BatchRenderer {
	
	private static final int MAX_ENTITIES = 2000;
	private static final int VERTEX_SIZE = 9;
	private static final int VERTICES_PER_QUAD = 4;
	
	private static int vaoId;
	private static int vboId;
	private static FloatBuffer vertexBuffer;
	
	private static Shader shader;
	
	private static List<BatchQuad> quads;
	
	private static Comparator<Entity> entitiesSorter = new Comparator<Entity>() {
		@Override
		public int compare(Entity a, Entity b) {
			double y1 = a.getRealY() + a.getHeight();
			double y2 = b.getRealY() + b.getHeight();
			
			if(y1 < y2)
				return -1;
			else if(y1 > y2) {
				return 1;
			}
			
			return 0;
		}
	};
	
	public static void init(Shader shader) throws IOException {
		BatchRenderer.shader = shader;
		
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);

		vertexBuffer = BufferUtils.createFloatBuffer(MAX_ENTITIES * VERTICES_PER_QUAD * VERTEX_SIZE);

		quads = new ArrayList<>();
		
		glVertexAttribPointer(0, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 2 * Float.BYTES);
		glVertexAttribPointer(2, 4, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 4 * Float.BYTES);
		glVertexAttribPointer(3, 1, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 8 * Float.BYTES);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		shader.use();
		shader.uploadMat4f("projection", Display.PROJECTION);
		shader.detach();
	}

	public static void render(List<Entity> entities) {
		entities.sort(entitiesSorter);

		vertexBuffer.clear();

		int currentSheetID = 0;

		for (Entity e : entities) {
			int sheetID = e.getTexture() == null ? 0 : e.getTexture().sheet.getID();
			
			if (sheetID != currentSheetID || !vertexBuffer.hasRemaining()) {
				if (vertexBuffer.position() > 0) {
					flush(vertexBuffer, currentSheetID);
					vertexBuffer.clear();
				}
				
				currentSheetID = sheetID;
			}

			addQuadToBuffer(e.getScreenPosition().x, e.getScreenPosition().y, (float) e.getWidth(), (float) e.getHeight(), e.getTexture(), new Vector4f(1, 1, 1, 1));
		}

		if (vertexBuffer.position() > 0) {
			flush(vertexBuffer, currentSheetID);
		}
	}
	
	public static void renderQuad(float x, float y, float w, float h, Vector4f color) {
		quads.add(new BatchQuad(x, y, w, h, color));
	}
	
	public static void renderAllQuads() {
		vertexBuffer.clear();

		for (BatchQuad quad : quads) {
			if(!vertexBuffer.hasRemaining() && vertexBuffer.position() > 0) {
				flush(vertexBuffer, 0);
				vertexBuffer.clear();
			}
			
			addQuadToBuffer(quad.x, quad.y, quad.w, quad.h, null, quad.color);
		}

		if (vertexBuffer.position() > 0) {
			flush(vertexBuffer, 0);
		}
		
		quads.clear();
	}

	private static void addQuadToBuffer(float x, float y, float w, float h, Texture texture, Vector4f color) {
		float[] uvs;
		int useTex = 0;
		
		if(texture != null) {
			uvs = new float[] { 
				texture.u0, texture.v0,
				texture.u1, texture.v0,
				texture.u1, texture.v1,
				texture.u0, texture.v1
			};
			
			useTex = 1;
		} else {
			uvs = new float[8];
		}

		vertexBuffer.put(x).put(y).put(uvs[0]).put(uvs[1]).put(color.x).put(color.y).put(color.z).put(color.w).put(useTex); // bottom left
		vertexBuffer.put(x + w).put(y).put(uvs[2]).put(uvs[3]).put(color.x).put(color.y).put(color.z).put(color.w).put(useTex); // bottom right
		vertexBuffer.put(x + w).put(y + h).put(uvs[4]).put(uvs[5]).put(color.x).put(color.y).put(color.z).put(color.w).put(useTex); // upper right
		vertexBuffer.put(x).put(y + h).put(uvs[6]).put(uvs[7]).put(color.x).put(color.y).put(color.z).put(color.w).put(useTex); // upper left
	}

	private static void flush(FloatBuffer buffer, int currentSheetID) {
		buffer.flip();
		
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);

		glBindTexture(GL_TEXTURE_2D, currentSheetID);

		shader.use();
		
		glBindVertexArray(vaoId);
		glDrawArrays(GL_QUADS, 0, buffer.limit() / VERTEX_SIZE);
		
		glBindVertexArray(0);
		shader.detach();
	}
	
	public static class BatchQuad {
		public float x, y, w, h;
		public Vector4f color;
		
		public BatchQuad(float x, float y, float w, float h, Vector4f color) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.color = color;
		}
	}
}
