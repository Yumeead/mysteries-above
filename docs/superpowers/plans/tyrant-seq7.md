# Tyrant Pathway — Sequence 7 (Seafarer / Storm Priest) Implementation Plan

Branch: `tyrant-pathway` (continues Seq 9/8 work).
Wiki source: `docs/pathways/tyrant/Tyrant PathwayAbilities … Fandom.md` (lines 36–60).
Rule: one milestone at a time; before each — state affected files, architectural
impact, why it's Ponytail; after each — summary + in-server test + save durable
decisions to memory. Wait for confirmation between milestones.

## Ability roster (all Sequence 7 wiki items accounted for)

| # | Wiki item | Ability class | Type | Milestone |
|---|-----------|---------------|------|-----------|
| 1 | Aquatic Affinity (enhanced) — T1 | `PhysicalEnhancement` (reused, `tyrant_physique`) | passive progression | M1 |
| 2 | Enhanced Mental + Enhanced Memory + Navigation-sense (T2+T3+A1a) | `SeaMemory` (new) | active — waypoints/guidance | M2 |
| 3 | Navigation — precise weapon throw (A1b) | `PreciseThrow` (new) | active — homing throw | M3 |
| 4 | Water Spell — Suffocation Film (A2a) | `WaterFilm` (new) | active — single-target control | M4 |
| 5 | Water Spell — Azure Lights → Water Wave (A2b) | `AzureWave` (new) | active — forward AoE | M5 |
| 6 | Water Spell — Restorative (A2c) | `RestorativeWater` (new) | active — heal | M6 |
| 7 | Water Spell — Aqueous Cleaning Light (A2d) | `CleansingLight` (new) | active — utility | M7 |

Rider "disperse water with a gesture" — satisfied implicitly: all water is
particles/temporary effects, never placed water blocks, so nothing lingers.

Design decisions locked with the user:
- T2/T3 (mental/memory) are represented functionally by the `SeaMemory` waypoint
  ability, not as flavor-only lines.
- Navigation split (option B): `PreciseThrow` is the combat half; "never get lost"
  is covered by `SeaMemory` + description text (no separate passive class).

## Milestones

**M1 — Aquatic Affinity progression + Seq 7 registration**
- Files: `pathways/tyrant/Tyrant.java` — add `sequenceAbilities.put(7, List.of(...))`
  with a `PhysicalEnhancement` under identity `tyrant_physique` (superset of Seq 8:
  `WATER_BREATHING`, `DOLPHINS_GRACE`, `SPEED`, `CONDUIT_POWER`), plus placeholder
  slots filled in later milestones. Ukrainian name/description.
- Architecture: reuses the existing shared-identity passive; the stronger version
  replaces Seq 8's via `AbilityTransformer`. No new class, no wiring change.
- Ponytail: extend the existing progression, add one `PotionEffectType`. Zero new
  abstraction.
- Test: `/pathway` grant Tyrant, advance to Seq 7, verify Conduit Power + prior
  effects, no HP glitch on relog.

**M2 — `SeaMemory` (waypoints / navigation memory)**
- Files: `pathways/tyrant/abilities/SeaMemory.java` (new); register in `Tyrant.put(7, …)`.
- Behavior:
  - normal cast → place marker at feet; per-caster `Map<UUID, List<Location>>`
    instance field, cap 5 FIFO; message with coords; returns
    `AbilityResult.deferred()` (bookkeeping — no cost/cooldown, per mode-switch rule).
  - sneak + cast → guide to nearest marker: action-bar bearing (N/E/S/W + arrow) +
    distance, refreshed ~15 s by a short `context.scheduling()` repeating task, plus
    a particle trail toward it; returns `success` (cost + cooldown + mastery).
  - `cleanUp()` clears markers + cancels tasks.
- Architecture: instance registry (never static), self-owned task, effects via
  context — matches session-lite conventions without a full Session class.
- Ponytail: markers are session-only (in-memory), reset on server restart.
  `ponytail:` comment marks the ceiling — add JSON persistence + ServiceContainer
  wiring only if cross-restart memory is later required (YAGNI now).
