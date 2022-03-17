package mods.arcomit.project.vanilla.item.register;

import mods.arcomit.project.vanilla.item.SwordChangeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @Author Arcomit
 * @Update 2022/03/15-Arcomit
 * 用于覆盖原版物品
 */
public class ChangeItemVanilla {
    //替换原版物品用的
    public static final DeferredRegister<Item> VANILLA_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "minecraft");

    //木剑
    public static final RegistryObject<Item> WOODEN_SWORD = VANILLA_ITEMS.register("wooden_sword", () -> new SwordChangeItem(Tiers.WOOD, 0, 96,-2, (new Item.Properties()).durability(240).tab(CreativeModeTab.TAB_COMBAT)));
    //金剑
    public static final RegistryObject<Item> GOLDEN_SWORD = VANILLA_ITEMS.register("golden_sword", () -> new SwordChangeItem(Tiers.GOLD, 1, 96,-1.5f, (new Item.Properties()).durability(240).tab(CreativeModeTab.TAB_COMBAT)));
    //石剑
    public static final RegistryObject<Item> STONE_SWORD = VANILLA_ITEMS.register("stone_sword", () -> new SwordChangeItem(Tiers.STONE, 1, 96,-1.5f, (new Item.Properties()).durability(350).tab(CreativeModeTab.TAB_COMBAT)));
    //铁剑
    public static final RegistryObject<Item> IRON_SWORD = VANILLA_ITEMS.register("iron_sword", () -> new SwordChangeItem(Tiers.IRON, 1, 96,-1.5f, (new Item.Properties()).durability(750).tab(CreativeModeTab.TAB_COMBAT)));
    //钻石剑
    public static final RegistryObject<Item> DIAMOND_SWORD = VANILLA_ITEMS.register("diamond_sword", () -> new SwordChangeItem(Tiers.DIAMOND, 2, 96,-1f, (new Item.Properties()).durability(2430).tab(CreativeModeTab.TAB_COMBAT)));
    //下届剑
    public static final RegistryObject<Item> NETHERITE_SWORD = VANILLA_ITEMS.register("netherite_sword", () -> new SwordChangeItem(Tiers.NETHERITE, 3, 96,0, (new Item.Properties()).durability(2710).tab(CreativeModeTab.TAB_COMBAT)));
}
