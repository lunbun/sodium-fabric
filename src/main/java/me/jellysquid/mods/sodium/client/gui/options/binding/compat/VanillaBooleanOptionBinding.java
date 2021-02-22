package me.jellysquid.mods.sodium.client.gui.options.binding.compat;

import me.jellysquid.mods.sodium.client.gui.options.binding.OptionBinding;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.client.options.GameOptions;

public class VanillaBooleanOptionBinding implements OptionBinding<GameOptions, Boolean> {
    private final CyclingOption<Boolean> option;

    public VanillaBooleanOptionBinding(CyclingOption<Boolean> option) {
        this.option = option;
    }

    @Override
    public void setValue(GameOptions storage, Boolean value) {
        this.option.setter.accept(storage, this.option, value);
    }

    @Override
    public Boolean getValue(GameOptions storage) {
        return this.option.getter.apply(storage);
    }
}
