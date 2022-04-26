package committee.nova.ns.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static committee.nova.ns.common.util.Utilities.tryCatalyze;

@Mixin(EntityItem.class)
public abstract class MixinItemEntity extends Entity {

    @Shadow
    private int age;

    public MixinItemEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    public abstract void setItem(ItemStack stack);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        final ItemStack stack = this.getItem();
        if (stack.getItem() != Items.ROTTEN_FLESH) return;
        if (this.age == 0) return;
        if (this.age % 100 != 0) return;
        if (!shouldCatalyze() || !catalyze()) return;
        final ItemStack newStack = stack.copy();
        newStack.setCount(newStack.getCount() - 1);
        if (newStack.getCount() <= 0) {
            this.remove();
            ci.cancel();
        }
        this.setItem(newStack);
    }

    private boolean catalyze() {
        final World world = this.world;
        final BlockPos pos = new BlockPos(getPositionVector().add(0, 0.7, 0));
        if (tryCatalyze(world, pos)) return true;
        return tryCatalyze(world, pos.down());
    }

    private boolean shouldCatalyze() {
        final Biome biome = world.getBiome(this.getPosition());
        final Random r = world.rand;
        final float temperature = biome.getTemperature(getPosition());
        final float humidity = biome.getDownfall();
        return (temperature * Math.abs(temperature) * (r.nextFloat() - 0.3F) + humidity * Math.abs(humidity) * (r.nextFloat() - 0.3F)) > 0.25F;
    }
}
