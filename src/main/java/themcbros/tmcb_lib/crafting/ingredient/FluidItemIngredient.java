package themcbros.tmcb_lib.crafting.ingredient;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import themcbros.tmcb_lib.util.FluidCraftingHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class FluidItemIngredient extends Ingredient {

    private final Fluid fluid;
    private final int amount;

    protected FluidItemIngredient(FluidStack fluidStack) {
        super(Stream.of(new FluidItemList(fluidStack)));
        this.fluid = fluidStack.getFluid();
        this.amount = fluidStack.getAmount();
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null) return false;
        return FluidUtil.getFluidHandler(input).map(fluidHandlerItem -> {
            FluidStack fluidStack = fluidHandlerItem.getFluidInTank(0);
            return fluidStack.getFluid() == this.fluid && fluidStack.getAmount() >= this.amount;
        }).orElse(false);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", Objects.requireNonNull(CraftingHelper.getID(NBTIngredient.Serializer.INSTANCE)).toString());
        json.addProperty("fluid", Objects.requireNonNull(this.fluid.getRegistryName()).toString());
        json.addProperty("amount", this.amount);
        return json;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements IIngredientSerializer<FluidItemIngredient> {
        public static Serializer INSTANCE = new Serializer();

        @Override
        public FluidItemIngredient parse(PacketBuffer buffer) {
            return new FluidItemIngredient(buffer.readFluidStack());
        }

        @Override
        public FluidItemIngredient parse(JsonObject json) {
            return new FluidItemIngredient(FluidCraftingHelper.getFluidStack(json, true));
        }

        @Override
        public void write(PacketBuffer buffer, FluidItemIngredient ingredient) {
            buffer.writeFluidStack(new FluidStack(ingredient.fluid, ingredient.amount));
        }
    }

    public static class FluidItemList implements IItemList {

        private final Collection<ItemStack> stacks;
        private final Fluid fluid;
        private final int amount;

        public FluidItemList(FluidStack fluidStack) {
            this.fluid = fluidStack.getFluid();
            this.amount = fluidStack.getAmount();
            this.stacks = Lists.newArrayList();
            for (Item item : ForgeRegistries.ITEMS) {
                NonNullList<ItemStack> items = NonNullList.create();
                item.fillItemGroup(ItemGroup.SEARCH, items);
                for (ItemStack stack : items) {
                    FluidStack fluidStack1 = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
                    if (fluidStack1.getFluid() == this.fluid && fluidStack1.getAmount() >= this.amount)
                        this.stacks.add(stack);
                }
            }
        }

        @Override
        public Collection<ItemStack> getStacks() {
            return this.stacks;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("fluid", Objects.requireNonNull(this.fluid.getRegistryName()).toString());
            jsonobject.addProperty("amount", this.amount);
            return jsonobject;
        }
    }

}
