# Error Pathway — Sequence 8 (Swindler / Аферист) Implementation Plan

Branch: `feat/error-seq8-swindler` (to be created from fresh `main` — see "Branch" below).
Wiki source: `docs/pathways/error/Error PathwayAbilities … Fandom.md` (lines 25–62).
Rule: one milestone at a time; before each — state affected files, architectural
impact, why it's Ponytail; after each — summary + in-server test + save durable
decisions to memory. Wait for confirmation between milestones.

## Gameplay role

Sequence 8 turns the Marauder from a **pickpocket** into a **con artist**. Seq 9 was
about hands — steal an item, vanish. Seq 8 is about *minds and matter you can't see*:
talk your way out of a fight, make several enemies act on wrong information, feed one
enemy a false reality, read what a person is about to do, and take spiritual materials
straight through a wall.

Server role: the **non-combat power spike**. All four active abilities deal **zero
damage**. The Sequence wins fights by making them not happen, and feeds the brewing
economy by taking ingredients instead of gathering them. Counterweight to
Justiciar/Tyrant (damage); sibling — not clone — of Visionary (perception).

Progression is healthy: 4 actives carry mastery, 3 passives carry identity.

## Ability roster (all 7 Sequence 8 wiki items accounted for)

| # | Wiki item | Ability class | Type | Milestone |
|---|-----------|---------------|------|-----------|
| 1 | Charm (Swindler's Charm) — trait | `SwindlerCharm` (rewritten) | permanent passive | M7 |
| 2 | Physical Enhancement — trait | `PhysicalEnhancement` (reused, `error_physique`) | passive progression | M4 |
| 3 | Eloquence — new | `Eloquence` (new) | active — single-target persuasion | M8 |
| 4 | Thought Misdirection — new | `ThoughtMisdirection` (new) | active — cone redirect | M9 |
| 5 | Mental Disruption — new | `MentalDisruption` (new) | active — single-target illusion | M10 |
| 6 | Superior Observation — strengthened | `SuperiorObservationII` (new, shared identity) | permanent passive | M6 |
| 7 | Theft — strengthened | `MaterialTheft` (new) | active — through-wall ingredient theft | M11 |

Inherited from Seq 9, untouched: `ShadowTheft`, `CombatProficiency`.

**Not implemented, deliberately:** the *Pathway Versions Difference* paragraph (Amon's
variant lacks the Superior Observation upgrade until Seq 4). It is a lore variant, not
an ability; the plugin models one canonical version per pathway. If pathway variants are
ever added, ability #6 is the single switch point.

## Balance numbers (all live in `SwindlerInfluence`, scaled by Sequence)

| Ability | Cost | Cooldown | Duration | Range | Scaling |
|---|---|---|---|---|---|
| 1 Charm | 0 | — | permanent | 16 (aggro check) | aggro-cancel 25% base `WEAK`; trade discount capped 30% |
| 2 Physique | 0 | — | permanent | self | hpBase 4 (Seq 9 = 3) + `SPEED` |
| 3 Eloquence | 35 | 45 s | 8 s base | 12, LoS required | duration `STRONG` |
| 4 Misdirection | 30 | 30 s | 6 s (mob re-target) | cone 10 / 90° | duration `MODERATE`; player scramble 70% |
| 5 Disruption | 45 | 60 s | 8 s base | 15, LoS required | duration `MODERATE`; sanity +3 `WEAK` on Beyonder victims |
| 6 Observation II | 0 | — | permanent | gaze 15; valuables 10 | reveal gate: `targetSeq >= casterSeq` |
| 7 Material Theft | 50 | 40 s | instant | **5, no LoS** | cooldown `MODERATE` |

Valuables radius stays **10** — the wiki only widens it to 50 m at Prometheus (Seq 6).
Sanity-checked against siblings: `ShadowTheft` 55/30 s, `Singing` 50/20 s,
`SurgeOfInsanity` 80.

Sequence-resistance roll (`getSequenceCheckTarget`): **Eloquence** and **Mental
Disruption** only.

## Design decisions locked with the user

- **Theft range = 5 blocks**, no line of sight, ingredients only, one item per cast,
  containers included. This is the lore upgrade (Hazel stealing through dirt).
- **Cut: somersault-dodge** on ability #2. `DangerIntuition` (fool) already owns
  projectile auto-dodge and `ClownAgility` (fool Seq 8) owns fall immunity; a third
  "passive chance to cancel incoming damage" would be duplication. `SPEED` + higher HP
  base already delivers the spec's "much more agile and fast".
- **Cut: decoy/afterimage** on ability #4. Speculative; mob redirect + hotbar scramble
  already express "actions, postures, arrangements".
- **Cut: `isSpiritualMaterial` context method.** It would drag `CustomItemService` into
  `BeyonderContext`'s constructor and therefore `ServiceContainer`. Making two stateless
  `CustomItemFactory` methods `static` is a smaller diff and mirrors how `ShadowTheft`
  already reaches `AbilityItemFactory.isAbilityItem`.
- **Cut: `TheftGuard` helper and the `ShadowTheft` edit.** `MaterialTheft` only ever
  touches plugin ingredients, so `ShadowTheft`'s protected list (netherite / elytra /
  totem / echo shard) is irrelevant to it — there is nothing to share. `MaterialTheft`'s
  whole filter is `isCustomItem(item) && !AbilityItemFactory.isAbilityItem(item)`,
  two inline boolean calls. `ShadowTheft` is not touched at all.
