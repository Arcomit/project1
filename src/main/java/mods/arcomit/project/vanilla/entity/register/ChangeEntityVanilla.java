package mods.arcomit.project.vanilla.entity.register;

import mods.arcomit.project.vanilla.entity.ZombieChange;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @Author Arcomit
 * @Update 2022/03/17-Arcomit
 * 用于覆盖原版实体
 */
public class ChangeEntityVanilla {
    //替换原版实体用的
    public static final DeferredRegister<EntityType<?>> VANILLA_ENTITYS = DeferredRegister.create(ForgeRegistries.ENTITIES, "minecraft");

    //僵尸
    public static final RegistryObject<EntityType<ZombieChange>> Zombie =
            VANILLA_ENTITYS.register("zombie", () ->
                    EntityType.Builder.of(ZombieChange::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F).clientTrackingRange(8).build("zombie"));

}
