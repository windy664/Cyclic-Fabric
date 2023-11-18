package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.conveyor.ConveyorItemEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class CyclicEntities {
    public static EntityType<ConveyorItemEntity> CONVEYOR_ITEM;

    public static void register() {
        CONVEYOR_ITEM = Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Cyclic.MOD_ID, "conveyor_item"),
                FabricEntityTypeBuilder.<ConveyorItemEntity>create(MobCategory.MISC, ConveyorItemEntity::new).build());
    }
}
