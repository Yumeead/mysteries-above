# Error Pathway — Sequence 5 (Dream Stealer / Крадій Снів) Implementation Plan

Branch: `error-pathway` (continues the Sequence 6/7/8 branch — same topic, same branch, per
`.claude/rules/git-workflow.md`). **No commits are to be made** unless explicitly asked;
the Sequence 6 work already in the working tree stays uncommitted alongside this.
Wiki source: `docs/pathways/error/Error PathwayAbilities  Lord of the Mysteries Wiki  Fandom.md`,
lines 124–165.
Rule: one milestone at a time; before each — state affected files, architectural impact,
why it is Ponytail; after each — summary + in-server test + durable decisions to memory.
Wait for confirmation between milestones.

## Gameplay role

Sequence 9 was **hands** (take the object). Sequence 8 was **minds** (sell a false
reality). Sequence 7 was **truth** (turn scraps into knowledge). Sequence 6 was
**appropriation** (take what the victim owns and wield it). Sequence 5 is
**conceptualization**: Theft stops needing a physical referent. A thought, a dream, an
incoming blow, the ability to walk, a heart, an ideal — all become objects that can be
lifted.

Server role: the **denial tier**. Prometheus turned another player's build against them;
Dream Stealer removes a capability from the fight altogether — mobility, flight, casting,
max health — and forges an identity it does not own. It is also the first Error tier that
touches the church subsystem, giving the pathway a non-combat outlet that no other pathway
has: entering a church without joining it.

Progression is healthy: 2 new actives carry mastery, 1 new passive tier carries identity,
and three inherited abilities (`PowerTheft`, `Decryption`, `PhysicalEnhancement`) grow in
place instead of accumulating near-duplicates.

---

## Part I — Wiki extraction (verified complete)

The Sequence 5 section contains 1 New Ability and 2 Strengthened Abilities. It has **no
Traits section** — unlike Sequence 6, which had Physical Enhancement and Mental Resistance.
The Theft umbrella expands into seven distinct manifestations, kept separate here per the
no-merging rule.

### A1. Disguise (Theft of Heavenly Secrets) — New Ability
> "They can **Disguise** themselves as a follower of a True Deity, bypass defenses,
> **Steal** prayer responses, and create all sorts of Ritualistic Magic."

Sub-points (part of the same entry, not separate abilities):
- "In battle, through the use of longer incantations, pre-arrangements, or the **Stealing**
  of prayer responses, they can get some kind of enhancement or use some kind of abilities."
- Harrison completed "Theft of the Secrets of Heaven", accessing powers from the Samaritan
  Woman's Spring; the last two incantation lines were *"Steal the Secrets of Heaven"* and
  *"Swift as a decree, be driven"*.
- "Through **Disguising** themselves as a follower of a True Deity, a **Dream Stealer** is
  able to create **Charms** of that corresponding True Deity's domain."
- "The higher the intelligence of the corresponding True Deity, the greater the probability
  for the **Disguise** to fail."

Type: Active / Ritual. Combat + non-combat.
Ambiguity: the wiki never states whether Disguise is a lasting state or a one-shot rite,
and gives no duration.

### B1. Theft (Stealing) — Strengthened, umbrella
> "A **Dream Stealer's** **Theft** has become more conceptualized, now allowing them to
> utilize **Theft** on thoughts, intentions, general abilities, knowledge, ideals, attacks,
> memories, and **Dreams** within _80 meters range_."

#### B1.1 — Stealing / storing / transferring Dreams (unnamed)
> "Though they are unable to enter **Dreams**, **Dream Stealers** are able to infuse scenes
> within the **Dream** to influence their target's future decisions and actions." /
> "This is done by **Stealing**, storing, and transferring **Dreams**." /
> "Through this they can also disrupt **Dream Divination**."

Type: Active / non-combat utility.
Ambiguity: "influence future decisions" has no direct Minecraft referent.

