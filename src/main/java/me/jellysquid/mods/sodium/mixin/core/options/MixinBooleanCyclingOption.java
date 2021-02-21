package me.jellysquid.mods.sodium.mixin.core.options;

import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;

@Mixin(CyclingOption.class)
public interface MixinBooleanCyclingOption {
    @Accessor("getter")
    public Function<GameOptions, Boolean> getGetter();

    @Accessor("setter")
    public CyclingOption.class_5675<Boolean> getSetter();
}

