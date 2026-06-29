package com.github.ob_yekt.simpleqol;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class simpleqolRecipeGenerator extends FabricRecipeProvider {
    public simpleqolRecipeGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                // Wood recipes for all wood types - includes logs, wood, stripped variants, signs, and shelves
                ItemLike[][] woodRecipes = {
                        // {log, wood, stripped_log, stripped_wood, planks, slab, stairs, fence, fence_gate, door, trapdoor, sign, shelf}
                        {Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD, Blocks.ACACIA_PLANKS, Blocks.ACACIA_SLAB, Blocks.ACACIA_STAIRS, Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_DOOR, Blocks.ACACIA_TRAPDOOR, Items.ACACIA_SIGN, Items.ACACIA_SHELF},
                        {Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD, Blocks.BIRCH_PLANKS, Blocks.BIRCH_SLAB, Blocks.BIRCH_STAIRS, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_DOOR, Blocks.BIRCH_TRAPDOOR, Items.BIRCH_SIGN, Items.BIRCH_SHELF},
                        {Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_CHERRY_WOOD, Blocks.CHERRY_PLANKS, Blocks.CHERRY_SLAB, Blocks.CHERRY_STAIRS, Blocks.CHERRY_FENCE, Blocks.CHERRY_FENCE_GATE, Blocks.CHERRY_DOOR, Blocks.CHERRY_TRAPDOOR, Items.CHERRY_SIGN, Items.CHERRY_SHELF},
                        {Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_STAIRS, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_DOOR, Blocks.CRIMSON_TRAPDOOR, Items.CRIMSON_SIGN, Items.CRIMSON_SHELF},
                        {Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_TRAPDOOR, Items.DARK_OAK_SIGN, Items.DARK_OAK_SHELF},
                        {Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_SLAB, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_TRAPDOOR, Items.JUNGLE_SIGN, Items.JUNGLE_SHELF},
                        {Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_SLAB, Blocks.MANGROVE_STAIRS, Blocks.MANGROVE_FENCE, Blocks.MANGROVE_FENCE_GATE, Blocks.MANGROVE_DOOR, Blocks.MANGROVE_TRAPDOOR, Items.MANGROVE_SIGN, Items.MANGROVE_SHELF},
                        {Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD, Blocks.OAK_PLANKS, Blocks.OAK_SLAB, Blocks.OAK_STAIRS, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.OAK_DOOR, Blocks.OAK_TRAPDOOR, Items.OAK_SIGN, Items.OAK_SHELF},
                        {Blocks.PALE_OAK_LOG, Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_WOOD, Blocks.PALE_OAK_PLANKS, Blocks.PALE_OAK_SLAB, Blocks.PALE_OAK_STAIRS, Blocks.PALE_OAK_FENCE, Blocks.PALE_OAK_FENCE_GATE, Blocks.PALE_OAK_DOOR, Blocks.PALE_OAK_TRAPDOOR, Items.PALE_OAK_SIGN, Items.PALE_OAK_SHELF},
                        {Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_TRAPDOOR, Items.SPRUCE_SIGN, Items.SPRUCE_SHELF},
                        {Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.WARPED_PLANKS, Blocks.WARPED_SLAB, Blocks.WARPED_STAIRS, Blocks.WARPED_FENCE, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_DOOR, Blocks.WARPED_TRAPDOOR, Items.WARPED_SIGN, Items.WARPED_SHELF}
                };

                for (ItemLike[] recipe : woodRecipes) {
                    // For each wood type, add recipes from all 4 input variants (log, wood, stripped_log, stripped_wood)
                    for (int inputIndex = 0; inputIndex < 4; inputIndex++) {
                        ItemLike input = recipe[inputIndex];
                        // Add recipes for non-stripped to stripped variants
                        if (inputIndex == 0) { // Log -> Stripped Log
                            addStonecutterRecipe(input, recipe[2], 1); // e.g., OAK_LOG -> STRIPPED_OAK_LOG
                        } else if (inputIndex == 1) { // Wood -> Stripped Wood
                            addStonecutterRecipe(input, recipe[3], 1); // e.g., OAK_WOOD -> STRIPPED_OAK_WOOD
                        }
                        // Recipes
                        addStonecutterRecipe(input, recipe[4], 4);  // Planks
                        addStonecutterRecipe(input, recipe[5], 8);  // Slabs
                        addStonecutterRecipe(input, recipe[6], 4);  // Stairs
                        addStonecutterRecipe(input, recipe[7], 2);  // Fence
                        addStonecutterRecipe(input, recipe[8], 1);  // Fence Gate
                        addStonecutterRecipe(input, recipe[9], 2);  // Door
                        addStonecutterRecipe(input, recipe[10], 1); // Trapdoor
                        addStonecutterRecipe(input, recipe[11], 2); // Sign
                        addStonecutterRecipe(input, recipe[12], 1); // Shelf
                    }

                    // Additional plank recipes (from planks)
                    ItemLike planks = recipe[4];
                    addStonecutterRecipe(planks, Items.STICK, 2);        // 1 plank = 2 sticks
                    addStonecutterRecipe(planks, recipe[5], 2);          // 1 plank = 2 slabs
                    addStonecutterRecipe(planks, recipe[6], 1);          // 1 plank = 1 stair
                }

                // Improved crafting table recipes (overriding vanilla)
                addImprovedCraftingRecipes();
            }

            private void addStonecutterRecipe(ItemLike input, ItemLike output, int count) {
                SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, output, count)
                        .unlockedBy(getHasName(input), has(input))
                        .save(this.output, "simpleqol:" + getItemName(output) + "_from_" + getItemName(input) + "_stonecutting");
            }

            private void addImprovedCraftingRecipes() {

                // Additional crafting table recipes
                // 1 wool (any color) -> 4 string
                shapeless(RecipeCategory.MISC, Items.STRING, 4)
                        .requires(ItemTags.WOOL)
                        .unlockedBy("has_wool", has(ItemTags.WOOL))
                        .save(this.output, "simpleqol:string_from_wool");

                // For packed ice
                shapeless(RecipeCategory.MISC, Blocks.ICE, 9)
                        .requires(Blocks.PACKED_ICE)
                        .unlockedBy(getHasName(Blocks.PACKED_ICE), has(Blocks.PACKED_ICE))
                        .save(this.output, "simpleqol:ice_from_packed_ice");

                // For blue ice
                shapeless(RecipeCategory.MISC, Blocks.PACKED_ICE, 9)
                        .requires(Blocks.BLUE_ICE)
                        .unlockedBy(getHasName(Blocks.BLUE_ICE), has(Blocks.BLUE_ICE))
                        .save(this.output, "simpleqol:packed_ice_from_blue_ice");

                // For quartz
                shapeless(RecipeCategory.MISC, Items.QUARTZ, 4)
                        .requires(Blocks.QUARTZ_BLOCK)
                        .unlockedBy(getHasName(Blocks.QUARTZ_BLOCK), has(Blocks.QUARTZ_BLOCK))
                        .save(this.output, "simpleqol:quartz_from_quartz_block");

                // === Improved stair crafting recipes (6 stairs instead of 4) ===
                addStairsRecipes();

                // Stone / other stairs
                addImprovedStairRecipe(Blocks.COBBLESTONE, Blocks.COBBLESTONE_STAIRS);
                addImprovedStairRecipe(Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE_STAIRS);
                addImprovedStairRecipe(Blocks.STONE, Blocks.STONE_STAIRS);
                addImprovedStairRecipe(Blocks.BRICKS, Blocks.BRICK_STAIRS);
                addImprovedStairRecipe(Blocks.MUD_BRICKS, Blocks.MUD_BRICK_STAIRS);
                addImprovedStairRecipe(Blocks.SANDSTONE, Blocks.SANDSTONE_STAIRS);
                addImprovedStairRecipe(Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_SANDSTONE_STAIRS);
                addImprovedStairRecipe(Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE_STAIRS);
                addImprovedStairRecipe(Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
                addImprovedStairRecipe(Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_STAIRS);
                addImprovedStairRecipe(Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_QUARTZ_STAIRS);
                addImprovedStairRecipe(Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_STAIRS);
                addImprovedStairRecipe(Blocks.RED_NETHER_BRICKS, Blocks.RED_NETHER_BRICK_STAIRS);
                addImprovedStairRecipe(Blocks.PRISMARINE, Blocks.PRISMARINE_STAIRS);
                addImprovedStairRecipe(Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICK_STAIRS);
                addImprovedStairRecipe(Blocks.DARK_PRISMARINE, Blocks.DARK_PRISMARINE_STAIRS);
                addImprovedStairRecipe(Blocks.BLACKSTONE, Blocks.BLACKSTONE_STAIRS);
                addImprovedStairRecipe(Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_STAIRS);
                addImprovedStairRecipe(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
                addImprovedStairRecipe(Blocks.END_STONE_BRICKS, Blocks.END_STONE_BRICK_STAIRS);
                addImprovedStairRecipe(Blocks.GRANITE, Blocks.GRANITE_STAIRS);
                addImprovedStairRecipe(Blocks.POLISHED_GRANITE, Blocks.POLISHED_GRANITE_STAIRS);
                addImprovedStairRecipe(Blocks.DIORITE, Blocks.DIORITE_STAIRS);
                addImprovedStairRecipe(Blocks.POLISHED_DIORITE, Blocks.POLISHED_DIORITE_STAIRS);
                addImprovedStairRecipe(Blocks.ANDESITE, Blocks.ANDESITE_STAIRS);
                addImprovedStairRecipe(Blocks.POLISHED_ANDESITE, Blocks.POLISHED_ANDESITE_STAIRS);
                addImprovedStairRecipe(Blocks.COBBLED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE_STAIRS);
                addImprovedStairRecipe(Blocks.POLISHED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE_STAIRS);
                addImprovedStairRecipe(Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_BRICK_STAIRS);
                addImprovedStairRecipe(Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_STAIRS);
                addImprovedStairRecipe(Blocks.TUFF, Blocks.TUFF_STAIRS);
                addImprovedStairRecipe(Blocks.POLISHED_TUFF, Blocks.POLISHED_TUFF_STAIRS);
                addImprovedStairRecipe(Blocks.TUFF_BRICKS, Blocks.TUFF_BRICK_STAIRS);
            }

            private void addStairsRecipes() {
                ItemLike[][] woodRecipesLocal = {
                        {null, null, null, null, Blocks.ACACIA_PLANKS, null, Blocks.ACACIA_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.BIRCH_PLANKS, null, Blocks.BIRCH_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.CHERRY_PLANKS, null, Blocks.CHERRY_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.CRIMSON_PLANKS, null, Blocks.CRIMSON_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.DARK_OAK_PLANKS, null, Blocks.DARK_OAK_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.JUNGLE_PLANKS, null, Blocks.JUNGLE_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.MANGROVE_PLANKS, null, Blocks.MANGROVE_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.OAK_PLANKS, null, Blocks.OAK_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.PALE_OAK_PLANKS, null, Blocks.PALE_OAK_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.SPRUCE_PLANKS, null, Blocks.SPRUCE_STAIRS, null, null, null, null, null, null},
                        {null, null, null, null, Blocks.WARPED_PLANKS, null, Blocks.WARPED_STAIRS, null, null, null, null, null, null}
                };

                for (ItemLike[] recipe : woodRecipesLocal) {
                    ItemLike planks = recipe[4];
                    ItemLike stairs = recipe[6];

                    shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 6)
                            .pattern("#  ")
                            .pattern("## ")
                            .pattern("###")
                            .define('#', planks)
                            .unlockedBy(getHasName(planks), has(planks))
                            .save(this.output);   // No custom path — uses default vanilla ID
                }
            }

            private void addImprovedStairRecipe(ItemLike input, ItemLike stairs) {
                shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 6)
                        .pattern("#  ")
                        .pattern("## ")
                        .pattern("###")
                        .define('#', input)
                        .unlockedBy(getHasName(input), has(input))
                        .save(this.output);   // No custom path — uses default vanilla ID
            }
        };
    }

    @Override
    public String getName() {
        return "simpleqol Recipe";
    }
}