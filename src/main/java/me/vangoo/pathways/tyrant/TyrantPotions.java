package me.vangoo.pathways.tyrant;

import me.vangoo.domain.IItemResolver;
import me.vangoo.domain.PathwayPotions;
import me.vangoo.domain.brewing.RecipeDefinition;
import me.vangoo.domain.entities.Pathway;

import java.util.List;
import java.util.Map;

/** Зілля шляху Tyrant: кольори з брендингу, рецепти (Посл. 9-5) — з potion-recipes.yml. */
public class TyrantPotions extends PathwayPotions {
    public TyrantPotions(Pathway pathway, IItemResolver itemResolver,
                         Map<Integer, RecipeDefinition> recipes) {
        super(pathway, List.of(), itemResolver);
        loadRecipes(recipes);
    }
}