#### B1.2 — Thought Usurpation (the only named sub-ability)
> "They can **Steal** the **Thoughts** the opponent is about to put into practice, causing
> them to experience a brief state of dullness." / "It is possible for the targets to
> completely miss the fact that **Thoughts** were **Stolen** by them, unless if they were to
> think about it carefully." / "At this stage, one cannot retain the **Stolen** thoughts and
> must instead perform the corresponding action in place of the **Stolen** target."

Type: Active / combat. Lore: Mobet Zoroast stole Ulyssan's intent to breathe an attack —
Mobet spat, Ulyssan was left confused.

#### B1.3 — Stealing general abilities (unnamed)
> "They can **Steal** certain general abilities, such as the ability to walk, fly, speak, or
> even the ability to speak particular language." / "However, those as well will take one of
> the **Theft** slots."

Type: Active / combat. Lore: Anderson Hood stole the ability to speak Dutanese via an
Error-related Mystical Item.

#### B1.4 — Stealing attacks (unnamed)
> "They can **Steal** the attacks targeting them in order to nullify them, and unless the
> attack itself has a side effect on the user, there won't be any side effect on the
> **Dream Stealer**." / "They can **Steal** a **Reaper's** **Cull** or **Flame** without
> their body being burned by the **Flames**."

Type: Active (reactive) / combat.
Ambiguity: reaction to one specific attack vs. an absorption window.

#### B1.5 — Stealing Beyonder powers (strengthened Prometheus theft)
> "They can **Steal** **Beyonder** powers within _80 meter range_." / "The corresponding
> **Stolen** **Beyonder** power can be used for _half an hour_, but it cannot be used in
> different time periods." / "If a **Dream Stealer** has not yet used the **Stolen Beyonder**
> power when they first got it, the power itself can be kept by them for a _week_." /
> "Within that time, the victim of the **Theft** cannot use the **Stolen** ability again
> unless they drink another corresponding **Beyonder** potion."

Type: Active / combat. Direct upgrade of the existing Seq-6 `PowerTheft` (50 m / 10 min).

#### B1.6 — Stealing parts of life (unnamed)
> "They can **Steal** things such as an organ or a fetus from a mother's womb."

Type: Active / combat. Ambiguity: Minecraft has no organs; needs substitution.

#### B1.7 — Stealing an ideal (unnamed)
> "By **Stealing** a target's ideal they can make the target lose motivation."

Type: Active / combat debuff.

### B2. Decryption — Strengthened
> "They are able to **Decrypt** Bizarro Sorcerer-level **Illusions**."

Lore: Hazel Macht recognized, *after* it ended, that she had been thrown into an illusion
lasting nearly ten seconds — not a dream, not a passing hallucination.

### Pathway version difference (context, not an ability)
> Amon's version removes **Theft of Heavenly Secrets** (partially restored at Sequence 4,
> fully at Sequence 3), and its users cannot keep stolen abilities; they do regain the
> second type of Theft that Prometheus lacked.

### Completeness check
Re-read line by line. **Every ability mentioned on the wiki is included**: 10 specification
entries — A1, the B1 umbrella, B1.1–B1.7, B2. No Traits section exists for Sequence 5, so
no passive trait was omitted. Nothing merged, nothing summarized away.

---

## Part II — Design decisions (settled by interview)

| # | Question | Decision |
|---|----------|----------|
| 1 | How to implement the 7 Theft manifestations | **One `ConceptualTheft` ability with a 6-mode menu**, plus tiers inside `PowerTheft` and `Decryption` |
| 2 | Scope of Disguise | **All three consequences in one ability** — church access, domain buff, ritual magic |
| 3 | "Kept for a week if unused" | **Implement fully, with a compressed week** — `firstUsedAt` mark, 6 h hold |
| 4 | Attack theft semantics | **Absorption window + charge** thrown back on the next hit |
| 5 | Dream theft | **Steal + disrupt divination**, exploit-proof |
| 6 | Dream carrier | **Not an item — an invisible slot in the ability.** Dupe / drop / chest / trade / death-keep are impossible by construction |
| 7 | Organ theft | **Heart theft = max-health theft** |
| 8 | Slot economy | **One shared slot, occupied only by "lasting" thefts**; instant modes are gated by cooldown alone |
| 9 | Decryption Seq 5 | **Passive auto-dispel on self** |
| 10 | General abilities | **Walk, Fly, Speak (casting only)** — chat is never muted |
| 11 | Ideal theft | **Both effects**: apathy debuff *and* spirituality transfer |
| 12 | Physique at Seq 5 | **Yes** — next `error_physique` tier, a deliberate deviation from the wiki |

