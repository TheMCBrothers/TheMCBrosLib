// This class is mostly from Mantle with some changes made to fit in our library
//
// MIT License
//
// Copyright (c) 2022 SlimeKnights
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package net.themcbrothers.lib.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.themcbrothers.lib.TheMCBrosLib;
import net.themcbrothers.lib.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Ingredient that matches a container of fluid
 */
public class FluidContainerIngredient extends AbstractIngredient {
    public static final ResourceLocation ID = TheMCBrosLib.rl("fluid_container");
    public static final Serializer SERIALIZER = new Serializer();

    /**
     * Ingredient to use for matching
     */
    private final FluidIngredient fluidIngredient;
    /**
     * Internal ingredient to display the ingredient recipe viewers
     */
    @Nullable
    private final Ingredient display;
    private ItemStack[] displayStacks;

    public FluidContainerIngredient(FluidIngredient fluidIngredient, @Nullable Ingredient display) {
        this.fluidIngredient = fluidIngredient;
        this.display = display;
    }

    /**
     * Creates an instance from a fluid ingredient with a display container
     */
    public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient, Ingredient display) {
        return new FluidContainerIngredient(ingredient, display);
    }

    /**
     * Creates an instance from a fluid ingredient with no display, not recommended
     */
    public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient) {
        return new FluidContainerIngredient(ingredient, null);
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        // first, must have a fluid capability
        return stack != null && !stack.isEmpty() && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().flatMap(cap -> {
            // second, must contain enough fluid
            if (cap.getTanks() == 1) {
                FluidStack contained = cap.getFluidInTank(0);
                if (!contained.isEmpty() && this.fluidIngredient.getAmount(contained.getFluid()) == contained.getAmount() && this.fluidIngredient.test(contained.getFluid())) {
                    // so far so good, from this point on we are forced to make copies as we need to try draining, so copy and fetch the copy's cap
                    ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
                    return copy.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                }
            }
            return Optional.empty();
        }).filter(cap -> {
            // alright, we know it has the fluid, the question is just whether draining the fluid will give us the desired result
            Fluid fluid = cap.getFluidInTank(0).getFluid();
            int amount = this.fluidIngredient.getAmount(fluid);
            FluidStack drained = cap.drain(amount, IFluidHandler.FluidAction.EXECUTE);
            // we need an exact match, and we need the resulting container item to be the same as the item stack's container item
            return drained.getFluid() == fluid && drained.getAmount() == amount && ItemStack.matches(stack.getCraftingRemainingItem(), cap.getContainer());
        }).isPresent();
    }

    @Override
    public ItemStack[] getItems() {
        if (this.displayStacks == null) {
            // no container? unfortunately hard to display this recipe so show nothing
            if (this.display == null) {
                this.displayStacks = new ItemStack[0];
            } else {
                this.displayStacks = this.display.getItems();
            }
        }

        return this.displayStacks;
    }

    @Override
    protected void invalidate() {
        super.invalidate();
        this.displayStacks = null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public JsonElement toJson() {
        JsonElement element = this.fluidIngredient.serialize();
        JsonObject json;

        if (element.isJsonObject()) {
            json = element.getAsJsonObject();
        } else {
            json = new JsonObject();
            json.add("fluid", element);
        }

        json.addProperty("type", ID.toString());

        if (this.display != null) {
            json.add("display", this.display.toJson());
        }

        return json;
    }

    /**
     * Serializer logic
     */
    private static class Serializer implements IIngredientSerializer<FluidContainerIngredient> {
        @Override
        public FluidContainerIngredient parse(FriendlyByteBuf buffer) {
            FluidIngredient fluidIngredient = FluidIngredient.read(buffer);
            Ingredient display = null;
            if (buffer.readBoolean()) {
                display = Ingredient.fromNetwork(buffer);
            }
            return new FluidContainerIngredient(fluidIngredient, display);
        }

        @Override
        public FluidContainerIngredient parse(JsonObject json) {
            FluidIngredient fluidIngredient;
            // If we have fluid, it's a nested ingredient. Otherwise, this object itself is the ingredient
            if (json.has("fluid")) {
                fluidIngredient = FluidIngredient.deserialize(json, "fluid");
            } else {
                fluidIngredient = FluidIngredient.deserialize((JsonElement) json, "fluid");
            }
            Ingredient display = null;
            if (json.has("display")) {
                display = Ingredient.fromJson(JsonHelper.getElement(json, "display"));
            }
            return new FluidContainerIngredient(fluidIngredient, display);
        }

        @Override
        public void write(FriendlyByteBuf buffer, FluidContainerIngredient ingredient) {
            ingredient.fluidIngredient.write(buffer);
            if (ingredient.display != null) {
                buffer.writeBoolean(true);
                ingredient.display.toNetwork(buffer);
            } else {
                buffer.writeBoolean(false);
            }
        }
    }
}
