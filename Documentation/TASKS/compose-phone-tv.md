# Задача: Compose phone + TV (2 в 1) — Appextractor

> Статус: **TV shell done** — 2026-10-20: Launcher + phone + `TvNavHost` / `ui.tv` (drawer, Extractor без overflow → detail actions, Logs, Settings). Phone screens still under `compose.screens` until `ui.mobile` move.  
> Ранее: stub 2026-10-20; описание 2026-10-19.  
> Референс: `C:\Synced\FireTv` (`:app-compose`, `Documentation/TASKS/compose-phone-tv.md`, skill `compose-tv-from-mobile`).  
> Живой чеклист продукта: корневой `AI-PROJECT-CHECKLIST.md` (§18 ниже по ссылке).

## Цель

В модуле **`:compose`** сделать универсальный APK (тот же `applicationId` / подпись, что legacy `:app`):

| Ветка | Вход | UI-пакеты | UI-библиотека |
|-------|------|-----------|---------------|
| Phone | `phone.MainActivity` | `ui.mobile.*` | `androidx.compose.material3` |
| TV | `tv.MainActivity` | `ui.tv.*` | `androidx.tv.material3` |

- Два **независимых** UI-дерева (не `if (isTv)` внутри одного `setContent`).
- **Максимальный reuse** вне UI: ViewModel, `:extractor` / `:shared`, models, pure helpers, строки.
- **TV UI ≠ phone UI:** в `ui.tv` **запрещены** phone-контролы и зависимости **`androidx.compose.material3`** (D-pad ломается на phone Material). Только `androidx.tv.material3` + foundation focus/keys.
- Имена экранов phone/tv **могут совпадать**; пакеты и Material-импорты разные. Не вызывать `ui.mobile.*` из TV.
- Детали: [`Documentation/guidelines/PHONE_TV_REUSE.md`](../guidelines/PHONE_TV_REUSE.md).

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
| Pure models / formatters (без Compose Material) | `compose/.../model`, мелкие Kotlin helpers |
| Строки продукта | `:extractor` resources + compose strings; TV-only строки — допустимы в compose |

### UI — не общий

| Ветка | Разрешено | Запрещено |
|-------|-----------|-----------|
| `ui.mobile` / phone `screens`/`components` | `androidx.compose.material3`, adaptive suite | — |
| `ui.tv` | `androidx.tv.material3`, foundation focus/keys | `androidx.compose.material3.*`, импорт phone screens/components |

Паттерн: `*Screen()` (VM) + `*ScreenBody(state, callbacks)` **в каждой ветке отдельно** — как FireTv `SCREEN_SCREENBODY.md`.

## Что зеркалить по смыслу (порядок работ)

1. **Shell:** Launcher + phone/tv MainActivity + манифест (`LEANBACK_LAUNCHER` / banner при необходимости — по FireTv cutover).
2. **Навигация TV:** боковой `NavigationDrawer` (scrollable) вместо bottom bar / FAB-паттернов; разделы Extractor | Logs | Settings (+ detail/manifest как destinations).
3. **Extractor list TV:** фокусные карточки / Lazy-список, действия через long-click dialog / destination actions (не вложенные IconButton внутри Card).
4. **App detail / META / ASSETS / Manifest:** TV-зеркала критического пути (D-pad, диалоги с захватом фокуса).
5. **Settings / About:** TV-диалоги — фокус + Back / влево-вправо закрывают (skill `compose-tv-from-mobile`).

Phone-ветка: сохранить текущий adaptive NavigationSuite + бренд-синюю тему; не ломать паритет с legacy.

## Запреты

- Один `MainActivity` / один `setContent` с ветвлением всего UI по `isTv`.
- TV UI на **phone Material3** / shared phone composables (D-pad / focus).
- Дублировать extract/cloud/info builders или ViewModels «под TV».
- Трогать общий слой «заодно» без явного согласования.

## Чеклист приёмки

- [x] LAUNCHER = `LauncherActivity` → phone **или** tv → `finish()`
- [x] Детект: `UI_MODE_TYPE_TELEVISION` (+ `FORCE_TV_IN_DEBUG` для отладки на телефоне/эмуляторе)
- [ ] Пакеты `ui.mobile` / `ui.tv` с зеркальными именами (TV: `ui.tv.*`; phone: ещё `screens/` → `ui.mobile` позже)
- [x] Общие ViewModel; phone и tv только подписываются
- [x] TV: только `androidx.tv.material3` (+ foundation focus/keys) для chrome/экранов
- [x] Manifest: TV banner / `LEANBACK_LAUNCHER`
- [x] List D-pad: **нет** overflow на айтемах; OK → detail → actions LazyRow → Back
- [ ] Smoke D-pad на реальном TV / leanback: drawer ↔ list ↔ detail; диалоги
- [ ] Phone-сборка не регрессирует при `FORCE_TV_IN_DEBUG = false`

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
| Reuse vs Material split (Appextractor) | `Documentation/guidelines/PHONE_TV_REUSE.md` |

## Следующий шаг

1. ~~Launcher + phone MainActivity + TV stub~~ (done 2026-10-20).
2. ~~TV shell (drawer + routes) + Extractor/detail/logs/settings на `tv.material3`~~ (done 2026-10-20).
3. Smoke на leanback / Fire TV; при необходимости ScreenBody split.
4. Перенос `screens/` → `ui.mobile/` для симметрии имён; `FORCE_TV_IN_DEBUG = false` перед phone release.
