package com.robertx22.mine_and_slash.uncommon.utilityclasses;

import com.robertx22.mine_and_slash.capability.world.WorldData;
import com.robertx22.mine_and_slash.maps.MapData;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class WorldUtils {

    public static ResourceLocation DUNGEON_DIM_ID = SlashRef.id("dungeon");

    public static void spawnEntity(Level world, Entity entity) {

        world.addFreshEntity(entity);

    }


    // todo rework this to point to dungeon mod instead
    public static boolean isMapWorldClass(Level world, BlockPos pos) {
        return world != null && ifMapData(world, pos).isPresent();

    }

    public static Optional<MapData> ifMapData(Level level, BlockPos pos) {
        return WorldData.DATA_GETTER.ifMapData(level, pos, true);
    }

    public static Optional<MapData> ifMapData(Level level, BlockPos pos, boolean grabConnectedData) {
        return WorldData.DATA_GETTER.ifMapData(level, pos, grabConnectedData);
    }

}
