# Aletheia Developer Guide (AGENTS.md)

This document provides architecture, tech stack, development conventions, and documentation maintenance guidelines for
AI agents developing the standalone Addon mod Aletheia.

## 1. Tech Stack & Dependency Architecture

* **Branch Relationship Overview**:
  The development of Aletheia Addon is based on the following clear branch evolution relationship of the HBM mod:
  1. `HBM`: The most upstream original Hbm's Nuclear Tech Mod.
  2. `HBM SPACE`: The official space branch derived from HBM. Aletheia depends on this branch as a core prerequisite,
     and the build package is located in `libs/HBM-NTM-*.jar`.
  3. `NTMC`: The Hard Fork branch based on HBM SPACE.
  4. `Local NTMC`: Located at `C:\Modding\NTMC` or relative path `../NTMC`, which is a locally secondary-developed NTMC
     branch.

* **Target Game Version**: Minecraft 1.7.10
* **Development Framework & Build System**:
  * Framework: Minecraft Forge 1.7.10
  * Core Language: Java
  * Build System: Gradle Kotlin DSL scripts `build.gradle.kts` and `settings.gradle.kts`, paired with the
    `com.gtnewhorizons.gtnhconvention` plugin and Jabel compiler plugin. Dependencies are centrally declared and loaded
    by `dependencies.gradle`.
  * Compilation Compatibility: Local builds use a higher-version JDK paired with Jabel for compilation, maintaining Java
    8 compatibility for build outputs. The build command is `.\gradlew.bat compileJava`.
* **Key Dependencies & Support**:
  Dependencies are centrally managed in `dependencies.gradle`:
  * **Hbm's Nuclear Tech Mod Space Branch**: Core dependency mod, stored in `libs/HBM-NTM-*.jar`.
  * **DecayLib**: Decay library dependency, stored in `libs/DecayLib-*.jar`.
  * **Porting Reference Source NTMC Codebase**: Located at `C:\Modding\NTMC` or relative path `../NTMC`. Contains the
    complete local NTMC source code and all pixel art assets. When porting any exclusive content, you must treat this as
    the supreme authoritative blueprint for verification and direct copying to ensure completely seamless porting.
  * **NotEnoughItems**: Recipe and item lookup support, introduced via `dependencies.gradle`.
  * **Peripheral Compatibility**: `dependencies.gradle` also includes compilation dependencies for mods such as
    InventoryTweaks, OpenComputers API, and Applied Energistics 2.
  * **Sedna Firearm Framework**: The mod reuses the modular firearm system from the HBM SPACE branch, supporting complex
    3D rendering and custom firearm configurations.
  * **Mixin Framework**: The mod adopts the UniMixins framework, fully supporting MixinExtras and modern Fabric Mixin
    features to inject logic into HBM SPACE and vanilla Minecraft. Two-phase loading is handled via MixinBooterLegacy:
    - **Early Mixins**: Driven by `mixins.aletheia.json`, reserved for injecting logic early in game initialization into
      vanilla Minecraft, Minecraft Forge, and core mods.
    - **Late Mixins**: Driven by `mixins.aletheia.late.json` and registered via `AletheiaLateMixins`, used for injecting
      logic into HBM SPACE or other standard mods to resolve classloading lifecycle conflicts.

## 2. Project Architecture Guide

The core source code of the project is located in `src/main/java`, and resource files are located in
`src/main/resources`.

### 2.1 Source Code Structure & Package Responsibilities

All core logic resides under package `com.pppopipupu.aletheia`, clearly divided by module functions:

* **`com.pppopipupu.aletheia`**:
  * Mod entry and main proxies: Includes `Aletheia.java` handling initialization registration and FML missing mapping
    remapping, and `CommonProxy.java` and `ClientProxy.java` handling side proxies and TileEntity / Renderer bindings.
  * Events and mechanisms: Includes global logic such as `AletheiaCommonEventHandler.java`,
    `AletheiaDecayEventHandler.java`, `AletheiaDecayRegistry.java`, `ClientEventHandler.java`, `Config.java`, etc.
* **`com.pppopipupu.aletheia.block` and `fluid`**:
  * Definitions for blocks and fluid classes such as QGP, as well as centralized registries `AletheiaBlocks.java` and
    `AletheiaFluids.java`.
* **`com.pppopipupu.aletheia.item` and `weapon`**:
  * Definitions for items, bombs, potions, custom firearms, and ammunition, as well as centralized registries
    `AletheiaItems.java` and `AletheiaBullets.java`.
* **`com.pppopipupu.aletheia.tileentity` and `machine`**:
  * Logic and TileEntity implementations for AMS reactors, Schrabidium Transmutators, and various heavy machinery.
* **`com.pppopipupu.aletheia.recipe`**:
  * Mod custom recipes, NTMC ported recipes, and dynamic Recipe centralized registry class `AletheiaRecipes.java`.
* **`com.pppopipupu.aletheia.render`**:
  * Custom 3D item renderer `IItemRenderer`, TileEntity special renderer `TileEntitySpecialRenderer`, entity models, and
    GLSL shader rendering logic.
