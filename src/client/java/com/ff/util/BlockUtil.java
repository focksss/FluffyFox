package com.ff.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.ff.FluffyFoxClient.MC;

public class BlockUtil {
    public static List<BlockPos> getBlocksInReach() {
        List<BlockPos> blocks = new ArrayList<BlockPos>();
        PlayerEntity player = MC.player;
        if (player == null) return blocks;
        World world = MC.world;
        if (world == null) return blocks;

        double reach = player.getAttributeValue(EntityAttributes.BLOCK_INTERACTION_RANGE);
        Vec3d eyePos = player.getEyePos();

        int minX = (int)Math.floor(eyePos.x - reach);
        int maxX = (int)Math.ceil(eyePos.x + reach);
        int minY = (int)Math.floor(eyePos.y - reach);
        int maxY = (int)Math.ceil(eyePos.y + reach);
        int minZ = (int)Math.floor(eyePos.z - reach);
        int maxZ = (int)Math.ceil(eyePos.z + reach);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    if (state == null) continue;
                    if (state.isAir()) continue;

                    Vec3d blockCenter = getTrueBlockCenter(pos);
                    double distance = eyePos.distanceTo(blockCenter);
                    if (distance > reach + (Math.sqrt(2) / 2) - 0.05) continue;

                    BlockHitResult mop = world.raycast(new RaycastContext(
                        eyePos,
                        blockCenter,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        player
                    ));

                    if (
                        mop != null &&
                            mop.getType() == HitResult.Type.BLOCK &&
                            mop.getBlockPos().equals(pos) &&
                            eyePos.distanceTo(mop.getPos()) <= reach
                    ) {
                        blocks.add(pos);
                    }
                }
            }
        }
        return blocks;
    }

    @Nullable
    public static BlockPos findNearestBlock(
            Block type,
            double radius,
            boolean raycastTest
    ) {
        PlayerEntity player = MC.player;
        World world = MC.world;

        if (player == null || world == null) return null;

        Vec3d eyePos = player.getEyePos();
        double radiusSq = radius * radius;

        BlockPos center = BlockPos.ofFloored(eyePos);
        int r = (int)Math.ceil(radius);

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        BlockPos closestPos = null;
        double closestDistSq = Double.MAX_VALUE;

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    mutable.set(center.getX() + x,
                        center.getY() + y,
                        center.getZ() + z);

                    Vec3d blockCenter = Vec3d.ofCenter(mutable);
                    double distSq = eyePos.squaredDistanceTo(blockCenter);

                    if (distSq > radiusSq) continue;
                    if (distSq >= closestDistSq) continue;

                    BlockState state = world.getBlockState(mutable);
                    if (state.isAir()) continue;
                    if (state.getBlock() != type) continue;

                    if (raycastTest) {
                        BlockHitResult hit = world.raycast(new RaycastContext(
                            eyePos,
                            blockCenter,
                            RaycastContext.ShapeType.OUTLINE,
                            RaycastContext.FluidHandling.NONE,
                            player
                        ));

                        if (hit.getType() != HitResult.Type.BLOCK ||
                                !hit.getBlockPos().equals(mutable)) {
                            continue;
                        }
                    }

                    closestDistSq = distSq;
                    closestPos = mutable.toImmutable();
                }
            }
        }

        return closestPos;
    }

    public static Vec3d getTrueBlockCenter(BlockPos pos) {
        if (MC.world == null) return null;
        BlockState state = MC.world.getBlockState(pos);
        if (state == null) return null;
        VoxelShape bb = state.getBlock().getDefaultState().getOutlineShape(MC.world, pos);
        if (pos != null) {
            double minX = bb.getMin(Direction.Axis.X);
            double minY = bb.getMin(Direction.Axis.Y);
            double minZ = bb.getMin(Direction.Axis.Z);
            double maxX = bb.getMax(Direction.Axis.X);
            double maxY = bb.getMax(Direction.Axis.Y);
            double maxZ = bb.getMax(Direction.Axis.Z);

            return new Vec3d(
                pos.getX() + minX + (maxX - minX) / 2.0,
                pos.getY() + minY + (maxY - minY) / 2.0,
                pos.getZ() + minZ + (maxZ - minZ) / 2.0
            );
        } else {
            return null;
        }
    }
}
