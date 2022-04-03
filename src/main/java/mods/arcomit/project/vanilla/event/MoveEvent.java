package mods.arcomit.project.vanilla.event;

import mods.arcomit.project.Project1;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @Author Arcomit
 * @Update 2022/04/03-Arcomit
 * 玩家疾跑时可跨越一格方块
 */
@Mod.EventBusSubscriber(modid = Project1.MODID)
public class MoveEvent {

    @SubscribeEvent
    public static void updatePlayerMaxUpStep(LivingEvent.LivingUpdateEvent event){
        if (event.getEntityLiving() instanceof Player){
            Player player = (Player) event.getEntityLiving();
            if (player.isSprinting()){//判断是否疾跑
                if(player.maxUpStep!=1.25F){//判断最大可跨越高度
                    player.maxUpStep = 1.25F;
                }
            }else {
                if(player.maxUpStep!=0.6F){
                    player.maxUpStep = 0.6F;
                }
            }
        }
    }
}
