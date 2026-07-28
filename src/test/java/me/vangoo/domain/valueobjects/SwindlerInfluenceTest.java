package me.vangoo.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Чиста математика Афериста — монотонність за силою, межі та підлоги.
 */
class SwindlerInfluenceTest {

    @Test
    void aggroCancelChanceGrowsWithPowerAndStaysBounded() {
        assertEquals(0.25, SwindlerInfluence.charmAggroCancelChance(Sequence.of(9)), 1e-9);
        assertTrue(SwindlerInfluence.charmAggroCancelChance(Sequence.of(5))
                > SwindlerInfluence.charmAggroCancelChance(Sequence.of(9)));
        for (int level = 0; level <= 9; level++) {
            double c = SwindlerInfluence.charmAggroCancelChance(Sequence.of(level));
            assertTrue(c >= 0.0 && c <= 0.40, "шанс поза межами на Seq " + level);
        }
    }

    @Test
    void tradeDiscountIsCappedAtThirtyPercent() {
        assertEquals(0.10, SwindlerInfluence.charmTradeDiscount(Sequence.of(9)), 1e-9);
        assertEquals(0.30, SwindlerInfluence.charmTradeDiscount(Sequence.of(0)), 1e-9);
        assertTrue(SwindlerInfluence.charmTradeDiscount(Sequence.of(5))
                > SwindlerInfluence.charmTradeDiscount(Sequence.of(8)));
    }

    @Test
    void durationsGrowWithPower() {
        assertEquals(160, SwindlerInfluence.eloquenceDurationTicks(Sequence.of(9)));
        assertEquals(120, SwindlerInfluence.misdirectionDurationTicks(Sequence.of(9)));
        assertEquals(160, SwindlerInfluence.disruptionDurationTicks(Sequence.of(9)));

        assertTrue(SwindlerInfluence.eloquenceDurationTicks(Sequence.of(0)) > 160 * 3,
                "красномовність масштабується STRONG");
        assertTrue(SwindlerInfluence.misdirectionDurationTicks(Sequence.of(4))
                > SwindlerInfluence.misdirectionDurationTicks(Sequence.of(8)));
    }

    @Test
    void sanityLossGrowsSlowly() {
        assertEquals(3, SwindlerInfluence.disruptionSanityLoss(Sequence.of(9)));
        assertEquals(4, SwindlerInfluence.disruptionSanityLoss(Sequence.of(0)));
    }

    @Test
    void theftCooldownShrinksWithPowerButHasFloor() {
        assertEquals(40, SwindlerInfluence.materialTheftCooldownSeconds(Sequence.of(9)));
        assertTrue(SwindlerInfluence.materialTheftCooldownSeconds(Sequence.of(0))
                < SwindlerInfluence.materialTheftCooldownSeconds(Sequence.of(9)));
        for (int level = 0; level <= 9; level++) {
            assertTrue(SwindlerInfluence.materialTheftCooldownSeconds(Sequence.of(level)) >= 10,
                    "кулдаун не коротший за підлогу на Seq " + level);
        }
    }
}
