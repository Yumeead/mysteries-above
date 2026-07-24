package me.vangoo.pathways.tyrant;

import me.vangoo.domain.entities.Pathway;
import me.vangoo.domain.entities.PathwayGroup;
import me.vangoo.pathways.justiciar.abilities.PhysicalEnhancement;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Tyrant Pathway (Тиран) — шлях моря, шторму й вітру.
 * Наповнюється по Sequence у {@link #initializeAbilities()}.
 * Група передається з PathwayManager.
 */
public class Tyrant extends Pathway {

    public Tyrant(PathwayGroup group, List<String> sequenceNames) {
        super(group, sequenceNames);
    }

    @Override
    protected void initializeAbilities() {
        // Sequence 9: Матрос (Хранитель Морів) — усі трейти пасивні.
        sequenceAbilities.put(9, List.of(
                new PhysicalEnhancement(
                        "Фізичне посилення",
                        "Сила моря наповнює тіло Матроса — його м'язи міцніші за людські.",
                        3,
                        PotionEffectType.STRENGTH),
                new PhysicalEnhancement(
                        "Морська спорідненість",
                        "Матрос споріднений з морем: дихає під водою й пливе спритно, мов риба.",
                        0,
                        PotionEffectType.WATER_BREATHING,
                        PotionEffectType.DOLPHINS_GRACE)));
    }
}
