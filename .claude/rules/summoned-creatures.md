# Істоти, викликані здібністю (і духи як істоти без шляху)

Про те, як **здібність** приводить у гру моба з MythicMobs-пака й керує ним. Про сам
контент мобів, join-ключ і спавн у світі — `docs/mythic-creatures.md`; про сесії й
пайплайн здібностей — `.claude/rules/pathway-abilities.md`.

## Спавн: тільки через `context.entity().summonCreature(...)`

```java
UUID spiritId = context.entity()
        .summonCreature("spirit_wandering", location)   // Optional<UUID>
        .orElse(null);
```

- `ArchitectureTest.mythicMobsApiIsConfinedToBridgePackage` забороняє `io.lumine` **поза**
  `infrastructure.mythic`. Тому шар здібностей не бачить MythicMobs; єдиний вхід —
  `IEntityContext.summonCreature`, що всередині йде в `MythicCreatureGateway`.
- Метод повертає `Optional` не для краси: моба з таким id може не бути в паку (буде лише
  WARNING у лог), а локація — бути без світу. Обробляй порожнє, а не вважай спавн успішним.
- Нового порту під це НЕ створюй: `IEntityContext` уже є портом манипуляцій із сутностями.
  `ServiceContainer` теж не чіпай — шлюз без стану, `EntityContext` створює його сам.

## Упізнання: ванільний scoreboard-тег, а не MythicMobs API

Питати «це наш моб?» через `MythicCreatureGateway.creatureId` зі шару здібностей не можна
(див. вище). Замість цього моб сам ставить собі тег у паку:

```yaml
  Skills:
  - addtag{t=ma_spirit} @self ~onSpawn
```

а код читає його чистим Bukkit'ом — через **одне** місце:
`me.vangoo.pathways.common.Spirits` (`TAG`, `isSpirit(Entity)`). Літерал тега не дублюй:
він мусить збігатися з паком, і розхід не дасть помилки компіляції — механіка просто тихо
перестане працювати.

Новий клас істот (не духи) — заводь **свій** тег і **свій** маленький клас-впізнавач у
`pathways.common`, якщо ним живуть кілька шляхів, або в пакеті шляху, якщо він один.

## Істота без шляху

Дух — істота, що не належить жодному шляху (Смерть Посл. 8 користується нею перша,
Darkness — згодом). Правила для такого випадку:

- Файл пака — **власний**, за родом істоти (`mythic-pack/Mobs/spirits.yml`), а не файл
  шляху-першого-споживача. Не забудь `MythicPackInstaller.PACK_FILES`.
- Шаблон `MA_<Pathway>_S<seq>` не потрібен: він існує, щоб нести кіт здібностей
  послідовності. Немає кіту — ставь `Type` і партикли прямо в мобі.
- У `creatures.yml` задавай `pathway:` і `sequence:` **явно** (інакше `CreatureConfigLoader`
  виведе шлях із префікса id, а послідовність — з суфікса, і покладе 9 з WARNING).
  Неіснуюче ім'я шляху (`pathway: spirits`) безпечне й діє так:
  - `CreatureSelector.multiplier` → 1.0, тобто ухил Закону Конвергенції на істоту не діє;
  - генератори завдань церков/орденів роблять `pathwayToGroup.get(...)` → `null`, тож
    істота не стає HUNT-цілю;
  - `ChurchDuelService` бере лише `sequence == 9` — тримай інше число, щоб істота не
    потрапила в пул дуелей ініціації.
- `loot: {min_items: 0, max_items: 0, items: []}` — якщо істота не дає інгредієнтів.

## Керування викликаною істотою — сесія, не «мій моб назавжди»

Живий супутник = **сесія** за правилами `.claude/rules/pathway-abilities.md`
(інстанс-реєстр `Map<UUID, Session>`, власний `BukkitTask`, Bukkit усередині `tick()`).
Додатково саме для мобів:

- **Мирну версію ворожого моба не роби другим id.** Обнули `Attribute.ATTACK_DAMAGE` при
  зарахуванні й скидай `setTarget(null)` щотакту — світовий моб лишиться небезпечним, а
  супутник фізично не зможе поранити. Рух при цьому йде через
  `Mob#getPathfinder().moveTo(...)` (Paper), а не через ціль.
- `setRemoveWhenFarAway(false)` супутникові; телепорт, якщо відстав далі за поріг — інакше
  стіни й вода його губитимуть.
- **Прибирає істот сама сесія**, коли бачить власника офлайн. `cleanUp()` для цього НЕ
  годиться: він кличеться на вихід **будь-якого** гравця на спільному екземплярі здібності
  (`PassiveAbilityManager.cleanupPlayer`) і не знає, хто вийшов, — тож відпустив би почет
  усім. Див. `.claude/rules/pathway-abilities.md`, п. 4 про сесії.
- Підписка сесії на події — `Integer.MAX_VALUE` під **власним** ключем (не `casterId`) +
  `unsubscribeAll(subKey)` у `cancel()`. Так робить `SpiritCompanionSession`.

## Ціль-«мертва душа» і рейміджери

Якщо здібність розрізняє живе й мертве: нежить — `Tag.ENTITY_TYPES_UNDEAD.isTagged(type)`
(`getCategory()` на 1.21+ кидає виняток), дух — `Spirits.isSpirit`, а гравець, що втратив
контроль, — `context.rampage().isInRampage(id)`. Еталон: `EyeOfDeath.isDeadSoul`.

## Заборони

- ❌ `io.lumine` чи `MythicBukkit` у `me.vangoo.pathways` — тільки `summonCreature`.
- ❌ Літерал тега (`"ma_spirit"`) у другому класі замість `Spirits`.
- ❌ Другий, «мирний» моб-близнюк у паку заради супутника.
- ❌ Деструктивний `cleanUp()` для прибирання викликаних істот.
- ❌ Істота без шляху у файлі пака шляху-споживача (`death.yml` для духів) — рід істоти
  визначає файл, а не той, хто нею скористався першим.
