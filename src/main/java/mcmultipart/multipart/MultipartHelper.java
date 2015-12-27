package mcmultipart.multipart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mcmultipart.MCMultiPartMod;
import mcmultipart.block.TileMultipart;
import mcmultipart.microblock.IMicroblockTile;
import mcmultipart.microblock.MicroblockContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MultipartHelper {

    public static boolean canAddPart(World world, BlockPos pos, IMultipart part) {

        IMultipartContainer container = getPartContainer(world, pos);
        if (container == null) {
            List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
            part.addCollisionBoxes(new AxisAlignedBB(0, 0, 0, 1, 1, 1), list, null);
            for (AxisAlignedBB bb : list)
                if (!world.checkNoEntityCollision(bb.offset(pos.getX(), pos.getY(), pos.getZ()))) return false;

            return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
        }
        return container.canAddPart(part);
    }

    public static void addPart(World world, BlockPos pos, IMultipart part) {

        addPart(world, pos, part, null);
    }

    public static void addPart(World world, BlockPos pos, IMultipart part, UUID id) {

        IMultipartContainer container = getPartContainer(world, pos);
        if (container == null) {
            world.setBlockState(pos, MCMultiPartMod.multipart.getDefaultState());
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileMultipart) container = (IMultipartContainer) te;
            if (container == null) world.setTileEntity(pos, (TileEntity) (container = new TileMultipart()));
        }
        if (container.getPartFromID(id) != null) return;
        part.setContainer(container);
        if (id != null) container.addPart(id, part);
        else container.addPart(part);
    }

    public static boolean addPartIfPossible(World world, BlockPos pos, IMultipart part) {

        if (!canAddPart(world, pos, part)) return false;
        addPart(world, pos, part);
        return true;
    }

    public static IMultipartContainer getPartContainer(IBlockAccess world, BlockPos pos) {

        TileEntity te = world.getTileEntity(pos);
        if (te == null) return null;
        if (te instanceof IMultipartContainer) return (IMultipartContainer) te;
        if (te instanceof IMicroblockTile) return ((IMicroblockTile) te).getMicroblockContainer();
        return null;
    }

    public static MicroblockContainer getMicroblockContainer(IBlockAccess world, BlockPos pos) {

        TileEntity te = world.getTileEntity(pos);
        if (te == null) return null;
        if (te instanceof IMicroblockTile) return ((IMicroblockTile) te).getMicroblockContainer();
        return null;
    }

}