---

## Part III — Ability roster

| # | Wiki item | Class | Type | Milestone |
|---|-----------|-------|------|-----------|
| A1 | Disguise (Theft of Heavenly Secrets) | `TheftOfHeavenlySecrets` (new) | active / ritual | M10 |
| B1 | Theft — umbrella (80 m, conceptual) | *(expressed by the entries below)* | — | — |
| B1.1 | Dreams | `ConceptualTheft` mode «Сон» | active, holds slot | M6 |
| B1.2 | Thought Usurpation | `ConceptualTheft` mode «Думка» | active, instant | M4 |
| B1.3 | General abilities | `ConceptualTheft` mode «Загальні здатності» | active, holds slot | M7 |
| B1.4 | Attacks | `ConceptualTheft` mode «Атака» | active, instant | M5 |
| B1.5 | Beyonder powers | `PowerTheft` (existing, Seq-5 tier in place) | active, holds slot | M3 |
| B1.6 | Organ | `ConceptualTheft` mode «Серце» | active, holds slot | M8 |
| B1.7 | Ideal | `ConceptualTheft` mode «Ідеал» | active, instant | M4 |
| B2 | Decryption | `Decryption` (existing, Seq-5 tier in place) | permanent passive | M9 |
| — | *(deviation, decision 12)* | `PhysicalEnhancement` (`ERROR_PHYSIQUE`, lvl 6) | permanent passive | M11 |

Inherited untouched: `ShadowTheft`, `CombatProficiency`, `SwindlerCharm`, `Eloquence`,
`ThoughtMisdirection`, `MentalDisruption`, `MaterialTheft`, `MentalFortitude`,
`SuperiorObservation`.

`sequenceAbilities.put(5, …)` therefore lists **three entries only** — `ConceptualTheft`,
`TheftOfHeavenlySecrets` and the level-6 `PhysicalEnhancement`. `PowerTheft` and
`Decryption` are *not* re-registered: their Sequence 5 tier is a branch inside the already
inherited instance, exactly as `MaterialTheft` and `SuperiorObservation` were handled at
Sequence 6.

---

## Part IV — Minecraft adaptation

| Ability / mode | Adaptation | Cost | Cooldown | Duration | Range | Slot |
|---|---|---|---|---|---|---|
| **Disguise** | Lit-candle altar (`RitualMagic` convention) → church menu → incantation → disguise session. Grants church membership, domain buff, Ritual Magic | 140 | 600 s | 10 min | self | no |
| **Сон** (B1.1) | Target cannot sleep; a second cast infuses the dream into a third party (scene + buff/debuff) | 70 | 90 s | 30 min hold | 80 m | **yes** |
| **Думка** (B1.2) | 3 s dullness — Slowness III + Mining Fatigue + cast lock; caster performs the stolen intent as a forward lunge | 70 | 90 s | 3 s | 80 m | no |
| **Загальні здатності** (B1.3) | Submenu: Walk (rooted, may still attack) / Fly (flight and elytra revoked) / Speak (cast lock; chat untouched). Caster mirrors what was taken | 70 | 90 s | 20 s | 80 m | **yes** |
| **Атака** (B1.4) | 5 s window; first incoming damage fully cancelled with its side effects, magnitude banked; next hit within 10 s adds it (cap 20.0) | 70 | 90 s | 5 s + 10 s | self | no |
| **Крадіжка сили** (B1.5) | Existing `PowerTheft`, Seq-5 tier: 50→80 m, 10→30 min, suppression 30→60 min, unused power held up to 6 h | 90 | scaled | 30 min / 6 h | 80 m | **yes** |
| **Серце** (B1.6) | 4 hearts of max health moved from victim to caster via `addTransientModifier` | 70 | 90 s | 60 s | 80 m | **yes** |
| **Ідеал** (B1.7) | Apathy (Weakness + Mining Fatigue + no sprint + no XP) **and** 15 % of the victim's *current* spirituality transferred | 70 | 90 s | 60 s | 80 m | no |
| **Розшифрування** (B2) | Passive: a hostile illusion on the caster collapses after 2 s | — | — | passive | self | no |

