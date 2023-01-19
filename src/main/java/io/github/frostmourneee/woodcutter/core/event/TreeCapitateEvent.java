package io.github.frostmourneee.woodcutter.core.event;

import io.github.frostmourneee.woodcutter.Woodcutter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;

import static io.github.frostmourneee.woodcutter.Woodcutter.*;

@Mod.EventBusSubscriber(modid = Woodcutter.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TreeCapitateEvent {

    @SubscribeEvent
    public static void removeTreeOutline(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos initPos = event.getPos();
        BlockState blockState = level.getBlockState(initPos);

        if (!level.getBlockState(initPos).is(BlockTags.LOGS_THAT_BURN) || !event.getItemStack().is(Items.AIR)) return;

        ArrayList<BlockPos> logPos = new ArrayList<>();
        logPos.add(initPos);
        Block block = blockState.getBlock();

        //Looking for logs in a tree
        int oldSize = 0;
        while (oldSize != logPos.size()) {
            ArrayList<BlockPos> oldPos = new ArrayList<>(logPos);

            for (BlockPos pos : oldPos.subList(oldSize, oldPos.size())) {
                if (event.getLevel().getBlockState(pos).is(BlockTags.LOGS_THAT_BURN)) {
                    for (int num = 1; num <= 26; num++) {
                        BlockPos neighbourPos = getNeighbour3DWDiag(pos, num);
                        if (event.getLevel().getBlockState(neighbourPos).is(block) && !logPos.contains(neighbourPos)) logPos.add(neighbourPos);
                    }
                }
            }

            oldSize = oldPos.size();
        }

        //Removing all the blocks found and drawing tree's outline
        for (BlockPos pos : logPos) level.destroyBlock(pos, false);
    }

    @SubscribeEvent
    public static void treeCapitate(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockState blockState = event.getState();

        boolean validExternalConditions = !player.isShiftKeyDown() && blockState.is(BlockTags.LOGS_THAT_BURN);
        if (!validExternalConditions) return;

        ResourceLocation resourceLocation = ForgeRegistries.BLOCKS.getKey(blockState.getBlock());
        if (resourceLocation == null) return;
        switch (resourceLocation.getPath()) {
            case "acacia_log" -> cutAcaciaTree(event.getLevel(), blockState, event.getPos());
            case "birch_log" -> cutBirchTree(event.getLevel(), blockState, event.getPos());
            case "dark_oak_log" -> cutDarkOakTree(event.getLevel(), blockState, event.getPos());
            case "jungle_log" -> cutJungleTree(event.getLevel(), blockState, event.getPos());
            case "mangrove_log" -> cutMangroveTree(event.getLevel(), blockState, event.getPos());
            case "oak_log" -> cutOakTree(event.getLevel(), blockState, event.getPos());
            case "spruce_log" -> cutSpruceTree(event.getLevel(), blockState, event.getPos());
            default -> cutSomeModTree(event.getLevel(), blockState, event.getPos());
        }
    }

    private static void cutAcaciaTree(LevelAccessor level, BlockState blockState, BlockPos initLogPos) {
    }

    private static void cutBirchTree(LevelAccessor level, BlockState blockState, BlockPos initLogPos) {
    }

    private static void cutDarkOakTree(LevelAccessor level, BlockState blockState, BlockPos initLogPos) {
    }

    private static void cutJungleTree(LevelAccessor level, BlockState blockState, BlockPos initLogPos) {
    }

    private static void cutMangroveTree(LevelAccessor level, BlockState blockState, BlockPos initLogPos) {
        /*ArrayList<BlockPos> logPos = new ArrayList<>();
        logPos.add(initLogPos);
        Block block = blockState.getBlock();

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
        if (logPos.contains(initLogPos)) {
            for (BlockPos pos : logPos) {
                level.setBlock(pos.above(30), level.getBlockState(pos), 2);
                level.destroyBlock(pos, true);
            }
        }*/
    }

    private static void cutOakTree(LevelAccessor level, BlockState blockState, BlockPos initLogPos) {
        ArrayList<BlockPos> logPos = new ArrayList<>();
        logPos.add(initLogPos);
        Block block = blockState.getBlock();

        //Looking for logs in a "tree", may be got several trees
        int logsBeforeAdding = 0;
        while (logsBeforeAdding != logPos.size()) {
            ArrayList<BlockPos> oldPos = new ArrayList<>(logPos);

            for (BlockPos pos : oldPos.subList(logsBeforeAdding, oldPos.size())) {
                for (int num = 1; num <= 26; num++) {
                    BlockPos neighbourPos = getNeighbour3DWDiag(pos, num);
                    if (level.getBlockState(neighbourPos).is(block) && !logPos.contains(neighbourPos)) logPos.add(neighbourPos);
                }
            }

            logsBeforeAdding = oldPos.size();
        }

        logPos.sort(Comparator.comparingInt(Vec3i::getY));
        if (logPos.get(logPos.size()-1).getY() - logPos.get(0).getY() + 1 < 4) return; //Too small tree

        //Is common small oak?
        boolean isSmallOak = true;
        boolean hasRotatedLogBlock = false;
        for (BlockPos pos : logPos) {
            if (!hasRotatedLogBlock && level.getBlockState(pos).getValue(RotatedPillarBlock.AXIS) != Direction.Axis.Y) hasRotatedLogBlock = true;
            if (pos.getX() != initLogPos.getX() || pos.getZ() != initLogPos.getZ()) {
                isSmallOak = false;
                break; //Grabbed several tree trunks (trees) or one trunk has branches
            }
        }

        if (isSmallOak) {
            //Natural tree or artificial
            if (!hasAtLeast9NaturalLeavesAround(level, logPos.get(logPos.size()-1)) &&
                    !hasAtLeast9NaturalLeavesAround(level, logPos.get(logPos.size()-2))) return;
            if (hasRotatedLogBlock) return;

            //Remove tree and reduce axe's durability
            for (BlockPos pos : logPos) {
                level.setBlock(pos.above(30), level.getBlockState(pos), 2);
                level.destroyBlock(pos, true);
            }

            return;
        }

        //Fancy oak or strange structure
        //Looking for trunks grabbed
        ArrayList<BlockPos> trunkRootPos = new ArrayList<>();
        ArrayList<Integer> trunkHeight = new ArrayList<>();
        for (BlockPos pos : logPos) {
            if (level.getBlockState(pos.below()).is(block) ||
                    level.getBlockState(pos).getValue(RotatedPillarBlock.AXIS) != Direction.Axis.Y) continue;

            int height = trunkHeight(level, pos);
            if (height >= 4) {
                trunkRootPos.add(pos);
                trunkHeight.add(height);
            }
        }

        //Looking for several nearby trunks which is artificial
        for (int root1 = 0; root1 < trunkRootPos.size() - 1; root1++) {
            for (int root2 = root1 + 1; root2 < trunkRootPos.size(); root2++) {
                BlockPos rootPos1 = trunkRootPos.get(root1);
                BlockPos rootPos2 = trunkRootPos.get(root2);
                if (Math.abs(rootPos1.getX() - rootPos2.getX()) <= 1 && Math.abs(rootPos1.getZ() - rootPos2.getZ()) <= 1) return; //Artificial
            }
        }

        ArrayList<BlockPos> branchBlockPos = new ArrayList<>(logPos);
        for (int trunk = 0; trunk < trunkRootPos.size(); trunk++) {
            for (int height = 0; height < trunkHeight.get(trunk); height++) {
                branchBlockPos.remove(trunkRootPos.get(trunk).above(height));
            }
        }
        for (BlockPos pos : branchBlockPos) if (level.getBlockState(pos).getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y) return; //Fake branch

        //One tree trunk grabbed, easy case
        if (trunkRootPos.size() == 1) oakLeavesCheckAndTreeDestroy(level, trunkRootPos.get(0), trunkHeight.get(0), branchBlockPos, logPos);
        else {
            //Looking for needed trunk
            //Whether initLogPos is a part of a trunk or not
            BlockPos realRoot = null;
            int realHeight = 0;
            for (int trunk = 0; trunk < trunkRootPos.size(); trunk++) {
                if (initLogPos.getX() == trunkRootPos.get(trunk).getX() &&
                    (initLogPos.getY() >= trunkRootPos.get(trunk).getY() && initLogPos.getY() < trunkRootPos.get(trunk).getY() + trunkHeight.get(trunk)) &&
                    initLogPos.getZ() == trunkRootPos.get(trunk).getZ()) {
                    realRoot = trunkRootPos.get(trunk);
                    realHeight = trunkHeight.get(trunk);
                    trunkRootPos.remove(trunk);
                    trunkHeight.remove(trunk);
                    break;
                }
            }

            //Real root wasn't found
            if (realRoot == null) {
                realRoot = oakFindRealRootAndChangeTrunkRootPos(level, initLogPos, trunkRootPos, trunkHeight, logPos.size());
                realHeight = trunkHeight(level, realRoot);
            }

            ArrayList<BlockPos> logsToBeRemoved = new ArrayList<>();
            oakSeparateRealTree(level, realRoot, realHeight, logsToBeRemoved, trunkRootPos, trunkHeight);

            branchBlockPos = new ArrayList<>(logsToBeRemoved);
            branchBlockPos.removeIf(pos -> level.getBlockState(pos).getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y);

            oakLeavesCheckAndTreeDestroy(level, realRoot, realHeight, branchBlockPos, logsToBeRemoved);
        } //Several tree trunks
    }
    private static void oakLeavesCheckAndTreeDestroy(LevelAccessor level, BlockPos realRoot, int realHeight, ArrayList<BlockPos> branchBlockPos, ArrayList<BlockPos> tree) {
        //Deciding whether the tree is natural or artificial
        if (!hasAtLeast9NaturalLeavesAround(level, realRoot.above(realHeight - 1)) &&
                !hasAtLeast9NaturalLeavesAround(level, realRoot.above(realHeight - 2))) return; //Artificial

        int leavesBlockAbove = 0;
        for (BlockPos pos : branchBlockPos) {
            //Decide whether there is leaves block above or not
            for (int height = 1; height <= 10; height++) {
                BlockState aboveState = level.getBlockState(pos.above(height));
                if (aboveState.is(Blocks.OAK_LEAVES) && !aboveState.getValue(LeavesBlock.PERSISTENT)) {
                    leavesBlockAbove++;
                    break;
                }
            }
        }

        if (branchBlockPos.size() - leavesBlockAbove > 2) return; //Artificial

        //Removing all the blocks found and drawing tree's outline
        for (BlockPos pos : tree) {
            level.setBlock(pos.above(30), level.getBlockState(pos), 2);
            level.destroyBlock(pos, true);
        }
    }
    private static void oakSeparateRealTree(LevelAccessor level, BlockPos realRoot, int realHeight, ArrayList<BlockPos> tree, ArrayList<BlockPos> trunkRootPos, ArrayList<Integer> trunkHeight) {
        Block block = level.getBlockState(realRoot).getBlock();
        tree.add(realRoot);
        int logsBeforeAdding = 0;
        while (logsBeforeAdding != tree.size()) {
            ArrayList<BlockPos> oldPos = new ArrayList<>(tree);
            for (BlockPos pos : oldPos.subList(logsBeforeAdding, oldPos.size())) {
                for (int num = 1; num <= 26; num++) {
                    BlockPos neighbourPos = getNeighbour3DWDiag(pos, num);
                    if (pos.equals(realRoot.above(realHeight - 1)) && neighbourPos.getY() > pos.getY()) continue;
                    if (level.getBlockState(neighbourPos).is(block) && !tree.contains(neighbourPos)) {
                        boolean isNeighbourAnotherTreeLog = false;
                        for (int trunk = 0; trunk < trunkRootPos.size(); trunk++) {
                            if (Math.abs(neighbourPos.getX()-trunkRootPos.get(trunk).getX()) <= 1 &&
                                (neighbourPos.getY() >= trunkRootPos.get(trunk).getY() && neighbourPos.getY() < trunkRootPos.get(trunk).getY() + trunkHeight.get(trunk)) &&
                                Math.abs(neighbourPos.getZ()-trunkRootPos.get(trunk).getZ()) <= 1) {
                                isNeighbourAnotherTreeLog = true;
                                break;
                            }
                        }
                        if (isNeighbourAnotherTreeLog) continue;

                        tree.add(neighbourPos);
                    }
                }
            }

            logsBeforeAdding = oldPos.size();
        }
    }
    private static BlockPos oakFindRealRootAndChangeTrunkRootPos(LevelAccessor level, BlockPos initLogPos, ArrayList<BlockPos> trunkRootPos, ArrayList<Integer> trunkHeight, int treeSize) {
        Block block = level.getBlockState(initLogPos).getBlock();
        ArrayList<BlockPos> neighboursLog = new ArrayList<>();
        neighboursLog.add(initLogPos);

        int logsBeforeAdding = 0;
        for (int i = 0; i < treeSize; i++) {
            ArrayList<BlockPos> oldPos = new ArrayList<>(neighboursLog);

            for (BlockPos pos : oldPos.subList(logsBeforeAdding, oldPos.size())) {
                for (int num = 1; num <= 26; num++) {
                    BlockPos neighbourPos = getNeighbour3DWDiag(pos, num);
                    if (!level.getBlockState(neighbourPos).is(block)) continue;

                    for (int trunk = 0; trunk < trunkRootPos.size(); trunk++) {
                        if (Math.abs(neighbourPos.getX()-trunkRootPos.get(trunk).getX()) <= 1 &&
                            neighbourPos.getY() >= trunkRootPos.get(trunk).getY() && neighbourPos.getY() < (trunkRootPos.get(trunk).getY() + trunkHeight.get(trunk)) &&
                            Math.abs(neighbourPos.getZ()-trunkRootPos.get(trunk).getZ()) <= 1 &&
                            pos.getY() >= trunkRootPos.get(trunk).getY() && pos.getY() < (trunkRootPos.get(trunk).getY() + trunkHeight.get(trunk))) {
                            BlockPos root = trunkRootPos.get(trunk);
                            trunkRootPos.remove(trunk);
                            trunkHeight.remove(trunk);
                            return root;
                        }
                    }

                    if (!neighboursLog.contains(neighbourPos)) neighboursLog.add(neighbourPos);
                }
            }

            logsBeforeAdding = oldPos.size();
        }

        throw new RuntimeException("findRealRoot() didn't find real root");
    }

    private static void cutSpruceTree(LevelAccessor level, BlockState blockState, BlockPos initLogPos) {
    }

    private static void cutSomeModTree(LevelAccessor level, BlockState blockState, BlockPos initLogPos) {
        ArrayList<BlockPos> logPos = new ArrayList<>();
        logPos.add(initLogPos);
        Block block = blockState.getBlock();

        //Looking for logs in a tree
        int oldSize = 0;
        while (oldSize != logPos.size()) {
            ArrayList<BlockPos> oldPos = new ArrayList<>(logPos);

            for (BlockPos pos : oldPos.subList(oldSize, oldPos.size())) {
                if (level.getBlockState(pos).is(BlockTags.LOGS_THAT_BURN)) {
                    for (int num = 1; num <= 26; num++) {
                        BlockPos neighbourPos = getNeighbour3DWDiag(pos, num);
                        if (level.getBlockState(neighbourPos).is(block) && !logPos.contains(neighbourPos)) logPos.add(neighbourPos);
                    }
                }
            }

            oldSize = oldPos.size();
        }

        //Removing artificial branches and deciding whether the tree is natural or artificial
        boolean someLogHasAtLeast9NaturalLeavesNearby = false;

        ArrayList<BlockPos> logPosBeforeRemoval = new ArrayList<>(logPos);
        for (BlockPos pos : logPosBeforeRemoval) {
            //Decide whether there is leaves block above or not
            boolean hasLeavesBlockAbove = false;
            for (int height = 1; height <= 40; height++) {
                BlockState aboveState = level.getBlockState(pos.above(height));
                if (aboveState.is(BlockTags.LEAVES) && !aboveState.getValue(LeavesBlock.PERSISTENT)) {
                    hasLeavesBlockAbove = true;
                    break;
                }
            }
            if (!hasLeavesBlockAbove) {
                logPos.remove(pos);
                continue;
            }

            //Looking for at least 9 natural leaves blocks around
            if (someLogHasAtLeast9NaturalLeavesNearby) continue;

            int naturalLeaves = 0;
            for (int num = 1; num <= 26; num++) {
                BlockPos neighbourPos = getNeighbour3DWDiag(pos, num);
                BlockState neighbourState = level.getBlockState(neighbourPos);
                if (neighbourState.is(BlockTags.LEAVES) && !neighbourState.getValue(LeavesBlock.PERSISTENT)) naturalLeaves++;
                if (naturalLeaves == 9) {
                    someLogHasAtLeast9NaturalLeavesNearby = true;
                    break;
                }
            }
        }
        if (!someLogHasAtLeast9NaturalLeavesNearby) return;

        //Removing all the blocks found and drawing tree's outline
        if (logPos.contains(initLogPos)) {
            for (BlockPos pos : logPos) {
                level.setBlock(pos.above(30), level.getBlockState(pos), 2);
                level.destroyBlock(pos, true);
            }
        }
    }
}
