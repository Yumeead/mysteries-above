package me.vangoo.pathways.death;

import me.vangoo.domain.entities.Pathway;
import me.vangoo.domain.entities.PathwayGroup;
import me.vangoo.domain.valueobjects.CorpseCollectorLore;
import me.vangoo.domain.valueobjects.GravediggerLore;
import me.vangoo.pathways.common.abilities.PhysicalEnhancement;
import me.vangoo.pathways.death.abilities.CadavericResilience;
import me.vangoo.pathways.death.abilities.DeathSight;
import me.vangoo.pathways.death.abilities.EyeOfDeath;
import me.vangoo.pathways.death.abilities.GloomyPresence;
import me.vangoo.pathways.death.abilities.SpiritCommunication;
import me.vangoo.pathways.death.abilities.SpiritVision;
import me.vangoo.pathways.death.abilities.UndeadKnowledge;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Death Pathway (Смерть).
 * Група передається з PathwayManager.
 */
public class Death extends Pathway {

    /** Спільний identity фізики шляху: сильніша версія ЗАМІНЮЄ слабшу, а не стакається. */
    private static final String DEATH_PHYSIQUE = "death_physique";

    public Death(PathwayGroup group, List<String> sequenceNames) {
        super(group, sequenceNames);
    }

    @Override
    protected void initializeAbilities() {
        sequenceAbilities.put(9, List.of(
                new UndeadKnowledge(),
                new CadavericResilience(),
                new GloomyPresence(),
                new DeathSight(),
                new SpiritVision(),
                new PhysicalEnhancement(
                        DEATH_PHYSIQUE,
                        "Тіло трупозбирача",
                        "Ваше тіло холодніє, а присутність стає похмурою",
                        CorpseCollectorLore.PHYSIQUE_HP_BASE)));

        sequenceAbilities.put(8, List.of(
                new SpiritCommunication(),
                new EyeOfDeath(),
                new PhysicalEnhancement(
                        DEATH_PHYSIQUE,
                        "Тіло могильника",
                        "Тіло могильника лишається холодним, але вже не важким: " +
                                "могили копаються легко, а кроки стають швидкими.",
                        GravediggerLore.PHYSIQUE_HP_BASE,
                        PotionEffectType.SPEED)));
    }
}
