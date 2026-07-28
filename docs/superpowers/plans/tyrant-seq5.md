# Tyrant Pathway — Sequence 5 (Ocean Songster / Океанський Співець) Design Proposal

Branch: `tyrant-pathway` (continues Seq 9/8/7/6 work).
Wiki source: `docs/pathways/tyrant/Tyrant PathwayAbilities … Fandom.md` lines 89–145.
Rule: one milestone at a time; before each — affected files, architectural impact, why
it's Ponytail; after each — summary + in-server test + durable decisions to memory.

Lore: the Ocean Songster commands **lightning**, **voice**, and a **greatly strengthened**
mastery of water and wind. Silver-white lightning (`Color.fromRGB(235, 245, 255)`-ish
highlight over `PathwayBranding.liquidOf("Tyrant")`), thunder sounds, sea-song sounds.

---

## PHASE 1 — Wiki extraction (18 leaf items, 8 headings)

### Traits

**1. Aquatic Affinity** — *"They have more comprehensive and longer-lasting talents for
underwater areas."* — Passive / non-combat umbrella.
- **1.1** *"able to move freely underwater for even longer periods of time"* (— *"Even many
  Saints couldn't dive into the depths to fight them"*). Passive, non-combat.
- **1.2** *"able to understand and communicate with aquatic animals"*. Passive/Utility,
  non-combat.
- **1.3** *"able to freely extract oxygen from the water"*. Passive, non-combat.
- **1.4** *"Underwater, they fear no Beyonder below Demigod level."* Passive, **combat**.

**2. Physical Enhancement** — *"When they sense an abnormality of intense danger, their
heart will contract and expand like the source of a Storm as their blood will surge
through their veins and arteries like a tidal wave."* — Passive (**reactive**), combat.

### New Abilities

**3. Lightning Manipulation** — *"Ocean Songsters have superficial control over Lightning,
and at this level, other than Lightning Strike, they must use other mediums to employ
Lightning, such as arrows."* — Enhancement umbrella, combat.

- **3.1 Lightning Bolt Arrow** — *"With the aid of a bow and arrow, an Ocean Songster can
  condense a solid arrow from Lightning that deals massive damage to enemies."* Active,
  **combat**. Lore steps: *"Their hair unfurls, defying nature, each strand distinct and
  swirling with Lightning. Upon drawing a bow, the sky darkens, clouds gather, and
  Lightning dances. They then release the silver-white Lightning from their hair,
  entwining it with the arrow before releasing the string… a thick Lightning Bolt descends
  superimposing itself on the arrow, making it completely silver white."* Moves *"at an
  unavoidable speed, as if it were from a god of thunder."* **Requires a bow medium.**

- **3.2 Lightning Strike** — *"can produce silver-white electric light capable of paralysis,
  generating a Lightning Strike to smite their targets."* Active, **combat**. The **only**
  lightning usable without a medium.
  - **3.2.1 Purification + weapon infusion** — *"Lightning Strikes contain Purification
    attributes that can damage Undead and Evil Spirits and can be infused into weapons such
    as bullets, and daggers to augment them."*
  - **3.2.2 Convulsion/char** — *"will cause the target's body to tremble and convulse as if
    performing a grotesque dance as their skin will quickly char."*
  - **3.2.3 Undodgeable + electric snakes** — *"It can't be dodged—only preemptively
    avoided. Even if it does not hit, it will spread to nearby conductive materials in the
    form of countless small 'electric snakes'."* *"Lightning makes even non-humanoid
    individuals (such as Shadow creatures) be paralyzed."*
  - **3.2.4 Cast by sight / from the back** — *"They can cast down a Lightning Strike with
    just sight as well as from their back."*

- **3.3 Lightning voice transmission** — *"capable of transmitting sound over long distances
  in the form of Lightning, easily passing through various obstacles… Upon reaching the
  target, the bolt of silver Lightning will turn back to the Ocean Songster's voice,
  relaying the message contained within."* Active, **non-combat** utility.

**4. Singing** — *"The ability to influence targets with one's voice, it varies based on the
user's individual traits."* Active, **combat + control**. Three canonical branches:
- **4.1** *"Using beautiful Singing to interfere with the enemy's Spirit Body, causing them
  to turn adrift and fall into a daze, **or** to enhance one's own explosive power."*
- **4.2** *"using chaotic and unpleasant Singing to leave the enemy frustrated, causing them
  to lose their rationality"* (Gehrman: *"instantly having the urge to kill the singer and
  destroy everything before him… his mind had the feeling of being ripped apart with his
  muscles and vessels squirming"*).
- **4.3** *"Simulating a Thunderous boom to leave others in awe."*
- **4.4 Defense-piercing property** — *"Blocking one's ears and converging one's Spirituality
  cannot fully eliminate the negative effects… Even a deaf person can hear it. This is
  because this includes an 'exchange' at the spiritual level… very difficult to avoid an
  Ocean Songster's Singing through Paper Figurine Substitutes within the affected area."*

**5. Pressure Resistance** — *"A Spell-like effect that makes them not fearful of deep sea
pressure."* Passive/Spell-like, non-combat.

### Strengthened Abilities

**6. Water Control** — *"A Ocean Songster's ability to control water has been greatly
improved."* Enhancement of the Seq-6 ability.
- **6.1 Water Curtain** — *"can create a Water Curtain for covering or defense."* Active,
  defensive.
- **6.2** *"can now create enormous water spheres and corrosive rainwater."* Active, combat.
- **6.3** *"can create sticky liquid that has lubrication effects… used to get rid of
  friction, and combined with the Wind-blessed's ability to create an air cushion, it can
  make the opening of a stone door not create any sound at all."* Active, utility/control.

**7. Wrath** — *"By drawing in a gasp, their eyes can burn with rage as their muscles
swell."* Strengthened Seq-8 active.

**8. Wind Control** — *"Their ability as a Wind-blessed has been moderately enhanced."*
- **8.1 Wind Binding** — *"able to create spiraling Winds to Bind a target's actions."*
  Active, **combat control**.
- **8.2 Flight** — *"can now Fly for longer periods of time… can wrap their body in a
  whirlwind and Fly through the air, avoiding a gun shot that has just been sent as well as
  the Black Flame from a Demoness of Pleasure."* Strengthened Seq-6 toggle.

### Completeness check

**"Have I included every ability mentioned on the Wiki?" — Yes.** All 8 headings
(Aquatic Affinity, Physical Enhancement, Lightning Manipulation, Singing, Pressure
Resistance, Water Control, Wrath, Wind Control) and all 18 leaf items above appear.
Nothing was merged, dropped, or summarized away at extraction time.

### Ambiguities flagged

- **4.1** — "interfere with the enemy's Spirit Body … **or** to enhance one's own explosive
  power" reads as *one* branch with two possible expressions, not two branches. Adapted as
  one mode doing both (enemy daze + caster damage amplification).
- **3.2.1** weapon infusion — the wiki nests it under Lightning Strike, so it is treated as
  a **mode of that same ability**, not a separate ability. This is not a merge of two wiki
  entries.
- **1.4** "fear no Beyonder below Demigod underwater" — narrative statement; adapted as a
  concrete underwater combat bonus.
- **6.3** the "silent stone door" is an *example* of the lubricant + air cushion combo, not
  a separate mechanic.
- **3.1** "unavoidable speed" — an arrow in Minecraft is always dodgeable in principle;
  adapted as a very high-velocity arrow with guaranteed on-hit lightning.

---

## PHASE 2 — Minecraft adaptation

| # | Wiki item | Adaptation | Cost | Cooldown | Duration | Targeting |
|---|-----------|-----------|------|----------|----------|-----------|
| 1.1/1.3/5 | free underwater movement, oxygen from water, pressure resistance | `PhysicalEnhancement` under `tyrant_physique` superset: retains `WATER_BREATHING`, `DOLPHINS_GRACE`, `SPEED`, `CONDUIT_POWER`, `NIGHT_VISION`; `hpBase` 7 | — | — | permanent | self |
| 1.2 | commune with aquatic animals | `SeaTongue` active: nearby aquatic mobs (Dolphin, Squid, Fish, Turtle, Axolotl, Drowned, Guardian) become allied for a while and retarget onto your current target; also reveals nearby hostiles via glowing | 35 | 60 s | 20 s | AoE around caster |
| 1.4 | underwater dominance | `AbyssalDominion` passive with damage-event subscription: while the caster is in water, incoming damage −35 %, outgoing melee damage ×1.4 | — | — | permanent | self |
| 2 | storm-heart danger response | `StormHeart` passive with damage-event subscription: when a hit drops you below 40 % HP, burst of `STRENGTH II` + `SPEED II` + `REGENERATION I` for 6 s; internal 60 s re-arm | — | 60 s internal | 6 s | self |
| 3.2 | Lightning Strike | `LightningStrike` active, mode-switch (Shift+cast) like `WaterControl`: **Удар** — bolt on the entity in sight, or on the nearest hostile behind you when nothing is in sight (3.2.4); paralysis (`SLOWNESS III` + `WEAKNESS`); **×2 damage vs `Undead`/`Phantom`/`Vex`** (3.2.1 purification); chains to up to 3 nearby entities within 5 blocks as "electric snakes" (3.2.3). **Насичення** — infuse the held weapon: next 5 melee hits add lightning damage + paralysis | 45 | 10 s | instant / infusion 30 s | sight, else nearest ≤25 blocks |
| 3.1 | Lightning Bolt Arrow | `LightningBoltArrow` active: arms the next arrow fired within 10 s (requires a bow in hand — the wiki's "medium"). Arrow flies at 3× velocity, on hit calls the same strike routine at ~2.5× damage plus a real bolt. Sky-darkening is faked with thunder sound + particle gloom, not by mutating world weather | 60 | 25 s | 10 s arming window | projectile |
| 3.3 | lightning voice transmission | `ThunderVoice` active, **deferred**: opens a small player-picker menu (`IUIContext`, same pattern as `SeaMemoryMenu`); the picked player hears a thunderclap and receives your next chat line as a lightning-borne whisper, at any distance, through walls | 25 | 30 s | 30 s to speak | one online player |
| 4 | Singing | `Singing` active, mode-switch (Shift+cast), **no line-of-sight check** — the spiritual "exchange" pierces walls (4.4): **Чарівний спів** (4.1) enemies in radius get `NAUSEA` + `SLOWNESS`, caster gets `amplification().amplifyDamage(1.3, 10)`; **Какофонія** (4.2) hostile mobs in radius retarget onto each other, players get `NAUSEA II` + `WEAKNESS II` + `MINING_FATIGUE`; **Громовий Рев** (4.3) mobs are pushed back and flee, players get `SLOWNESS II` + `DARKNESS`, plus a thunder boom | 50 | 20 s | 8–10 s | AoE radius 12 around caster |
| 6.1 | Water Curtain | `WaterCurtain` active: a wall of water in front of the caster for 8 s that removes fire, blocks projectiles (cancel `ProjectileHitEvent`/damage from projectiles crossing it) and grants `RESISTANCE II` while you stand behind it | 40 | 20 s | 8 s | in front of caster |
| 6.2 | water spheres + corrosive rain | `CorrosiveDeluge` active: a giant water sphere at the aimed location that bursts into corrosive rain over an 6-block area for 8 s, damaging entities per second and extinguishing fire | 55 | 25 s | 8 s | aimed location ≤25 blocks |
| 6.3 | sticky lubricant | `SlickWater` active: coats a 5-block area — entities there lose footing (strong horizontal `setVelocity` slide, `SLOWNESS` inverted into slipping, `JUMP_BOOST` negative), and the caster's own footsteps go silent (`SNEAK`-like sound suppression, flavor) | 30 | 15 s | 10 s | aimed location ≤20 blocks |
| 7 | Wrath (strengthened) | Existing `Wrath` — scale amplifiers/duration with `scaleValue(..., MODERATE)` so Sequence 5 is visibly stronger than Sequence 8; add the "eyes burn / muscles swell" visual layer | unchanged | unchanged | scaled | self |
| 8.1 | Wind Binding | `WindBinding` active: spiraling winds root a target — `SLOWNESS VI` + `JUMP_BOOST` (negative) + repeated `setVelocity(0)` for the duration | 40 | 18 s | 5 s | sight ≤20 blocks |
| 8.2 | Flight (longer) | Existing `WindFlight`/`WindFlightSession` — make the flight budget scale with sequence instead of a flat constant | unchanged | unchanged | scaled | self |

**Not reproducible exactly / closest implementation**
- *"can't be dodged"* (3.2.3) — Minecraft has no unavoidable attack primitive; adapted as
  instant hit resolution with no projectile travel + guaranteed chain arcs even when the
  primary target dies.
- *"sky darkens, clouds gather"* (3.1) — mutating world weather for one cast is a global
  side effect; adapted as local gloom particles + `ENTITY_LIGHTNING_BOLT_THUNDER`.
- *"even a deaf person hears it"* (4.4) — expressed as *no line-of-sight and no wall
  occlusion check* on the Singing AoE.
- *"Evil Spirits"* (3.2.1) — no such entity family; mapped onto `Undead` + `Phantom` + `Vex`.

---

## PHASE 3 — Architecture analysis (reuse first)

**Reused as-is, no new abstractions**
- `ActiveAbility` / `PermanentPassiveAbility` + `scaleValue(..., SequenceScaler.ScalingStrategy)`.
- `PhysicalEnhancement` (justiciar) under the shared identity `tyrant_physique` —
  the Seq-5 tier is one more superset entry, not a new class.
- Mode-switch pattern from `WaterControl`: Shift+cast → `AbilityResult.deferred()`,
  per-player `Map<UUID, Mode>` **instance** field. Used by `LightningStrike` and `Singing`.
- Passive-owns-its-subscription pattern from `AirCushion`
  (`context.events().subscribeToTemporaryEvent`) — used by `StormHeart` and `AbyssalDominion`.
- `context.entity()` (potion effects, velocity, damage), `context.targeting()`,
  `context.amplification().amplifyDamage`, `context.messaging()`, `context.glowing()`,
  `context.ui()` (player picker, `SeaMemoryMenu` as the shape reference).
- `AbilityResourceConsumer` for the deferred `ThunderVoice` flow.

**Existing visual effects reused**
`playSurgingWave` (water curtain / deluge), `playSphereEffect` (water sphere),
`playWaveEffect`, `playCircleEffect`, `playVortexEffect` + `playRisingSpiral` (Wind
Binding spiral, Wrath), `playFadingAura`, `playTravelingBeam`, `playExplosionRingEffect`,
`playTrailEffect` (lightning arrow trail), `playGlowingDust`.

**New visual effect — justified, one method**
`playHolyLightning(Location)` is hard-coded **gold** with a beacon sound (Sun flavour);
Ocean Songster lightning is **silver-white**. Per `.claude/rules/visual-effects-reuse.md`
("розшир або параметризуй наявний, а не копіюй") the fix is **parameterisation, not a
copy**:

```java
/** Удар блискавки в точку: спалах, стовп іскор і розгалужені дуги заданого кольору. */
void playLightningBolt(Location location, Color color);
```

`playHolyLightning(loc)` keeps its signature and delegates with the gold colour + its
beacon sound. Chain arcs ("electric snakes") reuse `playBeamEffect(..., Particle.DUST, …)`
— no second new method.

---

## PHASE 4 — Milestones (each independently testable)

| M | Scope | New classes | Edited files |
|---|-------|-------------|--------------|
| **M1** | Physique Seq-5 superset + `sequenceAbilities.put(5, …)` skeleton (items 1.1, 1.3, 5) | — | `Tyrant.java` |
| **M2** | `StormHeart` reactive passive (item 2) | `StormHeart` | `Tyrant.java` |
| **M3** | `AbyssalDominion` underwater combat passive (item 1.4) | `AbyssalDominion` | `Tyrant.java` |
| **M4** | `playLightningBolt` parameterisation + `LightningStrike` with both modes (items 3.2, 3.2.1–3.2.4) | `LightningStrike` | `IVisualEffectsContext`, `VisualEffectsContext`, `Tyrant.java` |
| **M5** | `LightningBoltArrow` (item 3.1) | `LightningBoltArrow` | `Tyrant.java` |
| **M6** | `ThunderVoice` (item 3.3) | `ThunderVoice`, `ThunderVoiceMenu` | `Tyrant.java` |
| **M7** | `Singing` with 3 modes (item 4 + 4.4) | `Singing` | `Tyrant.java` |
| **M8** | `SeaTongue` (item 1.2) | `SeaTongue` | `Tyrant.java` |
| **M9** | `WaterCurtain` (6.1) + `CorrosiveDeluge` (6.2) + `SlickWater` (6.3) | 3 classes | `Tyrant.java` |
| **M10** | `WindBinding` (8.1) | `WindBinding` | `Tyrant.java` |
| **M11** | Strengthened carry-overs: `Wrath` sequence-scaled (7), `WindFlight` budget sequence-scaled (8.2) | — | `Wrath.java`, `WindFlight(Session).java` |

**Balance summary** — Sequence 5 is the first Saint-adjacent tier: `LightningStrike` is the
bread-and-butter nuke (45 sp / 10 s), `LightningBoltArrow` the burst (60 sp / 25 s, needs a
bow), `Singing` the crowd-control (50 sp / 20 s, wall-piercing but short), water abilities
the sustain/zone layer, passives free but conditional (water for `AbyssalDominion`, low HP
for `StormHeart`). No new manager, service, repository, or wiring change in
`ServiceContainer`.
