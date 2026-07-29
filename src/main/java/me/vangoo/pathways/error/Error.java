package me.vangoo.pathways.error;

import me.vangoo.domain.entities.Pathway;
import me.vangoo.domain.entities.PathwayGroup;
import me.vangoo.pathways.error.abilities.ConceptualTheft;
import me.vangoo.pathways.error.abilities.Decryption;
import me.vangoo.pathways.error.abilities.Eloquence;
import me.vangoo.pathways.error.abilities.MaterialTheft;
import me.vangoo.pathways.error.abilities.MentalDisruption;
import me.vangoo.pathways.error.abilities.MentalFortitude;
import me.vangoo.pathways.error.abilities.PowerTheft;
import me.vangoo.pathways.error.abilities.ShadowTheft;
import me.vangoo.pathways.error.abilities.SuperiorObservation;
import me.vangoo.pathways.error.abilities.SwindlerCharm;
import me.vangoo.pathways.error.abilities.TheftOfHeavenlySecrets;
import me.vangoo.pathways.error.abilities.ThoughtMisdirection;
import me.vangoo.pathways.common.abilities.PhysicalEnhancement;
import me.vangoo.pathways.common.abilities.CombatProficiency;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Error extends Pathway {
    /** Спільний identity фізики шляху: сильніша версія ЗАМІНЮЄ слабшу, а не стакається. */
    private static final String ERROR_PHYSIQUE = "error_physique";

    public Error(PathwayGroup group, List<String> sequenceNames) {
        super(group, sequenceNames);
    }

    @Override
    protected void initializeAbilities() {
        sequenceAbilities.put(9, List.of(
                new ShadowTheft(),
                new SuperiorObservation(),
                new CombatProficiency(),
                new PhysicalEnhancement(
                        ERROR_PHYSIQUE,
                        "Фізичні посилення",
                        "Ви отримуєте сильне тіло",
                        3)));
        sequenceAbilities.put(8, List.of(
                new SwindlerCharm(),
                new Eloquence(),
                new ThoughtMisdirection(),
                new MentalDisruption(),
                new MaterialTheft(),
                new PhysicalEnhancement(
                        ERROR_PHYSIQUE,
                        "Тіло афериста",
                        "Ваше тіло стає значно спритнішим і швидшим",
                        4,
                        PotionEffectType.SPEED)));
        sequenceAbilities.put(7, List.of(
                new Decryption()));
        // Крадіжка матеріалів і Вища спостережливість НЕ реєструються тут: їхній
        // Прометеїв тір — це гілка всередині вже успадкованих класів, а не новий екземпляр.
        sequenceAbilities.put(6, List.of(
                new PowerTheft(),
                new MentalFortitude(),
                new PhysicalEnhancement(
                        ERROR_PHYSIQUE,
                        "Тіло Прометея",
                        "Крадене чуже вже частина вашого тіла",
                        5,
                        PotionEffectType.SPEED)));
        // Крадіжка сили і Розшифрування тут не реєструються: їхній тір Крадія снів —
        // гілка всередині успадкованих класів, а не новий екземпляр.
        sequenceAbilities.put(5, List.of(
                new ConceptualTheft(),
                new TheftOfHeavenlySecrets(),
                new Decryption.IllusionDispel(),
                new PhysicalEnhancement(
                        ERROR_PHYSIQUE,
                        "Тіло Крадія снів",
                        "Ви крадете саму суть — і тіло вже не зовсім ваше",
                        6,
                        PotionEffectType.SPEED,
                        PotionEffectType.NIGHT_VISION)));
    }
}
