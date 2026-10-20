# Phone / TV — что переиспользуем, что нет

Правило пользователя (2026-10-20): **максимально общий код** между phone и TV, но **TV UI не использует phone-контролы и phone Material3** (проблемы с D-pad).

Референс: FireTv `SCREEN_SCREENBODY.md`, skill `compose-tv-from-mobile`.

---

## Разрешено делить (один экземпляр)

| Слой | Примеры в Appextractor |
|------|-------------------------|
| Domain / extract | `:extractor` — `ExtractorHelper`, `F0Presenter`, `MetaPresenter`, cloud, icons… |
| ViewModel / UiState | `compose/.../viewmodel/*`, `AppLogBus` |
| Pure models / helpers | `model/*`, flatten tree, formatters, permission text maps |
| Строки / drawables | `:extractor` + общие `res` (без привязки к Material phone) |

Phone и TV **оба** зовут одни и те же ViewModel API и callbacks.

---

## Запрещено тянуть в `ui.tv`

- Импорты **`androidx.compose.material3.*`** (Scaffold, NavigationBar, Button, Card, OutlinedTextField, TopAppBar, NavigationSuiteScaffold, …).
- Composable из **`ui.mobile` / `screens` / `components`**, написанные под phone Material3 (в т.ч. «просто вызвать `ExtractorScreen` с телефона»).
- Touch-only паттерны без D-pad пути (FAB, hamburger-only drawer, вложенные IconButton внутри focusable Card).
- **Overflow / PopupMenu на айтемах LazyList** — лишние клики и захват фокуса на D-pad. Список = один фокусный surface на строку; действия (extract / share / …) на **экране айтема** (detail).

TV UI = **`androidx.tv.material3`** + Compose Foundation (focus, keys, Lazy*).

Phone UI = **`androidx.compose.material3`** (+ adaptive suite на phone).

---

## Паттерн экрана (зеркало имён)

```
ui/mobile/…/FooScreen.kt   → viewModel() + FooScreenBody(…)   // material3
ui/tv/…/FooScreen.kt       → тот же VM + FooScreenBody(…)     // tv.material3
viewmodel/FooViewModel.kt  → один на обе ветки
```

- `*Screen()` — без обязательных UI-параметров; внутри берёт VM.
- `*ScreenBody(state, callbacks)` — чистый UI **ветки**; **без** ViewModel в параметрах.
- Одинаковые **имена** экранов допустимы; пакеты разные.
- Общие **не-Composable** куски (маппинг state → строки дат, ключи меню) — в `viewmodel` / `model` / маленьких Kotlin-функциях **без** Material imports.

---

## Анти-паттерны

| ❌ | ✅ |
|---|---|
| `ui.tv` → `import …ui.mobile.ExtractorScreen` | Отдельный `ui.tv…ExtractorScreen` + общий VM |
| Shared `AppListItem` на material3, подключённый и в TV | `components` phone-only; TV — свой item на `tv.material3` |
| `expect/actual` Composable с Material внутри common | Common = VM/models; UI только в mobile/tv |
| Один `setContent { if (isTv) PhoneUi() else … }` | Launcher → phone **или** tv Activity |
| Menu IconButton / Dropdown на каждом row LazyList | Row click → detail; actions в detail LazyRow |

---

## Проверка ревьюером

Для каждого файла под `ui/tv/**`:

1. Нет `androidx.compose.material3` в imports.
2. Нет ссылок на `ui.mobile` / phone `screens` / phone `components`.
3. Есть (или планируется) пара имён с mobile; данные с того же ViewModel.
