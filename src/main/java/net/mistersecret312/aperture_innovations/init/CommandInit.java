package net.mistersecret312.aperture_innovations.init;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.aperture_innovations.ApertureInnovations;
import net.mistersecret312.aperture_innovations.data.PortalLinkData;
import net.mistersecret312.aperture_innovations.data.portal.PortalLink;
import net.mistersecret312.aperture_innovations.items.PortalGunItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CommandInit
{
	private static final String PORTALS = "portals";

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal(ApertureInnovations.MODID)
									.then(Commands.literal(PORTALS)
												  .then(Commands.literal("close")
																.then(Commands.argument("uuid", UuidArgument.uuid())
																			  .suggests(CommandInit::suggestGunID)
																			  .executes(CommandInit::closePair))))
									.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
	}

	private static CompletableFuture<Suggestions> suggestGunID(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder)
	{
		ServerPlayer player = context.getSource().getPlayer();
		if(player == null)
			return CompletableFuture.completedFuture(builder.build());

		Map<UUID, Component> uuids = new HashMap<>();
		for(ItemStack stack : player.getInventory().items)
		{
			if(stack.getItem() instanceof PortalGunItem gunItem)
			{
				UUID uuid = gunItem.getUUID(stack, false);
				Component component = ComponentUtils.wrapInSquareBrackets(stack.getHoverName().copy()
						.withStyle(style -> stack.getRarity().getStyleModifier().apply(style)));

				if(uuid != null)
					uuids.put(uuid, component);
			}
		}

		PortalLinkData linkData = PortalLinkData.get(player.level());
		for(UUID uuid : linkData.portalLinks.keySet())
		{
			if(!uuids.containsKey(uuid))
				uuids.put(uuid, Component.empty());
		}

		for(Map.Entry<UUID, Component> entry : uuids.entrySet())
			builder.suggest(String.valueOf(entry.getKey()), entry.getValue());

		return CompletableFuture.completedFuture(builder.build());
	}

	private static int closePair(CommandContext<CommandSourceStack> context)
	{
		UUID uuid = UuidArgument.getUuid(context, "uuid");

		ServerPlayer player = context.getSource().getPlayer();

		if(player == null)
			return Command.SINGLE_SUCCESS;

		Level level = player.level();
		PortalLink link = PortalLinkData.get(level).getLink(uuid);
		if(link != null)
		{
			link.reset(level);
			return Command.SINGLE_SUCCESS;
		}

		return Command.SINGLE_SUCCESS;
	}

}
