package committee.nova.nextspring.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static committee.nova.nextspring.common.util.Utilities.tryCatalyze;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

    @Shadow
    private int age;

    public MixinItemEntity(World world) {
        super(world);
    }

    @Shadow
    public abstract ItemStack getItemStack();

    @Shadow
    public abstract void setItemStack(ItemStack itemStack);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        final ItemStack stack = this.getItemStack();
        if (stack.getItem() != Items.ROTTEN_FLESH) return;
        if (this.age == 0) return;
        if (this.age % 100 != 0) return;
        if (!shouldCatalyze() || !catalyze()) return;
        final ItemStack newStack = stack.copy();
        newStack.count--;
        if (newStack.count <= 0) {
            this.remove();
            ci.cancel();
        }
        this.setItemStack(newStack);
    }

    private boolean catalyze() {
        final World world = this.world;
        final BlockPos pos = new BlockPos(getPos().add(0, 0.7, 0));
        if (tryCatalyze(world, pos)) return true;
        return tryCatalyze(world, pos.down());
    }

    private boolean shouldCatalyze() {
        final Biome biome = world.getBiome(this.getBlockPos());
        final Random r = world.random;
        final float temperature = biome.temperature;
        final float humidity = biome.downfall;
        return (temperature * Math.abs(temperature) * (r.nextFloat() - 0.3F) + humidity * Math.abs(humidity) * (r.nextFloat() - 0.3F)) > 0.25F;
    }
}