- **`SwindlerCharm` is retired as an ActiveAbility.** It currently merges three wiki
  abilities into one class (its own comments label them `Ефект 1/2/3`). Charm becomes a
  passive; Eloquence and Thought Misdirection become their own classes. Its hotbar-scramble
  code is **moved** into `ThoughtMisdirection`, not rewritten.
- **`Agility` (whitetower) is dropped from Error.** A pathway must not borrow another
  pathway's flavour class for a trait it has its own lineage for.

## Differentiation from existing abilities (checked, not assumed)

| Existing | Owns | Why ours is distinct |
|---|---|---|
| `ScanGazePassive` (visionary) | gaze readout of a **player's static vitals** (HP, hunger, armour) | Observation II reads **behavioural state** — is this entity targeting me, is it a Beyonder and of what pathway |
| `BattleHypnotism` (visionary) | active, player-only, target **loses sight of you** | Eloquence: target **can't attack you** and mobs **fight for you**; breaks the instant you attack |
| `SurgeOfInsanity` (visionary) | AoE, **4 hearts real damage**, blindness+weakness | Disruption: single-target, **zero damage**, payload is false sensory information |
| `DangerIntuition`, `ClownAgility` (fool) | projectile dodge, fall immunity, wall-climb | ability #2 adds neither — dodge cut for this reason |
| `ShadowTheft` (error Seq 9) | LoS required, teleports behind, random **vanilla** item | Material Theft: **no LoS**, no teleport, **ingredients only** |

## Existing architecture reused

**Base classes / pipeline** — `ActiveAbility`, `PermanentPassiveAbility`, the final
`execute` pipeline, `SequenceScaler.ScalingStrategy`, `getSequenceCheckTarget`,
`AbilityIdentity` + `AbilityTransformer.canReplace`.

**Classes reused outright**
- `common.abilities.PhysicalEnhancement` — 5-arg explicit-identity ctor. Ability #2 needs
  **zero new code**.
- `CustomItemFactory.isCustomItem` / `getCustomItemId` — the "is this a spiritual material"
  test, made `static`.
- `IDataContext.getTargetAnalysis(UUID)` — currently **dead code** (declared, implemented,
  zero callers repo-wide). Observation II adopts it instead of writing a new scan.
- `PathwayBranding.liquidOf("Error")` — every particle colour.

**Patterns reused**

