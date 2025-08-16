package konradn24.tml.graphics.renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import konradn24.tml.display.Display;
import konradn24.tml.graphics.shaders.Shader;
import konradn24.tml.utils.Logging;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class BatchRenderer {
	// Vertex
    // ======
    // Pos               Color                         tex coords     tex id
    // float, float,     float, float, float, float    float, float   float
    private static final int POS_SIZE = 2;
    private static final int COLOR_SIZE = 4;
    private static final int TEX_COORDS_SIZE = 2;
    private static final int TEX_ID_SIZE = 1;

    private static final int POS_OFFSET = 0;
    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private static final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private static final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private static final int VERTEX_SIZE = 9;
    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private List<BatchSprite> sprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public BatchRenderer(int maxBatchSize) throws IOException {
        this.shader = new Shader("res/shaders/batch_vertex_shader.glsl", "res/shaders/batch_fragment_shader.glsl");
        this.sprites = new ArrayList<>();
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void init() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void addSprite(BatchSprite spr) {
    	if(!hasRoom) {
    		Logging.error("Batch Renderer: cannot add sprite - maximum sprites amount reached");
    	}
    	
        // Get index and add renderObject
        this.sprites.add(spr);

        if (spr.getTexture() != null) {
            if (!textures.contains(spr.getTexture())) {
                textures.add(spr.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(this.sprites.size() - 1);

        if (sprites.size() >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }
    
    public void removeSprite(BatchSprite spr) {
        int index = sprites.indexOf(spr);
        if (index == -1) {
            Logging.warning("Batch Renderer: cannot remove sprite - sprite not found");
            return;
        }

        sprites.remove(index);

        if (spr.getTexture() != null && !sprites.stream().anyMatch(s -> s.getTexture() == spr.getTexture())) {
            textures.remove(spr.getTexture());
        }

        for (int i = index; i < sprites.size(); i++) {
            loadVertexProperties(i);
        }

        hasRoom = true;

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    }
    
    public void removeSprites(List<BatchSprite> toRemove) {
        if (toRemove.isEmpty()) return;

        List<Integer> removeIndices = new ArrayList<>();
        for (BatchSprite spr : toRemove) {
            int idx = sprites.indexOf(spr);
            if (idx != -1) {
                removeIndices.add(idx);
            }
        }
        if (removeIndices.isEmpty()) return;

        removeIndices.sort((a, b) -> Integer.compare(b, a));

        for (int idx : removeIndices) {
            int srcOffset = (idx + 1) * 4 * VERTEX_SIZE;
            int dstOffset = idx * 4 * VERTEX_SIZE;

            int floatsToMove = (sprites.size() - idx - 1) * 4 * VERTEX_SIZE;
            if (floatsToMove > 0) {
                System.arraycopy(vertices, srcOffset, vertices, dstOffset, floatsToMove);
            }

            sprites.remove(idx);
        }

        int usedFloats = sprites.size() * 4 * VERTEX_SIZE;
        for (int i = usedFloats; i < vertices.length; i++) {
            vertices[i] = 0;
        }

        textures.removeIf(tex -> sprites.stream().noneMatch(s -> s.getTexture() == tex));

        hasRoom = true;

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    }

    
    public void render(Matrix4f viewMatrix) {
        boolean rebufferData = false;
        for (int i=0; i < sprites.size(); i++) {
            BatchSprite spr = sprites.get(i);
            if (spr.isDirty()) {
                loadVertexProperties(i);
                spr.setClean();
                rebufferData = true;
            }
        }
        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use shader
        shader.use();
        shader.uploadMat4f("uProjection", Display.PROJECTION);
        shader.uploadMat4f("uView", viewMatrix);
        
        for (int i=0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, sprites.size() * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i=0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
        
        shader.detach();
    }

    private void loadVertexProperties(int index) {
        BatchSprite sprite = sprites.get(index);

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = null;
        int texId = 0;
        
        if(sprite.getTexture() != null) {
	        texCoords = new Vector2f[] {
	        	new Vector2f(sprite.getTexture().u1, sprite.getTexture().v0),
	        	new Vector2f(sprite.getTexture().u1, sprite.getTexture().v1),
	        	new Vector2f(sprite.getTexture().u0, sprite.getTexture().v1),
	        	new Vector2f(sprite.getTexture().u0, sprite.getTexture().v0)
	        };
	
	        if (sprite.getTexture() != null) {
	            for (int i = 0; i < textures.size(); i++) {
	                if (textures.get(i) == sprite.getTexture()) {
	                    texId = i + 1;
	                    break;
	                }
	            }
	        }
        } else {
        	texId = 0;
        }

        // Add vertices with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i=0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }
            
            // Load position
            vertices[offset] = sprite.getTransform().position.x + (xAdd * sprite.getTransform().size.x);
            vertices[offset + 1] = sprite.getTransform().position.y + (yAdd * sprite.getTransform().size.y);

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texture coordinates
            vertices[offset + 6] = texCoords != null ? texCoords[i].x : 0;
            vertices[offset + 7] = texCoords != null ? texCoords[i].y : 0;

            // Load texture id
            vertices[offset + 8] = texId;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i=0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }
}
