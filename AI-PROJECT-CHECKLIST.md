# Appextractor — AI Project Checklist

> **Purpose**
> 1. AI reviewer: step-by-step compare this checklist to the live repo and report gaps.
> 2. AI agent: rebuild / extend the project with high fidelity from an empty or partial tree.
> 3. Living doc: update on every user correction that changes understanding, and when a feature is fully implemented + tested.

> **Update protocol (mandatory for agents)**
> - When the user states a new requirement, constraint, or correction that differs from this file → update the relevant section in the same session.
> - When a feature is completed and verified (build/install/device or explicit user OK) → mark it `[x]` and note the key files.
> - Do not invent aspirational architecture that is not in the repo. Prefer reality over `FEATURES.md` drafts.
> - Shared logic goes in `:extractor` (or `:shared`); `:app` and `:compose` must not duplicate extract/cloud/icon/info builders.

---

## 0. Mission

- [x] Dual UI over one core: legacy `:app` + Jetpack Compose `:compose`, both production-capable.
- [x] Shared extraction / analysis / cloud logic in `:extractor` (+ `:shared` if needed).
- [x] Compose must reach feature parity with useful legacy UX; do not silently drop user-visible data.
- [x] Prefer reuse (`F0Presenter`, `MetaPresenter`, `ExtractorHelper`, `CloudBackupCoordinator`, `IconSaver`, `LauncherUtils`) over reimplementing in Compose.

---

## 1. Modules (actual layout)

| Module | Role |
|--------|------|
| `:extractor` | Core library: extract, PackageMeta, presenters (`F0Presenter`, `MetaPresenter`), cloud backup, utils, receivers, strings/drawables for core UI |
| `:shared` | Shared Kotlin bits used by modules |
| `:app` | Legacy Views/Fragments production app (`ApkChief` / Appextractor) |
| `:compose` | Compose single-activity app — `applicationId` **`com.walhalla.appextractor`** (same as `:app` for in-place replace); Kotlin namespace `com.walhalla.compose` |

- [x] Compose depends on `:extractor` + `:shared` only for product logic (no copy of extract pipeline).
- [x] Legacy `:app` also uses `:extractor`.
- [x] Compose release/debug signed with legacy keystore (`compose/keystore/w.jks`, alias `app-extractor`); launcher icons copied from `:app`; `versionCode`/`versionName` date scheme aligned (`1.4.yyMMdd`) so Compose APK can replace Play/legacy install.

### Build / run (agent)

- [x] Prefer `./gradlew :compose:installDebug` (or `:app:installDebug`) and verify on device.
- [x] If app crashes on launch → inspect logcat (`gradle-build-run` skill).

---

## 2. Architecture principles (user / session decisions)

- [x] **No code duplication** for extract, cloud upload, icon save, INFO row building, flags decode — extract to `:extractor`.
- [x] Compose detail INFO data source = same as legacy: `F0Presenter.buildInfoList()`.
- [x] Compose META data source = `MetaPresenter.doStuff()`; render real `V2Line` / `HeaderObject` (never rely on `toString()` + `"="`).
- [x] Extractor list: keep full list in `_allApps`; search filters a copy into `_apps` (never mutate the source list away).
- [x] Snackbars: collect + clear message carefully so UI is not cancelled by state clear races.
- [x] App detail must not create a fresh `ExtractorViewModel` / block Main thread on heavy PM work — load on `Dispatchers.Default` / `IO`, pass `PackageMeta` via `savedStateHandle`.
- [x] Nested Scaffold: avoid double system/inset padding (empty bottom strip).
- [x] Compose visual identity: **no** Material template purple / wallpaper dynamic color — brand blue from legacy (`#5C8FED`), dense utility typography, quiet chrome (selection bar / chips).
- [x] Phone+TV: share ViewModels/domain maximally; **never** feed TV UI phone Material3 widgets/composables (D-pad) — `Documentation/guidelines/PHONE_TV_REUSE.md`.

---

## 3. Navigation (Compose)

