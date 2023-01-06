package io.github.frostmourneee.woodcutter.core.event;

import io.github.frostmourneee.woodcutter.Woodcutter;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Woodcutter.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent
    public static void treeCapitate(BlockEvent.BreakEvent event) {
        if (event.getPlayer().level.getBlockState(event.getPos()).is(Tags.Blocks.GLASS)) {
            event.getPlayer().setHealth(10);
            System.out.println(event.getPlayer().getHealth());
        }
    }
}
