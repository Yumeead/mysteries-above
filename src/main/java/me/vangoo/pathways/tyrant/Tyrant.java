package me.vangoo.pathways.tyrant;

import me.vangoo.domain.entities.Pathway;
import me.vangoo.domain.entities.PathwayGroup;
import me.vangoo.pathways.common.abilities.PhysicalEnhancement;
import me.vangoo.pathways.tyrant.abilities.AbyssalDominion;
import me.vangoo.pathways.tyrant.abilities.AirCushion;
import me.vangoo.pathways.tyrant.abilities.AzureWave;
import me.vangoo.pathways.tyrant.abilities.Balance;
import me.vangoo.pathways.tyrant.abilities.CleansingLight;
import me.vangoo.pathways.tyrant.abilities.CorrosiveDeluge;
import me.vangoo.pathways.tyrant.abilities.Eavesdrop;
import me.vangoo.pathways.tyrant.abilities.IllusoryScales;
import me.vangoo.pathways.tyrant.abilities.LightningBoltArrow;
import me.vangoo.pathways.tyrant.abilities.LightningStrike;
import me.vangoo.pathways.tyrant.abilities.PressureWave;
import me.vangoo.pathways.tyrant.abilities.PreciseThrow;
import me.vangoo.pathways.tyrant.abilities.RagingBlow;
import me.vangoo.pathways.tyrant.abilities.RestorativeWater;
import me.vangoo.pathways.tyrant.abilities.SeaLunge;
import me.vangoo.pathways.tyrant.abilities.SeaMemory;
import me.vangoo.pathways.tyrant.abilities.SeaTongue;
import me.vangoo.pathways.tyrant.abilities.Singing;
import me.vangoo.pathways.tyrant.abilities.SlickWater;
import me.vangoo.pathways.tyrant.abilities.StormHeart;
import me.vangoo.pathways.tyrant.abilities.ThunderVoice;
import me.vangoo.pathways.tyrant.abilities.WaterControl;
import me.vangoo.pathways.tyrant.abilities.WaterCurtain;
import me.vangoo.pathways.tyrant.abilities.WaterFilm;
import me.vangoo.pathways.tyrant.abilities.WindBinding;
import me.vangoo.pathways.tyrant.abilities.WindFlight;
import me.vangoo.pathways.tyrant.abilities.WindImbuedHands;
import me.vangoo.pathways.tyrant.abilities.WindPull;
import me.vangoo.pathways.tyrant.abilities.WindSprint;
import me.vangoo.pathways.tyrant.abilities.Windblades;
import me.vangoo.pathways.tyrant.abilities.Wrath;
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

    // Спільний identity пасиву фізики Тирана: одна здібність, що прогресує між Sequence
    // (сильніша версія замінює слабшу через AbilityTransformer), а не кілька дублікатів.
    private static final String PHYSIQUE_IDENTITY = "tyrant_physique";

    @Override
    protected void initializeAbilities() {
        // Sequence 9: Матрос (Хранитель Морів) — усі трейти пасивні.
        sequenceAbilities.put(9, List.of(
                new PhysicalEnhancement(
                        PHYSIQUE_IDENTITY,
                        "Морська спорідненість",
                        "Матрос споріднений з морем: пливе спритно, мов риба.",
                        3,
                        PotionEffectType.DOLPHINS_GRACE),
                new IllusoryScales(),
                new Balance(),
                new SeaLunge()));

        // Sequence 8: Народ Люті (Вартовий Шторму). Та сама фізика Тирана прогресує —
        // superset Seq 9: зберігає водне дихання й спритність, додає силу, опір і швидкість,
        // тож зустріч із Народом Люті — мов зустріч зі штормом.
        sequenceAbilities.put(8, List.of(
                new PhysicalEnhancement(
                        PHYSIQUE_IDENTITY,
                        "Штормова Міць",
                        "Тіло Народу Люті загартоване штормом: швидкість підсилена, " +
                                "а спорідненість із морем збережена.",
                        4,
                        PotionEffectType.DOLPHINS_GRACE,
                        PotionEffectType.SPEED),
                new Wrath(),
                new RagingBlow()));

        // Sequence 7: Мореплавець (Штормовий Жрець). Фізика Тирана прогресує далі —
        // superset Seq 8: додає Силу Провідника (Conduit Power), тож Мореплавець
        // почувається у воді як удома навіть у пітьмі глибин.
        sequenceAbilities.put(7, List.of(
                new PhysicalEnhancement(
                        PHYSIQUE_IDENTITY,
                        "Морська Спорідненість",
                        "Тіло Мореплавця повністю зрослося з морем: до штормової міці " +
                                "додається Сила Провідника — нічне бачення, швидкість у воді й " +
                                "дихання під водою навіть у безодні.",
                        5,
                        PotionEffectType.WATER_BREATHING,
                        PotionEffectType.DOLPHINS_GRACE,
                        PotionEffectType.CONDUIT_POWER,
                        PotionEffectType.SPEED),
                new SeaMemory(),
                new PreciseThrow(),
                new WaterFilm(),
                new AzureWave(),
                new RestorativeWater(),
                new CleansingLight()));

        // Sequence 6: Благословенний Вітром. Фізика Тирана прогресує далі —
        // superset Seq 7: до Сили Провідника, штормової міці й морської спорідненості
        // додається нічне бачення (бачать у пітьмі), а глибоке занурення (100 м) і
        // плавучість покриваються збереженим водним диханням і грацією дельфіна.
        sequenceAbilities.put(6, List.of(
                new PhysicalEnhancement(
                        PHYSIQUE_IDENTITY,
                        "Спорідненість зі Стихіями",
                        "Тіло Благословенного Вітром зрослося не лише з морем, а й із небом: " +
                                "зберігає штормову міць і Силу Провідника, а до них додається " +
                                "нічне бачення — око бачить у пітьмі глибин і ночі.",
                        6,
                        PotionEffectType.WATER_BREATHING,
                        PotionEffectType.DOLPHINS_GRACE,
                        PotionEffectType.SPEED,
                        PotionEffectType.NIGHT_VISION),
                new Windblades(),
                new PressureWave(),
                new WindFlight(),
                new WindSprint(),
                new WindImbuedHands(),
                new WindPull(),
                new Eavesdrop(),
                new AirCushion(),
                new WaterControl()));

        // Sequence 5: Океанський Співець. Фізика Тирана прогресує далі — superset Seq 6:
        // ті самі ефекти, але глибший рівень (більше HP), тож вільний рух під водою,
        // видобування кисню прямо з води й байдужість до тиску безодні (Pressure Resistance)
        // покриваються збереженим водним диханням, грацією дельфіна й Силою Провідника.
        sequenceAbilities.put(5, List.of(
                new PhysicalEnhancement(
                        PHYSIQUE_IDENTITY,
                        "Володар Безодні",
                        "Тіло Океанського Співця більше не знає меж глибини: воно бере кисень " +
                                "просто з води, не боїться тиску безодні й тримає штормову міць, " +
                                "Силу Провідника та нічне бачення.",
                        7,
                        PotionEffectType.WATER_BREATHING,
                        PotionEffectType.DOLPHINS_GRACE,
                        PotionEffectType.SPEED,
                        PotionEffectType.CONDUIT_POWER,
                        PotionEffectType.NIGHT_VISION),
                new StormHeart(),
                new AbyssalDominion(),
                new LightningStrike(),
                new LightningBoltArrow(),
                new ThunderVoice(),
                new Singing(),
                new SeaTongue(),
                new WaterCurtain(),
                new CorrosiveDeluge(),
                new SlickWater(),
                new WindBinding()));
    }
}