- [x] Bottom bar: Extractor | Logs | Settings.
- [x] Routes: `extractor`, `logs`, `settings`, `manifest`, `app_detail/{packageName}`.
- [x] App detail opened with `PackageMeta` in `savedStateHandle` (`detail_app`).
- [x] Manifest opened with `app` + optional `xml_file` in `savedStateHandle`.
- [x] Hide bottom bar on `app_detail*`.
- [x] **Adaptive nav (skill Step 2):** `NavigationSuiteScaffold` — NavigationBar on compact, NavigationRail on medium/expanded (landscape/tablet); hidden via `NavigationSuiteType.None` on `app_detail*`. Still Nav Compose 2.x (Nav3 list-detail deferred).

Key files:
- `compose/.../MainActivity.kt`
- `compose/.../screens/AppDetailScreen.kt`
- dep: `material3-adaptive-navigation-suite:1.3.2`

---

## 4. Extractor list (Compose) — done

- [x] Load installed packages → `PackageMeta` (label, package, versions, size, system, splits, pinning, sourceDir, dates, category).
- [x] Search by label/package; empty state with Apps icon (not SearchOff).
- [x] Highlight search matches in list items.
- [x] Multi-select + extract selected.
- [x] Sort options (name/package/size/update/system-user).
- [x] Per-item actions: extract, share APK, Play Store, launch, uninstall, copy package, save icon, share icon, open detail, view manifest.
- [x] Selection bar / progress / success snackbar (top-friendly; file count messaging).
- [x] Main overflow: share app, discover more, rate, about, privacy, contact, accessibility, **Google Play categories**.
- [x] About via Compose `AboutDialog` (neptune `DialogAbout` not assumed available to `:compose`).
- [x] **Adaptive list (skill Step 4):** `LazyVerticalGrid` + `GridCells.Adaptive(360.dp)` (multi-column on landscape/tablet).
- [x] **Quick filters + totals (phone+TV, 2026-10-21):** `detail/AppFilters.kt` — `AppFilter` ALL / USER / SYSTEM / TV / SPLIT / SIDELOADED, `matches()`, `totals()`. Phone = `FilterChip` row; TV = filter button → `TvFilterDialog`. Line `filter · N apps · size`. TV/sideload traits (`AppTraits`) load in background (`ExtractorViewModel.loadTraits`); until they arrive those two filters stay empty.
- [x] **Search highlight** is shared `detail/TextHighlight.kt` `highlightMatches` (phone `AppListItem` + TV `AppRow`).
- [x] **Send to phone (QR):** `AppMenuActionId.SendQr` in the shared action catalog (`detail/AppMenuActions.kt`). Phone menu + TV long-press + detail action bar. Server/QR live in `compose/.../transfer/` (see §18).
- [x] **Storage gate runs once:** `storage/ExtractStoragePermission.kt` `rememberStorageGate` — prompt only on API < 29, action exactly once. Phone extract (one + selected) and TV extract both go through it.
- [x] **TV multi-select:** long-press → Select/Unselect; while a selection exists, OK toggles the row and the header shows Extract (N) + Deselect → `extractSelectedApps()`.

Key files:
- `compose/.../screens/ExtractorScreen.kt`, `ui/tv/extractor/ExtractorScreen.kt`
- `compose/.../viewmodel/ExtractorViewModel.kt`
- `compose/.../components/AppListItem.kt`
- `compose/.../detail/AppFilters.kt`, `TextHighlight.kt`, `AppMenuActions.kt`
- `compose/.../storage/ExtractStoragePermission.kt`

---

## 5. Extract pipeline — shared (done)

- [x] Compose extract uses **`ExtractorHelper.executeInternal`**, not a hand-rolled `Downloads/ExtractedApks/...` copier.
- [x] Output naming / location via `Util.get_out_filename` + `Troubleshooting.defLocation()` (legacy path semantics).
- [x] Progress / snack / failure / root-needed dialogs mapped into Compose StateFlows + `AppLogBus`.
- [x] Post-extract cloud: **`CloudBackupCoordinator`** (Telegram + Dropbox OAuth/upload).
- [x] Legacy `ExtractorFragment.successExtracted` also calls `CloudBackupCoordinator` (no second cloud implementation).
- [x] `UploadFileTask` lives in `:extractor` (`com.walhalla.appextractor.task`).
- [x] Dropbox: `db_app_key` / `db_scheme` in extractor strings; Compose manifest registers `com.dropbox.core.android.AuthActivity`.
- [x] `EXTRACT` broadcast: `IntentReceiver` enabled in Compose manifest (same as legacy).

