package net.mistersecret312.aperture_innovations.client.screen.inputs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class DropdownWidget extends AbstractButton
{
    private final List<String> options;
    private String selected;
    private boolean expanded = false;
    private int scrollOffset = 0;
    private final int maxVisibleOptions = 5;
    private final Consumer<String> onSelect;

    public DropdownWidget(int x, int y, int width, int height, List<String> options, String defaultOption, Consumer<String> onSelect)
    {
        super(x, y, width, height, Component.literal(defaultOption));
        this.options = options;
        this.selected = defaultOption;
        this.onSelect = onSelect;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        if (this.expanded)
        {
            int dropDownHeight = Math.min(options.size(), maxVisibleOptions) * this.height;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 500);

            guiGraphics.fill(this.getX(), this.getY() + this.height, this.getX() + this.width, this.getY() + this.height + dropDownHeight, 0xFF000000);
            guiGraphics.renderOutline(this.getX(), this.getY() + this.height, this.width, dropDownHeight, 0xFFAAAAAA);

            Font font = Minecraft.getInstance().font;
            int startIdx = this.scrollOffset;
            int endIdx = Math.min(this.options.size(), startIdx + maxVisibleOptions);

            for (int i = startIdx; i < endIdx; i++)
            {
                int itemY = this.getY() + this.height + (i - startIdx) * this.height;
                
                boolean isHovered = mouseX >= this.getX() && mouseX < this.getX() + this.width &&
                                    mouseY >= itemY && mouseY < itemY + this.height;

                if (isHovered)
                {
                    guiGraphics.fill(this.getX() + 1, itemY + 1, this.getX() + this.width - 1, itemY + this.height - 1, 0xFF555555);
                }

                Component component = Component.translatable("multi_tool."+options.get(i).replace(':', '.'));
                guiGraphics.drawCenteredString(font, component, this.getX() + this.width / 2, itemY + (this.height - font.lineHeight) / 2, 0xFFFFFF);
            }

            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void onPress()
    {
        this.expanded = !this.expanded;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (!this.active || !this.visible)
            return false;

        if (this.expanded)
        {
            int dropDownHeight = Math.min(options.size(), maxVisibleOptions) * this.height;
            boolean inDropdown = mouseX >= this.getX() && mouseX < this.getX() + this.width &&
                                 mouseY >= this.getY() + this.height && mouseY < this.getY() + this.height + dropDownHeight;

            if (inDropdown)
            {
                int clickedIndex = this.scrollOffset + (int) ((mouseY - (this.getY() + this.height)) / this.height);
                if (clickedIndex >= 0 && clickedIndex < this.options.size())
                {
                    this.selected = this.options.get(clickedIndex);
                    if (this.onSelect != null)
                    {
                        this.onSelect.accept(this.selected);
                    }
                    
                    this.expanded = false;
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    return true; // Click consumed
                }
            }
            else if (!this.isHovered())
            {
                this.expanded = false;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.expanded && this.options.size() > maxVisibleOptions)
        {
            if (scrollY > 0)
            {
                this.scrollOffset = Math.max(0, this.scrollOffset - 1);
            }
            else if (scrollY < 0)
            {
                this.scrollOffset = Math.min(this.options.size() - maxVisibleOptions, this.scrollOffset + 1);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
    {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}