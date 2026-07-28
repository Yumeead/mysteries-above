# Tyrant Pathway — Sequence 6 (Wind-blessed / Благословенний Вітром) Implementation Plan

Branch: `tyrant-pathway` (continues Seq 9/8/7 work).
Wiki source: `docs/pathways/tyrant/Tyrant PathwayAbilities … Fandom.md` (lines 62–87).
Rule: one milestone at a time; before each — state affected files, architectural
impact, why it's Ponytail; after each — summary + in-server test + save durable
decisions to memory. Wait for confirmation between milestones.

Lore: *"irascible at times, matching the characteristics of a gale; great Beyonder
powers in domains related to Water, Wind, and the Weather."* Wind particles use
`Particle.GUST`/`SMALL_GUST`; wind sound `ENTITY_WIND_CHARGE_WIND_BURST`; color from
`PathwayBranding.liquidOf("Tyrant")`.

## Ability roster (all 14 Sequence 6 wiki items accounted for)

| # | Wiki item | Ability class | Type | Milestone |
|---|-----------|---------------|------|-----------|
| 12 | Dive (100 m) | `PhysicalEnhancement` (reused, `tyrant_physique`) | passive progression | M1 |
| 13 | Float (on water) | ↳ same passive (water-breathing/dolphin-grace) | passive | M1 |
| 14 | Night Vision | ↳ same passive (`NIGHT_VISION`) | passive | M1 |
| 4 | Windblades | `Windblades` (new) | active — ranged | M2 |
| 1 | Wind Control (pressure wave / hurricane shroud) | `PressureWave` (new) | active — radial AoE | M3 |
| 5+2+10 | Flight + Glide + Floating Wind (hover) — **merged** | `WindFlight` (new) + session | toggle — movement | M4 |
| 3 | Wind Sprint (2× run speed) | `WindSprint` (new) + session | toggle — movement | M5 |
| 6 | Wind-imbued Hands (penetrative melee) | `WindImbuedHands` (new) | active — self-buff | M6 |
| 7 | Wind Pull (objects to palm) | `WindPull` (new) | active — utility | M7 |
| 8 | Eavesdrop (listen via wind) | `Eavesdrop` (new) | active — utility | M7 |
| 9 | Air Cushion (impact reduction) | `AirCushion` (new) + damage listener | passive — defensive | M8 |
| 11 | Water Control | `WaterControl` (new) | active — switchable, water-gated | M9 |

## Design decisions locked with the user