| Pattern | Reference | Used by |
|---|---|---|
| Event-driven permanent passive: own-key subscription in `onActivate`, `unsubscribeAll(ownKey)` in `onDeactivate`, `Integer.MAX_VALUE` duration | `DangerIntuition`, `ClownAgility` | `SwindlerCharm` |
| Per-caster state in `ConcurrentHashMap<UUID, …>`, never a bare instance field | `ScanGazePassive`, `DangerIntuition` | `SwindlerCharm`, `SuperiorObservationII` |
| Gaze readout: throttle + fire only on target change | `ScanGazePassive` | `SuperiorObservationII` |
| Timed state via `subscribeToTemporaryEvent(…, durationTicks)` — the subscription *is* the lifecycle | `PsychologicalInvisibility` | `Eloquence` |
| Bounded `scheduleRepeating` with elapsed counter | existing `SwindlerCharm` body | `ThoughtMisdirection` |
| Balance numbers in a domain VO + unit test | `DangerPremonition` / `DangerPremonitionTest` | all 7 |

> **Do not copy `CombatProficiency`'s subscription style** — it keys subscriptions by
> `casterId`, so another ability's `unsubscribeAll(casterId)` wipes them. Use the
> own-random-key variant from `DangerIntuition`/`ClownAgility`.

**Contexts used, all existing:** `targeting()`, `entity()`, `events()`, `scheduling()`,
`messaging()`, `effects()`, `playerData()`, `beyonder()`, `glowing()`.

**Sessions / runners created: zero.** All three timed abilities are covered by mechanisms
that already own their lifecycle — `subscribeToTemporaryEvent`'s duration parameter, a
bounded repeating task, and a self-reverting VFX method.

## New classes and why each is unavoidable

| Class | Why not extend something | Single responsibility |
|---|---|---|
| `SwindlerInfluence` (domain VO) | `DangerPremonition`, `HolyAffinity` etc. each hold one pathway's numbers; unrelated. Required by `domain-purity.md` rule 3 — without it the numbers sit in ability classes, the documented anti-pattern | all Seq-8 balance numbers as static sequence-scaled functions |
| `SwindlerCharm` (rewrite in place) | file exists but its `ActiveAbility` base is wrong for a trait | passive disposition of mobs and villagers toward the caster |
| `Eloquence` | `BattleHypnotism` is a different pathway with a different mechanic | suppress one entity's aggression toward the caster |
| `ThoughtMisdirection` | different shape (cone vs single) and mechanism (redirect vs suppress) from `Eloquence` | redirect attention and intended action of everything in a cone |
| `MentalDisruption` | `SurgeOfInsanity` is a different pathway, AoE, and deals real damage | feed one target false sensory information |
| `SuperiorObservationII` | identity replacement **requires two classes** (`ScanGaze`/`ScanGazePassive` precedent); subclassing gives `AbilityTransformer` nothing to swap | Seq-8 valuables sense + behavioural read of a gazed-at entity |
| `MaterialTheft` | `ShadowTheft` has opposite requirements on every axis | pull one spiritual material within 5 blocks, through solid blocks |

**Not created:** no session, no runner, no context interface, no listener, no service, no
config loader, no repository, no helper class.

## Visual effects

One new method in `VisualEffectsContext`; everything else reused.

| # | Ability | Effect | Verdict |
|---|---|---|---|
| 1 | Charm | `playFadingAura(loc, errorColor, 20)` + `spawnParticle(HEART, …)` on a fired aggro-cancel/discount | reused; two layers; deliberately quiet (passive tick) |
| 2 | Physique | none beyond the shared class | reused (nothing added) |
| 3 | Eloquence | `playHelixEffect(casterEyes → targetHead, ENCHANT, 30)` + `playFadingAura(targetFeet, errorColor, 40)` | reused; a helix from mouth to mind fits "words that convince" exactly |
| 4 | Misdirection | `playConeEffect(casterEyes, dir, 90°, 10, ENCHANT, 30)` + `spawnParticleForPlayer(victim, WITCH, …)` | reused; cone primitive matches the ability's geometry |
| 5 | Disruption | **`playPhantomBlocks(…)`** + `spawnParticleForPlayer(victim, WITCH/SMOKE, …)` | **new — justified below** |
| 6 | Observation II | no world effect; actionbar + `glowing().setGlowing(target, caster, colour, ticks)` | reused; a covert read must be invisible to the target — the documented "deliberately subtle" exception |
| 7 | Material Theft | `playTravelingBeam(sourceLoc, casterHand, errorColor, onArrival)` + `playGlowingDust(arrivalPoint, errorColor)` | reused; `playTravelingBeam`'s javadoc describes precisely this, and `onArrival` is where the item lands |

