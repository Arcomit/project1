package mods.arcomit.project.vanilla.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import mods.arcomit.project.Project1;
import mods.arcomit.project.vanilla.item.SwordChangeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

/**
 * @Author Arcomit
 * @Update 2022/03/16-Arcomit
 * 用于战斗系统方面的事件
 */
@Mod.EventBusSubscriber(modid = Project1.MODID)
public class Combat {
    protected static final UUID BASE_ATTACK_REACH_UUID = UUID.fromString("9E973CDE-CDDD-4D07-9050-F29D432D4A62");
    protected static final UUID BASE_ATTACK_UUID = UUID.fromString("805C7528-03CD-4645-AC61-98485987871F");

    //原版剑类攻击生物时,生物的无敌帧只有1Tick
    @SubscribeEvent
    public static void damageEntity(LivingDamageEvent event){
        if (event.getSource().getEntity() instanceof Player){
            Player player = (Player) event.getSource().getEntity();
            if (player.getMainHandItem().getItem() instanceof SwordChangeItem){
                event.getEntityLiving().invulnerableTime = 1;
            }
        }
        /**需要写配置文件用于设置是否开启无敌帧(默认不开启)*/
    }

    //穿草攻击
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level world = event.getWorld();
        if (world.isClientSide){
            BlockPos pos = event.getPos();
            BlockState clickBlock = event.getWorld().getBlockState(pos);
            //判断方块是否可以穿过
            if (clickBlock.getCollisionShape(world,pos).isEmpty()){
                Player player = event.getPlayer();
                //取距离
                double reach = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
                reach = player.isCreative() ? reach : reach - 0.5F;
                Vec3 startVec = player.getEyePosition(1.0F);
                Vec3 viewVector = player.getViewVector(1.0F);
                Vec3 endVec = startVec.add(viewVector.x * reach, viewVector.y * reach, viewVector.z * reach);
                AABB aabb = player.getBoundingBox().expandTowards(viewVector.scale(reach)).inflate(1.0D, 1.0D, 1.0D);
                EntityHitResult result = ProjectileUtil.getEntityHitResult(player, startVec, endVec, aabb, entity -> !entity.isSpectator() && entity.isAttackable(), reach * reach);
                if (result != null) {
                    event.setCanceled(true);
                    Minecraft.getInstance().gameMode.attack(player, result.getEntity());
                }
            }
        }
    }

    //修改攻击与选择距离
    @SubscribeEvent
    public static void changeReachDistance(ItemAttributeModifierEvent event){
        if (event.getItemStack() != null){
            if (event.getItemStack().getItem() instanceof SwordChangeItem){
                SwordChangeItem item = (SwordChangeItem) event.getItemStack().getItem();
                if (event.getSlotType().equals(EquipmentSlot.MAINHAND)){
                    event.addModifier(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(BASE_ATTACK_REACH_UUID, "Weapon modifier", item.getReach(), AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }

}
