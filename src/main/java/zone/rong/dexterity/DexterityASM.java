package zone.rong.dexterity;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class DexterityASM implements Runnable {

    public static final String INTERMEDIARY = "intermediary";
    public static final MappingResolver REMAPPER = FabricLoader.getInstance().getMappingResolver();

    @Override
    public void run() {
        // ClassTinkerers.addTransformation(REMAPPER.mapClassName(INTERMEDIARY, "net.minecraft.client.gui.hud.InGameHud"), this::transformInGameHud);
        // ClassTinkerers.addTransformation(REMAPPER.mapClassName(INTERMEDIARY, "net.minecraft.entity.passive.AnimalEntity"), this::insertStaticFieldsToAnimalEntity);
    }

    /*
    private void transformRecipeUnlocker(ClassNode clazz) {
        String shouldCraftRecipe = REMAPPER.mapMethodName(INTERMEDIARY, "net.minecraft.recipe.RecipeUnlocker", "shouldCraftRecipe", "(Lnet/minecraft/world/World;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/recipe/Recipe;)Z");
        for (MethodNode method : clazz.methods) {
            if (method.name.equals(shouldCraftRecipe)) {
                System.out.println("Found ya!");
                int count = 0;
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction.getOpcode() == Opcodes.INVOKEINTERFACE) {
                        ++count;
                        if (count == 2) {
                            MethodInsnNode call = new MethodInsnNode(Opcodes.INVOKESTATIC, "xyz/rongmario/dexterity/DexterityASM", "test", "(Lnet/minecraft/recipe/Recipe;Lnet/minecraft/entity/player/PlayerEntity;)V", false);
                            method.instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 3));
                            method.instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 2));
                            method.instructions.insertBefore(instruction, call);
                        }
                    }
                }
            }
        }
    }

    public static void test(Recipe<?> recipe, PlayerEntity player) {
        DexterityMain.LOGGER.info("Recipe: {}", recipe.getOutput());
        DexterityMain.LOGGER.info("Transformed! Here is the Player's Name: {}", player.getUuid());
    }
     */

}
