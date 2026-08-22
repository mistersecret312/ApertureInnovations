package net.mistersecret312.aperture_innovations.client.screen.inputs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SteppedSliderWidget extends AbstractSliderButton
{
    public double min;
    public double max;
    public double step;
    public String prefix;
    
    @Nullable
    private Consumer<Double> onValueChanged;

    public SteppedSliderWidget(int x, int y, int width, int height, Component prefix,
                               double min, double max, double step, double initialValue,
                               @Nullable Consumer<Double> onValueChanged)
    {

        super(x, y, width, height, Component.empty(), Mth.clamp((initialValue - min) / (max - min), 0.0D, 1.0D));
        
        this.min = min;
        this.max = max;
        this.step = step;
        this.prefix = prefix.getString();
        this.onValueChanged = onValueChanged;
        
        this.updateMessage();
    }

    public void setValue(double value)
    {
        value = value/max;
        double d0 = this.value;
        this.value = Mth.clamp(value, 0.0, 1.0);
        if (d0 != this.value)
            this.applyValue();

        this.updateMessage();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);

        double value = getActualValue();

        String formatted = (this.step % 1 == 0) ? String.format("%.0f", value) : String.format("%.2f", value);
//        graphics.drawString(Minecraft.getInstance().font, prefix+": "+formatted, this.getX()+2, this.getY()+height, 16777215);
    }

    public void setOnValueChanged(@Nullable Consumer<Double> onValueChanged)
    {
        this.onValueChanged = onValueChanged;
    }

    public double getActualValue()
    {
        double rawValue = this.min + (this.max - this.min) * this.value;
        return snapToStep(rawValue);
    }

    private double snapToStep(double val)
    {
        if (this.step <= 0.0D)
            return Mth.clamp(val, this.min, this.max);

        double snapped = Math.round((val - this.min) / this.step) * this.step + this.min;
        return Mth.clamp(snapped, this.min, this.max);
    }

    @Override
    protected void updateMessage()
    {
		this.setMessage(Component.empty());
    }

    @Override
    protected void applyValue()
    {
        double actualValue = getActualValue();
        this.value = (actualValue - this.min) / (this.max - this.min);

        if (this.onValueChanged != null)
            this.onValueChanged.accept(actualValue);
    }
}