### What cannot be reproduced exactly, and why

- **B1.6 (organ / fetus)** — Minecraft has neither organs nor pregnancy. Closest faithful
  mapping of "the aspects of Life" is maximum health. Wiki name preserved in the spec.
- **B1.1 "infuse a scene to influence future decisions"** — a plugin cannot steer a human
  player's decisions. Replaced with a material consequence: a scene (titles + effects) plus
  a buff/debuff that genuinely changes what is advantageous next.
- **B1.3 "speak a particular language"** — the server has one language; the sub-point
  dissolves into "Speak".
- **A1 "intelligence of the True Deity"** — no deity entities exist; substituted with the
  institution's standing from `InstitutionRegistry` (higher church → higher failure chance).

### Dream divination disruption — emergent, not coded

Stealing a dream forbids the victim from entering a bed. `DreamTraversal` filters on
`Player::isSleeping` and `Guidance` requires `target.isSleeping()`; therefore dream
divination against that victim fails on its own. **No divination gate is written** — the
wiki behaviour falls out of the sleep block. This is deliberate: a name-based or
type-based divination classifier would be fragile and is not needed.

---

## Part V — Architecture

### Reused as-is

| Need | Existing mechanism |
|---|---|
| Target / mode menus | `IUIContext.openChoiceMenu` + `AbilityResult.deferred()` + `AbilityResourceConsumer.consumeResources` (the `PowerTheft` flow) |
| Targets incl. creatures | `ITargetContext.getNearbyPlayers/getNearbyEntities` + `IBeyonderContext.getCreatureBeyonder` |
| Persistent slot, absolute time | `TheftLedger` + the existing 10 s sweep in `ServiceContainer.startSchedulers()` |
| Cast lock («Говорити», «Думка») | `AbilityLockManager.lockPlayer(uuid, seconds)` — **no new manager** |
| Incoming-damage reaction | `IEventContext.subscribeToTemporaryEvent(playerId, EntityDamageEvent.class, …)` |
| Sleep block | same `subscribeToTemporaryEvent` on `PlayerBedEnterEvent` |
| Altar + line-by-line incantation | `RitualSession` + the `RitualMagic.countLitCandles` pattern |
| Ritual Magic under disguise | `Beyonder.addOffPathwayAbility(new RitualMagic())` — the same carrier that already holds stolen powers |
| Colour | `PathwayBranding.liquidOf(...)` — never hardcoded |
| Seq-5 physique | another `PhysicalEnhancement` with identity `error_physique` — **not a new class** |

### New classes — exactly four

| Class | Why an existing one cannot be extended | Single responsibility |
|---|---|---|
| `domain.valueobjects.DreamStealerTheft` | `PrometheusTheft` is documented as "Sequence 6 numbers"; mixing Seq 5 in turns it into a constant dump | Sequence 5 balance numbers + unit test |
| `pathways.error.abilities.ConceptualTheft` | A new active with six modes; no existing ability carries this menu | Mode menu + execution of the six modes |
| `pathways.error.abilities.TheftOfHeavenlySecrets` | The only genuinely new wiki ability; a disguise rite matches nothing existing | Incantation-disguise + its three consequences |
| `domain.abilities.context.IChurchContext` (+ `ChurchContext`) | An ability may not import `application.services`; precedent is `IContractContext` for `ContractService` | 3 methods: `disguiseAs`, `dropDisguise`, `institutions` |

