package mods.arcomit.project.animation.event;

import mods.arcomit.project.Project1;
import mods.arcomit.project.animation.render.PlayerRender;
import mods.arcomit.project.IMixinEntityRenderDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @Author Arcomit
 * @Update 2022/03/19-Arcomit
 * 用与修改LivingEntity类
 */
public class PlayerRenderEvent {
    public static IMixinEntityRenderDispatcher entityRenderDispatcher = (IMixinEntityRenderDispatcher) Minecraft.getInstance().getEntityRenderDispatcher();

    private static PlayerRender playerRender = new PlayerRender(entityRenderDispatcher.getContext());

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderPlayer(RenderPlayerEvent.Pre event){
        event.setCanceled(true);

        if (event.getPlayer() instanceof AbstractClientPlayer){
            AbstractClientPlayer player = (AbstractClientPlayer) event.getPlayer();

            float yaw = Mth.lerp(event.getPartialTick(), player.yRotO, event.getPlayer().getYRot());

            playerRender.render(player,yaw,event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
        }

    }
}