### The one new effect

```
void playPhantomBlocks(UUID viewerId, Location center, Material material,
                       int count, double radius, int durationTicks)
```

- **Nothing existing works:** all 25 current methods emit particles or sound. Not one
  alters what a client believes the *world* looks like. `grep sendBlockChange` over the
  whole repo → **zero hits**. Rendering a hallucination as particles would be a visibly
  weaker, different effect — the "don't degrade quality to reuse" case the rules forbid.
- **Not a duplicate:** no overlap in mechanism or output; nothing to parameterize.
- **Belongs here:** per-viewer visual state, and `spawnParticleForPlayer` already sets the
  per-viewer precedent in this interface.
- **Reusable:** any future illusion power (Fool illusions, Seq-7 Cryptologist, Visionary
  hallucinations) can call it. Material, count, radius, duration all caller-supplied.
- **Owns its own lifecycle** — like `playRisingSpiral`/`playFadingAura`, it holds its own
  `BukkitTask` and reverts every faked block on a hard timer regardless of what happens to
  the caster. **This is why `MentalDisruption` needs no session.**
- **Safety invariants:** never fake a block intersecting the viewer's hitbox (no
  suffocation-illusion softlock); revert on expiry *and* on viewer quit.

## Sounds

| Ability | Sounds |
|---|---|
| 1 Charm | `ENTITY_VILLAGER_YES` (0.6f, 1.2f) — caster-private, on a fired discount/aggro-cancel |
| 2 Physique | none added |
| 3 Eloquence | cast `ENTITY_ILLUSIONER_CAST_SPELL` (1.0f, 1.3f); resolve `ENTITY_VILLAGER_YES` (1.0f, 0.9f) at target; victim-private `BLOCK_NOTE_BLOCK_CHIME` (0.7f, 0.8f) |
| 4 Misdirection | cast `ENTITY_ILLUSIONER_MIRROR_MOVE` (1.0f, 1.1f); victim-private `BLOCK_ENCHANTMENT_TABLE_USE` (1.0f, 2.0f) |
| 5 Disruption | cast `ENTITY_ELDER_GUARDIAN_CURSE` (0.7f, 0.6f); victim-private from random offsets `ENTITY_ENDERMAN_STARE` (0.5f, 0.7f), `BLOCK_STONE_STEP` (0.6f, 0.8f), `ENTITY_ZOMBIE_AMBIENT` (0.4f, 0.5f) |
| 6 Observation II | `BLOCK_NOTE_BLOCK_CHIME` (0.3f, 1.6f) on a new target — private, quiet |
| 7 Material Theft | cast `ENTITY_ENDERMAN_TELEPORT` (0.8f, 0.6f); arrival `BLOCK_AMETHYST_BLOCK_CHIME` (1.0f, 1.4f); victim-private `ENTITY_ITEM_PICKUP` (0.5f, 0.5f) |

In-world via `playSound`; caster/victim-private via `playSoundForPlayer`.

## Data changes — none

- **Configs:** none. `potion-recipes.yml:10-12` already defines Error Seq 8
  (`main: [sphinx_brain]`, `auxiliary: [lavar_octopus_crystal, black_mosquito]`).
  No `config.yml`, `custom-items.yml`, `creatures.yml`, `forage.yml` changes.
- **Persistence:** none. No new JSON store; no ability holds state that must survive a
  restart.
- **Serialization:** none.
- **Player data:** none beyond what `Beyonder` already tracks. `MentalDisruption` calls the
  existing `beyonder().updateSanityLoss` on a Beyonder victim; that field already persists.
- **`plugin.yml` / `ServiceContainer`:** no change.
- **MythicMobs creature kits:** deliberately **not** mirrored — all four actives are
  control/utility with no direct damage, which the ability-design skill classifies as
  not-mirrored. Conscious skip, not an oversight.
