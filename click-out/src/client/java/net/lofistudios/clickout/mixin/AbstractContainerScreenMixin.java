package net.lofistudios.clickout.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Click Out - closes a container screen when the player clicks outside it.
 *
 * <p>The injection point is deliberate, not incidental. AbstractContainerScreen#mouseClicked
 * gives the screen's own widgets first refusal through super.mouseClicked, and only then asks
 * hasClickedOutside. Injecting at HEAD would close the screen on a click that landed on the
 * creative-inventory tabs or the recipe book toggle, because both sit outside the main panel
 * while still being part of the screen. Injecting immediately before the hasClickedOutside call
 * means every widget has already declined the click.
 *
 * <p>"Outside" is asked of vanilla rather than computed here. AbstractRecipeBookScreen overrides
 * hasClickedOutside to accept the recipe book as well, so asking the virtual method is what makes
 * the recipe book work for free.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

	/**
	 * MouseButtonEvent carries 1-based button codes: 1 is primary, 2 is middle, 3 is secondary.
	 * Confirmed against AbstractContainerScreen#getContainerClickButton and #mouseClicked, both of
	 * which test 1 and 3. It is not the GLFW 0/1/2 layout, so do not "fix" this to 0.
	 */
	private static final int PRIMARY_BUTTON = 1;

	@Shadow
	protected int leftPos;

	@Shadow
	protected int topPos;

	// No @Shadow for `minecraft` on purpose. It is declared on Screen, not on
	// AbstractContainerScreen, and a @Shadow field does not resolve up the hierarchy -
	// the game dies with "@Shadow field minecraft was not located in the target class".
	// Minecraft.getInstance() is the same object and needs no shadow at all.

	@Shadow
	protected abstract boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop);

	@Shadow
	public abstract void onClose();

	@Inject(
		method = "mouseClicked",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;hasClickedOutside(DDII)Z",
			shift = At.Shift.BEFORE
		),
		cancellable = true
	)
	private void clickout$closeOnOutsideClick(MouseButtonEvent event, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
		if (event.button() != PRIMARY_BUTTON) {
			return;
		}

		if (!this.hasClickedOutside(event.x(), event.y(), this.leftPos, this.topPos)) {
			return;
		}

		// Carrying a stack means this click is vanilla's "drop it outside the window"
		// gesture. Closing here would silently eat the stack, so let vanilla have it.
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || !player.containerMenu.getCarried().isEmpty()) {
			return;
		}

		this.onClose();
		cir.setReturnValue(true);
	}
}
