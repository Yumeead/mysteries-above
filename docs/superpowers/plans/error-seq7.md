# Error Pathway — Sequence 7 (Cryptologist / Криптолог) Implementation Plan

Branch: `error-pathway` (continues the Sequence 8 branch — same topic, same branch,
per `.claude/rules/git-workflow.md`).
Wiki source: `docs/pathways/error/Error PathwayAbilities … Fandom.md` (lines 64–85).
Rule: one milestone at a time; before each — state affected files, architectural
impact, why it's Ponytail; after each — summary + in-server test + save durable
decisions to memory. Wait for confirmation between milestones.

## Gameplay role

Sequence 9 was **hands** (take the item, vanish). Sequence 8 was **minds** (talk your
way out, feed a false reality). Sequence 7 is **truth**: the first Error ability whose
output is *knowledge* rather than an effect on someone else.

Server role: the **investigation and counter-intelligence** tier. Decryption deals zero
damage and has no crowd control. It answers questions nothing else on the server can:
who broke this, what is really inside that item, what is currently acting on that
player, what mysticism is hidden nearby. It pays for those answers in sanity, which
routes into the existing rampage chain — knowledge is the only Error resource priced in
mental collapse.

Progression is healthy: 1 new active carries mastery, the strengthened passive carries
identity.

## Ability roster (both Sequence 7 wiki items accounted for)

| # | Wiki item | Ability class | Type | Milestone |
|---|-----------|---------------|------|-----------|
| 1 | Decryption — new | `Decryption` (new, «Дешифрування») | active — 4 target modes | M4–M7 |
| 2 | Superior Observation — strengthened | `SuperiorObservation` (existing, absorbs all tiers) | permanent passive | M3, M8 |

Inherited from Seq 9/8, untouched: `ShadowTheft`, `CombatProficiency`,
`PhysicalEnhancement`, `SwindlerCharm`, `Eloquence`, `ThoughtMisdirection`,
`MentalDisruption`, `MaterialTheft`.

**Not implemented, deliberately:**

- The *Pathway Versions Difference* paragraph (Amon's variant lacks the Superior
  Observation upgrade until Parasite). Lore variant, not an ability — same treatment as
  the identical paragraph at Sequence 8.
- **No Traits at all.** The wiki gives Sequence 7 **no Traits section**: no Physical
  Enhancement (present at Seq 8 and Seq 6) and no Theft strengthening (present at Seq 8
  and Seq 6). Confirmed with the user: follow the wiki exactly, add neither. Sequence 7
  is an intellectual step, not a combat one.

## The one rule the wiki insists on

> *"**Decryption** is not **Divination** or **Prophecy**. A **Cryptologist** needs enough
> information for deduction and they can't make wild guesses."*

This is the only thing separating Decryption from the Fool pathway's divination, so it
is modelled mechanically, not as flavour text: **answer depth scales with how much
evidence actually exists**, computed from `f(evidenceCount, casterSequenceLevel)`.
Thresholds fall as the Sequence improves. Depth 1 is vague, depth 3 is the full truth.

Consequence: Decryption never invents anything. Every line it prints is read from state
the server already holds (CoreProtect history, active potion effects, ability-event
history, item NBT, registry data). Nothing is predicted.

## Balance numbers (all live in `CryptologistInsight`, scaled by Sequence)

| Property | Value | Scaling |
|---|---|---|
| Spirituality cost | 60 | flat |
| Cooldown | 45 s at Seq 7, floor 20 s | `MODERATE` |
| Crosshair range | 20 | flat |
| Zone radius | 24 | flat |
| CoreProtect lookup radius / window | 8 / 600 s | flat |
| Depth thresholds (Seq 7) | d1 ≥ 2, d2 ≥ 5, d3 ≥ 9 traces | thresholds fall with Sequence |
| Sanity loss | d1 = 0, d2 = 2, d3 = 5 | `WEAK` |
| Incantation buff (mode C, d3) | ×1.20 damage / 180 s | `WEAK` |
| Passive evidence-mark radius / interval | 8 / 15 s | flat |

