package unknowndomain.engine.client.model.pipeline;

import com.google.gson.Gson;
import unknowndomain.engine.client.resource.Pipeline;
import unknowndomain.engine.client.resource.Resource;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResolveModelsNode implements Pipeline.Node {
    @Override
    public Object process(Pipeline.Context context, Object in) throws IOException {
        ResourceManager manager = context.manager();
        if (in instanceof ResourcePath) {
            return load(manager, (ResourcePath) in);
        } else if (in instanceof List) {
            List<ResourcePath> paths = (List<ResourcePath>) in;
            List<Model> models = new ArrayList<>();
            for (ResourcePath path : paths) {
                Model loaded = load(manager, path);
                models.add(loaded);
            }
            return models;
        } else {
            return new ArrayList<Model>();
        }
    }

    private Model load(ResourceManager manager, ResourcePath path) throws IOException {
        Resource load = manager.load(path);
        if (load == null) {
            return null;
        }

        Model model = new Gson().fromJson(new InputStreamReader(load.open()), Model.class);
        if (model.parent != null) {
            Model parent = load(manager, new ResourcePath("", "minecraft/models/" + model.parent + ".json"));
            if (parent == null) throw new IllegalArgumentException("Missing parent");
            if (model.elements == null) model.elements = parent.elements;
            if (model.ambientocclusion == null) model.ambientocclusion = parent.ambientocclusion;
            if (model.display == null) model.display = parent.display;

            if (parent.textures != null) model.textures.putAll(parent.textures);
        }
        model.ambientocclusion = model.ambientocclusion == null ? false : model.ambientocclusion;
        return model;
    }


}