package unknowndomain.engine.client;

import com.google.common.collect.Maps;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockBuilder;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.block.BlockRuntime;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.Mesh;
import unknowndomain.engine.client.model.pipeline.ModelToMeshNode;
import unknowndomain.engine.client.model.pipeline.ResolveModelsNode;
import unknowndomain.engine.client.model.pipeline.ResolveTextureUVNode;
import unknowndomain.engine.client.rendering.RendererDebug;
import unknowndomain.engine.client.rendering.RendererGlobal;
import unknowndomain.engine.client.rendering.gui.RendererGui;
import unknowndomain.engine.client.resource.Pipeline;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBuilder;
import unknowndomain.engine.item.ItemPrototype;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.registry.IdentifiedRegistry;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.world.Chunk;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MinecraftMod {
    private GLTexture textureMap;
    private GLMesh[] meshRegistry;

    void setupRender(GameContext context, ResourceManager manager, RendererGlobal renderer) throws Exception {
        Pipeline pipeline = new Pipeline(manager);
        pipeline.add("BlockModels", new ResolveModelsNode(), new ResolveTextureUVNode(), new ModelToMeshNode(),
                new MeshToGLNode());

        Registry<Block> registry = context.getManager().getRegistry(Block.class);
        List<ResourcePath> pathList = new ArrayList<>();
        for (Block value : registry.getValues()) {
            if (value.getRegistryName().equals("air"))
                continue;
            String path = "/minecraft/models/block/" + value.getRegistryName() + ".json";
            pathList.add(new ResourcePath(path));
        }

        Map<String, Object> result = pipeline.push("BlockModels", pathList);
        textureMap = (GLTexture) result.get("TextureMap");
        List<GLMesh> meshList = (List<GLMesh>) result.get("BlockModels");
        meshRegistry = new GLMesh[meshList.size()];
        for (int i = 0; i < meshList.size(); i++) {
            meshRegistry[i] = meshList.get(i);
        }

        RendererDebug debug = new RendererDebug();
        debug.setTexture(textureMap);
        debug.setMeshRegistry(meshRegistry);
        renderer.add(debug);
        context.register(debug);
//        RendererSkybox skybox = new RendererSkybox();
//        renderer.add(skybox);

        // v = new Shader(0, ShaderType.VERTEX_SHADER);
        // v.loadShader("assets/unknowndomain/shader/frame.vert");
        // f = new Shader(0, ShaderType.FRAGMENT_SHADER);
        // f.loadShader("assets/unknowndomain/shader/frame.frag");
        // RenderBoundingBox frame = new RenderBoundingBox(v, f);
        // renderer.add(frame);

        RendererGui gui = new RendererGui();
        renderer.add(gui);
    }

    private Item createPlace(Block object) {
        class PlaceBlock implements ItemPrototype.UseBlockBehavior {
            private Block object;

            private PlaceBlock(Block object) {
                this.object = object;
            }

            @Override
            public void onUseBlockStart(World world, Entity entity, Item item, BlockPrototype.Hit hit) {
                BlockPos side = hit.face.side(hit.position);
                System.out.println("HIT: " + hit.position + " " + hit.face + " " + hit.hit + " SIDE: " + side);
                world.setBlock(side, object);
            }
        }
        return ItemBuilder.create(object.getRegistryName() + "_placer").setUseBlockBehavior(new PlaceBlock(object))
                .build();
    }

    void setupResource(GameContext context, ResourceManager manager) throws Exception {
    }

    void postInit(GameContext context) {
        IdentifiedRegistry<Item> itemRegistry = context.getItemRegistry();
        IdentifiedRegistry<Block> blockRegistry = context.getBlockRegistry();
        Item stone = itemRegistry.getValue("stone_placer");
        UnknownDomain.getEngine().getController().getPlayer().getMountingEntity().getBehavior(Entity.TwoHands.class).setMainHand(stone);
        UnknownDomain.getEngine().getWorld().setBlock(new BlockPos(1, 0, 0), blockRegistry.getValue(1));
    }

    void init(GameContext context) {
        IdentifiedRegistry<Block> blockRegistry = context.getBlockRegistry();
        blockRegistry.register(BlockBuilder.create("air").setNoCollision().build());
        blockRegistry.register(BlockBuilder.create("stone").build());

        IdentifiedRegistry<Item> itemRegistry = context.getItemRegistry();
        itemRegistry.register(createPlace(blockRegistry.getValue("stone")));
    }

    public static class ChunkStore0 implements Chunk.Store {
        // should do the io operation to load chunk
        private LongObjectMap<Chunk> chunks = new LongObjectHashMap<>();
        private GameContext gameContext;

        ChunkStore0(GameContext gameContext) {
            this.gameContext = gameContext;
        }

        @Override
        public Collection<Chunk> getChunks() {
            return chunks.values();
        }

        @NonNull
        @Override
        public Chunk getChunk(@NonNull BlockPos pos) {
            ChunkPos chunkPos = pos.toChunk();
            long cp = (long) chunkPos.getChunkX() << 32 | chunkPos.getChunkZ();
            Chunk chunk = this.chunks.get(cp);
            if (chunk != null)
                return chunk;
            LogicChunk c = new LogicChunk(gameContext);
            c.data = decorateChunk();
            this.chunks.put(cp, c);
            return c;
        }

        @Override
        public void touchChunk(@Nonnull BlockPos pos) {
            ChunkPos chunkPos = pos.toChunk();
            long cp = (long) chunkPos.getChunkX() << 32 | chunkPos.getChunkZ();
            Chunk chunk = this.chunks.get(cp);
            if (chunk != null) {
                LogicChunk c = new LogicChunk(gameContext);
                c.data = decorateChunk();
                this.chunks.put(cp, c);
            }
        }

        @Override
        public void discardChunk(@Nonnull BlockPos pos) {
            ChunkPos chunkPos = pos.toChunk();
            long cp = (long) chunkPos.getChunkX() << 32 | chunkPos.getChunkZ();
            Chunk remove = this.chunks.remove(cp);

            // save this chunk?
        }

        private int[][] decorateChunk() {
            int[][] data = new int[16][16 * 16 * 16];
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    int x = i;
                    int y = 0;
                    int z = j;
                    data[y / 16][x << 8 | y << 4 | z] = 1;
                }
            }
            return data;
        }
    }

    public static class MeshToGLNode implements Pipeline.Node {
        @Override
        public Object process(Pipeline.Context context, Object in) {
            if (in instanceof Mesh) {
                return GLMesh.of((Mesh) in);
            } else if (in instanceof List) {
                List<Mesh> meshes = (List<Mesh>) in;
                List<GLMesh> glMeshes = new ArrayList<>();
                for (Mesh mesh : meshes) {
                    glMeshes.add(GLMesh.of(mesh));
                }
                return glMeshes;
            } else {
                return new ArrayList<Mesh>();
            }
        }
    }

    public static class LogicChunk implements Chunk {

        int[][] data = new int[16][16 * 16 * 16];
        private GameContext context;
        private List<Entity> entities = new ArrayList<>();
        private Map<BlockPos, Block> blockObjects = Maps.newHashMap();
        private Map<String, Object> components;

        LogicChunk(GameContext context) {
            this.context = context;
        }

        @Override
        public Collection<Block> getRuntimeBlock() {
            return blockObjects.values();
        }

        @NonNull
        @Override
        public List<Entity> getEntities() {
            return entities;
        }

        @Override
        public Block getBlock(BlockPos pos) {
            Block object = blockObjects.get(pos);
            if (object != null) return object;
            if (pos.getY() < 0) {
                return context.getBlockRegistry().getValue(0);
            }

            int x = pos.getX() & 0xF;
            int y = pos.getY() & 0xF;
            int z = pos.getZ() & 0xF;

            int heightIndex = pos.getY() >> 4;
            int posIndex = (x << 8) | (y << 4) | z;
            int id = data[heightIndex][posIndex];
            return context.getBlockRegistry().getValue(id);
        }

        @Override
        public Block setBlock(BlockPos pos, Block destBlock) {
            if (pos.getY() < 0) {
                return context.getBlockRegistry().getValue(0);
            }
            int x = pos.getX() & 0xF;
            int y = pos.getY() & 0xF;
            int z = pos.getZ() & 0xF;


            Block prev = blockObjects.get(pos);
            if (prev != null) {
                blockObjects.remove(pos);
            }

            int id = 0;
            if (destBlock != null) {
                if (destBlock instanceof BlockRuntime) {
                    blockObjects.put(pos, destBlock);
                }
                id = context.getBlockRegistry().getId(destBlock);
            }

            if (prev == null) {
                prev = context.getBlockRegistry().getValue(data[pos.getY() >> 4][(x << 8) | (y << 4) | z]);
            }

            data[pos.getY() >> 4][(x << 8) | (y << 4) | z] = id;

            context.post(new BlockChange(pos, id));

            return prev;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getComponent(@Nonnull String name) {
            return (T) components.get(name);
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getComponent(@Nonnull Class<T> type) {
            return (T) components.get(type.getName());
        }

        @Nullable
        @Override
        public <T> T getBehavior(Class<T> type) {
            return null;
        }

        public static class BlockChange implements Event {
            public final BlockPos pos;
            public final int blockId;

            public BlockChange(BlockPos pos, int blockId) {
                this.pos = pos;
                this.blockId = blockId;
            }
        }
    }
}
