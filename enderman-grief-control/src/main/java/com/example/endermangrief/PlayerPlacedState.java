package com.example.endermangrief;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PlayerPlacedState extends PersistentState {
    private final Set<BlockPos> playerBlocks = new HashSet<>();

    public void addBlock(BlockPos pos) {
        playerBlocks.add(pos.toImmutable());
    }

    public void removeBlock(BlockPos pos) {
        playerBlocks.remove(pos);
    }

    public boolean isPlayerPlaced(BlockPos pos) {
        return playerBlocks.contains(pos);
    }

    public void cleanupIfMismatch(ServerWorld world) {
        Iterator<BlockPos> iterator = playerBlocks.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (world.isAir(pos)) {
                iterator.remove();
            }
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (BlockPos pos : playerBlocks) {
            NbtCompound entry = new NbtCompound();
            entry.putInt("x", pos.getX());
            entry.putInt("y", pos.getY());
            entry.putInt("z", pos.getZ());
            list.add(entry);
        }
        nbt.put("playerBlocks", list);
        return nbt;
    }

    public static PlayerPlacedState readNbt(NbtCompound nbt) {
        PlayerPlacedState state = new PlayerPlacedState();
        NbtList list = nbt.getList("playerBlocks", 10);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            state.playerBlocks.add(new BlockPos(
                entry.getInt("x"),
                entry.getInt("y"),
                entry.getInt("z")
            ));
        }
        return state;
    }

    public static PlayerPlacedState get(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(
            PlayerPlacedState::readNbt,
            PlayerPlacedState::new,
            "player_blocks"
        );
    }
}
