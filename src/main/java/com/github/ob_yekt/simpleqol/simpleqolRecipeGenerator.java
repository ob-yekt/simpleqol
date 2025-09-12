package com.github.ob_yekt.simpleqol;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.recipe.StonecuttingRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;

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

                // Wood recipes for all wood types - includes logs, wood, stripped variants, signs, and shelves
                ItemConvertible[][] woodRecipes = {
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
                        addStonecutterRecipe(input, recipe[6], 2);  // Stairs
                        addStonecutterRecipe(input, recipe[7], 2);  // Fence
                        addStonecutterRecipe(input, recipe[8], 1);  // Fence Gate
                        addStonecutterRecipe(input, recipe[9], 2);  // Door
                        addStonecutterRecipe(input, recipe[10], 1); // Trapdoor
                        addStonecutterRecipe(input, recipe[11], 2); // Sign
                        addStonecutterRecipe(input, recipe[12], 1); // Shelf
                    }

                    // Additional plank recipes (from planks)
                    ItemConvertible planks = recipe[4];
                    addStonecutterRecipe(planks, Items.STICK, 2);        // 1 plank = 2 sticks
                    addStonecutterRecipe(planks, recipe[5], 2);          // 1 plank = 2 slabs
                    addStonecutterRecipe(planks, recipe[6], 1);          // 1 plank = 1 stair
                }

                // Improved crafting table recipes (overriding vanilla)
                addImprovedCraftingRecipes();
            }

            private void addStonecutterRecipe(ItemConvertible input, ItemConvertible output, int count) {
                StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), RecipeCategory.BUILDING_BLOCKS, output, count)
                        .criterion(hasItem(input), conditionsFromItem(input))
                        .offerTo(this.exporter, "simpleqol:" + getItemPath(output) + "_from_" + getItemPath(input) + "_stonecutting");
            }

            private void addImprovedCraftingRecipes() {

                // Additional crafting table recipes
                // 1 wool (any color) -> 4 string
                RegistryEntryList<Item> woolTag = this.registries.getOrThrow(RegistryKeys.ITEM)
                        .getOrThrow(net.minecraft.registry.tag.ItemTags.WOOL);
                ShapelessRecipeJsonBuilder.create(this.registries.getOrThrow(net.minecraft.registry.RegistryKeys.ITEM), RecipeCategory.MISC, Items.STRING, 4)
                        .input(Ingredient.ofTag(woolTag))
                        .criterion("has_wool", conditionsFromTag(net.minecraft.registry.tag.ItemTags.WOOL))
                        .offerTo(this.exporter, "simpleqol:string_from_wool");

                // For nether wart
                ShapelessRecipeJsonBuilder.create(this.registries.getOrThrow(net.minecraft.registry.RegistryKeys.ITEM), RecipeCategory.MISC, Items.NETHER_WART, 9)
                        .input(Blocks.NETHER_WART_BLOCK)
                        .criterion(hasItem(Blocks.NETHER_WART_BLOCK), conditionsFromItem(Blocks.NETHER_WART_BLOCK))
                        .offerTo(this.exporter, "simpleqol:nether_wart_from_nether_wart_block");

                // For packed ice
                ShapelessRecipeJsonBuilder.create(this.registries.getOrThrow(net.minecraft.registry.RegistryKeys.ITEM), RecipeCategory.MISC, Blocks.ICE, 9)
                        .input(Blocks.PACKED_ICE)
                        .criterion(hasItem(Blocks.PACKED_ICE), conditionsFromItem(Blocks.PACKED_ICE))
                        .offerTo(this.exporter, "simpleqol:ice_from_packed_ice");

                // For blue ice
                ShapelessRecipeJsonBuilder.create(this.registries.getOrThrow(net.minecraft.registry.RegistryKeys.ITEM), RecipeCategory.MISC, Blocks.PACKED_ICE, 9)
                        .input(Blocks.BLUE_ICE)
                        .criterion(hasItem(Blocks.BLUE_ICE), conditionsFromItem(Blocks.BLUE_ICE))
                        .offerTo(this.exporter, "simpleqol:packed_ice_from_blue_ice");

                // For quartz
                ShapelessRecipeJsonBuilder.create(this.registries.getOrThrow(net.minecraft.registry.RegistryKeys.ITEM), RecipeCategory.MISC, Items.QUARTZ, 4)
                        .input(Blocks.QUARTZ_BLOCK)
                        .criterion(hasItem(Blocks.QUARTZ_BLOCK), conditionsFromItem(Blocks.QUARTZ_BLOCK))
                        .offerTo(this.exporter, "simpleqol:quartz_from_quartz_block");
            }
        };
    }

    @Override
    public String getName() {
        return "simpleqol Recipe";
    }
}