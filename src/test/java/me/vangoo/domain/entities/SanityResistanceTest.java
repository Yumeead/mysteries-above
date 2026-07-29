package me.vangoo.domain.entities;

import me.vangoo.domain.abilities.core.IAbilityContext;
import me.vangoo.domain.abilities.core.PermanentPassiveAbility;
import me.vangoo.domain.valueobjects.Sequence;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Пасивка-опір послаблює ВСЯКУ вхідну втрату глузду — це чокпоінт
 * {@code Beyonder.increaseSanityLoss}, через який ідуть усі джерела.
 */
class SanityResistanceTest {

    private static final List<String> NAMES =
            List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    private static class Resistant extends PermanentPassiveAbility {
        @Override
        public String getName() {
            return "Опір";
        }

        @Override
        public String getDescription(Sequence userSequence) {
            return "";
        }

        @Override
        public double getSanityLossMultiplier() {
            return 0.5;
        }

        @Override
        public void tick(IAbilityContext context) {
        }
    }

    private static Beyonder beyonderWith(boolean resistant) {
        Pathway pathway = new Pathway(PathwayGroup.LordOfMysteries, NAMES) {
            @Override
            protected void initializeAbilities() {
                if (resistant) {
                    sequenceAbilities.put(6, List.of(new Resistant()));
                }
            }
        };
        return new Beyonder(UUID.randomUUID(), Sequence.of(6), pathway);
    }

    @Test
    void withoutResistanceSanityLossLandsInFull() {
        Beyonder beyonder = beyonderWith(false);
        beyonder.increaseSanityLoss(10);
        assertEquals(10, beyonder.getSanityLossScale());
    }

    @Test
    void resistanceHalvesIncomingSanityLoss() {
        Beyonder beyonder = beyonderWith(true);
        beyonder.increaseSanityLoss(10);
        assertEquals(5, beyonder.getSanityLossScale());
    }

    @Test
    void resistanceNeverFullyBlocksASingleHit() {
        Beyonder beyonder = beyonderWith(true);
        beyonder.increaseSanityLoss(1);
        assertEquals(1, beyonder.getSanityLossScale());
    }

    @Test
    void recoveryIsNotAffectedByResistance() {
        Beyonder beyonder = beyonderWith(true);
        beyonder.increaseSanityLoss(10);
        beyonder.decreaseSanityLoss(3);
        assertEquals(2, beyonder.getSanityLossScale());
    }
}