Key files:
- `extractor/.../ExtractorHelper.kt`
- `extractor/.../CloudBackupCoordinator.kt`
- `extractor/.../task/UploadFileTask.java`
- `compose/.../AndroidManifest.xml`

---

## 6. Icons — shared (done)

- [x] **`IconSaver.saveAppIcon`**: `Download/{label}.png` via `PackageMetaUtils.drw`.
- [x] Compose `ExtractorViewModel.saveIcon` uses `IconSaver`.
- [x] Legacy `MainActivity.saveIcons0` uses `IconSaver`.

Key file: `extractor/.../utils/IconSaver.kt`

---

## 7. Logs (Compose) — done / ahead of legacy

- [x] Scan extracted APKs on disk under def location.
- [x] Session messages via `AppLogBus` merged into Log UI.
- [x] Open / share / delete style actions (parity + enhancements).
- [x] **Share one implementation (2026-10-21):** `log/LogFileActions.kt` `shareApkFile` — phone `LogItem` and TV log menu both call it.
- [x] **Send file by QR** from the file row (phone menu + TV `TvChoiceDialog`).
- [x] **TV Logs:** file row OK → open folder / share / QR / delete; message row OK → copy to clipboard; header Refresh + Clear messages.

Key files:
- `compose/.../screens/LogScreen.kt`, `ui/tv/logs/LogScreen.kt`
- `compose/.../viewmodel/LogViewModel.kt`
- `compose/.../log/AppLogBus.kt`, `log/LogFileActions.kt`
- `compose/.../components/LogItem.kt`

---

## 8. App Detail — INFO tab (done)

### Data (must match legacy F0 list)

- [x] Build rows with `F0Presenter.buildInfoList()` (public API added for Compose).
- [x] Sections: basic (SDK min/target, UID, paths, splits…), activities, services, receivers, providers, flags, category, installation/source, storage (if usage stats), certificates, permissions, features, shared libs, etc.
- [x] Extra PackageMeta fields when useful: size, hasSplits, pinning (prepended into Basic).
- [x] Flags decoded via shared `ApplicationFlags.activeNames`.
- [x] Permission rows: green granted / red denied; system `loadDescription` + fallback `getPermissionDescription`.

### Actions

- [x] Launch exported **activity** → `LauncherUtils.onLaunchExportedActivity`.
- [x] Launch exported **service** → `F0Presenter.onLaunchExportedService`.
- [x] Launch **provider** authority → `LauncherUtils.onLaunchProvider`.
- [x] Permission row tap → app settings (`openSettingsRequest` / package settings).
- [x] `InfoApkLine` overflow → view path as XML (nav to Manifest).
- [x] Usage-stats missing → snackbar → `ACTION_USAGE_ACCESS_SETTINGS`.
- [x] Toolbar: system settings + **Manifest** shortcut.
- [x] **Overview facts (phone+TV, 2026-10-21):** `detail/AppInsights.kt` `loadAppInsights` (off main) → `AppDetailViewModel.insights`. TV launcher, touchscreen required, native ABIs (zip `lib/*`), install source, debuggable, permissions requested/granted/dangerous, split names, signer SHA-256, storage APK/data/cache (usage access). Display rows = shared `insightFacts()` — phone Overview card and TV hero/Overview sheet must not format these differently.
- [x] **Report export:** `detail/InfoReport.kt` `ReportAction` COPY / SHARE / SAVE_JSON → `Downloads/<package>_<versionCode>_info.json` (MediaStore). Phone = chips on the Overview card; TV = hero "Export report" → `TvChoiceDialog`.
- [x] **TV INFO is a catalog, not a row per field** (`ui/tv/detail/TvInformationTab.kt`): hero stat strip + badges + storage bar; one focusable spec sheet per section (OK copies the section); `LazyRow` cards only for lines with a status or a launch/settings/XML action. Data stays `groupIntoSections` + `mapInfoLine`.

