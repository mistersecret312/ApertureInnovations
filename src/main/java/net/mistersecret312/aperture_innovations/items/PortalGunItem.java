package net.mistersecret312.aperture_innovations.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.capabilities.ApertureEnergy;
import net.mistersecret312.aperture_innovations.client.renderer.item.PortalGunRenderer;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariant;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientPortalGunVariants;
import net.mistersecret312.aperture_innovations.config.PortalGunConfig;
import net.mistersecret312.aperture_innovations.datapack.CubeVariant;
import net.mistersecret312.aperture_innovations.datapack.PortalGunVariant;
import net.mistersecret312.aperture_innovations.init.*;
import net.mistersecret312.aperture_innovations.multitool.ConfigurationProperty;
import net.mistersecret312.aperture_innovations.multitool.IItemConfiguration;
import net.mistersecret312.aperture_innovations.multitool.InteractionType;
import net.mistersecret312.aperture_innovations.network.ClientboundGunZapSoundPacket;
import net.mistersecret312.aperture_innovations.network.ClientboundPortalSoundsPacket;
import net.mistersecret312.aperture_innovations.data.portal.PortalLink;
import net.mistersecret312.aperture_innovations.data.PortalLinkData;
import net.mistersecret312.aperture_innovations.utilities.PortalUtilities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PortalGunItem extends Item implements GeoItem, IItemConfiguration
{
	public static final String ENERGY = "Energy";

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	protected static class Animations
	{
		protected static final String MAIN_CONTROLLER = "main";

		protected static final RawAnimation SHOOT = RawAnimation.begin().thenPlay("shoot");
		protected static final RawAnimation RESET = RawAnimation.begin().thenPlay("reset");
		protected static final RawAnimation HOLD = RawAnimation.begin().thenPlayAndHold("hold");
		protected static final RawAnimation LET_GO = RawAnimation.begin().thenPlay("let_go");

		private Animations() {}
	}

	public PortalGunItem(Properties pProperties)
	{
		super(pProperties);

		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	public static ItemStack createPortalGun(ResourceLocation variantKey)
	{
		ItemStack stack = new ItemStack(ItemInit.PORTAL_GUN.get());
		PortalGunItem item = (PortalGunItem) stack.getItem();
		item.setVariant(stack, variantKey);

		return stack;
	}

	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return PortalGunConfig.portal_gun_uses_energy.get() && getEnergy(stack) != getCapacity();
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getEnergy(stack) / getCapacity());
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		return new Color(0, 200, 255).getRGB();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components,
								TooltipFlag flag)
	{
		components.add(Component.translatable("aperture_innovations.portal_gun.variant_" + getVariant(stack).getPath()).withStyle(
						ChatFormatting.YELLOW));

		int dualityState = getDualityState(stack);

		int hullColor = getHullColor(stack);

		int primaryStripeColor = getPrimaryStripeColor(stack);
		int secondaryStripeColor = getSecondaryStripeColor(stack);

		int primaryPortalColor = getPrimaryPortalColor(stack);
		int secondaryPortalColor = getSecondaryPortalColor(stack);

		if(dualityState == 0)
				components.add(Component.translatable("item.aperture_innovations.portal_gun.duality_primary").withStyle(ChatFormatting.LIGHT_PURPLE));
		if(dualityState == 1)
				components.add(Component.translatable("item.aperture_innovations.portal_gun.duality_secondary").withStyle(ChatFormatting.LIGHT_PURPLE));

		if(getPair(stack) != null && dualityState != 2)
			components.add(Component.translatable("item.aperture_innovations.portal_gun.paired").withStyle(ChatFormatting.DARK_PURPLE));

		if(PortalGunConfig.portal_gun_uses_energy.get())
		{
			components.add(Component.translatable("item.aperture_innovations.portal_gun.energy").append(ApertureEnergy.energyToString(getEnergy(stack), getCapacity())).withStyle(ChatFormatting.DARK_RED));
		}

		if(primaryPortalColor != -1 || primaryStripeColor != -1 || secondaryPortalColor != -1 || secondaryStripeColor != -1 || hullColor != -1)
			components.add(Component.literal(""));

		if((hullColor != -1 && hullColor != 16777215) || flag.hasShiftDown())
			components.add(Component.translatable("item.aperture_innovations.portal_gun.hull_color", Integer.toHexString(hullColor).toUpperCase()).withStyle(style -> style.withColor(hullColor)));

		if((primaryPortalColor != -1 && primaryPortalColor != 16777215) || flag.hasShiftDown())
			components.add(Component.translatable("item.aperture_innovations.portal_gun.portal_primary_color", Integer.toHexString(primaryPortalColor).toUpperCase()).withStyle(style -> style.withColor(primaryPortalColor)));

		if((secondaryPortalColor != -1 && secondaryPortalColor != 16777215) || flag.hasShiftDown())
			components.add(Component.translatable("item.aperture_innovations.portal_gun.portal_secondary_color", Integer.toHexString(secondaryPortalColor).toUpperCase()).withStyle(style -> style.withColor(secondaryPortalColor)));


		if((primaryStripeColor != -1 && primaryStripeColor != 16777215) || flag.hasShiftDown())
			components.add(Component.translatable("item.aperture_innovations.portal_gun.stripe_primary_color", Integer.toHexString(primaryStripeColor).toUpperCase()).withStyle(style -> style.withColor(primaryStripeColor)));

		if((secondaryStripeColor != -1 && secondaryStripeColor != 16777215) || flag.hasShiftDown())
			components.add(Component.translatable("item.aperture_innovations.portal_gun.stripe_secondary_color", Integer.toHexString(secondaryStripeColor).toUpperCase()).withStyle(style -> style.withColor(secondaryStripeColor)));

		Level level = context.level();
		if(level != null)
		{
			Color hsbColor = Color.getHSBColor(level.getTimeOfDay(1f)*50, 1f, 1f);
			components.add(Component.translatable("tooltip.aperture_innovations.is_configurable").withStyle((style -> style.withColor(
					hsbColor.getRGB()))));
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected)
	{

		if(level.isClientSide() || !(entity instanceof Player player))
			return;

		if(!isInitialized(stack)) {
			setInitialized(stack, true);
			PortalLinkData data = PortalLinkData.get(level);

			UUID linkID = getUUID(stack, true);

			PortalLink link = data.getLink(linkID);
			if (link == null) {
				data.addFreshLink(linkID, getVariant(stack));

				PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(player.blockPosition()),
						new ClientboundPortalSoundsPacket.GunActivate(linkID, player.blockPosition()));
			}
		}
		else
		{
			PortalLink link = PortalUtilities.getPortalLinks(level).get(getUUID(stack, false));
			if(link != null && link.isOpen() && PortalGunConfig.portal_gun_uses_energy.get())
			{
				@Nullable IEnergyStorage cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
				if(cap != null && cap instanceof ApertureEnergy energy)
				{
					long toExtract = link.isInterdimensionalLink() ?
											 PortalGunConfig.portal_gun_passive_consumption.get() :
											 PortalGunConfig.portal_gun_interdimensional_passive_consumption.get();
					long extracted = energy.extractLongEnergy(toExtract, false);
					if(extracted < toExtract)
					{
						player.displayClientMessage(Component.translatable("item.aperture_innovations.portal_gun.not_enough_energy").withStyle(ChatFormatting.DARK_RED), true);
						link.reset(level);
					}
				}
			}

			if(getHeldEntity(stack) != null)
			{
				Integer id = getHeldEntity(stack);
				if(id != null)
				{
					Entity heldEntity = level.getEntity(id);
					if(heldEntity == null)
					{
						setHeldEntity(stack, null);
						PacketDistributor.sendToAllPlayers(
								new ClientboundGunZapSoundPacket(player.getUUID(), true));

						level.playSound(null, player.blockPosition(), SoundInit.PORTAL_GUN_HOLD_STOP.get(), SoundSource.PLAYERS);
						this.triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel) level),
								"main", "let_go");
					}

					if(heldEntity != null && !heldEntity.getData(AttachmentTypeInit.HOLD_ENTITY).isHeld)
					{
						setHeldEntity(stack, null);
						PacketDistributor.sendToAllPlayers(
								new ClientboundGunZapSoundPacket(player.getUUID(), true));

						level.playSound(null, player.blockPosition(), SoundInit.PORTAL_GUN_HOLD_STOP.get(), SoundSource.PLAYERS);
						this.triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel) level),
								"main", "let_go");

					}
				}

				int tick = getZapTick(stack);
				setZapTick(stack, tick+1);

				int soundTick = getZapSoundTick(stack);
				if(soundTick == 20)
					setZapSoundTick(stack, 0);

				if(soundTick == 0)
				{
					PacketDistributor.sendToAllPlayers(
							new ClientboundGunZapSoundPacket(player.getUUID(), false));
				}

				setZapSoundTick(stack, soundTick+1);
			}
			else
			{
				setZapTick(stack, -1);
				setZapSoundTick(stack, -1);
			}

			if(link != null && (isSelected || slot == 40) && (getPair(stack) == null || getDualityState(stack) == 2) )
			{
				link.updateColors(level, getPrimaryPortalColor(stack), getSecondaryPortalColor(stack));
				link.updateVariant(level, getVariant(stack));
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		if(entity.isInFluidType())
		{
			Entity owner = entity.getOwner();
			if(owner instanceof Player player)
				AdvancementInit.THROWN_INTO_FLUID.get().trigger((ServerPlayer) (player), stack, entity.blockPosition());
		}
		return super.onEntityItemUpdate(stack, entity);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged;
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand)
	{
		return getDualityState(stack) != 2;
	}

	public static int getEnergy(ItemStack stack)
	{
		@Nullable IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if(energy != null)
		{
			return energy.getEnergyStored();
		}

		return 0;
	}

	public long getCapacity()
	{
		return PortalGunConfig.portal_gun_max_energy_stored.get();
	}

	public long getTransfer()
	{
		return PortalGunConfig.portal_gun_uses_energy.get() ? 10000L : 0L;
	}

	public static BlockHitResult rayTrace(Level level, Player player, double range) {
		float xRot = player.getXRot();
		float yRot = player.getYRot();
		Vec3 eyePos = player.getEyePosition();

		float f2 = Mth.cos(-yRot * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = Mth.sin(-yRot * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -Mth.cos(-xRot * ((float)Math.PI / 180F));
		float f5 = Mth.sin(-xRot * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;

		Vec3 lookVec = new Vec3(f6, f5, f7);
		Vec3 endPos = eyePos.add(lookVec.scale(range));

		ClipContext context = new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player)
		{
			@Override
			public VoxelShape getBlockShape(BlockState state, BlockGetter level, BlockPos pos)
			{
				if(state.is(TagInit.Blocks.SHOOT_THROUGH))
					return Shapes.empty();

				return super.getBlockShape(state, level, pos);
			}
		};

		return level.clip(context);
	}

	public boolean isLookingAtMoon(Player player, Level level)
	{
		HitResult hit = player.pick(PortalGunConfig.portal_gun_shoot_range.get(), 0.0F, false);

		if (hit.getType() != HitResult.Type.MISS)
			return false;

		float timeAngle = level.getSunAngle(0.0F);

		double moonX = Math.sin(timeAngle);
		double moonY = -Math.cos(timeAngle);

		Vec3 moonVector = new Vec3(moonX, moonY, 0);

		Vec3 lookVector = player.getLookAngle();

		double dot = lookVector.dot(moonVector);

		return dot > 0.995;
	}

	public UUID getUUID(ItemStack stack, boolean generateIfEmpty)
	{
		int dualityState = getDualityState(stack);
		if(dualityState != 2 && getPair(stack) != null)
			return getPair(stack);

		String linkString = stack.get(DataComponentInit.LINK_ID);
		if(linkString == null && generateIfEmpty)
		{
			UUID linkID = UUID.randomUUID();
			setUUID(stack, linkID);
			return linkID;
		}

		if(linkString != null)
			return UUID.fromString(linkString);


		return null;
	}

	public void setUUID(ItemStack stack, UUID uuid)
	{
		stack.set(DataComponentInit.LINK_ID, uuid.toString());
	}

	public UUID getPair(ItemStack stack)
	{
		String pairString = stack.get(DataComponentInit.PAIR_ID);
		if(pairString == null)
			return null;
		return UUID.fromString(pairString);
	}

	public void setPair(ItemStack stack, UUID pairID)
	{
		if(pairID == null)
		{
			stack.remove(DataComponentInit.PAIR_ID);
			return;
		}

		stack.set(DataComponentInit.PAIR_ID, pairID.toString());
	}

	@Nullable
	public Integer getHeldEntity(ItemStack stack)
	{
		return stack.get(DataComponentInit.HELD_ENTITY);
	}

	public void setHeldEntity(ItemStack stack, Entity entity)
	{
		if(entity == null)
			stack.remove(DataComponentInit.HELD_ENTITY);
		else stack.set(DataComponentInit.HELD_ENTITY, entity.getId());
	}

	public int getDualityState(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.DUALITY_STATE, 2);
	}


	public int getPrimaryStripeColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.PRIMARY_STRIPE_COLOR, -1);
	}

	public int getSecondaryStripeColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.SECONDARY_STRIPE_COLOR, -1);
	}

	public int getPrimaryPortalColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.PRIMARY_PORTAL_COLOR, -1);
	}

	public int getSecondaryPortalColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.SECONDARY_PORTAL_COLOR, -1);
	}

	public int getHullColor(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.HULL_COLOR, -1);
	}

	public ResourceLocation getVariant(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.PORTAL_GUN_VARIANT, ResourceLocation.fromNamespaceAndPath(
				ApertureInnovations.MODID, "chell"));
	}

	@OnlyIn(Dist.CLIENT)
	public ClientPortalGunVariant getGunVariant(ItemStack stack)
	{
		if(Minecraft.getInstance().level == null)
			return ClientPortalGunVariant.DEFAULT_VARIANT;

		RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
		PortalGunVariant variant = registryAccess.registryOrThrow(PortalGunVariant.REGISTRY_KEY).get(getVariant(stack));
		if(variant == null)
			return ClientPortalGunVariant.DEFAULT_VARIANT;

		return ClientPortalGunVariants.getPortalGunVariant(variant.getClientVariant());
	}

	public void setDualityState(ItemStack stack, int state)
	{
		stack.set(DataComponentInit.DUALITY_STATE, state);
	}

	public void setPrimaryStripeColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.PRIMARY_STRIPE_COLOR, color);
	}

	public void setSecondaryStripeColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.SECONDARY_STRIPE_COLOR, color);
	}

	public void setPrimaryPortalColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.PRIMARY_PORTAL_COLOR, color);
	}

	public void setSecondaryPortalColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.SECONDARY_PORTAL_COLOR, color);
	}

	public void setHullColor(ItemStack stack, int color)
	{
		stack.set(DataComponentInit.HULL_COLOR, color);
	}

	public void setVariant(ItemStack stack, ResourceLocation variantKey)
	{
		stack.set(DataComponentInit.PORTAL_GUN_VARIANT, variantKey);
	}

	public int getZapSoundTick(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ZAP_SOUND_TICK, -1);
	}

	public void setZapSoundTick(ItemStack stack, int tick)
	{
		if(tick > 40)
			tick = 0;
		stack.set(DataComponentInit.ZAP_SOUND_TICK, tick);
	}

	public int getLastShotPortal(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.LAST_PORTAL, -1);
	}

	public void setLastShotPortal(ItemStack stack, int portal)
	{
		stack.set(DataComponentInit.LAST_PORTAL, portal);
	}

	public void setInitialized(ItemStack stack, boolean value)
	{
		stack.set(DataComponentInit.INITIALIZED, value);
	}

	public boolean isInitialized(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.INITIALIZED, false);
	}

	public void setZapTick(ItemStack stack, int tick)
	{
		if(tick < 0)
			stack.remove(DataComponentInit.ZAP_TICK);
		else
			stack.set(DataComponentInit.ZAP_TICK, tick);

		if(tick > 8)
			stack.set(DataComponentInit.ZAP_TICK, 0);
	}

	public int getZapTick(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ZAP_TICK, -1);
	}


	private <T extends PortalGunItem> PlayState handleAnimationState(AnimationState state) {
		return PlayState.STOP;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
	{
		AnimationController<PortalGunItem> controller = new AnimationController<>(this,
				Animations.MAIN_CONTROLLER, 0, this::handleAnimationState);
		controller.triggerableAnim("shoot", Animations.SHOOT);
		controller.triggerableAnim("reset", Animations.RESET);
		controller.triggerableAnim("hold", Animations.HOLD);
		controller.triggerableAnim("let_go", Animations.LET_GO);
		controllers.add(controller);
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private PortalGunRenderer renderer;

			@Override
			public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new PortalGunRenderer();

				return this.renderer;
			}
		});
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return this.cache;
	}

	public static class Energy extends ApertureEnergy.Item
	{
		public Energy(ItemStack stack)
		{
			super(stack, PortalGunConfig.portal_gun_max_energy_stored.get(),
					10000L, 10000L);
		}

		@Override
		public long receiveLongEnergy(long maxReceive, boolean simulate)
		{
			return super.receiveLongEnergy(maxReceive, simulate);
		}

		@Override
		public long extractLongEnergy(long maxExtract, boolean simulate)
		{
			return super.extractLongEnergy(maxExtract, simulate);
		}

		@Override
		public long maxReceive()
		{
			if(stack.getItem() instanceof PortalGunItem portalGun)
				return portalGun.getTransfer();

			return 0;
		}

		@Override
		public long maxExtract()
		{
			if(stack.getItem() instanceof PortalGunItem portalGun)
				return portalGun.getTransfer();

			return 0;
		}

		@Override
		public long loadEnergy(ItemStack stack)
		{
			return stack.getOrDefault(DataComponentInit.ENERGY, PortalGunConfig.portal_gun_max_energy_stored.get());
		}

		@Override
		public long getTrueMaxEnergyStored()
		{
			if(stack.getItem() instanceof PortalGunItem portalGunItem)
				return portalGunItem.getCapacity();

			return 0;
		}

		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			stack.set(DataComponentInit.ENERGY, this.energy);
		}
	}

	@Override
	public List<ConfigurationProperty<?>> getConfigurationProperties(ItemStack stack, RegistryAccess registryAccess)
	{
		List<ConfigurationProperty<?>> properties = new ArrayList<>();

		properties.add(new ConfigurationProperty<>("primary_portal_color",
				"color", "multi_tool.aperture_innovations.portal_gun.primary_portal_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				clr -> setPrimaryPortalColor(stack, clr.packagedInt()),
				() -> net.mistersecret312.aperture_innovations.multitool.Color.fromInt(getPrimaryPortalColor(stack))));

		properties.add(new ConfigurationProperty<>("secondary_portal_color",
				"color", "multi_tool.aperture_innovations.portal_gun.secondary_portal_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				clr -> setSecondaryPortalColor(stack, clr.packagedInt()),
				() -> net.mistersecret312.aperture_innovations.multitool.Color.fromInt(getSecondaryPortalColor(stack))));

		properties.add(new ConfigurationProperty<>("primary_stripe_color",
				"color", "multi_tool.aperture_innovations.portal_gun.primary_stripe_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				clr -> setPrimaryStripeColor(stack, clr.packagedInt()),
				() -> net.mistersecret312.aperture_innovations.multitool.Color.fromInt(getPrimaryStripeColor(stack))));

		properties.add(new ConfigurationProperty<>("secondary_stripe_color",
				"color", "multi_tool.aperture_innovations.portal_gun.secondary_portal_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				clr -> setSecondaryStripeColor(stack, clr.packagedInt()),
				() -> net.mistersecret312.aperture_innovations.multitool.Color.fromInt(getSecondaryStripeColor(stack))));

		properties.add(new ConfigurationProperty<>("hull_color",
				"color", "multi_tool.aperture_innovations.portal_gun.hull_color",
				MultiToolConfigTypeInit.COLOR.get(),
				new InteractionType.RGBColorPicker(),
				clr -> setHullColor(stack, clr.packagedInt()),
				() -> net.mistersecret312.aperture_innovations.multitool.Color.fromInt(getHullColor(stack))));

		List<String> variants = new ArrayList<>();
		for(Map.Entry<ResourceKey<PortalGunVariant>, PortalGunVariant> entry : registryAccess
																		 .registryOrThrow(PortalGunVariant.REGISTRY_KEY)
																		 .entrySet())
		{
			variants.add(entry.getKey().location().toString());
		}

		properties.add(new ConfigurationProperty<>("variant", "variant",
				"multi_tool.aperture_innovations.variant",
				MultiToolConfigTypeInit.RESOURCE_LOCATION.get(),
				new InteractionType.ListChoice(variants, getVariant(stack).toString()),
				variant -> setVariant(stack, variant),
				() -> getVariant(stack)));

		return properties;
	}
}
