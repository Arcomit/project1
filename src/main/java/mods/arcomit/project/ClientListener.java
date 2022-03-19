package mods.arcomit.project;

import mods.arcomit.project.animation.event.PlayerRenderEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * @Author Arcomit
 * @Update 2022/03/20-Arcomit
 * 注册客户端事件
 */
@Mod.EventBusSubscriber(modid = Project1.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientListener {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerRenderers(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(PlayerRenderEvent.class);
    }
}
