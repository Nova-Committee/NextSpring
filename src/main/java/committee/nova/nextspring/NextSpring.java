package committee.nova.nextspring;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;

@Mod(NextSpring.MODID)
public class NextSpring {
    public static final String MODID = "nextspring";
    public static final ModConfigSpec COMMON_CONFIG;
    public static final ModConfigSpec.IntValue refreshInterval;
    public static final ModConfigSpec.IntValue expireTime;
    public static final ModConfigSpec.BooleanValue influencedByBiome;
    public static final ModConfigSpec.DoubleValue possibilityMultiplier;
    public static final ModConfigSpec.BooleanValue respectVanillaCriteria;

    static {
        final var builder = new ModConfigSpec.Builder();
        builder.comment("NextSpring Configuration");
        refreshInterval = builder.comment("The time of the refresh interval. After each interval, the rotten flesh item will try to catalyze the plant.")
                .defineInRange("refreshInterval", 100, 20, 6000);
        expireTime = builder.comment("The time before the item entity's expiration. The item entity whose age is larger than this will be cleared, just like what happens in vanilla.")
                .defineInRange("expireTime", 6000, 20, 36000);
        influencedByBiome = builder.comment("Should the possibility of catalysis be influenced by biome properties (temperature & humidity)?")
                .define("influencedByBiome", true);
        possibilityMultiplier = builder.comment("The possibility multiplier. The greater the value is, the more the possibility the plant is catalyzed.")
                .defineInRange("possibilityMultiplier", 1F, 0F, Float.MAX_VALUE / 2);
        respectVanillaCriteria = builder.comment("If true, will respect vanilla's bone meal catalysis criteria. An example is when you try to bone-meal a tree, It has a 45% chance of growing.")
                .define("respectVanillaCriteria", true);
        COMMON_CONFIG = builder.build();
    }

    public NextSpring() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onItemDrop(final EntityJoinLevelEvent event) {
        if (event.isCanceled()) return;
        final var entity = event.getEntity();
        if (!(entity instanceof final ItemEntity itemEntity)) return;
        if (itemEntity.getItem().getItem() != Items.ROTTEN_FLESH) return;
        itemEntity.lifespan = refreshInterval.get();
    }

    @SubscribeEvent
    public void onItemExpire(final ItemExpireEvent event) {
        if (event.isCanceled()) return;
        final var itemEntity = event.getEntity();
        final var stack = itemEntity.getItem();
        if (stack.getItem() != Items.ROTTEN_FLESH) return;
        if (itemEntity.tickCount >= expireTime.get()) return;
        if (!catalyze(itemEntity)) {
            event.setExtraLife(refreshInterval.get());
            event.setCanceled(true);
            return;
        }
        final var newStack = stack.copy();
        newStack.setCount(newStack.getCount() - 1);
        if (newStack.getCount() <= 0) return;
        itemEntity.setItem(newStack);
        event.setExtraLife(refreshInterval.get());
        event.setCanceled(true);
    }

    private boolean catalyze(ItemEntity itemEntity) {
        final var world = itemEntity.level();
        final var plantPos = new BlockPos((int) Math.floor(itemEntity.getX()), (int) Math.floor(itemEntity.getY() + 0.2), (int) Math.floor(itemEntity.getZ()));
        if (influencedByBiome.get() && !canCatalyze(world, plantPos)) return false;
        if (tryCatalyze(world, plantPos)) return true;
        final var dirtPos = plantPos.below();
        return tryCatalyze(world, dirtPos);
    }

    private boolean canCatalyze(Level world, BlockPos pos) {
        final var biome = world.getBiome(pos);
        final var temperature = biome.value().getBaseTemperature();
        final var r = world.random;
        return possibilityMultiplier.get() * temperature * Math.abs(temperature) * (r.nextFloat() - 0.3F) > .0F;
    }

    private boolean tryCatalyze(Level world, BlockPos pos) {
        final var state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof final BonemealableBlock plant)) return false;
        try {
            if (!plant.isValidBonemealTarget(world, pos, state)) return false;
            if (respectVanillaCriteria.get() && !plant.isBonemealSuccess(world, world.random, pos, state)) return false;
            if (!world.isClientSide) plant.performBonemeal((ServerLevel) world, world.random, pos, state);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
