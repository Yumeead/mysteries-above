# Error Pathway — Sequence 6 (Prometheus / Прометей) Implementation Plan

Branch: `error-pathway` (continues the Sequence 7/8 branch — same topic, same branch,
per `.claude/rules/git-workflow.md`).
Wiki source: `docs/pathways/error/Error PathwayAbilities … Fandom.md` (lines 87–122).
Rule: one milestone at a time; before each — state affected files, architectural
impact, why it's Ponytail; after each — summary + in-server test + save durable
decisions to memory. Wait for confirmation between milestones.

## Gameplay role

Sequence 9 was **hands** (take the item, vanish). Sequence 8 was **minds** (talk your way
out, feed a false reality). Sequence 7 was **truth** (knowledge as output). Sequence 6 is
**appropriation**: the first Error tier that takes something the victim genuinely owns and
uses it as its own.

Server role: the **counter-Beyonder** tier. Prometheus is the only class on the server that
can turn another player's build against them, and the only one whose scouting work
(unlocking a victim's pathway recipes) converts directly into combat advantage. It is also
the first Error tier with a real treasure-finding role — the 50 m valuables sense makes
Prometheus the party's scout for hidden storage.

Progression is healthy: 2 new actives carry mastery, 2 passives carry identity, and the
existing Seq-8 `MaterialTheft` grows instead of accumulating alongside a near-duplicate.

## Ability roster (all four Sequence 6 wiki items accounted for)

| # | Wiki item | Ability class | Type | Milestone |
|---|-----------|---------------|------|-----------|
| 1 | Physical Enhancement — trait | `PhysicalEnhancement` (existing, `ERROR_PHYSIQUE`, lvl 5) | permanent passive | M2 |
| 2 | Mental Resistance — trait | `MentalFortitude` (new, «Стійкість розуму») | permanent passive | M2 |
| 3a | Theft type 1 — Beyonder power | `PowerTheft` (new, «Крадіжка сили») | active | M7–M10 |
| 3b | Theft type 2 — objects / planting | `MaterialTheft` (existing, upgraded in place) | active — 2 modes | M4–M6 |
| 4 | Superior Observation — strengthened | `SuperiorObservation` (existing, absorbs all tiers) | permanent passive | M3 |

Inherited from Seq 9/8/7, untouched: `ShadowTheft`, `CombatProficiency`, `SwindlerCharm`,
`Eloquence`, `ThoughtMisdirection`, `MentalDisruption`, `Decryption`.

**`sequenceAbilities.put(6, …)` therefore lists only three entries** — `PowerTheft`,
`MentalFortitude` and the level-5 `PhysicalEnhancement`. `MaterialTheft` and
`SuperiorObservation` are *not* re-registered: they are already inherited from Sequences 8
and 9, and their Sequence-6 behaviour is a tier inside the class that switches on when the
holder's Sequence reaches 6. Re-registering them would create a second instance of an
ability the player already has.

**Not implemented, deliberately:**

- **Wiki 3b.2 — stealing natural phenomena (Lightning, Torrential Rain).** The wiki itself
  scopes this to *"at an unknown **Sequence**"*, i.e. it is explicitly not a Sequence 6
  capability. Recorded here so it is not mistaken for an omission.
- **Wiki 3a.3 — a timed "locating the symbols" phase.** No search-duration mechanic. The
  same idea is expressed through anonymous menu slots and a lower success chance, which is
  what the wiki line is actually about (ignorance costs you).
- **Concealment powers resisting Superior Observation** (wiki line 118). No pathway in the
  plugin has a concealment perception-block; nothing to interact with.
- **The *Pathway Versions Difference* paragraph** (Amon's variant lacks type-2 Theft and the
  Superior Observation transformation until Seq 4). Lore variant, not an ability — same
  treatment as the identical paragraphs at Sequences 7 and 8.

## Deliberate deviations from the wiki (locked with the user)

| Wiki says | We do | Why |
|---|---|---|
| Victim recovers in "a couple of hours to a couple of days", **min 12 hours** (3a.8, 3a.9) | **30 min** suppression | 12 h in Minecraft removes a player's build for a whole evening; 30 min keeps it a real loss without being a session-ender |
| "the more ignorant one is, the closer it is to random, depending on **Luck**" (3a.1) | Unknown abilities appear as **anonymous coloured lights** you pick blind; known ones show their name | Faithful to 3a.2, and readable as a game UI |

**Wiki 3a.10 — "a Stolen ability will continue to function if activated prior to the Theft"
— is satisfied for free.** Suppression is checked in `AbilityExecutor.execute`, which only
gates *new* casts. Sessions and toggles the victim already started keep ticking, because
nothing touches them. This is the correct behaviour by construction, not by extra code.

## The one rule this Sequence turns on

> *"The more one understands the target, the easier it is to **Steal** the specific desired
> one … Knowledge of the victim's **Pathway** and the corresponding mysticism is vitally
> important."*

Modelled mechanically, not as flavour: the caster's **unlocked recipe count for the
victim's pathway** (`IBeyonderContext.getUnlockedRecipesCount`, already used by `Analysis`)
both **reveals ability names in the menu** and **raises the success chance**. A Prometheus
who has never brewed on the victim's pathway is genuinely gambling.

## Balance numbers (all live in `PrometheusTheft`, scaled by Sequence)

| Property | Value | Scaling |
|---|---|---|
| `PowerTheft` spirituality cost | 90 | flat |
| `PowerTheft` cooldown | 300 s at Seq 6, floor 120 s | `MODERATE` |
| `PowerTheft` range | 50 | flat |
| Stolen ability duration | 10 min | flat |
| Victim suppression duration | 30 min | flat |
| Concurrent stolen abilities | 1 (own slot, independent of `Analysis`) | flat |
| Base success chance | **40 %** | flat |
| Per unlocked victim-pathway recipe | +3 %, cap +25 % | flat |
| Per victim Sequence level below 6 | −8 % | flat |
| Chance clamp | 10 % … 95 % | flat |
| `MaterialTheft` spirituality cost at Seq 6 | 50 (unchanged) | flat |
| `MaterialTheft` cooldown at Seq 6 | 25 s, floor 10 s (existing `materialTheftCooldownSeconds`) | `MODERATE` |
| `MaterialTheft` range at Seq 6 | 50 (was 5) | tier-gated |
| Corruption theft (3a.11) transfer | 3 sanity at Seq 6 | `WEAK` |
| Valuables sense radius | 50 (containers / drops / players) | flat |
| Ore scan radius | 16 (was 6) | flat |

A stolen ability is cast at **its own cost and cooldown, resolved against the thief's
Sequence 6**. Stealing a Sequence-3 ability therefore yields its weakened form — this is
what the wiki's "with proficiency" becomes without handing a Seq-6 player Seq-3 power.

Failure consumes spirituality and sets the cooldown. A 40 % base with a 300 s cooldown
means roughly one successful theft per ~12 minutes for an unprepared caster, and near-
reliable theft for one who has scouted the victim's pathway.

## PowerTheft — the flow

1. **Cast.** Collect valid targets within 50 m: player Beyonders, plus MythicMobs pathway
   creatures resolved through `MythicCreatureGateway.creatureId` → `CreatureDefinition`
   (`pathway`, `sequence`) → `PathwayManager`. Line of sight is *not* required — the wiki
   says "radius".
2. **Target menu.** `IUIContext.openChoiceMenu`, player heads / creature icons. Returns
   `AbilityResult.deferred()` — no resource spend yet.
3. **Coloured-lights menu.** One slot per target ability:
   - **known** (caster has unlocked recipes on the victim's pathway) → real name + icon;
   - **unknown** → anonymous dyed light, colour = `PathwayBranding.liquidOf` of the ability's
     pathway (wiki 3a.2);
   - **currently active** → enchant glint (wiki 3a.4), read via
     `IBeyonderContext.isAbilityActivated`;
   - **«Божевілля»** slot (wiki 3a.11), shown only if the victim has sanity loss.
   The target wears `playOrbitingMotes` for as long as the menu is open.
4. **Roll.** `PrometheusTheft.successChance(...)`. Resources consumed here via
   `AbilityResourceConsumer.consumeResources` — on success **and** on failure.
5. **Success.** `beyonder.addOffPathwayAbility(stolen)` on the thief;
   `TheftLedger.record(thiefId, victimId, identity, now)`. The travelling beam plays from
   victim to the thief's hand and grants on arrival.
6. **Expiry.** At +10 min the thief loses it (`removeAbility`); at +30 min the victim's
   suppression lifts. Both timers restored from `theft.json` on startup.

The corruption slot is the same flow with a different payload: victim
`decreaseSanityLoss(n)`, thief `increaseSanityLoss(n)`, routed through the existing
`SanityPenaltyHandler` → `RampageManager` chain. The wiki's "at one's own risk" is literal.

## Hard requirement: the stolen ability expires on wall-clock time

This is the load-bearing behaviour of the whole tier. Getting it wrong in either direction
breaks the feature: expire too eagerly and relogging robs the thief of what they paid for;
never expire and the theft is permanent, which is not what the wiki describes and not what
was agreed.

**The rule, stated once:** a stolen ability lives for **10 minutes of real time from the
moment of the theft** — not 10 minutes of session time, not 10 minutes of online time. The
clock does not pause, ever.

Which means, concretely:

| Situation | Required behaviour |
|---|---|
| Thief keeps playing | ability disappears exactly at `stolenAt + 10 min` |
| Thief relogs at minute 4 | ability is **still there** on rejoin, with ~6 min left; it disappears at minute 10 |
| Server restarts at minute 4 | same — ability present after restart, disappears at minute 10 |
| Thief is offline the whole 10 min | ability is already gone the moment they log back in |
| Victim relogs / restart | suppression continues counting; lifts at `stolenAt + 30 min` |

`TheftLedger` stores an absolute epoch millisecond stamp, never a remaining-ticks countdown
— a countdown is exactly the thing that stops when the server does. `theft.json` holds:

```json
{ "<thiefUuid>": { "victim": "<uuid>", "ability": "<identity>", "stolenAt": 1785315363448 } }
```

Everything else is derived: `expiresAt = stolenAt + 10 min`,
`suppressionEndsAt = stolenAt + 30 min`.

Three enforcement points, and no others:

1. **On theft** — write the entry, then schedule removal in `(expiresAt − now)` ticks.
2. **On player join and on plugin enable** — for every ledger entry, compare against `now`.
   Already expired → drop the entry and call `beyonder.removeAbility(identity)` if it is
   still in the persisted `offPathwayActiveAbilities`. Not yet expired → reschedule the
   removal for the remaining time. This single reconciliation pass covers relog, restart and
   crash-recovery identically, which is why there is one pass rather than three.
3. **On expiry** — remove the ability, keep the entry until the suppression window also
   closes, then drop it.

Note the interaction that makes step 2 mandatory: `offPathwayActiveAbilities` is persisted by
`BeyonderMapper`, so **the ability survives a relog on its own** — the thief keeps it for
free, which is what we want. What does *not* survive on its own is the timer. Without the
reconciliation pass the ability would simply never be taken back. This is the defect the
ledger exists to prevent, and M7 is where it gets tested directly.

## MaterialTheft — upgraded in place, two modes, sneak to switch

**No new class.** `MaterialTheft` (Sequence 8) grows a Sequence-6 tier inside itself, the
same way `SuperiorObservation` carries `GAZE_TIER` / `EVIDENCE_TIER` / `TREASURE_TIER` in one
file. No `AbilityIdentity` replacement, no `AbilityTransformer` swap, no second near-
identical class — the ability the player already has simply becomes stronger and gains a
second mode when their Sequence reaches 6.

Everything below is gated on `caster.getSequenceLevel() <= 6`; at Sequence 8 and 7 the
ability behaves exactly as it does today (5 m, plugin ingredients only, single mode).

| | Sequence 8/7 (unchanged) | Sequence 6 (`PROMETHEUS_TIER`) |
|---|---|---|
| Range | 5 m | **50 m** |
| What can be taken | plugin ingredients only | **any item** |
| Modes | one | **ВЗЯТИ / ПІДКИНУТИ** |
| Fire | — | **yes** |
| Ground drops | — | **yes, no roll** |

### Mode ВЗЯТИ (take)
Any item — not just plugin ingredients — from a living entity's inventory or a container
within 50 m, through walls. Reuses the existing `findInPockets` / `findInContainers` /
`pick` methods already in the class; the ingredient-only filter in `pick` becomes tier-
dependent. Protection list reused from `ShadowTheft.isProtected` (netherite, elytra, totem,
ability items). Ownerless ground drops need no roll (wiki 3b.3).

**Fire folded in here (wiki 3b.1):** if the target is burning, or a `FIRE` / `LAVA` block is
in range, the fire is extinguished and the caster receives a `FIRE_CHARGE` plus a short
`FIRE_RESISTANCE`. One mode, one cast — no separate ability for a single wiki sub-bullet.

### Mode ПІДКИНУТИ (plant)
The item in the caster's hand is moved into a target's inventory within 50 m, silent for
the victim.

Sneak-cast switches mode and returns `AbilityResult.deferred()` — the mode-switch branch
must not consume cooldown or spirituality (repo rule; see `Verdict.switchMode`).

**One thing must change inside the existing code, not just around it.**
`MaterialTheft.findInContainers` walks a cube of side `2r+1`. At the current 5 m that is
~1 300 blocks; at 50 m it becomes ~1 000 000 per cast and will stall the server thread. The
Sequence-6 tier therefore switches container lookup to `Chunk.getTileEntities()` over the
chunks the radius touches — the same technique M3 uses for the valuables sense — and keeps
the honest `distanceSquared` check that is already there. The 5 m path may keep the cube
loop, or share the chunk path; sharing is preferred, since one code path is less to
maintain than two.

## Superior Observation — fourth tier in the existing class

`TREASURE_TIER = 6` joins the existing `GAZE_TIER = 8` and `EVIDENCE_TIER = 7` inside the
one class, exactly as Sequence 7 did. No new class, no new passive.

- containers, ground drops and other players' inventories → **50 m**, found through
  `Chunk.getTileEntities()` and nearby-entity queries, not a cube scan;
- ore scan radius **6 → 16**;
- each hit is labelled with an approximate value tier (дрібниця / середня / багата) and a
  type, satisfying "their approximate value, and possible types".

**Why not 50 m for ore:** a 50-block cube is ~1 000 000 blocks per scan every 5 s. 16 blocks
is ~35 000, which is already the ceiling for a passive tick. Containers and entities scale
by chunk and entity count, so those keep the full 50 m the wiki asks for.

## Blocking gaps found in existing code

Three, all real, all found by reading the code rather than assuming:

1. **`CooldownManager` cannot express an arbitrary duration.** Its expiry is derived from
   `ability.getCooldown(sequence)`, so it cannot hold a 30-minute suppression. Ruled out as
   the suppression store.
2. **`AbilityLockManager` blocks *all* abilities**, not one identity. Ruled out too.
3. **`offPathwayActiveAbilities` is persisted** (`BeyonderMapper:42`). Without a stored
   expiry, a stolen ability survives a restart **permanently**. This is the actual reason a
   persistent ledger is required — not a preference for durability.

Also noted: `Analysis` (WhiteTower) shares the same off-pathway set with a cap of 5.
`PowerTheft` must count **its own** slot from `TheftLedger`, or a Prometheus holding 5
copied abilities could never steal.

## New classes and why an existing one cannot be extended

| Class | Why not extend something | Single responsibility |
|---|---|---|
| `domain.valueobjects.PrometheusTheft` | `SwindlerInfluence` is the Sequence-8 number set; merging tiers destroys the one-source-of-truth property it exists for | Sequence 6 balance numbers + success formula |
| `pathways.error.abilities.PowerTheft` | nothing in the repo transfers a foreign power temporarily; `Analysis` copies permanently and takes nothing from the target | steal a Beyonder power |
| `pathways.error.abilities.MentalFortitude` | no existing Error passive touches sanity or mental effects | resistance to mental corruption |
| `infrastructure.theft.TheftLedger` | gaps 1–3 above rule out both existing candidates | stolen/suppressed expiry + `theft.json` |

**Deliberately not created:** no new manager, no new listener, no `ITheftContext`, and no
`ObjectTheft`.

**The theft methods live on `IBeyonderContext` — decided, not open.** It already owns
`removeOffPathwayAbility`, `isAbilityActivated`, `getUnlockedRecipesCount` and
`updateBeyonder`; stealing a power is a mutation of exactly that state, so it belongs on
exactly that contract. A dedicated `ITheftContext` would be a second interface, a second
implementation and a second wiring line in `ServiceContainer` for four methods on state the
existing context already exposes. If the subsystem later grows past that, it splits out
following the `IContractContext` precedent — but not preemptively.

Four methods are added:

```java
Optional<Beyonder> getCreatureBeyonder(UUID entityId);   // MythicMobs pathway creature as a Beyonder
void stealAbility(UUID thiefId, UUID victimId, AbilityIdentity identity);
boolean isAbilitySuppressed(UUID playerId, AbilityIdentity identity);
Optional<StolenGrant> getStolenGrant(UUID thiefId);      // what is held, and until when
```

`TheftLedger` follows `WaypointStore` exactly: Gson, write after every mutation,
corrupt/missing file → empty.

## Visual effects and sounds

One new reusable primitive is needed. Everything else already exists.

```java
/** Різнокольорові вогники, що обертаються навколо сутності й рухаються з нею. */
void playOrbitingMotes(UUID entityId, List<Color> colors, double radius, int durationTicks);
```

**Why new:** the wiki's "various coloured lights on the target's body" is the identity image
of this Sequence. `playTrailEffect` follows an entity but is a single formless particle;
`playVortexEffect` has the shape but is pinned to a static location. Bending either one
degrades the central visual of the tier, which `.claude/rules/visual-effects-reuse.md`
explicitly forbids. Parameterised by colours / radius / duration, so it is reusable for any
future "soul aura" or target marker.

| Moment | Effect | Sound |
|---|---|---|
| Target acquired | `playOrbitingMotes` (**new**) | `BLOCK_ENCHANTMENT_TABLE_USE` 0.8 / 0.7 |
| Wrist flick | `playConeEffect` caster → target, Error colour | `ENTITY_ILLUSIONER_MIRROR_MOVE` 1.0 / 0.6 |
| Ability flies to thief | `playTravelingBeam(victim → hand, PathwayBranding.liquidOf("Error"), onArrival)` | — |
| Arrival | `playGlowingDust` + `playFadingAura` | `ITEM_TRIDENT_RETURN` 0.9 / 1.4 + `BLOCK_AMETHYST_BLOCK_CHIME` |
| Victim loses power | `playAlertHalo` above head | `BLOCK_BEACON_DEACTIVATE` 0.7 / 0.5 |
| Failure | `playExplosionRingEffect` (**DUST only** — other particles crash the helper) | `ENTITY_ITEM_BREAK` 1.0 / 0.5 |
| Fire theft | `playHelixEffect` fire → caster + `playGlowingDust` | `ITEM_FIRECHARGE_USE` + `BLOCK_FIRE_EXTINGUISH` |
| Planting | `playTravelingBeam` reversed, caster-only feedback | `ENTITY_ITEM_PICKUP` 0.3 / 0.4, caster only |
| Treasure sensed | `playDustMark` on the container + `playGroundTrail` to the nearest | `BLOCK_AMETHYST_BLOCK_CHIME` 0.4 / 1.8, ≤ once per 5 s |

All colours resolve through `PathwayBranding`; no hardcoded pathway colour anywhere. Every
ability effect combines at least two layers (shape + sound), per
`.claude/rules/ability-visual-effects.md`.

## Files affected

**New (6)**

```
src/main/java/me/vangoo/domain/valueobjects/PrometheusTheft.java
src/main/java/me/vangoo/pathways/error/abilities/PowerTheft.java
src/main/java/me/vangoo/pathways/error/abilities/MentalFortitude.java
src/main/java/me/vangoo/infrastructure/theft/TheftLedger.java
src/test/java/me/vangoo/domain/valueobjects/PrometheusTheftTest.java
```

**Modified (9)**

```
src/main/java/me/vangoo/pathways/error/Error.java                            + sequenceAbilities.put(6, …)
src/main/java/me/vangoo/pathways/error/abilities/MaterialTheft.java          + PROMETHEUS_TIER: 50 m, any item, 2 modes, fire
src/main/java/me/vangoo/pathways/error/abilities/SuperiorObservation.java    + TREASURE_TIER = 6
src/main/java/me/vangoo/domain/abilities/context/IBeyonderContext.java       + 4 theft methods
src/main/java/me/vangoo/application/services/context/BeyonderContext.java    implementations
src/main/java/me/vangoo/domain/abilities/context/IVisualEffectsContext.java  + playOrbitingMotes
src/main/java/me/vangoo/application/services/context/VisualEffectsContext.java  implementation
src/main/java/me/vangoo/application/services/AbilityExecutor.java            + one suppression `if`
src/main/java/me/vangoo/infrastructure/di/ServiceContainer.java              + TheftLedger, threaded to 2 places
```

**Docs**

```
.claude/rules/ability-theft.md   new — ledger, expiry, the single enforcement point
CLAUDE.md                        persistence section: theft.json
```

`SwindlerInfluence:34` already carries the comment *"вікі розширює його до 50 лише на
Прометеї (Seq 6)"* — that note gets resolved by M3.

## Why this is Ponytail

- Four of five wiki items reuse existing machinery: `PhysicalEnhancement` is a constructor
  call, `SuperiorObservation` is a fourth tier in a class that already has three,
  `MaterialTheft` is **upgraded in place** rather than cloned into a successor class, and the
  stolen-ability menu item is produced by `AbilityItemFactory:109` with **zero** new code.
- Upgrading `MaterialTheft` instead of adding `ObjectTheft` deletes an entire class, its
  `AbilityIdentity` plumbing, the `AbilityTransformer` swap and the duplicated
  pocket/container scan — and removes the menu discontinuity where a Seq-8 ability silently
  vanishes on advance. Two files' worth of code becomes one tier constant and a branch.
- Suppression is enforced with **one `if` in `AbilityExecutor.execute`** — the single entry
  point every cast already routes through — rather than a guard per call site.
- One new persistent store, forced by a real defect (persisted off-pathway abilities would
  otherwise leak forever), not by taste. It copies `WaypointStore` rather than inventing a
  persistence pattern.
- One new visual primitive, and only because reusing an unrelated one would visibly degrade
  the tier's signature image.
- No new manager, no new context interface, no new listener, no refactor of anything
  outside Sequence 6.

## Milestones

| # | Scope | How it is verified |
|---|---|---|
| **M1** | `PrometheusTheft` VO + `PrometheusTheftTest` | `mvn test -Dtest=PrometheusTheftTest` — chance formula, clamps, durations, cooldown floor |
| **M2** | `MentalFortitude` + `PhysicalEnhancement` lvl 5 + register Sequence 6 in `Error.java` | `/pathway` to Seq 6: both passives listed, sanity climbs slower, NAUSEA/BLINDNESS/DARKNESS cleared on tick |
| **M3** | `SuperiorObservation` tier 6 (containers/drops 50 m, ore 16, value labels) | stand 40 m from a chest — mark + value label appear; no TPS drop |
| **M4** | `MaterialTheft` `PROMETHEUS_TIER`: 50 m, any item, chunk-based container lookup | at Seq 8 behaviour is byte-for-byte unchanged; at Seq 6 theft works at 50 m through a wall with no TPS drop |
| **M5** | `MaterialTheft` mode ПІДКИНУТИ + sneak switching | item appears in the target's inventory; switching consumes neither cooldown nor spirituality |
| **M6** | Fire theft inside mode ВЗЯТИ | burning target stops burning, caster gets a fire charge |
| **M7** | `TheftLedger` + `theft.json` + suppression `if` in `AbilityExecutor` + join/enable reconciliation | **the expiry test matters most here:** steal, relog at minute 4 → ability still present, ~6 min left; restart the server at minute 4 → same; stay offline past minute 10 → ability gone on next login; victim's suppression keeps counting across both |
| **M8** | `PowerTheft`: player targets, coloured-lights menu, `playOrbitingMotes`, roll, 10 min grant | two players: theft lands, both sides see the consequence, ability disappears at 10 min |
| **M9** | `PowerTheft`: MythicMobs creature targets via `getCreatureBeyonder` | `/mm mobs spawn <error-mob>` → theft resolves against the creature's pathway |
| **M10** | `PowerTheft`: corruption slot (wiki 3a.11) | sanity transfers both ways; rampage reachable |
| **M11** | `mvn clean package` + `.claude/rules/ability-theft.md` + `CLAUDE.md` update | build green except the known-red `ResourcePackItemModelTest` (pre-existing, Tyrant ingredients) |

## Design decisions locked with the user

1. Power theft grants a **concrete ability of the victim**, castable by the thief.
2. Victim suppression is **longer than the theft** but not 12 h — **30 min** vs 10 min.
3. Timers run on **wall-clock time and survive relog and restart**: the stolen ability does
   *not* vanish when the thief reconnects mid-window — it stays, with the remaining time
   intact, and disappears only when the 10 minutes are genuinely up. Persistent ledger with
   absolute timestamps; see the dedicated section above.
4. Ability selection is a **coloured-lights menu**: known abilities named, unknown anonymous,
   active ones glinting.
5. **No successor class.** `MaterialTheft` itself gains the Sequence-6 tier (50 m, any item,
   two modes, fire). Sequence 8 and 7 behaviour is untouched.
6. Valuables sense: **passive tick**, 50 m for containers/drops/players, ore radius up to 16.
7. Mental Resistance covers **all three** vectors: slower sanity loss, resistance to other
   players' mental abilities, immunity to vanilla mental effects.
8. Theft targets **players and MythicMobs pathway creatures**.
9. Base chance **40 %**, modified by unlocked recipes of the victim's pathway and by the
   victim's Sequence.
10. **One** stolen ability at a time; ~5 min cooldown; failure still consumes resources.

11. Theft methods live on **`IBeyonderContext`**; no `ITheftContext`.

## Open question before M1

1. **Sanity cost for `PowerTheft`.** Every other Error ability that reaches into another mind
   prices itself in sanity. Should a *successful* theft cost the thief sanity as well, or is
   the corruption slot (M10) the only sanity interaction at this tier?

Not blocking: M1–M6 do not depend on the answer, so implementation can start regardless.
