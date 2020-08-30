package themcbros.tmcb_lib.crafting;

import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShapedFluidRecipe extends ShapedRecipe {

    public static final IRecipeType<ShapedFluidRecipe> RECIPE_TYPE;
    public static final IRecipeSerializer<ShapedFluidRecipe> RECIPE_SERIALIZER;

    static {
        RECIPE_TYPE = IRecipeType.register("tmcb_lib:fluid_shaped");
        RECIPE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, "tmcb_lib:fluid_shaped", new Serializer());
    }

    private final int amount;

    public ShapedFluidRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, int amount) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
        this.amount = amount;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < itemStacks.size(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            FluidUtil.getFluidHandler(item).ifPresent(handlerItem -> handlerItem.drain(this.amount, IFluidHandler.FluidAction.EXECUTE));
            if (item.hasContainerItem()) {
                itemStacks.set(i, item.getContainerItem());
            }
        }

        return itemStacks;
    }

    @Override
    public IRecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RECIPE_SERIALIZER;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedFluidRecipe> {
        public ShapedFluidRecipe read(ResourceLocation recipeId, JsonObject json) {
            String s = JSONUtils.getString(json, "group", "");
            Map<String, Ingredient> map = ShapedRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] astring = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = ShapedRecipe.deserializeIngredients(astring, map, i, j);
            ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            int amount = JSONUtils.getInt(json, "amount", FluidAttributes.BUCKET_VOLUME);
            return new ShapedFluidRecipe(recipeId, s, i, j, nonnulllist, itemstack, amount);
        }

        public ShapedFluidRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            String s = buffer.readString(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for(int k = 0; k < ingredients.size(); ++k) {
                ingredients.set(k, Ingredient.read(buffer));
            }

            ItemStack itemstack = buffer.readItemStack();
            int amount = buffer.readVarInt();
            return new ShapedFluidRecipe(recipeId, s, i, j, ingredients, itemstack, amount);
        }

        public void write(PacketBuffer buffer, ShapedFluidRecipe recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeString(recipe.getGroup());

            for(Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getRecipeOutput());
            buffer.writeVarInt(recipe.amount);
        }
    }
}