**Not created:** no new manager, listener, repository, store, or standalone session class.
`ConceptualTheft` keeps an **instance** `Map<UUID, …>` of live states (session rule — never
`static`), timers come from `TheftLedger` and `subscribeToTemporaryEvent`, and `cleanUp()`
cancels everything non-destructively (it fires on every quit).

### Conflicts found up front

1. **`PhysicalEnhancement` drives health through `setBaseValue`, not modifiers.** If heart
   theft also touched `setBaseValue` the two would overwrite each other and leave a player
   stuck with a broken maximum. → the «Серце» mode uses **`addTransientModifier` only**;
   the base value stays `PhysicalEnhancement`'s alone. Transient modifiers are not
   persisted, so a restart heals any leak by itself (confirmed against Paper 1.21.11 docs;
   note `getModifier(UUID)` is deprecated for removal — use `getModifier(Key)`).
2. **`TheftLedger.Theft.expiresAt()` hardcodes `PrometheusTheft.STOLEN_DURATION_MILLIS`.**
   Sequence 5 and the conceptual thefts need different durations. → add `holdMillis` and
   `firstUsedAt` to the record; records with `holdMillis == 0` fall back to the Prometheus
   duration, so existing `theft.json` files keep loading.
3. **`ChurchService.membershipOf` is the single source of truth on membership**
   (`ChurchMenu`, `ChurchCommand`, `SecretOrderService`, `AbilityMenu` all call it). The
   disguise hooks in **there**, not into each consumer — one branch, every consumer follows.
4. **`Ability.cleanUp()` fires on every player quit**, so session teardown must never hand
   anything back to a victim as a side effect.

### Slot semantics

One shared slot per thief, held only by *lasting* thefts: Beyonder power **or** dream
**or** general ability **or** heart. Instant modes (Думка, Атака, Ідеал) never occupy it.
Conceptual thefts are written to `TheftLedger` under synthetic identities
(`conceptual:dream`, `conceptual:walk`, `conceptual:heart`, …); the sweep's revoke callback
is a no-op for those (there is no such ability to remove) and simply frees the slot.
Victim-side effects are self-expiring by construction — potion effects carry their own
duration, `AbilityLockManager` is in-memory, transient attribute modifiers die with the
server — so **the ledger never has to restore a victim**.

---

## Part VI — Visual effects

One new reusable method in `IVisualEffectsContext` / `VisualEffectsContext`:

```java
/** Куполоподібна оболонка, що слідує за сутністю: вікна поглинання, щити, кокони. */
void playWardingShell(UUID entityId, Color color, double radius, int durationTicks);
```

Justification: no existing primitive gives a **sphere that travels with the player**.
`playSphereEffect` is anchored to a static `Location`, `playPersistentHalo` is a flat halo,
`playTrailEffect` is a trail. Stretching any of them over a 5 s absorption window would
degrade the visual to save a method, which `.claude/rules/visual-effects-reuse.md`
explicitly forbids. The method is parameterized (colour, radius, duration) and will serve
future shields.

Everything else reuses existing primitives, two or three layers per ability, coloured from
`PathwayBranding.liquidOf("Error")` plus the victim's pathway colour:

- **Крадіжка сили (Seq 5)** — `playOrbitingMotes` on the victim → `playConeEffect` from the
  caster's eyes → `playTravelingBeam` home (existing chain, longer distances).
