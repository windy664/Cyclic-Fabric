package net.knsh.cyclic.mixin.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

// forked off of Porting-lib
@Mixin(ServerPlayer.class)
public interface ServerPlayerAccessor {
    @Invoker
    void callInitMenu(AbstractContainerMenu abstractContainerMenu);

    @Invoker
    void callNextContainerCounter();

    @Accessor
    int getContainerCounter();
}
