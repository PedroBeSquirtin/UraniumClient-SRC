package com.uranium.utils.mining;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import com.uranium.UraniumClient;
import com.uranium.mixin.CountPlacementModifierAccessor;
import com.uranium.mixin.HeightRangePlacementModifierAccessor;
import com.uranium.mixin.RarityFilterPlacementModifierAccessor;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

public class Ore {
    public int generationStep;
    public int index;
    public IntProvider countProvider;
    public HeightProvider heightProvider;
    public HeightContext heightContext;
    public float discardChance;
    public float rarityChance;
    public int veinSize;
    public Color color;
    public boolean isScattered;

    public static Map<RegistryKey<Biome>, List<Ore>> register() {
        RegistryWrapper.WrapperLookup wrapperLookup = BuiltinRegistries.createWrapperLookup();
        RegistryWrapper.Impl<PlacedFeature> placedFeatureRegistry = wrapperLookup.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE);
        
        var biomes = wrapperLookup.getWrapperOrThrow(RegistryKeys.WORLD_PRESET)
            .getOrThrow(WorldPresets.DEFAULT)
            .value()
            .createDimensionsRegistryHolder()
            .dimensions()
            .get(DimensionOptions.NETHER)
            .chunkGenerator()
            .getBiomeSource()
            .getBiomes()
            .stream()
            .toList();
            
        var indexedFeatures = PlacedFeatureIndexer.collectIndexedFeatures(
            biomes, 
            registryEntry -> registryEntry.value().getGenerationSettings().getFeatures(), 
            true
        );
        
        Map<PlacedFeature, Ore> ores = new HashMap<>();
        
        // Register ores
        registerOre(ores, indexedFeatures, placedFeatureRegistry, 
                   OrePlacedFeatures.ORE_DEBRIS_SMALL, 7, new Color(209, 27, 245));
        registerOre(ores, indexedFeatures, placedFeatureRegistry, 
                   OrePlacedFeatures.ORE_ANCIENT_DEBRIS_LARGE, 7, new Color(209, 27, 245));
        
        Map<RegistryKey<Biome>, List<Ore>> biomeOreMap = new HashMap<>();
        
        biomes.forEach(biomeEntry -> {
            biomeOreMap.put(biomeEntry.getKey().get(), new ArrayList<>());
            
            Stream<PlacedFeature> featureStream = biomeEntry.value()
                .getGenerationSettings()
                .getFeatures()
                .stream()
                .flatMap(RegistryEntryList::stream)
                .map(RegistryEntry::value);
                
            featureStream.filter(ores::containsKey)
                .forEach(placedFeature -> 
                    biomeOreMap.get(biomeEntry.getKey().get()).add(ores.get(placedFeature))
                );
        });
        
        return biomeOreMap;
    }

    private static void registerOre(
            Map<PlacedFeature, Ore> oreMap,
            List<PlacedFeatureIndexer.IndexedFeatures> indexedFeatures,
            RegistryWrapper.Impl<PlacedFeature> placedFeatureRegistry,
            RegistryKey<PlacedFeature> oreKey,
            int genStep,
            Color color
    ) {
        PlacedFeature orePlacement = placedFeatureRegistry.getOrThrow(oreKey).value();
        int index = indexedFeatures.get(genStep).indexMapping().applyAsInt(orePlacement);
        Ore ore = new Ore(orePlacement, genStep, index, color);
        oreMap.put(orePlacement, ore);
    }

    private Ore(PlacedFeature placedFeature, int genStep, int featureIndex, Color oreColor) {
        this.countProvider = ConstantIntProvider.create(1);
        this.rarityChance = 1.0f;
        this.generationStep = genStep;
        this.index = featureIndex;
        this.color = oreColor;
        this.heightContext = new HeightContext(
            null, 
            HeightLimitView.create(
                MinecraftClient.getInstance().world.getBottomY(), 
                MinecraftClient.getInstance().world.getDimension().logicalHeight()
            )
        );

        for (Object modifier : placedFeature.placementModifiers()) {
            if (modifier instanceof CountPlacementModifier) {
                this.countProvider = ((CountPlacementModifierAccessor) modifier).getCount();
            } else if (modifier instanceof HeightRangePlacementModifier) {
                this.heightProvider = ((HeightRangePlacementModifierAccessor) modifier).getHeight();
            } else if (modifier instanceof RarityFilterPlacementModifier) {
                this.rarityChance = (float) ((RarityFilterPlacementModifierAccessor) modifier).getChance();
            }
        }

        FeatureConfig config = placedFeature.feature().value().config();
        if (config instanceof OreFeatureConfig) {
            OreFeatureConfig oreConfig = (OreFeatureConfig) config;
            this.discardChance = oreConfig.discardOnAirChance;
            this.veinSize = oreConfig.size;
            this.isScattered = placedFeature.feature().value().feature() instanceof ScatteredOreFeature;
        } else {
            throw new IllegalStateException("Config for " + placedFeature + " is not OreFeatureConfig");
        }
    }

    // Anti-tamper verification method
    private static byte[] getVerificationBytes() {
        return new byte[]{40, 4, 103, 33, 11, 101, 15, 97, 99, 53, 50, 44, 91, 16, 69, 71, 114, 86, 103, 108, 27, 65};
    }
}
