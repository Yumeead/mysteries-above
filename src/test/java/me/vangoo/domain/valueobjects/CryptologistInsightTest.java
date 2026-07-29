package me.vangoo.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Чиста математика Криптолога — пороги глибини, скейлінг і підлога кулдауну.
 */
class CryptologistInsightTest {

    @Test
    void depthThresholdsMatchPlanAtSequenceSeven() {
        Sequence seq7 = Sequence.of(7);
        assertEquals(2, CryptologistInsight.depthThreshold(1, seq7));
        assertEquals(5, CryptologistInsight.depthThreshold(2, seq7));
        assertEquals(9, CryptologistInsight.depthThreshold(3, seq7));
    }

    @Test
    void depthThresholdsFallWithPowerButStayOrdered() {
        for (int level = 0; level <= 7; level++) {
            Sequence sequence = Sequence.of(level);
            int d1 = CryptologistInsight.depthThreshold(1, sequence);
            int d2 = CryptologistInsight.depthThreshold(2, sequence);
            int d3 = CryptologistInsight.depthThreshold(3, sequence);
            assertTrue(d1 >= 1 && d1 < d2 && d2 < d3, "пороги розлізлись на Seq " + level);
            assertTrue(d1 <= CryptologistInsight.depthThreshold(1, Sequence.of(7)));
            assertTrue(d3 <= CryptologistInsight.depthThreshold(3, Sequence.of(7)));
        }
        assertTrue(CryptologistInsight.depthThreshold(3, Sequence.of(0))
                < CryptologistInsight.depthThreshold(3, Sequence.of(7)));
    }

    @Test
    void depthFollowsAvailableEvidence() {
        Sequence seq7 = Sequence.of(7);
        assertEquals(0, CryptologistInsight.depthFor(0, seq7));
        assertEquals(0, CryptologistInsight.depthFor(1, seq7));
        assertEquals(1, CryptologistInsight.depthFor(2, seq7));
        assertEquals(1, CryptologistInsight.depthFor(4, seq7));
        assertEquals(2, CryptologistInsight.depthFor(5, seq7));
        assertEquals(3, CryptologistInsight.depthFor(9, seq7));
        assertEquals(3, CryptologistInsight.depthFor(100, seq7));

        // Той самий доказовий матеріал читається глибше сильнішим Криптологом.
        assertTrue(CryptologistInsight.depthFor(5, Sequence.of(2))
                >= CryptologistInsight.depthFor(5, seq7));
    }

    @Test
    void cooldownShrinksWithPowerButHasFloor() {
        assertEquals(45, CryptologistInsight.decryptionCooldownSeconds(Sequence.of(7)));
        for (int level = 0; level <= 7; level++) {
            int cooldown = CryptologistInsight.decryptionCooldownSeconds(Sequence.of(level));
            assertTrue(cooldown >= 20 && cooldown <= 45, "кулдаун поза межами на Seq " + level);
        }
        assertTrue(CryptologistInsight.decryptionCooldownSeconds(Sequence.of(0))
                < CryptologistInsight.decryptionCooldownSeconds(Sequence.of(7)));
    }

    @Test
    void sanityCostRisesWithDepthAndPower() {
        Sequence seq7 = Sequence.of(7);
        assertEquals(0, CryptologistInsight.sanityLoss(0, seq7));
        assertEquals(0, CryptologistInsight.sanityLoss(1, seq7));
        assertEquals(2, CryptologistInsight.sanityLoss(2, seq7));
        assertEquals(5, CryptologistInsight.sanityLoss(3, seq7));
        assertTrue(CryptologistInsight.sanityLoss(3, Sequence.of(0))
                > CryptologistInsight.sanityLoss(3, seq7));
    }

    @Test
    void incantationMultiplierStartsAtTwentyPercent() {
        assertEquals(1.20, CryptologistInsight.incantationDamageMultiplier(Sequence.of(7)), 1e-9);
        assertTrue(CryptologistInsight.incantationDamageMultiplier(Sequence.of(0)) > 1.20);
    }

    @Test
    void depthOutsideOneToThreeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> CryptologistInsight.depthThreshold(4, Sequence.of(7)));
        assertThrows(IllegalArgumentException.class,
                () -> CryptologistInsight.sanityLoss(4, Sequence.of(7)));
    }
}