- **Docs / rules:** none planned. No new cross-cutting mechanic is introduced;
  `playPhantomBlocks` is a VFX primitive already governed by `visual-effects-reuse.md`, and
  `CLAUDE.md` does not enumerate abilities. If M3 shows phantom-block illusions want their
  own safety rule, add it then — not pre-emptively.

## Files affected

**New (7)**
```
src/main/java/me/vangoo/domain/valueobjects/SwindlerInfluence.java
src/test/java/me/vangoo/domain/valueobjects/SwindlerInfluenceTest.java
src/main/java/me/vangoo/pathways/error/abilities/Eloquence.java
src/main/java/me/vangoo/pathways/error/abilities/ThoughtMisdirection.java
src/main/java/me/vangoo/pathways/error/abilities/MentalDisruption.java
src/main/java/me/vangoo/pathways/error/abilities/SuperiorObservationII.java
src/main/java/me/vangoo/pathways/error/abilities/MaterialTheft.java
```

**Modified (6)**
```
src/main/java/me/vangoo/pathways/error/Error.java                              registration; drop Agility import
src/main/java/me/vangoo/pathways/error/abilities/SwindlerCharm.java            ActiveAbility → PermanentPassiveAbility
src/main/java/me/vangoo/pathways/error/abilities/SuperiorObservation.java      + getIdentity; fix shared tickCounter; fix cube scan
src/main/java/me/vangoo/domain/abilities/context/IVisualEffectsContext.java    + playPhantomBlocks
src/main/java/me/vangoo/application/services/context/VisualEffectsContext.java + impl
src/main/java/me/vangoo/infrastructure/items/CustomItemFactory.java            2 methods → static
```
Plus whatever call sites the compiler surfaces for the static change (`CustomItemRegistry`
at minimum). **`ShadowTheft.java` is not touched.**

## Two pre-existing bugs fixed on the way

Both live in `SuperiorObservation`, which M5 rewrites anyway:

1. **`SuperiorObservation:42` — `private int tickCounter = 0`** is a shared instance field
   on an ability object shared per-pathway. With N Error players it advances N× per tick
   round, so the 40-tick interval silently becomes 40/N. `lastAlertTime` is correctly keyed
   by UUID; `tickCounter` is not. → per-UUID map, as `ScanGazePassive` does.
2. **`scanForValuableOres`** walks a 21×21×21 cube = **9 261 `getBlockAt` calls every 2 s
   per player** on the main thread. → sampled shell (~200 points) or radius 6. Do not carry
   the cube forward.

## Milestones

Each independently buildable; most touch 1–3 files.

**M1 — `SwindlerInfluence` VO + test**
- Files: 2 new (`domain/valueobjects/SwindlerInfluence.java`, matching test).
- Architecture: pure domain, zero Bukkit, zero runtime impact. Follows
  `DangerPremonition`.
- Ponytail: one file holds every number for seven abilities; no per-ability config class.
- Verify: `mvn test -Dtest=SwindlerInfluenceTest`.

**M2 — `CustomItemFactory.isCustomItem` / `getCustomItemId` → `static`**
- Files: `infrastructure/items/CustomItemFactory.java` + call sites (≈1–3).
- Architecture: makes the ingredient check reachable from the pathways layer with no
  wiring, mirroring `AbilityItemFactory.isAbilityItem`.
- Ponytail: two-word diff instead of a new context method + `ServiceContainer` churn.
- Verify: `mvn -q -DskipTests compile`.

**M3 — `playPhantomBlocks`**
- Files: `IVisualEffectsContext.java`, `VisualEffectsContext.java`.
- Architecture: one new primitive, self-owning task, reusable by any future illusion.
- Ponytail: the only genuinely absent capability in the repo; everything else reuses.
- Verify: compile; smoke-tested in-server at M10.

**M4 — Physique swap + drop `Agility`**
- Files: `pathways/error/Error.java` only.
- Architecture: Seq 9's `PhysicalEnhancement` gains identity `error_physique`; Seq 8 gets
  the upgraded instance (hpBase 4 + `SPEED`). `AbilityTransformer` replaces, not stacks.
