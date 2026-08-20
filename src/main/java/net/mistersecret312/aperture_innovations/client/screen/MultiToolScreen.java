package net.mistersecret312.aperture_innovations.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.mistersecret312.aperture_innovations.client.resourcepack.ClientMultiToolVariant;
import net.mistersecret312.aperture_innovations.client.screen.renderers.BlockEntityPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.EntityPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.ItemPreviewRenderer;
import net.mistersecret312.aperture_innovations.client.screen.renderers.PreviewRenderer;
import net.mistersecret312.aperture_innovations.multitool.*;
import net.mistersecret312.aperture_innovations.network.ServerboundMultiToolApplyBlockEntityPacket;
import net.mistersecret312.aperture_innovations.network.ServerboundMultiToolApplyEntityPacket;
import net.mistersecret312.aperture_innovations.network.ServerboundMultiToolApplyItemStackPacket;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MultiToolScreen extends Screen
{
	public HashMap<String, Object> properties = new HashMap<>();
	public HashMap<String, Category> categories = new HashMap<>();

	public HashMap<Category, List<Renderable>> categoryWidgets = new HashMap<>();

	private IHaveConfiguration config = null;
	public final PreviewRenderer renderer;
	public final ClientMultiToolVariant variant;
	public final int mainColor;
	public final int glowColor;

	public boolean hsbMode = false;
	public boolean colorSliderMode = false;

	private String openCategory = "";

	public MultiToolScreen(Component title, IHaveConfiguration configuration, PreviewRenderer renderer,
						   ClientMultiToolVariant variant, int mainColor, int glowColor)
	{
		super(title);
		this.renderer = renderer;
		this.variant = variant;
		this.mainColor = mainColor;
		this.glowColor = glowColor;

		if(configuration != null)
		{
			this.config = configuration;
			if(configuration instanceof Item)
				constructItemProperties();
			else constructProperties();
		}

		this.renderer.applyFakeState(properties);
	}

	public void constructProperties()
	{
		for(ConfigurationProperty<?> property : this.config.getConfigurationProperties(Minecraft.getInstance().level.registryAccess()))
		{
			properties.put(property.getName(), property.get());

			Category category = categories.computeIfAbsent(property.getCategory(), name -> new Category(
					name, new HashMap<>()));
			category.entries.computeIfAbsent(property.getName(), name -> new CategoryEntry(name,
					property.getTranslatable()));
		}
	}

	public void constructItemProperties()
	{
		if(!(renderer instanceof ItemPreviewRenderer itemPreviewRenderer))
			return;
		if(!(config instanceof IItemConfiguration configuration))
			return;

		for(ConfigurationProperty<?> property : configuration.getConfigurationProperties(itemPreviewRenderer.stack, Minecraft.getInstance().level.registryAccess()))
		{
			properties.put(property.getName(), property.get());

			Category category = categories.computeIfAbsent(property.getCategory(), name -> new Category(
					name, new HashMap<>()));
			category.entries.computeIfAbsent(property.getName(), name -> new CategoryEntry(name,
					property.getTranslatable()));
		}
	}

	@Override
	protected void init()
	{
		super.init();
		this.clearWidgets();

		if(config == null)
			return;

		int categoryID = 0;
		for(Map.Entry<String, Category> entry : categories.entrySet())
		{
			categoryID++;
			if(!openCategory.isBlank() && !entry.getKey().equals(openCategory))
				continue;

			String type = "category.aperture_innovations."+entry.getKey();
			MutableComponent component = Component.translatable(type);

			int x = (int) (width/2f)-240;
			int y = (int) (height/2f)-196/2 + categoryID*12;

			this.addRenderableWidget(new PlainTextButton(2*x+10, y, Minecraft.getInstance().font.width(component), Minecraft.getInstance().font.lineHeight,
					component, button ->
				{
					if(this.openCategory.equals(entry.getKey()))
						this.openCategory = "";
					else this.openCategory = entry.getKey();
					init();
				}, Minecraft.getInstance().font));

			boolean hasColor = entry.getValue().entries.entrySet().stream().anyMatch(catEntry -> properties.get(catEntry.getKey()) instanceof Color);
			if(hasColor)
			{
				String colorMode = "RGB";
				if(hsbMode)
					colorMode = "HSB";

				String sliderMode = "Text";
				if(colorSliderMode)
					sliderMode = "Slider";

				int position = 2*x-24;
				this.addCategoryWidget(new PlainTextButton(position, y, Minecraft.getInstance().font.width(colorMode),
						Minecraft.getInstance().font.lineHeight, Component.literal(colorMode),
						button ->
							{
								this.hsbMode = !this.hsbMode;
								this.init();
							}, Minecraft.getInstance().font),
						entry.getValue());

				this.addCategoryWidget(new PlainTextButton(position-24, y, Minecraft.getInstance().font.width(sliderMode),
						Minecraft.getInstance().font.lineHeight, Component.literal(sliderMode),
						button ->
							{
								this.colorSliderMode = !this.colorSliderMode;
								this.init();
							}, Minecraft.getInstance().font),
						entry.getValue());
			}

			Category category = entry.getValue();
			if(config instanceof IItemConfiguration)
				makeWidgetForItemProperty(category, x, y);
			else makeWidgetForProperty(category, x, y);
		}
	}

	public void makeWidgetForProperty(Category category, int x, int y)
	{
		int entryID = 0;
		for(Map.Entry<String, CategoryEntry> entry : category.entries.entrySet())
		{
			Optional<ConfigurationProperty<?>> property = config.getConfigurationProperties(Minecraft.getInstance().level.registryAccess()).stream().filter(
					prop -> prop.getName().equals(entry.getValue().name)
									&& prop.getCategory().equals(category.category)).findFirst();
			if(property.isPresent())
			{
				property.get().getInteraction().makeWidget(property.get(), x, y+(entryID)*24+12, this);
				entryID++;
			}
		}
	}

	public void makeWidgetForItemProperty(Category category, int x, int y)
	{
		if(!(renderer instanceof ItemPreviewRenderer itemRenderer))
			return;
		if(!(config instanceof IItemConfiguration configuration))
			return;

		int entryID = 0;
		for(Map.Entry<String, CategoryEntry> entry : category.entries.entrySet())
		{
			Optional<ConfigurationProperty<?>> property = configuration.getConfigurationProperties(itemRenderer.stack, Minecraft.getInstance().level.registryAccess()).stream().filter(
					prop -> prop.getName().equals(entry.getValue().name)
									&& prop.getCategory().equals(category.category)).findFirst();
			if(property.isPresent())
			{
				property.get().getInteraction().makeWidget(property.get(), x, y+(entryID)*24+12, this);
				entryID++;
			}
		}
	}

	@Override
	public void onClose()
	{
		super.onClose();
		if(Minecraft.getInstance().player != null)
			Minecraft.getInstance().player.inventoryMenu.broadcastChanges();

		if(this.config == null)
			return;

		for(ConfigurationProperty<?> property : this.config.getConfigurationProperties(Minecraft.getInstance().level.registryAccess()))
		{
			Object value = properties.get(property.getName());
			if(renderer instanceof BlockEntityPreviewRenderer blockEntityPreviewRenderer)
				PacketDistributor.sendToServer(new ServerboundMultiToolApplyBlockEntityPacket(blockEntityPreviewRenderer.blockEntity.getBlockPos(),
						property.getName(), property.getType(), value));
			if(renderer instanceof EntityPreviewRenderer entityPreviewRenderer)
				PacketDistributor.sendToServer(new ServerboundMultiToolApplyEntityPacket(entityPreviewRenderer.entity.getUUID(),
						property.getName(), property.getType(), value));
		}
		if(config instanceof IItemConfiguration itemConfiguration)
		{
			if(renderer instanceof ItemPreviewRenderer itemPreviewRenderer)
				PacketDistributor.sendToServer(new ServerboundMultiToolApplyItemStackPacket(itemPreviewRenderer.stack,
						itemPreviewRenderer.hand == InteractionHand.MAIN_HAND));

		}
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		renderBackground(graphics, mouseX, mouseY, partialTick);
		PoseStack poseStack = graphics.pose();
		poseStack.pushPose();

		poseStack.pushPose();
		poseStack.translate(width/2f, height/2f, 0);

		Color color = Color.fromInt(mainColor);

		graphics.setColor(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
		graphics.blit(getMenuTexture(), -227/2, -196/2, 0, 0, 227, 196, 227, 227);
		graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

		color = Color.fromInt(glowColor);
		graphics.setColor(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
		graphics.blit(getMenuInsideTexture(), -227/2, -196/2, 0, 0, 227, 196, 227, 227);
		graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

		poseStack.popPose();

		for(Renderable renderable : renderables)
		{
			renderable.render(graphics, mouseX, mouseY, partialTick);
		}

		poseStack.translate(this.width / 2f, this.height / 2f, 0);
		this.renderer.render(graphics, poseStack, mouseX, mouseY, partialTick);

		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(width/2f, height/2f, 0);
		poseStack.translate(0, -196/2f, 0);
		int length = Minecraft.getInstance().font.width(title);
		int height = Minecraft.getInstance().font.lineHeight;
		float scale = 209f/length;
		if(scale < 1)
			poseStack.scale(scale, scale, 1);
		else scale = 1;

		graphics.drawString(Minecraft.getInstance().font, title,
				-length/2, (int) (height/scale)-height/4, (glowColor == -1 || glowColor == 16777215) ? 0x0000FF : glowColor, false);

		poseStack.popPose();
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	public String getOpenCategory()
	{
		return openCategory;
	}

	public void setOpenCategory(String openCategory)
	{
		this.openCategory = openCategory;
	}

	public <T extends GuiEventListener & Renderable & NarratableEntry> void addCategoryWidget(T widget,
																							  Category category)
	{
		this.categoryWidgets.computeIfAbsent(category, ctg -> new ArrayList<>()).add(widget);
		if(this.openCategory.equals(category.category))
		{
			super.addRenderableWidget(widget);
		}
	}

	public ResourceLocation getMenuTexture()
	{
		return variant.menuTexture();
	}

	public ResourceLocation getMenuInsideTexture()
	{
		if(glowColor == -1 || glowColor == 16777215)
			return variant.menuInsideTexture();
		return variant.genericMenuInsideTexture();
	}

	public void renderStretchedButton(GuiGraphics graphics, ResourceLocation texture,
									  int x, int y, int width, int height,
									  int u, int v, int rawWidth, int rawHeight,
									  int topBorder, int bottomBorder, int leftBorder, int rightBorder) {

		int midW = width - leftBorder - rightBorder;
		int midH = height - topBorder - bottomBorder;

		int rawMidW = rawWidth - leftBorder - rightBorder;
		int rawMidH = rawHeight - topBorder - bottomBorder;

		int texRes = 227;

		graphics.blit(texture, x, y, leftBorder, topBorder, u, v, leftBorder, topBorder, texRes, texRes);
		graphics.blit(texture, x + width - rightBorder, y, rightBorder, topBorder, u + rawWidth - rightBorder, v, rightBorder, topBorder, texRes, texRes);
		graphics.blit(texture, x, y + height - bottomBorder, leftBorder, bottomBorder, u, v + rawHeight - bottomBorder, leftBorder, bottomBorder, texRes, texRes);
		graphics.blit(texture, x + width - rightBorder, y + height - bottomBorder, rightBorder, bottomBorder, u + rawWidth - rightBorder, v + rawHeight - bottomBorder, rightBorder, bottomBorder, texRes, texRes);

		graphics.blit(texture, x + leftBorder, y, midW, topBorder, u + leftBorder, v, rawMidW, topBorder, texRes, texRes);
		graphics.blit(texture, x + leftBorder, y + height - bottomBorder, midW, bottomBorder, u + leftBorder, v + rawHeight - bottomBorder, rawMidW, bottomBorder, texRes, texRes);
		graphics.blit(texture, x, y + topBorder, leftBorder, midH, u, v + topBorder, leftBorder, rawMidH, texRes, texRes);
		graphics.blit(texture, x + width - rightBorder, y + topBorder, rightBorder, midH, u + rawWidth - rightBorder, v + topBorder, rightBorder, rawMidH, texRes, texRes);

		graphics.blit(texture, x + leftBorder, y + topBorder, midW, midH, u + leftBorder, v + topBorder, rawMidW, rawMidH, texRes, texRes);
	}
	public record CategoryEntry(String name, String translatable)
	{

	}

	public record Category(String category, HashMap<String, CategoryEntry> entries)
	{

	}
}
