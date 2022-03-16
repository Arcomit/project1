package mods.arcomit.project.vanilla.event;

import mods.arcomit.project.Project1;
import mods.arcomit.project.vanilla.item.SwordChangeItem;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @Author Arcomit
 * @Update 2022/03/16-Arcomit
 */
@Mod.EventBusSubscriber(modid = Project1.MODID)
public class ToolTipEvent {
    //修改工具Tip
    @SubscribeEvent
    public static void toolTip(ItemTooltipEvent event){
        if (event.getItemStack().getItem() instanceof SwordChangeItem){
            for(int x = 0;x < event.getToolTip().size();x++){
                if(event.getToolTip().get(x).getString().contains(new TranslatableComponent(Attributes.ATTACK_SPEED.getDescriptionId()).getString())){
                    //移除原版剑类的攻击速度显示Tip
                    event.getToolTip().remove(x);
                    return;
                }
            }
        }
    }
}
