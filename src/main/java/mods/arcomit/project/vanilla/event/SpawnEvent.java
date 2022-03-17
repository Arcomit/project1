package mods.arcomit.project.vanilla.event;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import mods.arcomit.project.Project1;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.server.commands.LocateBiomeCommand;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

/**
 * @Author Arcomit
 * @Update 2022/03/17-Arcomit
 * 修改世界出生地的事件
 */
@Mod.EventBusSubscriber(modid = Project1.MODID)
public class SpawnEvent {

    //固定玩家在哪个生物群系出生
    @SubscribeEvent
    public static void worldLoad(WorldEvent.CreateSpawnPosition e) {
        if (e.getWorld() instanceof ServerLevel){
            e.setCanceled(true);
            ServerLevel world = (ServerLevel) e.getWorld();
            ServerLevelData worldData = e.getSettings();
            WorldGenSettings genSettings = world.getServer().getWorldData().worldGenSettings();

            ChunkGenerator chunkgenerator = world.getChunkSource().getGenerator();
            BlockPos pos = new BlockPos(0,0,0);

            //用于判断生物群系
            Predicate<Holder<Biome>> biomeResult = biomeHolder -> biomeHolder.is(Biomes.FOREST);

            Pair<BlockPos, Holder<Biome>> pair = world.findNearestBiome(biomeResult, pos, 99999, 8);
            ChunkPos chunkpos = world.getChunk(pair.getFirst()).getPos();
            int i = chunkgenerator.getSpawnHeight(world);
            if (i < world.getMinBuildHeight()) {
                BlockPos blockpos = chunkpos.getWorldPosition();
                i = world.getHeight(Heightmap.Types.WORLD_SURFACE, blockpos.getX() + 8, blockpos.getZ() + 8);
            }

            worldData.setSpawn(chunkpos.getWorldPosition().offset(8, i, 8), 0.0F);
            int k1 = 0;
            int j = 0;
            int k = 0;
            int l = -1;
            int i1 = 5;

            for(int j1 = 0; j1 < Mth.square(11); ++j1) {
                if (k1 >= -5 && k1 <= 5 && j >= -5 && j <= 5) {
                    BlockPos blockpos1 = PlayerRespawnLogic.getSpawnPosInChunk(world, new ChunkPos(chunkpos.x + k1, chunkpos.z + j));
                    if (blockpos1 != null) {
                        worldData.setSpawn(blockpos1, 0.0F);
                        break;
                    }
                }

                if (k1 == j || k1 < 0 && k1 == -j || k1 > 0 && k1 == 1 - j) {
                    int l1 = k;
                    k = -l;
                    l = l1;
                }

                k1 += k;
                j += l;
            }

            if (genSettings.generateBonusChest()) {
                ConfiguredFeature<?, ?> configuredfeature = MiscOverworldFeatures.BONUS_CHEST.value();
                configuredfeature.place(world, chunkgenerator, world.random, new BlockPos(worldData.getXSpawn(), worldData.getYSpawn(), worldData.getZSpawn()));
            }

        }
    }
}
