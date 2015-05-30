package mountainrangepvp.engine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * From <a href="https://code.google.com/p/bloom-lib">bloom-lib</a>. Licensed Apache License 2.0
 */
public final class BloomShaderLoader {

    static final public ShaderProgram createShader(String vertexName,
                                                   String fragmentName) {

        String vertexShader = Gdx.files.classpath(
                "bloom/" + vertexName
                        + ".vertex.glsl").readString();
        String fragmentShader = Gdx.files.classpath(
                "bloom/" + fragmentName
                        + ".fragment.glsl").readString();
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            System.err.println("Error compiling " + vertexName + " and " + fragmentName + ":\n" + shader.getLog());
            Gdx.app.exit();
        }
        return shader;
    }
}
