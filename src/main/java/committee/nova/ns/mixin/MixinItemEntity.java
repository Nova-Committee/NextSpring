package committee.nova.ns.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static committee.nova.ns.common.util.Utilities.tryCatalyze;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
    @Shadow
    private int itemAge;

    public MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    public abstract void setStack(ItemStack stack);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        final ItemStack stack = this.getStack();
        if (stack.getItem() != Items.ROTTEN_FLESH) return;
        if (this.itemAge == 0) return;
        if (this.itemAge % 100 != 0) return;
        if (!shouldCatalyze() || !catalyze()) return;
        final ItemStack newStack = stack.copy();
        newStack.setCount(newStack.getCount() - 1);
        if (newStack.getCount() <= 0) {
            this.remove(RemovalReason.DISCARDED);
            ci.cancel();
        }
        this.setStack(newStack);
    }

    private boolean catalyze() {
        final World world = this.world;
        final BlockPos pos = new BlockPos(getPos().add(0, 0.7, 0));
        if (tryCatalyze(world, pos)) return true;
        return tryCatalyze(world, pos.down());
    }

    private boolean shouldCatalyze() {
        final RegistryEntry<Biome> biome = world.getBiome(this.getBlockPos());
        final Random r = world.random;
        final float temperature = biome.value().getTemperature();
        final float humidity = biome.value().getDownfall();
        return (temperature * Math.abs(temperature) * (r.nextFloat() - 0.3F) + humidity * Math.abs(humidity) * (r.nextFloat() - 0.3F)) > 0.25F;
    }
}
