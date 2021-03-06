package unknowndomain.engine.mod.java;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModMetadata;
import unknowndomain.engine.mod.java.harvester.HarvestedInfo;

//TODO: collect mod's class loader, instance of mod main class, mod config, mod looger, config dir.
public class JavaModContainer implements ModContainer {

    private final Path source;

    private final String modId;

    private final Logger logger;

    private ModClassLoader classLoader;

    private Object instance;

    private ModMetadata metadata;

    private HarvestedInfo harvestedInfo;

    private Path dataDir;

    public JavaModContainer(String modId, Path source) {
        this.modId = modId;
        this.logger = LoggerFactory.getLogger(modId);
        this.source = source;
    }

    @Override
    public String getModId() {
        return modId;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Path getSource() {
        return source;
    }

    @Override
    public ModMetadata getMetadata() {
        return metadata;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public HarvestedInfo getHarvestedInfo() {
        return harvestedInfo;
    }

    @Override
    public Path getDataDir() {
        return dataDir;
    }

    void initialize(ModClassLoader classLoader, ModMetadata metadata, HarvestedInfo harvestedInfo, Object instance) {
        if (this.classLoader != null)
            throw new IllegalStateException("Mod has already initilaized!");
        this.classLoader = classLoader;
        this.metadata = metadata;
        this.harvestedInfo = harvestedInfo;
        this.instance = instance;
    }
}
