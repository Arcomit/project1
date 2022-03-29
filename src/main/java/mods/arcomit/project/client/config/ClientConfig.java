package mods.arcomit.project.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ClientConfig {
    public static ForgeConfigSpec CLIENT_CONFIG;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> armorModel;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER.comment("客户端配置").push("client");
        armorModel = CLIENT_BUILDER.comment("设置盔甲对应的模型贴图,格式:[物品名字=资源目录]").defineList("armorModel", Arrays.asList(
                "minecraft:diamond_leggings=project1:armors/minecraft/diamond",
                "minecraft:diamond_helmet=project1:armors/minecraft/diamond"
        ), (s) -> {
            return s instanceof String;
        });
        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
