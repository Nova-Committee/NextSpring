package committee.nova.nextspring.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    public ItemStack stack;

    @Shadow
    public int age;

    public MixinItemEntity(World world) {
        super(world);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        final ItemStack stack = this.stack;
        if (stack.getItem() != Item.ROTTEN_FLESH) return;
        if (this.age == 0) return;
        if (this.age % 100 != 0) return;
        if (!shouldCatalyze() || !catalyze()) return;
        final ItemStack newStack = stack.copy();
        newStack.count--;
        if (newStack.count <= 0) {
            this.remove();
            ci.cancel();
        }
        this.stack = newStack;
    }

    private boolean catalyze() {
        final World world = this.world;
        final int x = (int) Math.floor(this.x);
        final int y = (int) Math.floor(this.y + 0.2);
        final int z = (int) Math.floor(this.z);
        final int plant = world.getBlock(x, y, z);
        if (tryCatalyze(world, plant, x, y, z)) return true;
        final int dirt = world.getBlock(x, y - 1, z);
        return tryCatalyze(world, dirt, x, y - 1, z);
    }

    private boolean shouldCatalyze() {
        final Biome biome = world.method_3773((int) x, (int) z);
        final Random r = world.random;
        final float temperature = biome.getTemperatureValue();
        final float humidity = biome.downfall;
        return (temperature * Math.abs(temperature) * (r.nextFloat() - 0.3F) + humidity * Math.abs(humidity) * (r.nextFloat() - 0.3F)) > 0.25F;
    }
}
