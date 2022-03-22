package mods.arcomit.project.event;

import mods.arcomit.project.animation.controller.entity.PlayerController;
import mods.arcomit.project.client.render.PlayerRender;
import mods.arcomit.project.IMixinEntityRenderDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Arcomit
 * @Update 2022/03/20-Arcomit
 * 用与修改玩家渲染类
 */
public class PlayerRenderEvent {
    public static IMixinEntityRenderDispatcher entityRenderDispatcher = (IMixinEntityRenderDispatcher) Minecraft.getInstance().getEntityRenderDispatcher();

    private static PlayerRender playerRender = new PlayerRender(entityRenderDispatcher.getContext());
    //用于储存玩家的动画控制器
    private static ConcurrentHashMap<UUID, PlayerController> playerControllerHashMap = new ConcurrentHashMap(20);


    @SubscribeEvent
    public static void renderPlayer(RenderPlayerEvent.Pre event){
        //取消玩家渲染
        event.setCanceled(true);

        if (event.getPlayer() instanceof AbstractClientPlayer){
            AbstractClientPlayer player = (AbstractClientPlayer) event.getPlayer();

            float yaw = Mth.lerp(event.getPartialTick(), player.yRotO, event.getPlayer().getYRot());
            //换成我们的玩家渲染
            playerRender.render(player,playerControllerHashMap.get(player.getUUID()),yaw,event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());

        }

    }

    @SubscribeEvent
    public static void playerJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof AbstractClientPlayer){
            AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();
            //玩家进入游戏时创建或更新HashMap中的动画控制器
            playerControllerHashMap.put(player.getUUID(),new PlayerController(player));
        }
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event){
        if (event.getEntity() instanceof AbstractClientPlayer && event.isWasDeath()){
            AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();
            //玩家重生时创建或更新HashMap中的动画控制器
            playerControllerHashMap.put(player.getUUID(),new PlayerController(player));
        }
    }

}