Sanity-checked against siblings: `MentalDisruption` 45/60 s, `MaterialTheft` 50/40 s,
`ShadowTheft` 55/30 s, `MysticalReenactment` 200/60 s. Decryption at 60 sits above the
Seq-8 actives and well below the WhiteTower history-reader, which is correct — it is
stronger than any single Swindler tool and narrower than a full block-history audit.

Sequence-resistance roll (`getSequenceCheckTarget`): **none**. Decryption reads state;
it does not impose anything on a victim, so there is nothing to resist. (Mode A reads a
living target, but reveals only what is already publicly observable in principle — and
deliberately never their pathway or Sequence, see below.)

## Decryption — four modes, one priority chain

Mode is chosen by what the caster is looking at. Locked with the user:

**living target → block in crosshair → mystical item in hand → zone (nothing targeted)**

One `if` chain, no GUI, no mode toggle; the player steers with the crosshair.

### Mode A — living target

Wiki: *"They can **Decrypt** dreams, **Illusions** …"*

| Depth | Reveals |
|---|---|
| d1 | active magical states on the target (potion effects) |
| d2 | + whether the target recently used Beyonder powers |
| d3 | + whether the target is in rampage / mental collapse |

**Hard constraint from the user: never prints pathway or Sequence.** That is already
`SuperiorObservation`'s Sequence 8 tier, and duplicating it here would make the
strengthened passive worthless.

Output: chat block. Effects: `playCircleEffect` around the target + `IGlowingContext`.

### Mode B — block / place

Wiki: *"perception of subtle clues and faint traces"*, *"the location of enemies"*.

| Depth | Reveals |
|---|---|
| d1 | "тут хтось був" + trace count |
| d2 | + the culprit's name |
| d3 | + a trail drawn from the scene to their current position |

**Deliberately not `MysticalReenactment`.** The WhiteTower ability *audits* — it lists
what changed, as holograms, in radius 15. This one *pursues* — it names the actor and
draws the line to where they are now. Different question, different output. Locked with
the user after the overlap was flagged.

Output: holograms at the scene (matching the established `MysticalReenactment` idiom for
place-anchored information) + chat summary. Effects: `playAlertHalo` at the scene,
`playTravelingBeam` scene → culprit.

### Mode C — mystical item in hand

Wiki: *"the secrets behind **Mystical Items** and **Sealed Artifacts**"* and *"the
incantations to activate various charms and **Mystical Items**"*.

| Depth | Reveals |
|---|---|
| d1 | hidden properties — true item id, source pathway, ingredient Sequence |
| d2 | + origin — which loot source / creature / recipe this item comes from |
| d3 | + the incantation — a temporary buff (see caveat) |

**Origin carries zero new state.** Locked with the user: it is *derived from what the
item already is* (NBT id → `CustomItemRegistry` / `IngredientSequenceIndex` / loot
tables), not from per-instance ownership tracking. No NBT stamping, no new repository, no
touching item factories or loot generation.

Output: chat block. Effect: `playScriptureAura` around the caster.

> **Open caveat — the incantation buff.** The user chose "temporary buff to the item's
> effect". **No hook exists** in this codebase to modify a custom item's effect strength
> from another ability; the only amplification service, `IAmplificationContext`, is a
> **damage multiplier only** (`amplifyDamage(playerId, multiplier, durationSeconds)`).
> Plan of record: ship the amplification approximation at M6 with a `ponytail:` comment
> naming the ceiling. Building the real item-effect-modifier hook means changing every
> custom-item ability to consult it — cross-cutting work well outside Sequence 7, to be
> raised as its own task if wanted.

### Mode D — zone

Wiki: *"various mysteries"*.

Radius scan for concealed mysticism: how many Beyonders are near (**count only, no
identity**), where mystic activity happened recently, which containers were disturbed.
Deliberately disjoint from `SuperiorObservation`'s valuables scan — that one finds
*treasure*, this one finds *mysticism*.

Output: holograms at hit points + chat summary. Effects: `playWaveEffect` expanding ring
+ `playAlertHalo` per hit.

## Superior Observation — merge all tiers into one class

Locked with the user, and it deletes code rather than adding it: instead of a third
`SuperiorObservationIII` subclass, the **single existing `SuperiorObservation`** gates
features on the caster's Sequence level and is registered **only at Seq 9** — it is
inherited upward automatically by `AbilityTransformer`.

