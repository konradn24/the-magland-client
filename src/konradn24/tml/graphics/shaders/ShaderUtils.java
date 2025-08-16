package konradn24.tml.graphics.shaders;

import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShaderUtils {

	public static int loadShader(String filePath, int type) throws IOException {
		String source = new String(Files.readAllBytes(Paths.get(filePath)));
		int shaderId = glCreateShader(type);
		glShaderSource(shaderId, source);
		glCompileShader(shaderId);

		if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new RuntimeException("Shader compile error: " + glGetShaderInfoLog(shaderId));
		}
		return shaderId;
	}

	public static int createProgram(String vertexPath, String fragmentPath) throws IOException {
		int vertexShader = loadShader(vertexPath, GL_VERTEX_SHADER);
		int fragmentShader = loadShader(fragmentPath, GL_FRAGMENT_SHADER);

		int programId = glCreateProgram();
		glAttachShader(programId, vertexShader);
		glAttachShader(programId, fragmentShader);
		glLinkProgram(programId);

		if(glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException("Program link error: " + glGetProgramInfoLog(programId));
		}

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);

		return programId;
	}
}