package com.robertx22.mine_and_slash.mixins;

import com.robertx22.mine_and_slash.database.data.spells.summons.entity.SummonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeBlock.class)
public class SlimeBlockMixin {

    @Inject(method = "updateEntityAfterFallOn", at = @At("HEAD"), cancellable = true)
    private void keepSummonsGrounded(BlockGetter level, Entity entity, CallbackInfo ci) {
        if (entity instanceof SummonEntity) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            ci.cancel();
        }
    }

    @Inject(method = "stepOn", at = @At("HEAD"), cancellable = true)
    private void keepSummonsMoving(Level level, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (entity instanceof SummonEntity) {
            ci.cancel();
        }
    }
}
