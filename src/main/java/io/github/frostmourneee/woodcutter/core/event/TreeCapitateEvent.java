package io.github.frostmourneee.woodcutter.core.event;

import io.github.frostmourneee.woodcutter.Woodcutter;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

import static io.github.frostmourneee.woodcutter.Woodcutter.*;

@Mod.EventBusSubscriber(modid = Woodcutter.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TreeCapitateEvent {

    @SubscribeEvent
    public static void treeCapitate(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockState blockState = event.getState();

        boolean validExternalConditions = !player.isShiftKeyDown() && blockState.is(BlockTags.LOGS_THAT_BURN);
        if (!validExternalConditions) return;

        ArrayList<BlockPos> logPos = new ArrayList<>();
        logPos.add(event.getPos());
        Block block = blockState.getBlock();
        LevelAccessor level = event.getLevel();

        //Looking for logs and mangrove roots in a tree
        int oldSize = 0;
        while (oldSize != logPos.size()) {
            ArrayList<BlockPos> oldPos = new ArrayList<>(logPos);

            for (BlockPos pos : oldPos.subList(oldSize, oldPos.size())) {
                if (level.getBlockState(pos).is(BlockTags.LOGS_THAT_BURN)) {
                    for (int num = 1; num <= 26; num++) {
                        BlockPos neighbourPos = getNeighbour3DWDiag(pos, num);
                        BlockState neighbourState = level.getBlockState(neighbourPos);
                        boolean sameLogOrMangroveRoot = neighbourState.is(block) ||
                                                        (blockState.is(Blocks.MANGROVE_LOG) && neighbourState.is(Blocks.MANGROVE_ROOTS));
                        if (sameLogOrMangroveRoot && !logPos.contains(neighbourPos)) logPos.add(neighbourPos);
                    }
                }

                if (level.getBlockState(pos).is(Blocks.MANGROVE_ROOTS)) {
                    for (int num = 1; num <= 6; num++) {
                        BlockPos neighbourPos = getNeighbour3DWODiag(pos, num);
                        BlockState neighbourState = level.getBlockState(neighbourPos);
                        if (neighbourState.is(Blocks.MANGROVE_ROOTS) && !logPos.contains(neighbourPos)) logPos.add(neighbourPos);
                    }
                }
            }

            oldSize = oldPos.size();
        }

        //Decide whether the tree is natural or artificial
        boolean someLogHasAtLeast9NaturalLeavesNearby = false;
        ArrayList<BlockPos> oldPos = new ArrayList<>(logPos);
        for (BlockPos pos : oldPos) {
            if (level.getBlockState(pos).is(Blocks.MANGROVE_ROOTS)) continue;
            int naturalLeaves = 0;

            for (int num = 1; num <= 26 && !someLogHasAtLeast9NaturalLeavesNearby; num++) {
                BlockPos neighbourPos = getNeighbour3DWDiag(pos, num);
                BlockState neighbourState = level.getBlockState(neighbourPos);
                if (neighbourState.is(BlockTags.LEAVES) && !neighbourState.getValue(LeavesBlock.PERSISTENT)) naturalLeaves++;
                if (naturalLeaves == 9) {
                    someLogHasAtLeast9NaturalLeavesNearby = true;
                    break;
                }
            }

            //if no natural leaves above then erase a log block
            boolean hasLeaveBlockAbove = false;
            for (int height = 1; height <= 40; height++) {
                BlockState aboveState = level.getBlockState(pos.above(height));
                if (aboveState.is(BlockTags.LEAVES) && !aboveState.getValue(LeavesBlock.PERSISTENT)) {
                    hasLeaveBlockAbove = true;
                    break;
                }
            }
            if (!hasLeaveBlockAbove) logPos.remove(pos);
        }
        if (!someLogHasAtLeast9NaturalLeavesNearby) return;

        //Removing all the blocks found and drawing tree's outline
        if (logPos.contains(event.getPos())) {
            for (BlockPos pos : logPos) {
                level.setBlock(pos.above(30), level.getBlockState(pos), 2);
                level.destroyBlock(pos, true);
            }
        }
    }
}
