package mods.arcomit.project.vanilla.event;

import mods.arcomit.project.Project1;
import mods.arcomit.project.vanilla.item.SwordChangeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Arcomit
 * @Update 2022/03/16-Arcomit
 * 修改工具tip
 */
@Mod.EventBusSubscriber(modid = Project1.MODID)
public class ToolTipEvent {
    //修改工具Tip
    @SubscribeEvent
    public static void toolTip(ItemTooltipEvent event){
        if (event.getItemStack().getItem() instanceof SwordChangeItem){
            String tip;
            for(int x = event.getToolTip().size()-1;x >= 0;x--){
                tip = event.getToolTip().get(x).getString();

                if(judgmentToolTip(tip)){
                    //移除Tip
                    event.getToolTip().remove(x);
                }
            }

        }

    }

    //判断是否为攻击速度或距离的Tip
    public static Boolean judgmentToolTip(String string){


        if (string.contains(new TranslatableComponent(Attributes.ATTACK_SPEED.getDescriptionId()).getString())
                || string.contains(new TranslatableComponent(ForgeMod.REACH_DISTANCE.get().getDescriptionId()).getString())){
            return true;
        }
        return false;

    }
}