- Ponytail: zero new classes; two constructor arguments.
- Verify: in-server — advance to Seq 8, confirm **one** physique passive in the menu.

**M5 — `SuperiorObservation` hardening**
- Files: `pathways/error/abilities/SuperiorObservation.java`.
- Architecture: adds `getIdentity()`, fixes the two bugs above.
- Ponytail: fixes at the shared source, not per-caller.
- Verify: in-server at Seq 9 — readout still fires; two players don't skew each other's
  interval.

**M6 — `SuperiorObservationII` + register**
- Files: 1 new, `Error.java`.
- Verify: in-server — advance 9→8, old passive **replaced** not duplicated; gaze readout
  reports aggro/pathway; reveal gate respected.

**M7 — `SwindlerCharm` rewritten as passive + register**
- Files: `SwindlerCharm.java`, `Error.java`.
- Architecture: `ActiveAbility` → `PermanentPassiveAbility`; own-key event subscription.
- Verify: in-server — hostile mobs sometimes fail to acquire you; villager discount
  applies; iron golems stay neutral.

**M8 — `Eloquence` + register**
- Files: 1 new, `Error.java`.
- Verify: in-server — mob stops attacking you and fights another mob; a player can't damage
  you until you swing first; a higher-Sequence Beyonder resists.

**M9 — `ThoughtMisdirection` + register**
- Files: 1 new, `Error.java`.
- Architecture: hotbar-scramble code **moved** from the old `SwindlerCharm`.
- Verify: in-server — cone of mobs re-targets each other; victim's held slot jumps.
- Constraint: only change the *selected* slot and swap main/off-hand. Never move items
  between slots — that is dupe surface.

**M10 — `MentalDisruption` + register** (consumes M3)
- Files: 1 new, `Error.java`.
- Verify: in-server — fake walls appear for the victim only, revert on time, no
  suffocation; caster sees nothing; sanity ticks up on a Beyonder victim.

**M11 — `MaterialTheft` + register**
- Files: 1 new, `Error.java`.
- Architecture: filter is `isCustomItem(item) && !AbilityItemFactory.isAbilityItem(item)`,
  inline. No helper class. No `ShadowTheft` change.
- Verify: in-server — pull an ingredient from a chest behind a wall at 5 blocks; confirm a
  diamond and an ability item are both refused; confirm 6 blocks is out of range.

**M12 — Final pass**
- Files: `Error.java` tidy; whatever the build surfaces.
- Verify: `mvn clean package` green, `ArchitectureTest` green, JAR loads on the server,
  all 9 Seq-8 menu entries render (pagination was added Jul 28 — worth an eyes-on check).

M1–M3 are foundation, any order. M4 is the cheapest visible win. M6 must follow M5;
M10 must follow M3.

## Branch

Per `.claude/rules/git-workflow.md`, this whole topic — plan, code, tests — lives on one
branch created from fresh `main` **before** the first line of code.

Blocked right now: the working tree carries **74 uncommitted entries** of in-progress
Tyrant work on branch `tyrant-pathway`. This plan file is untracked and survives a branch
switch, so it is safe where it is, but the branch must be created before M1 starts.
Options, user's call: (a) commit/stash the Tyrant work first, then branch from `main`;
(b) branch from current `HEAD` and accept Tyrant work riding along; (c) finish Tyrant
first. Nothing will be committed without an explicit ask.

## Out of scope / follow-ups

- **Amon pathway variant** for ability #6 — needs a pathway-variant system that does not
  exist.
- **`CombatProficiency`'s fragile subscription style** (`casterId`-keyed; `registeredPlayers`
  cleared only in `cleanUp()`, not `onDeactivate`) — inherited into Seq 8 but not in scope.
  The new passives use the safer own-key pattern, which will make the inconsistency visible.
- **Villager price manipulation** (ability #1) is the least certain implementation detail
  (`MerchantRecipe` is fiddly). If it proves unstable, drop to the aggro-cancel half only —
  the ability still stands.
- **Creature kit mirroring** for the Seq-8 actives, if Error mobs should ever gain them.