- **Думка** — `playDustMark` above the target's head → `playTravelingBeam` → `playExplosionRingEffect` at their feet.
- **Атака** — `playWardingShell` for 5 s → on absorption `playExplosionRingEffect` + `playGlowingDust`; the charge shows as `playPersistentHalo`, the release as `playSurgingWave`.
- **Сон** — `playRisingSpiral` off the sleeper → `playTravelingBeam` → `playOrbitingMotes` around the holder.
- **Ідеал** — `playVortexEffect` downward around the target → grey `playFadingAura` → `playAlertHalo`.
- **Серце** — red `playTravelingBeam` + `playGlowingDust` on the caster, `playAlertHalo` + `playDustMark` on the victim.
- **Загальні здатності** — `playGroundTrail` from target to caster + `playCircleEffect` shackles under the target.
- **Личина** — `playScriptureAura` in the church's pathway colour + `playPersistentHalo` for the whole session; failure adds `playExplosionRingEffect` + `playHolyLightning`.
- **Розшифрування ілюзії** — `playExplosionRingEffect` around self + `playDustMark` (glass falling away).

Reminder from memory: `playExplosionRingEffect` accepts **`Particle.DUST` only** — anything
else crashes every tick.

## Part VII — Sounds

| Moment | Sound |
|---|---|
| Conceptual Theft cast | `ENTITY_ILLUSIONER_PREPARE_MIRROR` + `BLOCK_SCULK_CATALYST_BLOOM` |
| Theft succeeds | `ITEM_TRIDENT_RETURN` + `BLOCK_AMETHYST_BLOCK_CHIME` |
| Theft fails | `ENTITY_ITEM_BREAK`, low pitch |
| Absorption window opens / absorbs | `BLOCK_CONDUIT_ACTIVATE` / `ITEM_SHIELD_BLOCK` + `BLOCK_BEACON_POWER_SELECT` |
| Charge released | `ENTITY_WARDEN_SONIC_BOOM` (quiet, 0.6f) |
| Dream stolen / infused | `ENTITY_PHANTOM_AMBIENT` + `BLOCK_AMETHYST_BLOCK_RESONATE` |
| Heart torn out | `ENTITY_PLAYER_HURT` + `BLOCK_SCULK_SHRIEKER_SHRIEK` |
| Disguise holds / breaks | `BLOCK_BEACON_ACTIVATE` + `ENTITY_ELDER_GUARDIAN_CURSE` / `BLOCK_BELL_RESONATE` + `ENTITY_LIGHTNING_BOLT_THUNDER` |
| Illusion collapses | `BLOCK_GLASS_BREAK` + `BLOCK_AMETHYST_BLOCK_RESONATE` |

---

## Part VIII — Balance

- **The single slot** carries one lasting theft at a time. That is the wiki's "one slot
  until Sequence 4", and it also prevents stacking four debuffs on one victim.
- **Instant modes** cost 70 spirituality on a 90 s cooldown — roughly one cast per fight.
- **Success chance** reuses the `successChance(victim-pathway recipes, victim sequence)`
  shape with a Sequence 5 base: a stronger victim still resists.
- **A miss still costs resources**, consistent with `PowerTheft`.
- **The 6 h hold on an unused power** is paid for by the slot staying occupied that whole
  time — nothing else can be stolen.
- **Heart theft**: 4 hearts for 60 s is a large swing, so it consumes the single slot, and
  it moves `MAX_HEALTH` by transient modifier, so a restart undoes it.
- **Ideal theft** transfers **15 % of the victim's *current* spirituality** — current, not
  maximum, so a drained victim is not a better target than a full one, which would be
  absurd. Transferred amount is clamped to the thief's own missing spirituality; the
  overflow burns, so a friendly target cannot be farmed as a battery. Against a non-Beyonder
  only the apathy applies. At Sequence 5 (pool 3000–5000) that is roughly 450–750 points per
  90 s — meaningful but not self-financing, since the cast still costs 70 and burns the
  cooldown that could have taken a heart or a power.
- **Disguise failure** is punished: exposure, `playHolyLightning`, and the 10 min cooldown,
  so spamming it is unprofitable.
- **Sanity price**: +1 corruption per successful conceptual theft (consistent with the rest
  of Error), +3 for a Disguise.

---

## Part IX — Files touched