Key files:
- `extractor/.../sdk/F0Presenter.kt` (`buildInfoList`)
- `extractor/.../utils/ApplicationFlags.kt`
- `extractor/.../utils/LauncherUtils.kt`
- `compose/.../screens/InformationScreen.kt`, `ui/tv/detail/TvInformationTab.kt`
- `compose/.../viewmodel/AppDetailViewModel.kt`
- `compose/.../detail/AppInsights.kt`, `InfoReport.kt`

---

## 9. App Detail — META tab (done)

- [x] Load via `MetaPresenter` (strings/colors/application & component metadata → `HeaderObject` + `V2Line`).
- [x] Render `V2Line.key` / `.value` (bug fixed: old Compose used `toString()` and dropped rows).
- [x] Expandable sections, icons, tap-to-copy.
- [x] Load off main thread (`Dispatchers.Default`).

Key file: `compose/.../screens/MetaScreen.kt`

---

## 10. App Detail — STRING / XML / ASSETS / Manifest (done)

- [x] Tabs: INFO | META | STRING | XML | ASSETS.
- [x] STRING/XML via `StringsPresenter` + `StringScreen` (copy name/value, share text).
- [x] XML tab: View XML → Manifest route with `xml_file`.
- [x] ASSETS tree: read/save/export/copy/zip; View XML for `.xml` → `assets/{path}`.
- [x] Assets success/error toasts wired (`AssetsViewModel.toastMessage`).
- [x] **Assets UI polish:** flat `LazyColumn` tree (indent + chevron + folder/file/preview icons), expand/collapse all, no nested Cards. **Not** adrielcafe/bonsai (Kotlin 1.6 / stale) nor vooft/compose-treeview (JetBrains CMP clashes with AndroidX Compose here) — same Bonsai-style UX in-house.
- [x] Assets listing skips `dexopt` and `webkit` (same as zip filter in `AssetsPresenter`).
- [x] **Asset image preview (2026-10-21):** png/jpg/webp/gif open as a picture. Text assets on TV use `ui/tv/components/TvTextSectionViewer` (`VerticalPager`, one D-pad step = one page). Pages are packed by wrapped rows that fit the pane (`splitTextSections`). Marker is `n / total`; dots only when there are 2–12 pages. Phone text dialog stays touch-scroll.
- [x] `ManifestViewModel.loadManifests` (APK manifests) + `loadXmlResource` (arbitrary XML inside APK).
- [x] `ManifestScreen(xmlFileName: String?)`.

Key files:
- `compose/.../screens/StringScreen.kt`
- `compose/.../screens/AssetsScreen.kt`
- `compose/.../screens/ManifestScreen.kt`
- `compose/.../viewmodel/ManifestViewModel.kt`
- `compose/.../viewmodel/AssetsViewModel.kt`

---

## 11. Settings (Compose) — done

- [x] Toggles: Google Drive / Dropbox / Telegram (+ token, chat id) via `LocalStorage`.
- [x] About row opens Compose `AboutDialog` (version).
- [x] **TV Settings (2026-10-21):** same three toggles; Telegram token + chat id edited with `TvEditableField` (not a "edit on phone" placeholder).

Key files:
- `compose/.../screens/SettingsScreen.kt`, `ui/tv/settings/SettingsScreen.kt`
- `compose/.../viewmodel/SettingsViewModel.kt`
- `extractor/.../storage/LocalStorage.kt`

---

## 12. Parity backlog (explicit user list) — status

| # | Item | Status |
|---|------|--------|
| 1 | Extract → `ExtractorHelper` | [x] |
| 2 | Cloud backup after extract | [x] |
| 3 | Dropbox OAuth in Compose | [x] |
| 4 | STRING/XML → View XML | [x] |
| 5 | ASSETS → View XML + toasts | [x] |
| 6 | Manifest viewer for arbitrary XML | [x] |
| 7 | App Detail Manifest button | [x] |
| 8 | INFO launch exported service | [x] |
| 9 | INFO launch provider | [x] |
| 10 | INFO permission → settings | [x] |
| 11 | INFO `InfoApkLine` overflow | [x] |
| 12 | INFO usage-stats snackbar | [x] |
| 13 | Settings About | [x] |
| 14 | Main menu Google Play | [x] |
| 15 | `EXTRACT` broadcast receiver | [x] |
| 16 | STRING/XML overflow/copy UX | [x] (copy + share + view XML) |
| 17 | Save icon same path as legacy | [x] |
| 18 | Debug suite | [ ] deferred |
| 19 | ServiceIntentScanner | [ ] deferred |
| 20 | Ads / rate / double-back | [ ] deferred |

