package konradn24.tml.graphics.shaders;

import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class Shader {
	
	private int shaderProgramID;

    public Shader(String vertexPath, String fragmentPath) throws IOException {
    	shaderProgramID = ShaderUtils.createProgram(vertexPath, fragmentPath);
    	
//    	int vertexID, fragmentID;
//    	
//        // First load and compile the vertex shader
//        vertexID = glCreateShader(GL_VERTEX_SHADER);
//        // Pass the shader source to the GPU
//        glShaderSource(vertexID, vertexPath);
//        glCompileShader(vertexID);
//
//        // Check for errors in compilation
//        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
//        if (success == GL_FALSE) {
//            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
//            Logging.error("Shader: '" + vertexPath + "' vertex shader compilation failed");
//            Logging.error(glGetShaderInfoLog(vertexID, len));
//            throw new IOException("Shader: '" + vertexPath + "' vertex shader compilation failed");
//        }
//
//        // First load and compile the vertex shader
//        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
//        // Pass the shader source to the GPU
//        glShaderSource(fragmentID, fragmentPath);
//        glCompileShader(fragmentID);
//
//        // Check for errors in compilation
//        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
//        if (success == GL_FALSE) {
//            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
//            Logging.error("Shader: '" + fragmentPath + "' fragment shader compilation failed");
//            Logging.error(glGetShaderInfoLog(fragmentID, len));
//            throw new IOException("Shader: '" + fragmentPath + "' fragment shader compilation failed");
//        }
//
//        // Link shaders and check for errors
//        shaderProgramID = glCreateProgram();
//        glAttachShader(shaderProgramID, vertexID);
//        glAttachShader(shaderProgramID, fragmentID);
//        glLinkProgram(shaderProgramID);
//
//        // Check for linking errors
//        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
//        if (success == GL_FALSE) {
//            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
//            Logging.error("Shader: linking '" + vertexPath + "' and '" + fragmentPath + "' failed");
//            Logging.error(glGetProgramInfoLog(shaderProgramID, len));
//            throw new IOException("Shader: linking '" + vertexPath + "' and '" + fragmentPath + "' failed");
//        }
    }

    public void use() {
        glUseProgram(shaderProgramID);
    }

    public void detach() {
        glUseProgram(0);
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1i(varLocation, slot);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1iv(varLocation, array);
    }
}
