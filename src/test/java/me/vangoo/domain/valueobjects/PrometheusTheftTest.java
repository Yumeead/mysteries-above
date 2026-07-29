package me.vangoo.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Чиста математика Прометея — формула шансу, затиски, тривалості й підлога кулдауну.
 */
class PrometheusTheftTest {

    private static final Sequence SEQ_6 = Sequence.of(6);

    @Test
    void baseChanceIsFortyPercentAgainstAnEqualVictim() {
        assertEquals(0.40, PrometheusTheft.successChance(0, SEQ_6), 1e-9);
        // Слабша жертва не додає бонусу — лише сильніша віднімає.
        assertEquals(0.40, PrometheusTheft.successChance(0, Sequence.of(9)), 1e-9);
    }

    @Test
    void knowledgeOfVictimPathwayRaisesChanceUpToCap() {
        assertEquals(0.55, PrometheusTheft.successChance(5, SEQ_6), 1e-9);
        assertEquals(0.65, PrometheusTheft.successChance(9, SEQ_6), 1e-9);
        assertEquals(0.65, PrometheusTheft.successChance(100, SEQ_6), 1e-9,
                "бонус за рецепти має впиратись у +25%");
        assertEquals(0.40, PrometheusTheft.successChance(-3, SEQ_6), 1e-9,
                "від'ємна кількість рецептів не має знижувати шанс");
    }

    @Test
    void strongerVictimLowersChanceEightPercentPerLevel() {
        assertEquals(0.32, PrometheusTheft.successChance(0, Sequence.of(5)), 1e-9);
        assertEquals(0.16, PrometheusTheft.successChance(0, Sequence.of(3)), 1e-9);
    }

    @Test
    void chanceIsClampedBetweenTenAndNinetyFivePercent() {
        assertEquals(0.10, PrometheusTheft.successChance(0, Sequence.of(0)), 1e-9);
        for (int level = 0; level <= 9; level++) {
            for (int recipes = 0; recipes <= 50; recipes++) {
                double chance = PrometheusTheft.successChance(recipes, Sequence.of(level));
                assertTrue(chance >= 0.10 && chance <= 0.95,
                        "шанс поза межами: Seq " + level + ", рецептів " + recipes);
            }
        }
    }

    @Test
    void cooldownShrinksWithPowerButHasFloor() {
        assertEquals(300, PrometheusTheft.powerTheftCooldownSeconds(SEQ_6));
        for (int level = 0; level <= 6; level++) {
            int cooldown = PrometheusTheft.powerTheftCooldownSeconds(Sequence.of(level));
            assertTrue(cooldown >= 120 && cooldown <= 300, "кулдаун поза межами на Seq " + level);
        }
        assertTrue(PrometheusTheft.powerTheftCooldownSeconds(Sequence.of(0))
                < PrometheusTheft.powerTheftCooldownSeconds(SEQ_6));
    }

    @Test
    void suppressionOutlivesTheStolenAbility() {
        assertEquals(10L * 60_000L, PrometheusTheft.STOLEN_DURATION_MILLIS);
        assertEquals(30L * 60_000L, PrometheusTheft.SUPPRESSION_DURATION_MILLIS);
        assertTrue(PrometheusTheft.SUPPRESSION_DURATION_MILLIS > PrometheusTheft.STOLEN_DURATION_MILLIS);
    }

    @Test
    void corruptionTransferStartsAtThreeAndGrowsWithPower() {
        assertEquals(3, PrometheusTheft.corruptionTransfer(SEQ_6));
        assertTrue(PrometheusTheft.corruptionTransfer(Sequence.of(0))
                > PrometheusTheft.corruptionTransfer(SEQ_6));
    }

    @Test
    void namesAreRevealedOnlyWhenThePathwayIsKnown() {
        assertTrue(PrometheusTheft.revealsAbilityNames(1));
        assertTrue(!PrometheusTheft.revealsAbilityNames(0));
    }
}
