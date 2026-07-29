package me.vangoo.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Чиста математика Крадія снів — тривалості, дальність, шанс і обидва затиски «Ідеалу».
 */
class DreamStealerTheftTest {

    private static final Sequence SEQ_5 = Sequence.of(5);

    @Test
    void baseChanceIsFiftyPercentAgainstAnEqualOrWeakerVictim() {
        assertEquals(0.50, DreamStealerTheft.successChance(0, SEQ_5), 1e-9);
        assertEquals(0.50, DreamStealerTheft.successChance(0, Sequence.of(9)), 1e-9);
    }

    @Test
    void knowledgeRaisesChanceUpToCapAndStrongerVictimLowersIt() {
        assertEquals(0.65, DreamStealerTheft.successChance(5, SEQ_5), 1e-9);
        assertEquals(0.75, DreamStealerTheft.successChance(100, SEQ_5), 1e-9,
                "бонус за рецепти має впиратись у +25%");
        assertEquals(0.50, DreamStealerTheft.successChance(-3, SEQ_5), 1e-9,
                "від'ємна кількість рецептів не має знижувати шанс");
        assertEquals(0.42, DreamStealerTheft.successChance(0, Sequence.of(4)), 1e-9);
        assertEquals(0.10, DreamStealerTheft.successChance(0, Sequence.of(0)), 1e-9);
    }

    @Test
    void chanceIsAlwaysClampedBetweenTenAndNinetyFivePercent() {
        for (int level = 0; level <= 9; level++) {
            for (int recipes = 0; recipes <= 50; recipes++) {
                double chance = DreamStealerTheft.successChance(recipes, Sequence.of(level));
                assertTrue(chance >= 0.10 && chance <= 0.95,
                        "шанс поза межами: Seq " + level + ", рецептів " + recipes);
            }
        }
    }

    @Test
    void dreamStealerBeatsPrometheusOnRangeAndHoldTime() {
        assertEquals(80.0, DreamStealerTheft.CONCEPTUAL_RANGE, 1e-9);
        assertTrue(DreamStealerTheft.POWER_THEFT_RANGE > PrometheusTheft.POWER_THEFT_RANGE);
        assertTrue(DreamStealerTheft.POWER_STOLEN_DURATION_MILLIS
                > PrometheusTheft.STOLEN_DURATION_MILLIS);
        assertTrue(DreamStealerTheft.successChance(0, Sequence.of(6))
                        > PrometheusTheft.successChance(0, Sequence.of(6)),
                "проти тієї ж жертви Крадій снів краде надійніше за Прометея");
    }

    @Test
    void suppressionOutlivesTheStolenPowerAndUnusedHoldOutlivesBoth() {
        assertEquals(30L * 60_000L, DreamStealerTheft.POWER_STOLEN_DURATION_MILLIS);
        assertEquals(60L * 60_000L, DreamStealerTheft.POWER_SUPPRESSION_DURATION_MILLIS);
        assertTrue(DreamStealerTheft.POWER_SUPPRESSION_DURATION_MILLIS
                > DreamStealerTheft.POWER_STOLEN_DURATION_MILLIS);
        assertTrue(DreamStealerTheft.POWER_UNUSED_HOLD_MILLIS
                > DreamStealerTheft.POWER_SUPPRESSION_DURATION_MILLIS,
                "невикористана сила має чекати довше, ніж триває пригнічення");
    }

    @Test
    void conceptualDurationsMatchTheDesignTable() {
        assertEquals(30L * 60_000L, DreamStealerTheft.DREAM_HOLD_MILLIS);
        assertEquals(20, DreamStealerTheft.GENERAL_ABILITY_SECONDS);
        assertEquals(5, DreamStealerTheft.ATTACK_WINDOW_SECONDS);
        assertEquals(10, DreamStealerTheft.ATTACK_CHARGE_SECONDS);
        assertEquals(60, DreamStealerTheft.HEART_DURATION_SECONDS);
        assertEquals(60, DreamStealerTheft.IDEAL_APATHY_SECONDS);
        assertEquals(8.0, DreamStealerTheft.HEART_HEALTH_TRANSFER, 1e-9);
        assertEquals(10L * 60_000L, DreamStealerTheft.DISGUISE_DURATION_MILLIS);
    }

    @Test
    void heartTakesFourHeartsButAlwaysLeavesTheVictimOne() {
        assertEquals(8.0, DreamStealerTheft.heartTransfer(20.0), 1e-9);
        assertEquals(8.0, DreamStealerTheft.heartTransfer(40.0), 1e-9,
                "більше чотирьох сердець не беруть навіть у товстої цілі");
        assertEquals(4.0, DreamStealerTheft.heartTransfer(6.0), 1e-9,
                "у слабкої жертви лишається щонайменше серце");
        assertEquals(0.0, DreamStealerTheft.heartTransfer(2.0), 1e-9);
        assertEquals(0.0, DreamStealerTheft.heartTransfer(1.0), 1e-9,
                "крадіжка не має добивати ціль");
    }

    @Test
    void idealTakesFifteenPercentOfCurrentSpirituality() {
        assertEquals(450, DreamStealerTheft.idealSpiritualityTransfer(3000, 9999));
        assertEquals(750, DreamStealerTheft.idealSpiritualityTransfer(5000, 9999));
    }

    @Test
    void idealIsClampedByTheThiefsOwnMissingSpirituality() {
        assertEquals(100, DreamStealerTheft.idealSpiritualityTransfer(3000, 100),
                "надлишок згорає — союзника не можна доїти як батарейку");
        assertEquals(0, DreamStealerTheft.idealSpiritualityTransfer(3000, 0),
                "повний злодій не забирає нічого");
        assertEquals(0, DreamStealerTheft.idealSpiritualityTransfer(3000, -50));
    }

    @Test
    void disguiseGetsHarderTheWiderTheChurchDomainIs() {
        assertEquals(0.90, DreamStealerTheft.disguiseSuccessChance(0), 1e-9,
                "церква без домену нічим не викриває самозванця");
        assertEquals(0.84, DreamStealerTheft.disguiseSuccessChance(1), 1e-9);
        assertEquals(0.36, DreamStealerTheft.disguiseSuccessChance(9), 1e-9,
                "Церква Блазня тримає 9 шляхів — під її личину лізти найризикованіше");
        assertEquals(0.25, DreamStealerTheft.disguiseSuccessChance(50), 1e-9,
                "шанс упирається в підлогу, а не падає в нуль");
        assertEquals(0.90, DreamStealerTheft.disguiseSuccessChance(-3), 1e-9);
    }

    @Test
    void idealNeverReturnsNegativeForADrainedVictim() {
        assertEquals(0, DreamStealerTheft.idealSpiritualityTransfer(0, 500));
        assertEquals(0, DreamStealerTheft.idealSpiritualityTransfer(-200, 500));
        assertEquals(0, DreamStealerTheft.idealSpiritualityTransfer(3, 500),
                "3 * 0.15 = 0.45 → округлення вниз до нуля");
    }
}
