package unknowndomain.engine.client.block;

import unknowndomain.engine.api.block.BlockBase;
import unknowndomain.engine.api.math.AxisAlignedBB;
import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.math.BoundingBox;
import unknowndomain.engine.api.world.World;
import unknowndomain.engine.client.block.model.BasicData;
import unknowndomain.engine.client.block.model.GameItem;
import unknowndomain.engine.client.block.model.Mesh;
import unknowndomain.engine.client.block.model.Texture;

public class Grass extends BlockBase {
    private final GameItem gameItem;
    private BoundingBox box;
    private World world;
    private BlockPos blockPos;
    private Texture texture;

    public Grass(World world, BlockPos blockPos) {
        box = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
        this.world = world;
        this.blockPos = blockPos;
        try {
//            setTexture(new Texture("textures/grassblock.png"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Mesh mesh = new Mesh(BasicData.INSTANCE.getPositions(), BasicData.INSTANCE.getTextCoords()
                , BasicData.INSTANCE.getIndices(), texture);
        gameItem = new GameItem(mesh);
        gameItem.setPosition(blockPos);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return box;
    }

    @Override
    public boolean onBlockPlaced(BlockPos pos) {
        return false;
    }

    @Override
    public boolean onBlockDestroyed(BlockPos pos, boolean harvested) {
        return false;
    }

    @Override
    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public World getWorld() {
        return world;
    }

    public GameItem getGameItem() {
        return this.gameItem;
    }
}