- Test: place 2 markers, walk away, sneak-cast, follow bearing back; verify cap/FIFO
  and cleanup on quit.

**M3 — `PreciseThrow`**
- Files: `pathways/tyrant/abilities/PreciseThrow.java` (new); register in `Tyrant.put(7, …)`.
- Behavior: pick looked-at target via `context.targeting()`; launch a water bolt
  with velocity aimed at the target (guaranteed hit), scaled damage. Effects:
  `playBeamEffect`/`playTravelingBeam` + `Particle.BUBBLE`/`SPLASH`, sound
  `ITEM_TRIDENT_THROW`. Cost ~25, cooldown ~6 s, scaled with `scaleValue`.
- Ponytail: reuse `SeaLunge` shape + existing beam effects; no projectile entity
  needed — direct hit + travel beam is enough.
- Test: hit a mob at range with crosshair roughly on it; verify unmissable + damage.

**M4 — `WaterFilm` (suffocation)**
- Files: `pathways/tyrant/abilities/WaterFilm.java` (new); register.
- Behavior: on looked-at `LivingEntity`, apply `DARKNESS` + `SLOW`/`WEAKNESS` +
  suffocation DoT while out of water; fixed ~5–6 s ("hard to remove"). Effects:
  `playCircleEffect` around target head + `Particle.DRIPPING_WATER`, sound
  `AMBIENT_UNDERWATER_ENTER`. Cost ~40, cooldown ~14 s.
- Ponytail: pure `applyPotionEffect` + a scheduled DoT tick; reuse existing effect
  primitives.
- Test: cast at a mob on land, verify blindness + slow + ticking damage for the
  duration.

**M5 — `playSurgingWave` effect + `AzureWave`**
- Files: `domain/abilities/context/IVisualEffectsContext.java` +
  `application/services/context/VisualEffectsContext.java` — add
  `playSurgingWave(origin, direction, length, width, color, durationTicks)`
  (forward-traveling water wall; reusable, color from `PathwayBranding`);
  `pathways/tyrant/abilities/AzureWave.java` (new); register.
- Behavior: azure light flash, then wave sweeps forward from caster; damage +
  knockback everything in its path. Cost ~50, cooldown ~18 s, scaled.
- Architecture: one new reusable visual effect justified — existing `playWaveEffect`
  is an omnidirectional expanding ring, not a directional sweeping wall; degrading it
  would break `visual-effects-reuse.md`. New method is generic (future Seq-4 Tsunami).
- Ponytail: exactly one new effect method, parameterized; ability holds only "what/
  where", not particle loops.
- Test: cast toward a line of mobs, verify wall travels + knocks back; color = Tyrant
  branding.

**M6 — `RestorativeWater`**
- Files: `pathways/tyrant/abilities/RestorativeWater.java` (new); register.
- Behavior: `REGENERATION I` short on self or looked-at ally (deliberately weaker
  than potions — matches "inferior"). Effects: `playSphereEffect` + `Particle.SPLASH`,
  sound `BLOCK_WATER_AMBIENT`. Cost ~35, cooldown ~20 s.
- Test: heal self and an ally; confirm weak regen, correct targeting.

**M7 — `CleansingLight` (utility)**
- Files: `pathways/tyrant/abilities/CleansingLight.java` (new); register.
- Behavior: extinguish fire on self + nearby burning entities (`setFireTicks(0)`),
  douse fire blocks in a small radius. Effects: `playWaveEffect` + `Particle.FALLING_WATER`,
  sound `ITEM_BUCKET_EMPTY`. Cost ~15, cooldown ~8 s.
- Ponytail: minor by design (faithful to "clean surfaces"); direct Bukkit calls, no
  new abstraction.
- Test: set self on fire, cast, verify fire cleared on self + nearby.

## Out of scope / follow-ups
- Cross-restart persistence for `SeaMemory` markers (JSON store + wiring) — only if
  requested.
- Potion brew recipe for Tyrant Seq 7 — separate task (scaffold potions already exist).
- No `ServiceContainer`, `PathwayManager`, or `ArchitectureTest` changes needed; the
  new visual-effect method follows the existing convention and needs no test change.