---

## 13. Compose ahead of legacy (keep)

- [x] List sort options + search highlighting.
- [x] Logs: `AppLogBus` + filesystem scan.
- [x] Single-activity in-app detail (vs separate `AppDetailInfoActivity`).

---

## 14. Explicit user UX / bugfix (session)

Record of corrections that changed implementation:

1. [x] Selection / snackbar / full-width / strings for extract list.
2. [x] Snackbar must not vanish due to `LaunchedEffect` clear race.
3. [x] Search must not destroy full app list (`_allApps` vs filtered `_apps`).
4. [x] Empty search state: Apps icon (usable), not SearchOff.
5. [x] Highlight query matches in list.
6. [x] Logs parity (+ report enhancements only when asked).
7. [x] Device extract verification (split / non-split).
8. [x] App detail freeze: no wrong ViewModel + off-main load.
9. [x] INFO must not lose legacy data → full `F0Presenter` list.
10. [x] Exported activity launch like legacy.
11. [x] Permission items green/red.
12. [x] Permission descriptions restored.
13. [x] META broken rendering → fix `V2Line`.
14. [x] Full remaining-parity column list → execute without duplicating code.
15. [x] This checklist: AI review + empty-folder rebuild source of truth; update on new user info and on feature close.
16. [x] Landscape/tablet: stretchy bottom nav + single-column list → NavigationSuiteScaffold rail + Adaptive grid (Nav3 multi-pane still deferred).
17. [x] Assets tree UI looked poor → rewrite flat Material3 tree; skip Bonsai dep (too old / wrong Compose stack).
18. [x] Assets tree must exclude `dexopt` / `webkit` (was only in zip path).
19. [x] UI felt “нейро-шаблонно” → drop purple/dynamic Material defaults; ApkChief blue + denser list chrome.
20. [x] META section title showed `(2)(2)` — count already in `HeaderObject.title`; Compose must not append `items.size` again.
21. [x] Compose must use legacy `applicationId` + signing keystore + launcher icon to replace `:app` install.
22. [x] Compose phone+TV shell start: Launcher + phone move + TV stub (2026-10-20); full `ui.tv` port still open.
23. [x] Phone/TV: max reuse outside UI; TV forbids phone Material3 / phone controls (`PHONE_TV_REUSE.md`).
24. [x] TV shell: NavigationDrawer + Extractor/Logs/Settings + detail actions (2026-10-20). List rows: **no overflow menu** (D-pad); OK → detail for extract/share/….
25. [x] No hardcoded UI action labels (`Text("Save icon")` etc.) — use existing `ExtR.string.*` / `stringResource` (phone+TV menus, Assets overflow).
26. [x] Phone↔TV App Detail functional parity (tabs + INFO rows + Manifest); shared mappers/VMs, TV-only chrome (`PHONE_TV_REUSE.md`).
27. [x] TV list long-press → actions dialog with thematic icons (`appMenuActions` / `TvAppActionsDialog`; FireTv KeyUp long-press).
28. [x] UI colors via `MaterialTheme.colorScheme` (no ad-hoc `Color(0x…)` in screens); brand hex only in `ui.theme.Color` / theme schemes.
29. [x] TV toasts: `dokar3/compose-sonner` top-center (`TvToasterHost` + `AppLogBus.toasts`); not `android.widget.Toast` on TV.
30. [x] TV extractor sort: button right of search → `TvSortDialog`; shared `SortOption` / `sortedByOption` / `sortMenuItems` with phone (`detail/SortOptions.kt`) (2026-10-20).
31. [x] TV nav crash `LazyLayoutPinnableItem` / "Release should only be called once": `rememberClearFocusThen` before leaving any `tvLazyFocusGroup` Lazy* (detail/manifest/panel switch); keep Extractor LazyColumn mounted (2026-10-20).
32. [x] TV search → Sort: `TvEditableField.rightFocus` at caret end + Sort `FocusRequester` (D-pad Right) (2026-10-20).
33. [x] TV focus crash "FocusRequester is not initialized": `TvEditableField` `runCatching` + `moveFocus` fallback (2026-10-20).
34. [x] TV list focus = FireTv Case B adapted: pin-only `focusRestorer()`; enter via `rememberEnterListFocus` (scroll→child); never `focusProperties { down = group }` (uninit crash on our BOM) (2026-10-20).
35. [x] TV extract “Need root”: shared `ExtractorHelper` — `get_out_filename` leading `/` made `File(parent,"/…")` absolute (TV MediaStore fail → bad fallback); `relativeOutName` + same storage gate as phone (`rememberExtractWithStoragePermission`) (2026-10-20).
36. [x] TV search Down→list: clamp `lastFocused` on filter; `prepareEnterIndex` + 2 frames then child focus; clear TextField focus before enter (2026-10-20).
37. [x] TV INFO = catalog page, not a row/tile per field (`TvInformationTab.kt`): hero stat strip + insight badges + storage bar; one focusable spec sheet per section (OK copies the section); LazyRow cards only for status/launch/settings/XML lines. Data stays `groupIntoSections` + `mapInfoLine` (2026-10-21, emulator: hero + badges confirmed).
38. [x] Phone+TV list filters + totals: shared `detail/AppFilters.kt` (`AppFilter` ALL/USER/SYSTEM/TV/SPLIT/SIDELOADED, `totals()`); traits (`AppTraits`: leanback launcher, install source) loaded in background by `ExtractorViewModel.loadTraits`; phone = `FilterChip` row, TV = filter button → `TvFilterDialog`; "filter · N apps · size" line (2026-10-21, emulator-verified TV).
39. [x] TV multi-select extract: long-press menu "Select/Unselect" (`TvAppActionsDialog(isSelected)`); in selection mode OK toggles; header "Extract (N)" + Deselect → `vm.extractSelectedApps()` through `rememberStorageGate` (2026-10-21).
40. [x] Search highlight shared: `detail/TextHighlight.kt` `highlightMatches` (phone `AppListItem` + TV `AppRow`) (2026-10-21).
41. [x] Storage gate fix: `rememberStorageGate` runs the action exactly once (old `rememberExtractWithStoragePermission` extracted twice: immediately + in permission callback); prompt only on API < 29; phone `ExtractorScreen` uses the same gate (2026-10-21).
42. [x] INFO overview (`detail/AppInsights.kt` → `AppDetailViewModel.insights`): TV launcher, touchscreen required, native ABIs (zip `lib/*`), install source, debuggable, perms requested/granted/dangerous, splits, signer SHA-256, storage APK/data/cache. Rows = shared `insightFacts()`; TV hero badges + storage bar + "Overview" spec sheet; phone "Overview" card (2026-10-21, emulator-verified TV).
43. [x] INFO report export: `detail/InfoReport.kt` `ReportAction` COPY / SHARE / SAVE_JSON (`Downloads/<pkg>_<code>_info.json` via MediaStore); TV hero button → `TvChoiceDialog`, phone overview chips (2026-10-21, JSON verified on emulator).
44. [x] LAN QR transfer: `transfer/LanFileServer.kt` (ServerSocket, random token, one file → direct URL, splits → index page) + `transfer/QrTransfer.kt` (`appSharedFiles`, `QrTransferController`, zxing `qrBitmap`); shared `AppMenuActionId.SendQr`; TV `TvQrTransferDialog`, phone `QrTransferDialog`; entries: app actions menu, detail actions, Logs file menu (2026-10-21, download byte-identical via adb forward).
45. [x] TV Logs parity: file row OK → open folder / share / QR / delete; message row OK → copy; Refresh + Clear messages; `log/LogFileActions.kt` `shareApkFile` shared with phone `LogItem` (2026-10-21).
46. [x] TV Settings: Telegram token + chat id via `TvEditableField` (2026-10-21).
47. [x] `ui/tv/components/TvChoiceDialog.kt` = single TV pick-one dialog (sort, filter, app actions, report, log file). Do not add new copy-paste dialogs.
48. [x] Compose BOM `2024.04.01` → `2025.12.01` (`compileSdk` 37, `activity-compose` 1.13). `2026.09` (FireTv) needs AGP 9.1; this repo stays on AGP 8.13. `animateItemPlacement` → `animateItem` in the unused search samples (2026-10-21).
49. [x] TV list focus: Cinema `MoviesRow` — `focusRequester(parent).focusRestorer(firstItem)`, `firstItem` only on the first child. No saved row index (2026-10-21).
50. [x] TV INFO tall card clipped after focus restore: `BringIntoViewRequester.bringIntoView()` on focus (Cinema `EpisodeCardTV`). Spec sheet + component card bring themselves; hero button brings the whole hero column (2026-10-21).
51. [x] TV list row, one extra line only: `lastUpdateTime` (`Updated 12 Oct 2026`) and `minSdk–targetSdk` (`SDK 24–35`). Stored on `PackageMeta` at the existing package scan (no zip, no permission or storage stats). Phone list unchanged (2026-10-21).