* **`com.pppopipupu.aletheia.mixin`**:
  * Mixin classes injecting into HBM SPACE's PWR, RBMK, explosion systems, etc., or vanilla Minecraft and Forge.
* **`com.pppopipupu.aletheia.explosion` / `packet` / `entity` / `stats` / `inventory` / `interfaces`**:
  * Explosion protection filtering logic, network packet transmission, custom entities, statistics, and container
    interfaces.

### 2.2 Resource File Structure

* **`assets/aletheia/lang`**: Localization language files `en_US.lang` and `zh_CN.lang`.
* **`assets/aletheia/sounds.json`**: Sound resources and gun firing registration.
* **`assets/aletheia/textures` / `models` / `shaders`**: Texture assets, firearm & machine 3D models, and GLSL shader
  resources.
* **`mixins.aletheia.json`**: Early Mixin configuration.
* **`mixins.aletheia.late.json`**: Late Mixin configuration.

## 3. Coding Conventions

* **Comment Standards**: Do not write any inline comments inside functions or logic. Do not write any comments inside
  shaders.
* **Localization / Terminology Standards**: Use `zh_cn.lang` and `en_us.lang` from NTMC / HBM SPACE as the ultimate
  authoritative translations. Do not manufacture localization names based solely on registration names in code—for
  instance, do not confuse Sa326 and SCHRABIDIUM as different substances; both represent a fictional element in the mod.
* **Mixin Conventions**:
  - **Safe Injection Principle**: Abusing `@Overwrite` is strictly prohibited. Unless under extremely exceptional
    circumstances where non-`@Overwrite` approaches are impossible, you must prioritize using `@Inject`,
    `@ModifyVariable`, or `@Redirect`.
  - **Client-Server Separation Principle**: When writing Mixins, always check whether the target class and method are
    client-only. If it is client-only, it must be added to the `client` array in `mixins.aletheia.late.json` without
    confusion.
  - **Leverage UniMixins & MixinExtras**: Use advanced operators from MixinExtras such as `@ModifyReceiver`,
    `@WrapOperation`, `@Local`, `@Share`, etc. Avoid using fragile `@Inject` calls that forcibly `cancel()` at the head
    of a method, and never use aggressive `@Overwrite`.
  - **Remap Rules**:
    - When injecting into mods, native Java libraries, or open-source libraries used by Mojang, set `remap` at class,
      method, and field levels to `false`.
    - When injecting into vanilla Minecraft classes, omit `remap` (defaults to `true`).
    - When injecting vanilla methods into mod classes, keep class-level `remap = false`, and explicitly specify
      `remap = true` for the vanilla method; this cannot be omitted.
  - **Mixin Config Allocation Principles**:
    - **Early Mixin (`mixins.aletheia.json`)**: Applies exclusively to Mixins that must load early to inject into
      vanilla Minecraft `net.minecraft.*`, Forge `net.minecraftforge.*`, or core loaders. Mixins in this config MUST NOT
      explicitly reference or load classes from regular mods such as `com.hbm.*`, otherwise it will cause
      ClassNotFoundException or premature classloading crashes.
    - **Late Mixin (`mixins.aletheia.late.json`)**: Applies to all Mixins injecting into non-vanilla and non-core mods,
      including dependency mods like HBM SPACE. Most porting-related Mixins should be placed here for loading.
* **Import Conventions**: Never use fully qualified package names. Whenever a package can be imported, `import` must be
  used. Never take shortcuts by writing fully qualified names inline; always insert proper `import` statements at the
  top of the file using editing tools.
* **Compilation & Execution Testing**: After modifying any code, you must run `.\gradlew.bat compileJava` in a terminal
  configured with a Java 25 environment to verify compilation and ensure there are no syntax or compile-time errors.
  After compilation succeeds, you can run `.\gradlew.bat runclient25` to launch the client for runtime testing, ensuring
  there are no runtime errors.
  **When inspecting build, test, and execution log outputs, strictly prohibit using filtering commands such as `findstr`
  or `select-string` to intercept key information. You must read all log output produced by compilation and execution
  directly to ensure capturing any implicit exceptions, warnings, or potential errors.**
* **Logging Conventions**: Using `System.out` or `System.err` to print debugging or runtime logs is strictly forbidden.
  For logging, you must use Log4j 2 Logger, and you must exclusively use `com.pppopipupu.aletheia.Aletheia.LOG`.
* **Operation & Read/Write Scope Restrictions**: You must only read files within `C:\Modding\NTMC` or relative path
  `../NTMC` and the current project directory `./`. You are only allowed to modify files within the current project
  directory, and must never read or modify any other external files.
* **Prohibition of Temporary Bytecode Stubs**: Strictly prohibit generating temporary physical `.class` bytecode or
  decompiled `.java` files (e.g., extracting into `/com/` or `/libs/com/`) to fake compilation success. If such
  non-project physical files are generated for temporary debugging needs, they must be completely cleaned up before the
  end of the turn.