**New (7):**
```
src/main/java/me/vangoo/domain/valueobjects/DreamStealerTheft.java
src/main/java/me/vangoo/domain/abilities/context/IChurchContext.java
src/main/java/me/vangoo/application/services/context/ChurchContext.java
src/main/java/me/vangoo/pathways/error/abilities/ConceptualTheft.java
src/main/java/me/vangoo/pathways/error/abilities/TheftOfHeavenlySecrets.java
src/test/java/me/vangoo/domain/valueobjects/DreamStealerTheftTest.java
docs/superpowers/plans/error-seq5.md                      (this file)
```

**Modified (11):**
```
pathways/error/Error.java                               + sequenceAbilities.put(5, …)
pathways/error/abilities/PowerTheft.java                + Sequence 5 tier
pathways/error/abilities/Decryption.java                + illusion auto-dispel passive
infrastructure/theft/TheftLedger.java                   + holdMillis / firstUsedAt (compatible)
domain/abilities/context/IVisualEffectsContext.java     + playWardingShell
application/services/context/VisualEffectsContext.java  + implementation
application/services/ChurchService.java                 + disguise registry inside membershipOf
domain/abilities/core/IAbilityContext.java              + church()
application/services/BukkitAbilityContext.java          + church()
application/services/AbilityContextFactory.java         + ChurchService pass-through
infrastructure/di/ServiceContainer.java                 + minimal wiring
```

**Docs / rules:** extend `.claude/rules/ability-theft.md` to cover Sequence 5 and the
conceptual thefts (new ledger fields, synthetic identities, slot semantics); update the
persistence section of `CLAUDE.md` for the new `theft.json` fields.

---

## Part X — Milestones

Each is independently testable. One at a time, confirmation between them.

| # | Deliverable | Verification |
|---|---|---|
| **M1** | `DreamStealerTheft` + unit test (durations, ranges, success chance, the 15 % ideal share with both clamps) | `mvn test -Dtest=DreamStealerTheftTest` — pure domain, zero Bukkit |
| **M2** | `TheftLedger`: `holdMillis` + `firstUsedAt` + migration of old records | `TheftLedgerTest` with a deterministic `now` |
| **M3** | Sequence 5 tier in `PowerTheft` (80 m, 30 min, 6 h until first use, 60 min suppression) | in-server: `/pathway` → Seq 5, steal, relog, confirm the timer survived |
| **M4** | `ConceptualTheft` skeleton — target menu, mode menu, slot gate — plus modes **Думка** and **Ідеал** | compiles, `ArchitectureTest` green, in-server check |
| **M5** | `playWardingShell` + mode **Атака** | in-server: absorb a shot, return it with the next hit |
| **M6** | Mode **Сон** (steal, sleep block, infuse into a third party) | in-server: victim cannot enter a bed; `DreamTraversal` cannot reach them |
| **M7** | Mode **Загальні здатності** (Walk / Fly / Speak) | in-server: target rooted / dropped from flight / unable to cast, chat still works |
| **M8** | Mode **Серце** (transient modifier on both sides) | in-server: hearts move, restart restores |
| **M9** | Sequence 5 passive in `Decryption` — illusion auto-dispel | in-server: `IllusionCreation` on the caster collapses in 2 s |
| **M10** | `TheftOfHeavenlySecrets` + `IChurchContext` + disguise registry in `ChurchService` | in-server: under disguise `/church info` reports the foreign church, rituals become available |
| **M11** | Register Sequence 5 in `Error.java` (+ level-6 `PhysicalEnhancement`), rules and docs | `mvn clean package`, full Sequence 5 walkthrough in-server |

## Settled before M1

1. **Branch and commits.** Work continues on `error-pathway`; **nothing is to be committed**
   unless explicitly requested. No new branch is created for Sequence 5.
2. **`IChurchContext` is accepted.** The plan keeps all four new classes and all three
   consequences of A1, including "the church treats you as its own". The fallback of
   dropping that consequence to avoid a new context is **rejected** — do not revisit it.
   The context stays narrow (3 methods, mirroring `IContractContext`), and the disguise
   hooks into `ChurchService.membershipOf` as the single choke point.
