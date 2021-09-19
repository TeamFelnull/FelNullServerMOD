package dev.felnull.fnssu.mixin;

import com.github.alexthe666.rats.server.entity.RatUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RatUtils.class)
public class RatUtilsMixin {
    @Inject(method = "accelerateTick", at = @At("HEAD"), cancellable = true, remap = false)
    private static void accelerateTick(World world, BlockPos pos, CallbackInfo ci) {
        ci.cancel();
    }
}
