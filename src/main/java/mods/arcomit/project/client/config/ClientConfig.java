package mods.arcomit.project.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ClientConfig {
    /**
    public static ForgeConfigSpec CLIENT_CONFIG;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> armorModel;
     */

    static {
        /**
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER.comment("客户端配置").push("client");
        armorModel = CLIENT_BUILDER.comment("需要自定义模型贴图的盔甲").defineList("armorCustom", Arrays.asList(
                "minecraft:diamond_leggings",
                "minecraft:diamond_helmet"
        ), (s) -> {
            return s instanceof String;
        });
        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
        弃用,使用了其他方法 */
    }
}