- **Flight / Glide / Floating Wind merged (#5+#2+#10)** → one `WindFlight` toggle:
  switch between common flight and glide; when flight is prohibited (e.g. a Justiciar
  Judge's Power Prohibition), only glide remains available. One class, not three.
- **Water Control (#11)** → one new `WaterControl` ability holding several small water
  outputs (switchable modes in a single ability), usable **only when near water or
  while it is raining**. Distinct from the inherited Seq-7 water spells.
- **Dive / Float / Night Vision (#12–14)** → folded into the progressing
  `tyrant_physique` superset (shared-identity passive), not separate classes. **DONE M1.**
- **Visual effects:** no new `IVisualEffectsContext` method required — `Particle.GUST`
  through existing primitives (`playBeamEffect`, `playExplosionRingEffect`,
  `playWaveEffect`, `playVortexEffect`, `playHelixEffect`) represents wind faithfully.
  Optional `playWindSlash` crescent only if extra polish is wanted later.

## Milestones

**M1 — Physique Seq-6 superset + Seq 6 registration — ✅ DONE**
- Files: `pathways/tyrant/Tyrant.java` — `sequenceAbilities.put(6, List.of(...))` with a
  `PhysicalEnhancement` under identity `tyrant_physique` (superset of Seq 7:
  `WATER_BREATHING`, `DOLPHINS_GRACE`, `SPEED`, `CONDUIT_POWER`, **+`NIGHT_VISION`**),
  `hpBase` 6. Dive-100m/Float served by retained water effects.
- Architecture: reuses shared-identity passive; `AbilityTransformer` replaces Seq 7's.
  No new class, no wiring change. Compiles clean.
- Test: `/pathway` grant Tyrant, advance to Seq 6, verify Night Vision + prior effects,
  no HP glitch on relog.

**M2 — `Windblades` (ranged combat)**
- Files: `pathways/tyrant/abilities/Windblades.java` (new); register in `Tyrant.put(6, …)`.
- Behavior: ray-traced invisible slash to first `LivingEntity` in line of sight (reuse
  `PreciseThrow`/`AirBullet` ray-trace pattern); scaled damage + light knockback.
  Shift → 3-blade fan (spread angles), x2 cost. Effects: `playBeamEffect` with
  `Particle.GUST` + `SWEEP_ATTACK` impact; sound `ENTITY_WIND_CHARGE_WIND_BURST`.
  Cost ~40, cooldown ~4 s, range 30, base dmg ~7 (`scaleValue` MODERATE).
- Ponytail: reuse ray-trace + existing beam effect; no projectile entity.
- Test: hit a mob at range; shift for the fan; verify damage + wind visual.

**M3 — `PressureWave` (Wind Control — radial)**
- Files: `pathways/tyrant/abilities/PressureWave.java` (new); register.
- Behavior: radial gale burst — knock back + light damage all entities within ~6 blocks;
  brief self-vortex "hurricane shroud" visual. Effects: `playExplosionRingEffect` +
  `playWaveEffect` + short `playVortexEffect` around caster; sound wind-burst.
  Cost ~55, cooldown ~14 s, base dmg ~6.
- Ponytail: `getNearbyEntities` + `setVelocity`; reuse ring/wave/vortex primitives.
- Test: surround self with mobs, cast, verify radial knockback + damage.

**M4 — `WindFlight` (Flight + Glide + hover, merged) + session**
- Files: `pathways/tyrant/abilities/WindFlight.java` (+ `WindFlightSession.java`) (new);
  register.
- Behavior: toggle-with-periodic-cost (Sun `NightVision` pattern). Modes: common flight
  (`setAllowFlight`/`setFlying`) and glide; sneak switches mode; when flight is
  prohibited, force glide only. Auto-off when spirituality drains; hover = zero vertical
  drift while stationary. Effects: gust trail at feet, `ENTITY_PHANTOM_FLAP`.
  Periodic ~6 sp/s, cooldown ~3 s. `cleanUp()` cancels sessions + clears fly state.
- Architecture: instance `Map<UUID, WindFlightSession>`, session owns its task, Bukkit
  direct in `tick()`; toggle-off returns no-cooldown per mode-switch rule.
- Test: toggle flight, sneak to glide, drain spirituality → auto-land; relog safe.

**M5 — `WindSprint` (2× run speed) + session**
- Files: `pathways/tyrant/abilities/WindSprint.java` (+ session) (new); register.
- Behavior: toggle granting `SPEED II` + gust trail while active; periodic cost; same
  session shape as M4. Periodic ~3 sp/s, cooldown ~3 s.
- Ponytail: could share the M4 session shape; keep separate class (distinct wiki
  ability) but mirror the pattern — no new abstraction.
- Test: toggle, confirm speed boost + drain-off + cleanup.

**M6 — `WindImbuedHands` (penetrative melee buff)**
- Files: `pathways/tyrant/abilities/WindImbuedHands.java` (new); register.
- Behavior: MC can't ignore armor → timed self-buff where melee hits deal bonus **true
  damage** (`damage()` bypass) + gust burst on hit; ~6 s. Effects: spiral gust around
  hands (`playHelixEffect`), wind-burst on strike. Cost ~45, cooldown ~12 s.
- Note: needs a short melee-hit hook (EntityDamageByEntity) or a per-tick check of the
  buffed set; decide simplest at implementation (flagged).
- Test: activate, punch a mob, verify extra true damage for the window.

**M7 — `WindPull` + `Eavesdrop` (utility cluster)**
- Files: `pathways/tyrant/abilities/WindPull.java`, `Eavesdrop.java` (new); register.
- `WindPull`: pull nearby dropped `Item` entities (~8 blocks) toward player via
  `setVelocity`, auto-pickup; helix gust from items to hand. Cost ~20, cooldown ~4 s.
- `Eavesdrop`: no NPC conversations exist in MC → closest faithful mapping: reveal
  nearby entities/players through walls with a glowing outline via `context.glowing()`
  for ~8 s within ~15 blocks ("sense presences on the wind"). Cost ~30, cooldown ~20 s.
- Ponytail: both direct-Bukkit + existing `glowing()` context; no new service.
- Test: drop items and pull them; cast Eavesdrop, verify nearby entities glow briefly.

**M8 — `AirCushion` (impact reduction passive) — ✅ DONE**
- Files: `pathways/tyrant/abilities/AirCushion.java` (new, `extends Balance`);
  `Balance.java` (identity `tyrant_stance` + `protected resistance()`); register in `Tyrant`.
- Behavior: negates fall damage (`EntityDamageEvent` subscription via `context.events()`,
  `GracefulDescent` pattern) + raises knockback resistance 0.25 → 0.5. Landing from >5
  blocks shows a wind ring (`playExplosionRingEffect`, `DUST` in Tyrant colour) + `GUST`
  + wind-burst sound.
- Architecture change vs plan: **no listener, no `ServiceContainer` wiring** — the passive
  owns its own subscription. `AirCushion` is the Seq-6 superset of Seq-9 `Balance` under a
  shared identity, because two passives writing `KNOCKBACK_RESISTANCE` would fight.
- Test: fall from height (no damage, wind ring on landing), take a knockback hit, relog —
  attribute must return to 0 on deactivate.

**M9 — `WaterControl` (switchable, water-gated) — ✅ DONE**
- Files: `pathways/tyrant/abilities/WaterControl.java` (new); registered in `Tyrant.put(6, …)`.
- Behavior: one class, three modes in a `switch` — **Струмінь** (targeted traveling water
  beam: scaled damage + knockback + douse), **Щит** (self `RESISTANCE II` + `REGENERATION`
  6 s + rising water spiral), **Просочення** (target: douse, small damage, `SLOWNESS II` +
  `MINING_FATIGUE` 5 s). Shift+cast cycles the mode → `AbilityResult.deferred()` (no cost,
  no cooldown, per the mode-switch rule). Cost 30, cooldown 8 s, range 20.
- Gate before any effect: `player.isInWater()` ∨ water block within 4 blocks ∨ storm under
  open sky; otherwise `AbilityResult.failure` — `Beyonder` charges nothing on failure.
- Ponytail: no session (all effects are one-shot or vanilla potion effects), no new
  `IVisualEffectsContext` method — `playTravelingBeam`/`playRisingSpiral`/`playSphereEffect`
  + `playWaveEffect` cover it.
- Test: cast on dry land inland (blocked message), then near water / in rain (works);
  Shift-cycle all three modes and verify each output.

## Out of scope / follow-ups
- Potion brew recipe for Tyrant Seq 6 — separate task (scaffold potions already exist).
- Optional `playWindSlash` crescent effect — only if extra Windblades polish requested.
- No `PathwayManager` / `ArchitectureTest` / `ServiceContainer` changes needed — M8 turned
  out not to need a listener either (passive owns its event subscription).
