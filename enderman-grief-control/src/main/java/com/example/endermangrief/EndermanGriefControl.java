package com.example.endermangrief;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class EndermanGriefControl implements ModInitializer {
    public static final String MODID = "endermangriefcontrol";

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient) {
                BlockPos pos = hitResult.getBlockPos().offset(hitResult.getSide());
                if (world instanceof ServerWorld serverWorld) {
                    PlayerPlacedState stateStorage = PlayerPlacedState.get(serverWorld);
                    stateStorage.addBlock(pos);
                    stateStorage.markDirty();
                }
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient && world instanceof ServerWorld serverWorld) {
                PlayerPlacedState stateStorage = PlayerPlacedState.get(serverWorld);
                if (stateStorage.isPlayerPlaced(pos)) {
                    stateStorage.removeBlock(pos);
                    stateStorage.markDirty();
                }
            }
        });

        System.out.println("[EndermanGriefControl] Initialized and tracking!");
    }
}
