package themcbros.tmcb_lib.util;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidCraftingHelper {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static FluidStack getFluidStack(JsonObject json, boolean readNBT) {
        String fluidName = JSONUtils.getString(json, "fluid");

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));

        if (fluid == null)
            throw new JsonSyntaxException("Unknown fluid '" + fluidName + "'");

        if (readNBT && json.has("nbt")) {
            // Lets hope this works? Needs test
            try {
                JsonElement element = json.get("nbt");
                CompoundNBT nbt;
                if (element.isJsonObject())
                    nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                else
                    nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(element, "nbt"));

                CompoundNBT tmp = new CompoundNBT();

                tmp.put("Tag", nbt);
                tmp.putString("FluidName", fluidName);
                tmp.putInt("Amount", JSONUtils.getInt(json, "amount", FluidAttributes.BUCKET_VOLUME));

                return FluidStack.loadFluidStackFromNBT(tmp);
            } catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
            }
        }

        return new FluidStack(fluid, JSONUtils.getInt(json, "amount", FluidAttributes.BUCKET_VOLUME));
    }

}
