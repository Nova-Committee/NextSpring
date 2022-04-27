package committee.nova.ns.mixin;

import net.minecraft.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
	public ItemStack item;

	@Shadow
	public int age;

	public MixinItemEntity(World world) {
		super(world);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void onTick(CallbackInfo ci) {
		final ItemStack stack = this.item;
		if (stack.getItem() != Item.ROTTEN_LESH) return;
		if (this.age == 0) return;
		if (this.age % 100 != 0) return;
		if (!shouldCatalyze() || !catalyze()) return;
		final ItemStack newStack = stack.copy();
		newStack.count--;
		if (newStack.count <= 0) {
			this.remove();
			ci.cancel();
		}
		this.item = newStack;
	}

	private boolean shouldCatalyze() {
		final Biome biome = world.getBiomeSource().getBiome((int) x, (int) z);
		final Random r = world.random;
		final float humidity = biome.getDownfall();
		final float temperature = biome.getTemperature();
		return (temperature * Math.abs(temperature) * (r.nextFloat() - 0.3F) + humidity * Math.abs(humidity) * (r.nextFloat() - 0.3F)) > 0.25F;
	}

	private boolean catalyze() {
		if (tryCatalyze(world, new double[]{x, y + 0.7, z})) return true;
		return tryCatalyze(world, new double[]{x, y - 0.3, z});
	}
}
