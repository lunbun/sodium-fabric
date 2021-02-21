package me.jellysquid.mods.sodium.client.gui.options.binding.compat;

import me.jellysquid.mods.sodium.client.gui.options.binding.OptionBinding;
import me.jellysquid.mods.sodium.mixin.core.options.MixinBooleanCyclingOption;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.GameOptions;

public class VanillaBooleanOptionBinding implements OptionBinding<GameOptions, Boolean> {
    private final CyclingOption<Boolean> option;

    public VanillaBooleanOptionBinding(CyclingOption<Boolean> option) {
        this.option = option;
    }

    @Override
    public void setValue(GameOptions storage, Boolean value) {
        ((MixinBooleanCyclingOption) this.option).getSetter().accept(storage, this.option, value);
    }

    @Override
    public Boolean getValue(GameOptions storage) {
        return ((MixinBooleanCyclingOption) this.option).getGetter().apply(storage);
    }
}
