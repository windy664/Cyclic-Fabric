package net.knsh.cyclic.util;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.fluid.*;
import net.knsh.flib.capabilities.FluidAction;
import net.knsh.flib.capabilities.FluidTankBase;
import net.knsh.flib.data.Model3D;
import net.knsh.flib.render.FluidRenderMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FluidHelpers {
    private static final int COLOUR_DEFAULT = 0xADD8E6; // if some random mod adds a fluid with no colour
    private static final int COLOUR_MILK = 0xF1F1F1; // mojang/forge didnt give any
    private static final int COLOUR_LAVA = 0xff8c00; // mojang/forge didnt give lava any colour value
    public static final FluidRenderMap<Int2ObjectMap<Model3D>> CACHED_FLUIDS = new FluidRenderMap<>();
    public static final int STAGES = 1400;

    public static int getColorFromFluid(FluidStack fstack) {
        if (fstack != null && fstack.getFluid() != null) {
            //first check mine
            if (fstack.getFluid() == FluidBiomassHolder.STILL) {
                return FluidBiomassHolder.COLOR;
            }
            else if (fstack.getFluid() == FluidHoneyHolder.STILL) {
                return FluidHoneyHolder.COLOR;
            }
            else if (fstack.getFluid() == FluidMagmaHolder.STILL) {
                return FluidMagmaHolder.COLOR;
            }
            else if (fstack.getFluid() == FluidSlimeHolder.STILL) {
                return FluidSlimeHolder.COLOR;
            }
            else if (fstack.getFluid() == FluidXpJuiceHolder.STILL) {
                return FluidXpJuiceHolder.COLOR;
            } //now check if the fluid has a color
            //      else if (fstack.getFluid().getAttributes().getColor() > 0) {
            //        return fstack.getFluid().getAttributes().getColor();
            //      }
           // else if (fstack.getFluid() == ForgeMod.MILK.get()) {
            //    return COLOUR_MILK;
            //}
            else if (fstack.getFluid() == Fluids.LAVA) {
                return COLOUR_LAVA;
            }
        }
        return COLOUR_DEFAULT;
    }

    public static void extractSourceWaterloggedCauldron(Level level, BlockPos posTarget, FluidTankBase tank) {
        if (tank == null) {
            return;
        }
        //fills always gonna be one bucket but we dont know what type yet
        //test if its a source block, or a waterlogged block
        BlockState targetState = level.getBlockState(posTarget);
        FluidState fluidState = level.getFluidState(posTarget);
        if (targetState.hasProperty(BlockStateProperties.WATERLOGGED) && targetState.getValue(BlockStateProperties.WATERLOGGED)) {
            //for waterlogged it is hardcoded to water
            long simFill = tank.fill(new FluidStack(new FluidStack(Fluids.WATER, FluidConstants.BUCKET), FluidConstants.BUCKET), FluidAction.SIMULATE);
            if (simFill == FluidConstants.BUCKET
                    && level.setBlockAndUpdate(posTarget, targetState.setValue(BlockStateProperties.WATERLOGGED, false))) {
                tank.fill(new FluidStack(Fluids.WATER, FluidConstants.BUCKET), FluidAction.EXECUTE);
            }
        }
        else if (targetState.getBlock() == Blocks.WATER_CAULDRON) {
            long simFill = tank.fill(new FluidStack(new FluidStack(Fluids.WATER, FluidConstants.BUCKET), FluidConstants.BUCKET), FluidAction.SIMULATE);
            if (simFill == FluidConstants.BUCKET
                    && level.setBlockAndUpdate(posTarget, Blocks.CAULDRON.defaultBlockState())) {
                tank.fill(new FluidStack(new FluidStack(Fluids.WATER, FluidConstants.BUCKET), FluidConstants.BUCKET), FluidAction.EXECUTE);
            }
        }
        else if (targetState.getBlock() == Blocks.LAVA_CAULDRON) {
            //copypasta of water cauldron code
            long simFill = tank.fill(new FluidStack(new FluidStack(Fluids.LAVA, FluidConstants.BUCKET), FluidConstants.BUCKET), FluidAction.SIMULATE);
            if (simFill == FluidConstants.BUCKET
                    && level.setBlockAndUpdate(posTarget, Blocks.CAULDRON.defaultBlockState())) {
                tank.fill(new FluidStack(new FluidStack(Fluids.LAVA, FluidConstants.BUCKET), FluidConstants.BUCKET), FluidAction.EXECUTE);
            }
        }
        else if (fluidState != null && fluidState.isSource() && fluidState.getType() != null) { // from ze world
            //not just water. any fluid source block
            long simFill = tank.fill(new FluidStack(new FluidStack(fluidState.getType(), FluidConstants.BUCKET), FluidConstants.BUCKET), FluidAction.SIMULATE);
            if (simFill == FluidConstants.BUCKET
                    && level.setBlockAndUpdate(posTarget, Blocks.AIR.defaultBlockState())) {
                tank.fill(new FluidStack(fluidState.getType(), FluidConstants.BUCKET), FluidAction.EXECUTE);
            }
        }
    }

    /**
     * Thank you Mekanism which is MIT License https://github.com/mekanism/Mekanism
     *
     * @param fluid
     * @param type
     * @return
     */
    public static TextureAtlasSprite getBaseFluidTexture(Fluid fluid, FluidRenderMap.FluidFlow type) {
        return FluidRenderMap.getFluidTexture(FluidStack.EMPTY, type);
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
    }

    public static Model3D getFluidModel(FluidStack fluid, int stage) {
        if (CACHED_FLUIDS.containsKey(fluid) && CACHED_FLUIDS.get(fluid).containsKey(stage)) {
            return CACHED_FLUIDS.get(fluid).get(stage);
        }
        Model3D model = new Model3D();
        model.setTexture(FluidRenderMap.getFluidTexture(fluid, FluidRenderMap.FluidFlow.STILL));
        if (FluidVariantRendering.getSprite(fluid.getType()) != null) {
            double sideSpacing = 0.00625;
            double belowSpacing = 0.0625 / 4;
            model.minX = sideSpacing;
            model.minY = belowSpacing;
            model.minZ = sideSpacing;
            model.maxX = 1 - sideSpacing;
            model.maxY = 1 - belowSpacing;
            model.maxZ = 1 - sideSpacing;
        }
        if (CACHED_FLUIDS.containsKey(fluid)) {
            CACHED_FLUIDS.get(fluid).put(stage, model);
        }
        else {
            Int2ObjectMap<Model3D> map = new Int2ObjectOpenHashMap<>();
            map.put(stage, model);
            CACHED_FLUIDS.put(fluid, map);
        }
        return model;
    }

    public static float getScale(FluidTank tank) {
        return getScale(tank.getFluidAmount(), tank.getCapacity(), tank.isEmpty());
    }

    public static float getScale(long stored, long capacity, boolean empty) {
        float targetScale = (float) stored / capacity;
        return targetScale;
    }


    public static Storage<FluidVariant> getTank(Level world, BlockPos pos, Direction side) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile == null) {
            return null;
        }
        return FluidStorage.SIDED.find(world, pos, side);
    }


    public static boolean tryFillPositionFromTank(Level world, BlockPos posSide, Direction sideOpp, Storage<FluidVariant> tankFrom, final int amount) {
        if (tankFrom == null || amount <= 0) {
            return false;
        }
        try {
            Storage<FluidVariant> fluidTo = FluidStorage.SIDED.find(world, posSide, sideOpp);
            if (fluidTo == null) {
                return false;
            }

            long result = StorageUtil.move(
                    tankFrom,
                    fluidTo,
                    fluidVariant -> true,
                    amount,
                    null
            );
            return result <= 0;
        }
        catch (Exception e) {
            Cyclic.LOGGER.error("A fluid tank had an issue when we tried to fill", e);
            //charset crashes here i guess
            //https://github.com/PrinceOfAmber/Cyclic/issues/605
            // https://github.com/PrinceOfAmber/Cyclic/issues/605https://pastebin.com/YVtMYsF6
            return false;
        }
    }
}
