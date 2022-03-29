package mods.arcomit.project.vanilla.event;

import mods.arcomit.project.animation.controller.entity.PlayerController;
import mods.arcomit.project.animation.render.PlayerRender;
import mods.arcomit.project.animation.render.IMixinEntityRenderDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author Arcomit
 * @Update 2022/03/25-Arcomit
 * 用与修改玩家渲染类
 */
public class PlayerRenderEvent {
    public static IMixinEntityRenderDispatcher entityRenderDispatcher = (IMixinEntityRenderDispatcher) Minecraft.getInstance().getEntityRenderDispatcher();

    public static PlayerRender playerRender = new PlayerRender(entityRenderDispatcher.getContext());

    public static PlayerRender firstPersonRender = new PlayerRender(entityRenderDispatcher.getContext(),true);

    //用于储存玩家的动画控制器
    public static ConcurrentHashMap<UUID, PlayerController> playerControllerHashMap = new ConcurrentHashMap(20);


    //修改玩家第三人称渲染
    @SubscribeEvent
    public static void playerRender(RenderPlayerEvent.Pre event){
        //取消玩家渲染
        if (!event.getPlayer().isSpectator()){
            event.setCanceled(true);

            if (event.getPlayer() instanceof AbstractClientPlayer){
                AbstractClientPlayer player = (AbstractClientPlayer) event.getPlayer();
                float yaw = Mth.lerp(event.getPartialTick(), player.yRotO, player.getYRot());
                //换成我们的玩家渲染
                playerRender.render(playerControllerHashMap.get(player.getUUID()),yaw,event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
            }
        }
    }

    //修改玩家第一人称渲染（只能是自己）
    @SubscribeEvent
    public static void renderFirstPerson(RenderHandEvent event){
        AbstractClientPlayer player = Minecraft.getInstance().player;
        //取消玩家渲染
        if (!player.isSpectator()){
            event.setCanceled(false);
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
