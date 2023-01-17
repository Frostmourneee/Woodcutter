package io.github.frostmourneee.woodcutter;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;

@Mod(Woodcutter.MODID)
public class Woodcutter {

    public static final String MODID = "woodcutter";

    public static BlockPos getNeighbour3DWDiag(BlockPos pos, int num) {
        return switch (num) {
            case 1 -> pos.below().north().east();
            case 2 -> pos.below().east();
            case 3 -> pos.below().south().east();
            case 4 -> pos.below().south();
            case 5 -> pos.below().south().west();
            case 6 -> pos.below().west();
            case 7 -> pos.below().north().west();
            case 8 -> pos.below().north();
            case 9 -> pos.north().east();
            case 10 -> pos.east();
            case 11 -> pos.south().east();
            case 12 -> pos.south();
            case 13 -> pos.south().west();
            case 14 -> pos.west();
            case 15 -> pos.north().west();
            case 16 -> pos.north();
            case 17 -> pos.above().north().east();
            case 18 -> pos.above().east();
            case 19 -> pos.above().south().east();
            case 20 -> pos.above().south();
            case 21 -> pos.above().south().west();
            case 22 -> pos.above().west();
            case 23 -> pos.above().north().west();
            case 24 -> pos.above().north();
            case 25 -> pos.below();
            case 26 -> pos.above();
            default -> throw new RuntimeException("getNeighbour3DWDiag goes to default");
        };
    }

    public static BlockPos getNeighbour3DWODiag(BlockPos pos, int num) {
        return switch (num) {
            case 1 -> pos.above();
            case 2 -> pos.north();
            case 3 -> pos.east();
            case 4 -> pos.south();
            case 5 -> pos.west();
            case 6 -> pos.below();
            default -> throw new RuntimeException("getNeighbour3DWODiag goes to default");
        };
    }

    public static boolean hasAtLeast9NaturalLeavesAround (LevelAccessor level, BlockPos pos) {
        int count = 0;
        for (int num = 1; num <= 26; num++) {
            BlockPos neighbourPos = getNeighbour3DWDiag(pos, num);
            BlockState neighbourState = level.getBlockState(neighbourPos);
            if (neighbourState.is(BlockTags.LEAVES) && !neighbourState.getValue(LeavesBlock.PERSISTENT)) count++;
            if (count == 9) return true;
        }

        return false;
    }

    public static void customPrint(Object... str) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Object s : str) {
            stringBuilder.append(s);
            stringBuilder.append(" ");
        }
        System.out.println(stringBuilder);
    }
}


