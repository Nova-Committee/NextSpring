package committee.nova.nextspring.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static committee.nova.nextspring.common.util.Utilities.tryCatalyze;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

    @Shadow
    public int age;

    public MixinItemEntity(World world) {
        super(world);
    }

    @Shadow
    public abstract ItemStack method_4548();

    @Shadow
    public abstract void setItemStack(ItemStack itemStack);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        final ItemStack stack = this.method_4548();
        if (stack.getItem() != Item.ROTTEN_FLESH) return;
        if (this.age == 0) return;
        if (this.age % 100 != 0) return;
        if (!catalyze()) return;
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
        final int x = (int) Math.floor(this.x);
        final int y = (int) Math.floor(this.y + 0.2);
        final int z = (int) Math.floor(this.z);
        final int plant = world.getBlock(x, y, z);
        if (tryCatalyze(world, plant, x, y, z)) return true;
        final int dirt = world.getBlock(x, y - 1, z);
        return tryCatalyze(world, dirt, x, y - 1, z);
    }
}