---

## 15. AI reviewer procedure

For each section 0–14:

1. Confirm files exist and APIs match the checklist (names above).
2. Confirm Compose path uses shared `:extractor` types (no parallel extract/cloud/icon builders).
3. Spot-check critical UI: Extractor search/select/extract, Detail INFO (SDK/certs/flags/permissions colors+desc+launch), META rows visible, Manifest from toolbar/XML/assets, Settings cloud toggles, Logs.
4. Report **only gaps** as unchecked items or missing files; do not restate passed items unless asked.
5. Deferred items (18–20) are OK to remain open unless product asks for them.

---

## 16. Empty-folder rebuild order (agent)

1. Create Gradle multi-module: `:extractor`, `:shared`, `:app`, `:compose` with catalogs matching repo AGP/Kotlin.
2. Port / implement `:extractor` first: PackageMeta, ExtractorHelper, F0/Meta presenters, ViewModels sealed types, LocalStorage, CloudBackupCoordinator, IconSaver, LauncherUtils, ApplicationFlags, IntentReceiver, core resources.
3. Wire `:app` legacy UI against extractor (Fragments/Adapters) if building both.
4. Wire `:compose`: MainActivity NavHost, Extractor/Log/Settings/Detail/Manifest screens + ViewModels as above.
5. Manifests: storage/internet/usage-stats; FileProvider; Dropbox AuthActivity; EXTRACT receiver.
6. Run checklist §15 as acceptance.

