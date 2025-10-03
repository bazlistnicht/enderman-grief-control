package com.example.endermangrief.mixin;

import com.example.endermangrief.PlayerPlacedState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {

    @Inject(method = "canPickupBlock", at = @At("HEAD"), cancellable = true)
    private void preventPlayerBlockPickup(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        EndermanEntity enderman = (EndermanEntity)(Object)this;
        if (enderman.getWorld() instanceof ServerWorld serverWorld) {
            BlockPos pos = enderman.getBlockPos().down();
            PlayerPlacedState storage = PlayerPlacedState.get(serverWorld);

            storage.cleanupIfMismatch(serverWorld);

            if (storage.isPlayerPlaced(pos)) {
                cir.setReturnValue(false);
            }
        }
    }
}
