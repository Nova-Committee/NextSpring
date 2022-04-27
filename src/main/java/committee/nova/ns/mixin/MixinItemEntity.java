package committee.nova.ns.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemInstance;
import net.minecraft.item.ItemType;
import net.minecraft.level.Level;
import net.minecraft.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static committee.nova.ns.util.WorldUtil.tryCatalyze;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
	@Shadow
	public ItemInstance item;

	@Shadow
	public int age;

	public MixinItemEntity(Level arg) {
		super(arg);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void onTick(CallbackInfo ci) {
		final ItemInstance stack = this.item;
		if (item.itemId != ItemType.dyePowder.id) return;
		if (item.getDamage() != 15) return;
		if (this.age == 0) return;
		if (this.age % 100 != 0) return;
		if (!shouldCatalyze() || !catalyze()) return;
		final ItemInstance newStack = stack.copy();
		newStack.count--;
		if (newStack.count <= 0) {
			this.remove();
			ci.cancel();
		}
		this.item = newStack;
	}

	private boolean shouldCatalyze() {
		final Biome biome = level.getBiomeSource().getBiome((int) x, (int) z);
		final Random r = level.rand;
		final float humidity = biome.canRain() ? 0.6F : 0F;
		final float temperature = biome.canSnow() ? 0.1F : 0.8F;
		return (temperature * Math.abs(temperature) * (r.nextFloat() - 0.3F) + humidity * Math.abs(humidity) * (r.nextFloat() - 0.3F)) > 0.25F;
	}

	private boolean catalyze() {
		final Level world = this.level;
		if (tryCatalyze(world, new double[]{x, y + 0.7, z})) return true;
		return tryCatalyze(world, new double[]{x, y - 0.3, z});
	}
}
