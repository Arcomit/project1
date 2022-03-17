package mods.arcomit.project.vanilla.attributes.register;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @Author Arcomit
 * @Update 2022/03/17-Arcomit
 * 用于覆盖原版属性
 */
public class ChangeAttributesVanilla {
    //替换原版属性用的
    public static final DeferredRegister<Attribute> VANILLA_ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "minecraft");

    //护甲值
    public static final RegistryObject<Attribute> ARMOR = VANILLA_ATTRIBUTES.register("generic.armor", () -> new RangedAttribute("attribute.name.generic.armor", 0.08D, 0.0D, 99999.0D).setSyncable(true));

}