| Sequence | Behaviour (cumulative) |
|---|---|
| ≤ 9 | valuable ores r6 + players' valuables r10 |
| ≤ 8 | + gaze-reading (intent, Beyonder nature, pathway/Sequence) |
| ≤ 7 | + evidence marking: blocks with recent traces r8, refreshed every 15 s |

`SuperiorObservationII` is **deleted**. The `error_observation` `AbilityIdentity` stays
(it is already registered and persisted), but no longer has any replacement to perform.

The Sequence 7 tier is what feeds Decryption Mode B: the passive finds the evidence, the
active reads it.

> **Performance risk — the single riskiest line in this plan.** CoreProtect lookups hit a
> database. The Seq-7 tier **must** run through `context.scheduling().runAsync` and on a
> 15 s interval (not the passive's 5 s ore scan). A synchronous lookup here would stall a
> populated server. Called out before implementation, not after.

## Naming collision found via Graphify

`me.vangoo.pathways.door.abilities.DecryptPatterns` already exists and is displayed as
**«Розшифровка патернів»** (passive, reports nearby ability usage in radius 30).

Error's Sequence 7 ability is therefore named **«Дешифрування»** — distinct display name,
distinct class name, distinct concept (active, evidence-gated, four modes). Flagged so a
future reader does not "consolidate" the two: they share a translation of *decrypt* and
nothing else.

## Visual effects and sounds

**No new `VisualEffectsContext` methods are needed** — each reuse below was checked
against the lore, not chosen for being nearest:

| Purpose | Method | Why it genuinely fits |
|---|---|---|
| Trail to culprit | `playTravelingBeam` | documented as a beam whose head *crawls* toward the target leaving a wake — literally following a trail |
| Reading an item / incantation | `playScriptureAura` | glyph/inscription aura, the right register for decoding written mysticism |
| Zone scan | `playWaveEffect` + `playAlertHalo` | expanding ring + per-hit marker |
| Living-target read | `playCircleEffect` + `IGlowingContext` | target-anchored, non-invasive |
| Evidence markers (passive) | `playGlowingDust` | per-block subtle marker |

All colours from `PathwayBranding.liquidOf("Error")`, never hardcoded, per
`.claude/rules/pathway-branding.md`.

**Escape hatch:** `playScriptureAura` was built for Sun and may read too *holy* in-server.
If it does, add a dedicated reusable `playCipherGlyphs` to `VisualEffectsContext` rather
than keep a wrong-looking effect — per `.claude/rules/visual-effects-reuse.md`, visual
accuracy beats minimising helper count.

**Sounds:** `BLOCK_BEACON_ACTIVATE` (read begins, pitch rises with depth) →
`ENTITY_ILLUSIONER_MIRROR_MOVE` (truth surfaces) → `BLOCK_NOTE_BLOCK_BIT` per revealed
line → `ENTITY_ELDER_GUARDIAN_CURSE`, low and quiet, on a depth-3 read (the wiki's
*"attention from high-level beings"*).

## The risk clause

Wiki: *"They need to be wary as some **Decryption** efforts may bring attention from
high-level beings."*

Modelled as **sanity loss scaling with depth** (d1 = 0, d2 ≈ 2, d3 ≈ 5, `WEAK`) via
`IBeyonderContext.updateSanityLoss`, feeding the existing `SanityPenaltyHandler` →
`RampageManager` chain. Zero new machinery. Deep reads cannot be spammed; shallow reads
are free.

## Blocking gap found in existing code

`RecordedEvent` carries `location / description / timestamp / type` — **no actor field**.
CoreProtect's `ParseResult.getPlayer()` is available but gets baked into a formatted
`§c`-coloured description string by `CoreProtectHandler`.

Mode B cannot name the culprit without it, and regex-parsing an actor back out of a
colour-coded display string is exactly the fragility the rules forbid. Fix is additive:
one nullable `actor` field + getter, threaded through the two construction sites in
`CoreProtectHandler`. `MysticalReenactment` reads `getDescription()` and is untouched.
No Bukkit type added to `domain`.

## Files affected

**New (2)**

| File | Responsibility |
|---|---|
| `domain/valueobjects/CryptologistInsight.java` | Sequence 7 balance math: cost, cooldown, ranges, depth thresholds, sanity per depth, buff table. Pure, zero Bukkit. |
| `pathways/error/abilities/Decryption.java` | The ability: mode dispatch + effects. All numbers delegated to the VO. |

**New test (1)**

| File | Covers |
|---|---|
| `test/…/CryptologistInsightTest.java` | depth thresholds, sequence scaling, sanity table, cooldown floor |

**Modified (4)**

| File | Change |
|---|---|
| `domain/valueobjects/RecordedEvent.java` | + `actor` field and getter |
| `application/services/CoreProtectHandler.java` | pass `result.getPlayer()` into both construction sites |
| `pathways/error/abilities/SuperiorObservation.java` | absorbs the Seq-8 gaze tier and the new Seq-7 evidence tier, gated on Sequence |
| `pathways/error/Error.java` | register `Decryption` at Seq 7; drop `SuperiorObservationII` from Seq 8 |

**Deleted (1)**

| File | Reason |
|---|---|
| `pathways/error/abilities/SuperiorObservationII.java` | folded into its parent |

**Untouched, deliberately:** `ServiceContainer` (no new service), `PathwayManager`
(Error already registers 10 Sequence names), `IVisualEffectsContext`,
`plugin.yml`, every config file.

## Why this is Ponytail

No new manager, service, session, runner, context interface, or visual-effect method.
Two new classes, one deletion, four small edits. No session lifecycle — the user chose a
one-shot trail, so there is no registry, no `BukkitTask` ownership, no `cleanUp()`
override. The only new *field* anywhere (`RecordedEvent.actor`) exists because the
alternative is parsing a display string. The observation merge is net-negative code.

## Milestones

| # | Milestone | Verified by |
|---|---|---|
| **M1** | `CryptologistInsight` VO + `CryptologistInsightTest` | `mvn test -Dtest=CryptologistInsightTest` |
| **M2** | `RecordedEvent.actor` + `CoreProtectHandler` wiring | compile + `ArchitectureTest`; `MysticalReenactment` unchanged in-server |
| **M3** | Merge observation tiers, delete `SuperiorObservationII`, register at Seq 9 only | in-server: Seq 9 and Seq 8 behave exactly as before |
| **M4** | `Decryption` skeleton + mode dispatch + **Mode A** (living target) | in-server: crosshair on a mob / player |
| **M5** | **Mode B** — async CoreProtect, culprit, traveling trail | in-server: break blocks, then read them |
| **M6** | **Mode C** — item properties, origin, incantation amplification | in-server: hold a custom item |
| **M7** | **Mode D** — zone scan | in-server: cast with empty crosshair |
| **M8** | Seq-7 evidence-marking tier on the passive (async, 15 s) | in-server: watch TPS with traces nearby |
| **M9** | Register Seq 7 in `Error.java`, full build, menu check | `mvn clean package`, `/pathway`, advance to Seq 7 |

Build note: exclude `ResourcePackItemModelTest` — it has been red since Tyrant commit
`cf3bbff` (ingredients added without pack models) and is unrelated to this work.

## Design decisions locked with the user

- **One ability, mode selected by target** — not four separate abilities. Covers all
  three wiki application bullets under a single name.
- **Evidence gate is mechanical**, driven by `f(evidence, sequence)` → depth 1–3. This is
  the only thing distinguishing Decryption from divination, so it is not flavour text.
- **Mode A never reveals pathway or Sequence** — that belongs to `SuperiorObservation`.
- **Mode B pursues, `MysticalReenactment` audits** — the overlap was flagged and resolved
  by giving each a different question to answer.
- **Item origin derives from existing registry data** — no ownership tracking, no NBT
  stamping, no touching loot generation.
- **All Superior Observation tiers merge into one Sequence-gated class**, registered only
  at Seq 9.
- **No Traits at Sequence 7** — the wiki grants none.
- **Trail is a one-shot effect**, not a session.
- **Output: chat + holograms by mode** — no GUI, so the ability returns a normal success
  result and never `deferred()`.

## Open questions before M1

1. **Incantation buff** — ship the `IAmplificationContext` damage-multiplier
   approximation (recommended), or build the real item-effect-modifier hook as separate
   cross-cutting work?
2. **Ability name** — «Дешифрування» confirmed, to avoid the `door` collision?
