package committee.nova.ns;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Random;

@Mod(NextSpring.MODID)
public class NextSpring {
    public static final String MODID = "nextspring";
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.IntValue refreshInterval;
    public static final ForgeConfigSpec.IntValue expireTime;
    public static final ForgeConfigSpec.BooleanValue influencedByBiome;
    public static final ForgeConfigSpec.DoubleValue possibilityMultiplier;
    public static final ForgeConfigSpec.BooleanValue respectVanillaCriteria;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("QuickPlant Configuration");
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
        MinecraftForge.EVENT_BUS.addListener(this::onItemDrop);
        MinecraftForge.EVENT_BUS.addListener(this::onItemExpire);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onItemDrop(final EntityJoinWorldEvent event) {
        if (event.isCanceled()) return;
        final Entity entity = event.getEntity();
        if (!(entity instanceof EntityItem)) return;
        final EntityItem itemEntity = (EntityItem) entity;
        if (itemEntity.getItem().getItem() != Items.ROTTEN_FLESH) return;
        itemEntity.lifespan = refreshInterval.get();
    }

    public void onItemExpire(final ItemExpireEvent event) {
        if (event.isCanceled()) return;
        final EntityItem itemEntity = event.getEntityItem();
        final ItemStack stack = itemEntity.getItem();
        if (stack.getItem() != Items.ROTTEN_FLESH) return;
        if (itemEntity.ticksExisted >= expireTime.get()) return;
        if (!catalyze(itemEntity)) {
            event.setExtraLife(refreshInterval.get());
            event.setCanceled(true);
            return;
        }
        final ItemStack newStack = stack.copy();
        newStack.setCount(newStack.getCount() - 1);
        if (newStack.getCount() <= 0) return;
        itemEntity.setItem(newStack);
        event.setExtraLife(refreshInterval.get());
        event.setCanceled(true);
    }

    private boolean catalyze(EntityItem itemEntity) {
        final World world = itemEntity.world;
        final BlockPos plantPos = new BlockPos(itemEntity.posX, itemEntity.posY + 0.2, itemEntity.posZ);
        if (influencedByBiome.get() && !canCatalyze(world, plantPos)) return false;
        if (tryCatalyze(world, plantPos)) return true;
        final BlockPos dirtPos = plantPos.down();
        return tryCatalyze(world, dirtPos);
    }

    private boolean canCatalyze(World world, BlockPos pos) {
        final Biome biome = world.getBiome(pos);
        final float temperature = biome.getTemperature(pos);
        final float humidity = biome.getDownfall();
        final Random r = world.rand;
        return possibilityMultiplier.get() * (temperature * Math.abs(temperature) * (r.nextFloat() - 0.3F) + humidity * Math.abs(humidity) * (r.nextFloat() - 0.3F)) > 0.25F;
    }

    private boolean tryCatalyze(World world, BlockPos pos) {
        final IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof IGrowable)) return false;
        final IGrowable plant = (IGrowable) state.getBlock();
        try {
            if (!plant.canGrow(world, pos, state, world.isRemote)) return false;
            if (respectVanillaCriteria.get() && !plant.canUseBonemeal(world, world.rand, pos, state)) return false;
            plant.grow(world, world.rand, pos, state);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
