package unknowndomain.engine.client.rendering;

import org.apache.commons.lang3.Validate;
import unknowndomain.engine.client.camera.CameraDefault;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.resource.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public class RendererGlobal implements Renderer.Context {
    private final List<Renderer> renderers = new ArrayList<>();
    private Camera camera = new CameraDefault();
    private boolean inited = false;

    public Camera getCamera() {
        return camera;
    }

    public void init(ResourceManager manager) {
        try {
            for (Renderer renderer : renderers) {
                renderer.init(manager);
            }
            inited = true;
        } catch (Exception e) {
            // TODO: handle exception
            inited = false;
        }
    }

    public RendererGlobal add(Renderer renderer) {
        Validate.notNull(renderer);
        renderers.add(renderer);
        return this;
    }

    public void render() {
        if (!inited) return;
        for (Renderer renderer : renderers) {
            renderer.render(this);
        }
    }
}