---

## 17. Out of scope / do not confuse with

- `FEATURES.md` — older aspirational notes (`:domain` / `:data` layers); **this checklist wins** when they conflict.
- `android-prepublish-refresh` skill — Play publish / edge-to-edge / SDK 37 checklist; separate from product parity.
- Monetization / debug / scanner (items 18–20) until explicitly requested.

---

## 18. Compose phone + TV

> Full task write-up: [`Documentation/TASKS/compose-phone-tv.md`](Documentation/TASKS/compose-phone-tv.md).  
> Reference: `C:\Synced\FireTv` + skill `compose-tv-from-mobile`.

- [x] **Launcher** → `launcher/LauncherActivity` detects `UI_MODE_TYPE_TELEVISION` (+ optional `FORCE_TV_IN_DEBUG`) → `phone.MainActivity` **or** `tv.MainActivity`; LAUNCHER + LEANBACK_LAUNCHER; leanback/touchscreen `required=false`; banner.
- [x] Phone entry: former root `MainActivity` moved to `phone/MainActivity` (screens still under `compose.screens` until `ui.mobile` move).
- [x] TV entry: `tv/MainActivity` landscape + `TvNavHost` + `AppextractorTvTheme` (`androidx.tv:tv-material:1.0.1`).
- [x] **Reuse rule:** maximize shared ViewModels / `:extractor` / models; **TV UI must not use phone Material3 controls or phone composables** (D-pad) — see `Documentation/guidelines/PHONE_TV_REUSE.md`.
- [x] **TV D-pad lists:** one focus target per row — no PopupMenu/IconButton overflow; OK → detail; long-press → `TvAppActionsDialog` (`appMenuActions` + icons).
- [x] **TV Extractor search:** `TvEditableField` (FireTv D-pad/IME pattern) → shared `ExtractorViewModel.setSearchQuery` (2026-10-20).
- [x] **TV Extractor sort:** Sort icon right of search → `TvSortDialog`; same options as phone via shared `detail/SortOptions.kt` (2026-10-20).
- [ ] Packages: phone screens still under `compose.screens` until full `ui.mobile.*` move (TV already under `ui.tv.*`).
- [x] TV shell: `NavigationDrawer` + focusRestorer; Extractor → AppDetail (action LazyRow) → Logs → Settings on `tv.material3` only. Debug: `FORCE_TV_IN_DEBUG` in `LauncherActivity`.
- [x] **Share APK:** `ExtractorViewModel.shareApk` → `ShareUtils.shareApkFile` (copy to app storage first). Direct `FileProvider` on `/system/...` sourceDir crashes (2026-10-20).
- [x] **TV App Detail parity (2026-10-20):** same tabs INFO|META|STRING|XML|ASSETS as phone (`AppDetailTabs`); shared `groupIntoSections` / `loadMetaSections` / `mapInfoLine`; TV chrome via `tv.material3` (`TvInformationTab`, `TvMetaTab`, `TvStringTab`, `TvAssetsTab`, `TvFocusTabRow`); Manifest route + action; overflow actions + App settings. No Material3 in `ui.tv`.
- [x] **TV Sonner toasts:** `io.github.dokar3:sonner` via `TvToasterHost` (`Alignment.TopCenter`, dark+richColors); events from `AppLogBus.toasts`.
- [x] **TV list row facts (2026-10-21):** under version/size, one line — update date and SDK range (`list_updated`, `list_sdk_range`). `PackageMeta.minSdk` / `targetSdk` filled in `ExtractorViewModel.loadInstalledApps`. Not on the phone row.
- [x] **TV INFO catalog + overview + report** — §8. One `TvChoiceDialog` for sort, filter, app actions, report and log-file actions; do not add another copy.
- [x] **LAN QR transfer (phone+TV, 2026-10-21):** `transfer/LanFileServer.kt` (plain `ServerSocket`, random token; one file → direct URL, splits → index page) + `transfer/QrTransfer.kt` (`appSharedFiles`, `QrTransferController`, zxing `qrBitmap`). Dep `com.google.zxing:core` (encoder only). Dialogs: `ui/tv/components/TvQrTransferDialog`, `components/QrTransferDialog`. Closes when dismissed. Verified: download byte-identical, bad token → 404.
- [x] **TV Logs + TV Telegram fields** — §7 / §11.

