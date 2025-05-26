package com.github.ob_yekt.simpleqol;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.StonecuttingRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class simpleqolRecipeGenerator extends FabricRecipeProvider {
    public simpleqolRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        return new RecipeGenerator(registries, exporter) {
            @Override
            public void generate() {
                // Deepslate recipes
                // COBBLED
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE_SLAB, 2);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE_STAIRS, 1);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE_WALL, 1);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE, 1);
                // BRICKS
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICK_SLAB, 2);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICK_STAIRS, 1);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICK_WALL, 1);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICKS, 1);
                // TILES
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.DEEPSLATE_TILE_SLAB, 2);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.DEEPSLATE_TILE_STAIRS, 1);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.DEEPSLATE_TILE_WALL, 1);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.DEEPSLATE_TILES, 1);
                // POLISHED
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE, 1);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE_SLAB, 2);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE_STAIRS, 1);
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE_WALL, 1);
                // CHISELED
                addStonecutterRecipe(Blocks.DEEPSLATE, Blocks.CHISELED_DEEPSLATE, 1);

                // Wood recipes for all wood types - includes logs, wood, stripped variants, and signs
                ItemConvertible[][] woodRecipes = {
                        // {log, wood, stripped_log, stripped_wood, planks, slab, stairs, fence, fence_gate, door, trapdoor, sign}
                        {Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD, Blocks.OAK_PLANKS, Blocks.OAK_SLAB, Blocks.OAK_STAIRS, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.OAK_DOOR, Blocks.OAK_TRAPDOOR, Items.OAK_SIGN},
                        {Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_TRAPDOOR, Items.SPRUCE_SIGN},
                        {Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD, Blocks.BIRCH_PLANKS, Blocks.BIRCH_SLAB, Blocks.BIRCH_STAIRS, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_DOOR, Blocks.BIRCH_TRAPDOOR, Items.BIRCH_SIGN},
                        {Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_SLAB, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_TRAPDOOR, Items.JUNGLE_SIGN},
                        {Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD, Blocks.ACACIA_PLANKS, Blocks.ACACIA_SLAB, Blocks.ACACIA_STAIRS, Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_DOOR, Blocks.ACACIA_TRAPDOOR, Items.ACACIA_SIGN},
                        {Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_TRAPDOOR, Items.DARK_OAK_SIGN},
                        {Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_SLAB, Blocks.MANGROVE_STAIRS, Blocks.MANGROVE_FENCE, Blocks.MANGROVE_FENCE_GATE, Blocks.MANGROVE_DOOR, Blocks.MANGROVE_TRAPDOOR, Items.MANGROVE_SIGN},
                        {Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG, Blocks.STRIPPED_CHERRY_WOOD, Blocks.CHERRY_PLANKS, Blocks.CHERRY_SLAB, Blocks.CHERRY_STAIRS, Blocks.CHERRY_FENCE, Blocks.CHERRY_FENCE_GATE, Blocks.CHERRY_DOOR, Blocks.CHERRY_TRAPDOOR, Items.CHERRY_SIGN},
                        {Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_STAIRS, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_DOOR, Blocks.CRIMSON_TRAPDOOR, Items.CRIMSON_SIGN},
                        {Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.WARPED_PLANKS, Blocks.WARPED_SLAB, Blocks.WARPED_STAIRS, Blocks.WARPED_FENCE, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_DOOR, Blocks.WARPED_TRAPDOOR, Items.WARPED_SIGN}
                };

                for (ItemConvertible[] recipe : woodRecipes) {
                    // For each wood type, add recipes from all 4 input variants (log, wood, stripped_log, stripped_wood)
                    for (int inputIndex = 0; inputIndex < 4; inputIndex++) {
                        ItemConvertible input = recipe[inputIndex];
                        // Add recipes for non-stripped to stripped variants
                        if (inputIndex == 0) { // Log -> Stripped Log
                            addStonecutterRecipe(input, recipe[2], 1); // e.g., OAK_LOG -> STRIPPED_OAK_LOG
                        } else if (inputIndex == 1) { // Wood -> Stripped Wood
                            addStonecutterRecipe(input, recipe[3], 1); // e.g., OAK_WOOD -> STRIPPED_OAK_WOOD
                        }
                        // Recipes
                        addStonecutterRecipe(input, recipe[4], 4);  // Planks
                        addStonecutterRecipe(input, recipe[5], 8);  // Slabs
                        addStonecutterRecipe(input, recipe[6], 1);  // Stairs
                        addStonecutterRecipe(input, recipe[7], 1);  // Fence
                        addStonecutterRecipe(input, recipe[8], 1);  // Fence Gate
                        addStonecutterRecipe(input, recipe[9], 1);  // Door
                        addStonecutterRecipe(input, recipe[10], 1); // Trapdoor
                        addStonecutterRecipe(input, recipe[11], 1); // Sign
                    }
                }
            }

            private void addStonecutterRecipe(ItemConvertible input, ItemConvertible output, int count) {
                StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), RecipeCategory.BUILDING_BLOCKS, output, count)
                        .criterion(hasItem(input), conditionsFromItem(input))
                        .offerTo(this.exporter, getItemPath(output) + "_from_" + getItemPath(input) + "_stonecutting");
            }
        };
    }

    @Override
    public String getName() {
        return "simpleqol Recipes";
    }
}