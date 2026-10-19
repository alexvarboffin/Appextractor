# Задача: Compose phone + TV (2 в 1) — Appextractor

> Статус: **описание зафиксировано** 2026-10-19 (код ещё не начат).  
> Референс: `C:\Synced\FireTv` (`:app-compose`, `Documentation/TASKS/compose-phone-tv.md`, skill `compose-tv-from-mobile`).  
> Живой чеклист продукта: корневой `AI-PROJECT-CHECKLIST.md` (§18 ниже по ссылке).

## Цель

В модуле **`:compose`** сделать универсальный APK (тот же `applicationId` / подпись, что legacy `:app`):

| Ветка | Вход | UI-пакеты | UI-библиотека |
|-------|------|-----------|---------------|
| Phone | `phone.MainActivity` | `ui.mobile.*` | `androidx.compose.material3` |
| TV | `tv.MainActivity` | `ui.tv.*` | `androidx.tv.material3` |

- Два **независимых** UI-дерева (не `if (isTv)` внутри одного `setContent`).
- Общие: **ViewModel**, логика в `:extractor` / `:shared` (extract, INFO/META builders, cloud, icons…).
- Имена экранов/компонентов phone и tv **могут совпадать** (`ExtractorScreen`, `AppDetailScreen`, …) — различаются пакет и Material / фокус.

Сейчас (до задачи): один `com.walhalla.compose.MainActivity` + `screens/*` на Material3 + adaptive NavigationSuite (phone/tablet). Это станет **phone**-веткой; TV — зеркало.

## Детект устройства (как FireTv)

Единственная точка с детектом — **Launcher** (intent `MAIN` / `LAUNCHER` в манифесте).

Референс: `FireTv/app-compose/.../launcher/LauncherActivity.kt`

```kotlin
val isTv =
    (resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK) ==
        Configuration.UI_MODE_TYPE_TELEVISION
    // опционально: || BuildConfig.DEBUG  — форс TV на телефоне для отладки
val target = if (isTv) TvMainActivity::class.java else PhoneMainActivity::class.java
startActivity(Intent(this, target))
finish()
```

- Landscape / TV theme — на **`tv.MainActivity`**, не на Launcher (он сразу `finish()`).
- Debug-форс TV: согласовать явно (FireTv использовал `BuildConfig.DEBUG` и/или whitelist модели).

## Целевая раскладка пакетов (`:compose`)

```
compose/src/main/java/com/walhalla/compose/
├── launcher/LauncherActivity.kt     # только роутинг phone|tv
├── phone/MainActivity.kt            # нынешний MainActivity → сюда
├── tv/MainActivity.kt               # tv.material3 shell + NavHost
├── viewmodel/                       # общие ViewModels (уже есть) — не дублировать под tv
├── model/ / log/ / …
└── ui/
    ├── mobile/                      # перенос текущих screens/ + components/
    │   ├── screens/ExtractorScreen.kt
    │   ├── screens/AppDetailScreen.kt
    │   ├── …
    │   └── theme/…                  # нынешний AppextractorTheme
    └── tv/
        ├── screens/ExtractorScreen.kt   # зеркало смысла, свой UI
        ├── screens/AppDetailScreen.kt
        ├── …
        └── theme/…                  # тёмная TV-тема (JetStream / FireTv style)
```

Kotlin `namespace` модуля может остаться `com.walhalla.compose`; меняется только структура пакетов и манифест activities.

## Общий слой (не дублировать)

| Слой | Где |
|------|-----|
| Extract / cloud / presenters / IconSaver / … | `:extractor` (как сейчас) |
| ViewModels, UiState, AppLogBus | `compose/.../viewmodel`, `log` — **одни** на phone+tv |
| Строки продукта | `:extractor` resources + compose strings; TV-only строки — допустимы в compose |

TV-экран **не** зовёт `ui.mobile.*ScreenBody` «как есть» — свой composable на `tv.material3` + D-pad.

## Что зеркалить по смыслу (порядок работ)

1. **Shell:** Launcher + phone/tv MainActivity + манифест (`LEANBACK_LAUNCHER` / banner при необходимости — по FireTv cutover).
2. **Навигация TV:** боковой `NavigationDrawer` (scrollable) вместо bottom bar / FAB-паттернов; разделы Extractor | Logs | Settings (+ detail/manifest как destinations).
3. **Extractor list TV:** фокусные карточки / Lazy-список, действия через long-click dialog / destination actions (не вложенные IconButton внутри Card).
4. **App detail / META / ASSETS / Manifest:** TV-зеркала критического пути (D-pad, диалоги с захватом фокуса).
5. **Settings / About:** TV-диалоги — фокус + Back / влево-вправо закрывают (skill `compose-tv-from-mobile`).

Phone-ветка: сохранить текущий adaptive NavigationSuite + бренд-синюю тему; не ломать паритет с legacy.

## Запреты

- Один `MainActivity` / один `setContent` с ветвлением всего UI по `isTv`.
- TV UI на чистом phone Material3 без `tv.material3` и без D-pad пути.
- Дублировать extract/cloud/info builders в `ui.tv`.
- Трогать общий слой «заодно» без явного согласования (правило FireTv / Appextractor checklist).

## Чеклист приёмки (когда начнём код)

- [ ] LAUNCHER = `LauncherActivity` → phone **или** tv → `finish()`
- [ ] Детект: `UI_MODE_TYPE_TELEVISION` (+ согласованный DEBUG-форс)
- [ ] Пакеты `ui.mobile` / `ui.tv` с зеркальными именами экранов
- [ ] Общие ViewModel; phone и tv только подписываются
- [ ] TV: только `androidx.tv.material3` (+ foundation focus/keys) для chrome/экранов
- [ ] Manifest: TV banner / `LEANBACK_LAUNCHER` по необходимости
- [ ] Smoke D-pad: меню → список → detail → back; диалоги; поля ввода без залипания фокуса
- [ ] Phone-сборка не регрессирует (install + list/extract/detail)

## Куда смотреть (референс FireTv)

| Тема | Путь |
|------|------|
| Описание задачи (шаблон) | `FireTv/Documentation/TASKS/compose-phone-tv.md` |
| Launcher | `FireTv/app-compose/.../launcher/LauncherActivity.kt` |
| Phone / TV entry | `…/phone/MainActivity.kt`, `…/tv/MainActivity.kt` |
| UI mobile / tv | `…/ui/mobile/`, `…/ui/tv/` |
| TV UX / focus / long-click / TextField | skill `compose-tv-from-mobile` |
| User-corrected TV cases | `FireTv/Documentation/guidelines/USER_CORRECTED_CHECKLIST.md` |
| Screen / ScreenBody | `FireTv/Documentation/guidelines/SCREEN_SCREENBODY.md` |

## Следующий шаг

После ок на это описание — реализация по этапам выше, начиная с Launcher + переноса текущего `MainActivity` в `phone/` и пустого TV shell. Обновлять `AI-PROJECT-CHECKLIST.md` §18 по мере закрытия пунктов.