Key files:
- `compose/.../launcher/LauncherActivity.kt`
- `compose/.../phone/MainActivity.kt`
- `compose/.../tv/MainActivity.kt`, `TvNavHost.kt`, `TvRoutes.kt`
- `compose/.../ui/tv/shell/MainShellScreen.kt`
- `compose/.../ui/tv/extractor/ExtractorScreen.kt`
- `compose/.../ui/tv/components/TvChoiceDialog.kt`, `TvSortDialog.kt`, `TvQrTransferDialog.kt`
- `compose/.../detail/SortOptions.kt`, `AppFilters.kt`, `AppInsights.kt`, `InfoReport.kt`
- `compose/.../transfer/LanFileServer.kt`, `QrTransfer.kt`
- `compose/.../ui/tv/detail/AppDetailScreen.kt`, `TvInformationTab.kt`, `TvMetaTab.kt`, `TvStringTab.kt`, `TvAssetsTab.kt`, `TvManifestScreen.kt`
- `compose/.../detail/AppDetailTabs.kt`, `InfoSections.kt`, `MetaSections.kt`, `InfoLineMapping.kt`
- `compose/.../ui/tv/logs/LogScreen.kt`, `…/settings/SettingsScreen.kt`
- `compose/.../ui/tv/components/TvToasterHost.kt` (`dokar3:sonner`)
- `compose/.../log/AppLogBus.kt` (`toasts` SharedFlow)
- `compose/src/main/AndroidManifest.xml`

---
