package me.jellysquid.mods.sodium.mixin.features.texture_tracking;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.texture.SpriteExtended;
import me.jellysquid.mods.sodium.client.render.texture.SpriteUpdateForcable;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureTickListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Sprite.class)
public abstract class MixinSprite implements SpriteExtended {
    @Shadow @Final
    private Sprite.Animation animation;

    @Override
    public void markActive() {
        if (this.animation instanceof SpriteUpdateForcable) {
            ((SpriteUpdateForcable) this.animation).setForceNextUpdate(true);
        }
    }

    @Mixin(targets = "net.minecraft.client.texture.Sprite.Animation")
    public static abstract class MixinSpriteAnimation implements SpriteUpdateForcable {
        private boolean forceNextUpdate;

        @Override
        public void setForceNextUpdate(boolean forceNextUpdate) {
            this.forceNextUpdate = forceNextUpdate;
        }

        @Override
        public boolean getForceNextUpdate() {
            return this.forceNextUpdate;
        }

        @Shadow
        private int frameTicks;

        @Shadow
        private int frameIndex;

        @Shadow @Final
        private int frameCount;

        @Shadow
        protected abstract void upload(int frameIndex);

        @Shadow @Final
        private Sprite.Interpolation interpolation;

        @Shadow @Final
        private List<Sprite.AnimationFrame> frames;

        /**
         * @author JellySquid
         * @reason Allow conditional texture updating, ported for 21w07a by Lunbun
         */
        @Overwrite
        public void tick() {
            ++this.frameTicks;

            boolean onDemand = SodiumClientMod.options().advanced.animateOnlyVisibleTextures;

            if (!onDemand || this.forceNextUpdate) {
                this.uploadTexture();
            }
        }

        private void uploadTexture() {
            if (this.frameTicks >= this.getFrameTime(this.frameIndex)) {
                int prevFrameIndex = this.getFrameIndex(this.frameIndex);
                int frameCount = this.frameCount == 0 ? this.frameCount : this.frameCount;

                this.frameIndex = (this.frameIndex + 1) % frameCount;
                this.frameTicks = 0;

                int frameIndex = this.getFrameIndex(this.frameIndex);

                if (prevFrameIndex != frameIndex && frameIndex >= 0 && frameIndex < this.frameCount) {
                    this.upload(frameIndex);
                }
            } else if (this.interpolation != null) {
                if (!RenderSystem.isOnRenderThread()) {
                    RenderSystem.recordRenderCall(this::updateInterpolatedTexture);
                } else {
                    this.updateInterpolatedTexture();
                }
            }

            this.forceNextUpdate = false;
        }

        private void updateInterpolatedTexture() {
            this.interpolation.apply((Sprite.Animation) ((Object) this));
        }

        private Sprite.AnimationFrame getFrame(int frameIndex) {
            return this.frames.get(frameIndex);
        }

        private int getFrameTime(int frameIndex) {
            return getFrame(frameIndex).time;
        }

        private int getFrameIndex(int frameIndex) {
            return getFrame(frameIndex).index;
        }
    }
}
