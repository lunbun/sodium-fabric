package me.jellysquid.mods.sodium.mixin.features.texture_tracking;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.texture.SpriteExtended;
import me.jellysquid.mods.sodium.client.render.texture.SpriteUpdateForcible;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Sprite.class)
public abstract class MixinSprite implements SpriteExtended {
    @Shadow @Final
    // private Sprite.Animation animation
    private Sprite.class_5790 field_28468;

    @Override
    public void markActive() {
        if (this.field_28468 instanceof SpriteUpdateForcible) {
            ((SpriteUpdateForcible) this.field_28468).setForceNextUpdate(true);
        }
    }

    @Mixin(targets = "net.minecraft.client.texture.Sprite.class_5790")
    public static abstract class MixinSpriteAnimation implements SpriteUpdateForcible {
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
        // private int frameTicks
        private int field_28471;

        @Shadow
        // private int frameIndex
        private int field_28470;

        @Shadow @Final
        // private int frameCount
        private int field_28473;

        @Shadow
        // protected abstract void upload(int frameIndex)
        protected abstract void method_33455(int frameIndex);

        @Shadow @Final
        private Sprite.Interpolation field_28474;

        @Shadow @Final
        private List<Sprite.class_5791> field_28472;

        /**
         * @author JellySquid
         * @reason Allow conditional texture updating, ported for 21w07a by Lunbun
         */
        @Overwrite
        public void tick() {
            ++this.field_28471;

            boolean onDemand = SodiumClientMod.options().advanced.animateOnlyVisibleTextures;

            if (!onDemand || this.forceNextUpdate) {
                this.uploadTexture();
            }
        }

        private void uploadTexture() {
            if (this.field_28471 >= this.getFrameTime(this.field_28470)) {
                int prevFrameIndex = this.getFrameIndex(this.field_28470);
                int frameCount = this.field_28473 == 0 ? this.field_28473 : this.field_28473;

                this.field_28470 = (this.field_28470 + 1) % frameCount;
                this.field_28471 = 0;

                int frameIndex = this.getFrameIndex(this.field_28470);

                if (prevFrameIndex != frameIndex && frameIndex >= 0 && frameIndex < this.field_28473) {
                    this.method_33455(frameIndex);
                }
            } else if (this.field_28474 != null) {
                if (!RenderSystem.isOnRenderThread()) {
                    RenderSystem.recordRenderCall(this::updateInterpolatedTexture);
                } else {
                    this.updateInterpolatedTexture();
                }
            }

            this.forceNextUpdate = false;
        }

        private void updateInterpolatedTexture() {
            this.field_28474.apply((Sprite.class_5790) ((Object) this));
        }

        private Sprite.class_5791 getFrame(int frameIndex) {
            return this.field_28472.get(frameIndex);
        }

        private int getFrameTime(int frameIndex) {
            return getFrame(frameIndex).field_28476;
        }

        private int getFrameIndex(int frameIndex) {
            return getFrame(frameIndex).field_28475;
        }
    }
}