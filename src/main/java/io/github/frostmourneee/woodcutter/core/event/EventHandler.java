package io.github.frostmourneee.woodcutter.core.event;

import io.github.frostmourneee.woodcutter.Woodcutter;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

import static io.github.frostmourneee.woodcutter.Woodcutter.getNeighbour3D;

@Mod.EventBusSubscriber(modid = Woodcutter.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent
    public static void treeCapitate(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockState blockState = event.getState();

        boolean flag1 = !player.isCreative() && blockState.is(BlockTags.LOGS) &&
                        player.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof AxeItem;
        if (!flag1) return;

        ArrayList<BlockPos> logPos = new ArrayList<>();
        logPos.add(event.getPos());
        Block block = blockState.getBlock();
        LevelAccessor level = event.getLevel();

        //Looking for all logs in a tree
        int oldSize = 0;
        while (oldSize != logPos.size()) {
            ArrayList<BlockPos> oldPos = new ArrayList<>(logPos);

            for (BlockPos pos : oldPos.subList(oldSize, oldPos.size())) {
                for (int num = 1; num <= 26; num++) {
                    BlockPos neighbourPos = getNeighbour3D(pos, num);
                    if (level.getBlockState(neighbourPos).is(block) && !logPos.contains(neighbourPos)) logPos.add(neighbourPos);
                }
            }

            oldSize = oldPos.size();
        }

        //Decide whether the tree is natural or artificial
        BlockPos rootOrUpperPos = null;
        for (BlockPos pos : logPos) {
            int naturalLeaves = 0;

            for (int num = 1; num <= 26; num++) {
                BlockPos neighbourPos = getNeighbour3D(pos, num);
                BlockState neighbourState = level.getBlockState(neighbourPos);
                if (neighbourState.is(BlockTags.LEAVES) && !neighbourState.getValue(LeavesBlock.PERSISTENT)) naturalLeaves++;
                if (naturalLeaves == 9) {
                    rootOrUpperPos = pos;
                    break;
                }
            }
        }
        if (rootOrUpperPos == null) return;

        //Removing all the blocks found
        for (BlockPos pos : logPos) level.destroyBlock(pos, true);
        player.getItemBySlot(EquipmentSlot.MAINHAND).hurt(logPos.size(), RandomSource.create(), null);
    }
}
