# Aletheia 开发者指南 (AGENTS.md)

本文件为开发智能体 (Agent) 提供独立 Addon 模组 Aletheia 项目的架构、技术栈与开发规约介绍。

## 1. 技术栈介绍

* **分支关系说明**:
  Aletheia Addon 的开发基于以下清晰的 HBM 模组分支演进关系：
  1. `HBM`：最上游的原始 Hbm's Nuclear Tech Mod。
  2. `HBM SPACE`：基于 HBM 衍生出的官方太空分支。**Aletheia 作为核心前置依赖此分支**（即 `libs/HBM-NTM-.1.0.27_X5751_H261.jar`）。
  3. `NTMC`：基于 HBM SPACE 的 Hard Fork 分支。
  4. `本地 NTMC (C:\Modding\NTMC)`：本地进行二次开发的 NTMC 分支。
  
  **Aletheia 的最终目标**是作为一个独立的 Addon，在 `HBM SPACE` 上游基础上，通过代码与 Mixin 补全所有 `本地 NTMC` 中的独占特性（机器、流体、实体、配方、成就及贴图材质），从而在游戏体验上彻底取代 Hard Fork 的 NTMC 模组。

* **目标游戏版本**: Minecraft 1.7.10
* **开发框架**: Minecraft Forge (1.7.10)
* **核心语言**: Java
* **编译与构建工具**: Gradle (配备 Jabel 编译器插件)
  * 注: 本地构建采用的高版本 JDK (如 JDK 25) 搭配 Jabel 进行编译，编译输出目标保持 Java 8 兼容性，构建指令为 `.\gradlew.bat compileJava`。
* **主要依赖与支持**:
  * **Hbm's Nuclear Tech Mod (Space Branch)**: 作为核心前置模组，依赖 `HBM SPACE` 分支的构建包 `HBM-NTM-.1.0.27_X5751_H261.jar`。
  * **移植对照参考源 (NTMC 源码库)**: 本地路径为 `C:\Modding\NTMC`。包含完整的 `本地 NTMC` 源码及全部像素美术资产。你在移植任何独占内容时，必须将其作为最高权威的对照蓝本进行核对与直接复制，确保完全无缝移植。
  * **NotEnoughItems (NEI)**: 配方与物品检索支持。
  * **Sedna 枪械框架**: 模组重用了 HBM SPACE 分支的模块化枪械系统，支持复杂的 3D 渲染和自定义枪械配置。
  * **Mixin (UniMixins & MixinBooterLegacy)**: 模组采用 UniMixins 框架（全面支持 MixinExtras 和新版 Fabric Mixin 特性）向 `HBM SPACE` 和原版 Minecraft 注入逻辑。通过 `mixinbooterlegacy` 进行双阶段装载：
    - **Early Mixins** (由 `mixins.aletheia.json` 驱动)：用于在游戏早期向 Minecraft 原版、Minecraft Forge 以及核心 Mod (Coremods) 注入逻辑。
    - **Late Mixins** (由 `mixins.aletheia.late.json` 驱动)：用于在模组装载后期向 HBM SPACE 或其他常规模组注入逻辑，以解决类加载生命周期冲突。

## 2. 项目结构介绍

项目的核心源代码位于 `src/main/java`，资源文件位于 `src/main/resources`。

### 2.1 源代码结构 (`src/main/java`)

所有核心逻辑均位于包 `com.pppopipupu.aletheia` 下：

* **`com.pppopipupu.aletheia`**:
  * 模组的入口点与主注册类。
  * `Aletheia.java`: 各种方块、物品的实例化与注册，以及 `FMLMissingMappingsEvent` 重映射处理器（实现与旧版私货命名空间的存档兼容）。
  * `CommonProxy.java` / `ClientProxy.java`: 服务端和客户端代理，处理 TileEntity 注册、IItemRenderer 和 TileEntitySpecialRenderer 绑定。
* **`com.pppopipupu.aletheia.block`**:
  * 包含 QGP 流体方块、流体类以及 AMS 反应堆方块（AMS Base, Emitter, Limiter）。
* **`com.pppopipupu.aletheia.item`**:
  * 包含 QGP 桶、QGP 采矿炸弹。终极升级插件和 PPPOP 枪直接在主类中调用原版类进行配置实例化。
* **`com.pppopipupu.aletheia.tileentity`**:
  * 包含 `TileEntityAMSBase`、`TileEntityAMSEmitter` 和 `TileEntityAMSLimiter` 反应堆机器逻辑的实现。
* **`com.pppopipupu.aletheia.render`**:
  * 包含 QGP 流体、AMS 特殊渲染以及 PPPOP 枪等自定义 3D 物品渲染器（`ItemRenderPPPOP` 等）。
* **`com.pppopipupu.aletheia.mixin`**:
  * `MixinUpgradeManagerNT.java`: 拦截升级槽并计算终极升级的 `ultimateCount`。
  * `MixinExplosionFilter.java`: 重写原版爆炸过滤器 `ExplosionFilter` 的 `shouldBlock` 方法，以同时兼容老版和 Addon 注册的新 PPPOP 炮护盾功能。
* **`com.pppopipupu.aletheia.explosion`**:
  * 包含 `ExplosionFilter.java` 的实现，用以判断世界中是否装有 PPPOP 枪并吸收爆炸。
* **`com.pppopipupu.aletheia.packet`**:
  * 包含 QGP 相关的包数据传输逻辑。

### 2.2 资源文件结构 (`src/main/resources`)

* **`assets/aletheia/lang`**:
  * 语言本地化文件，如 `en_US.lang`（英文）和 `zh_CN.lang`（中文）。
* **`assets/aletheia/sounds.json`**:
  * 注册枪械发射和音效资源。
* **`assets/aletheia/textures` / `models` / `shaders`**:
  * 存放夸克-胶子等离子体、枪支模型和着色器资源。

## 3. 编码规约

* **回复语言规范**: 必须始终使用中文回复用户，无论是正常对话、计划制定还是编写临时文档，除非用户明确要求使用其他语言。
* **注释规范**: 不要在函数或逻辑内部编写任何的内联注释。不要在shader中编写任何注释。
* **Mixin 规范**: 
  - **安全注入原则**：严禁滥用 `@Overwrite`。除了极其特殊、非 `@Overwrite` 不可的情况之外，必须优先使用 `@Inject`、`@ModifyVariable` 或 `@Redirect`。
  - **客户端服务端分离准则** 在编写mixin时一定要注意mixin目标类/目标方法是否为仅客户端类，如果是加到mixin配置的client列表里，一定不要混淆。
  - **善用 UniMixins 与 MixinExtras**：UniMixins 支持先进的 MixinExtras 特性（如 `@ModifyReceiver`、`@WrapOperation`、`@Local`、`@Share` 等）。在进行复杂拦截或局部变量修改时，应积极采用 MixinExtras 的高级操作符，避免使用脆弱的 `@Inject` 在方法头部强制 `cancel()`，更不要使用暴力 `@Overwrite`，以提高代码的兼容性与健壮性。
  - **remap 重映射准则（极重要，违反会导致生产环境混淆 jar 注入静默失效）**：
    - 本项目所有 Mixin 类的目标类均为 HBM 等第三方 Mod 类，类名不在原版 SRG 映射表中，因此 `@Mixin` 上统一使用 `remap = false`，避免类名被错误映射。
    - 但 `@Inject` / `@Redirect` / `@ModifyArg` 等注入器的 `method`（或 `@At` 的 `target`）若指向 **Minecraft 原版被混淆的方法/字段**（如 `updateEntity`→`func_145845_h`、`onUpdate`→`func_70071_h_`、`ItemStack.getItem()`→`func_77973_b`、`addInformation`→`func_77624_a`、`isItemValidForSlot`→`func_94041_b`、`canExtractItem`→`func_102007_a`、`registerIcons`→`func_94581_a`、`getIconFromDamage`→`func_77617_a`、`getSubItems`→`func_150895_a`、`doRender`→`func_76986_a`、`getEntityTexture`→`func_110775_a` 等），**必须**在该注入器上单独显式设置 `remap = true`，让注释处理器为其生成 refmap 条目；否则在开发环境（deobf，方法名是 MCP 名）能匹配，而生产环境（混淆 jar，方法名是 SRG 名）匹配不到，注入静默失效，出现 "runclient 正常、实际游戏失效" 的诡异 bug。
    - 若该原版方法在项目所用 SRG 映射表中查不到（注释处理器报 `Unable to locate obfuscation mapping` 错误），改用**双语方法名数组** `method = { "MCP名", "SRG名" }` 并保持该注入器继承类级 `remap = false`，手动覆盖两种环境（如 `@Inject(method = { "isItemValidForSlot", "func_94041_b" }, ...)`）。
    - 注入 HBM 自定义方法（如 `canProcess`、`process`、`getValidUpgrades`、`checkSlotsInternal`、`decay`、`renderTank` 等）或 HBM 字段时，保持继承类级 `remap = false`，不要写 `remap = true`，否则注释处理器会因找不到映射而报错。
    - `@At` 的 `target` 指向原版方法/字段时同样需要 `remap = true`（由所在注入器的 `remap` 决定，通常无需单独在 `@At` 上重复声明）。
    - 简明判断表（适用本项目，目标类均为第三方 Mod 类、类级 `remap = false`）：
      | 目标对象 | 推荐设置 |
      | --- | --- |
      | Minecraft 原版被混淆的类/方法/字段（如 `updateEntity`、`ItemStack.getItem()`） | 注入器单独 `remap = true`；若 SRG 表查不到则用双语方法名 `{"MCP","SRG"}` |
      | Java 自带类库 / 系统 API | `remap = false` |
      | Guava、Gson、Netty 等通用第三方依赖库 | `remap = false` |
      | Mojang 的未混淆基础库（如 Brigadier） | `remap = false` |
      | 未集成在当前项目编译映射流中的其他 Mod（如 HBM 自定义方法） | `remap = false` |
      | 编译器生成的 Synthetic / Lambda 方法 | `remap = false` |
  - **Mixin配置文件分配原则**：
    - **Early Mixin (`mixins.aletheia.json`)**：仅适用于需要尽早加载并向原版 Minecraft (`net.minecraft.*`)、Forge (`net.minecraftforge.*`) 或核心加载器 (Coremods) 注入的 Mixin。在此配置文件中的 Mixin **绝对不能**显式引用或加载普通模组的类（如 `com.hbm.*`），否则会引发 ClassNotFoundException 或提前类加载（ClassLoader Leak）导致崩溃。
    - **Late Mixin (`mixins.aletheia.late.json`)**：适用于所有向非原版/非核心模组（包括 `HBM SPACE` 等依赖模组，如 `com.hbm.*`）注入的 Mixin。大部分移植相关的 Mixin 都应该放在此处加载，以确保模组类已安全就绪。
* **导入规范**: 绝对不要使用任何包名全称，能import包必须使用import。绝对不可以偷懒直接写全称，必须用编辑工具正常在顶部插入import。
*   **编译与运行测试**: 每次修改完代码后，必须使用配置好 Java 25 环境的终端运行 `.\gradlew.bat compileJava` 进行编译校验，确保没有语法和编译期报错。在编译通过后，可以使用 `.\gradlew.bat runclient25` 启动客户端进行运行测试，确保没有任何运行时报错。**在检查测试和编译的日志输出时，严禁使用 `findstr`、`select-string` 等过滤命令拦截关键信息，必须直接阅读编译与运行产生的全部日志内容，以确保能捕获任何隐性的异常、警告或潜在报错。**
*   **日志规范**: 绝对禁止使用 `System.out` 或 `System.err` 输出调试或运行日志。在进行日志记录时，必须使用 Log4j 2 Logger，且必须只使用 `com.pppopipupu.aletheia.Aletheia.LOG`。
*   **操作与读写限制**: 你必须只读取 `C:\Modding\NTMC` 和 `C:\Users\pppop\Desktop\HBMAletheiaAddon` 内的文件，只能修改 `C:\Users\pppop\Desktop\HBMAletheiaAddon` 内的文件，绝对不可读取或修改任何其他文件。
*   **禁止制造临时字节码桩**: 严禁通过生成临时物理 `.class` 字节码或反编译 `.java` 文件（例如解压放入 `/com/` 或 `/libs/com/`）来蒙混通过编译。如果因调试需要临时产生了此类非项目本身的物理文件，必须在 turn 结束前彻底清除。
*   **资源贴图规范**: 绝对禁止使用代码/脚本生成 1x1 像素透明图、纯色块或低质量 Base64 占位图作为缺失的贴图资产。对于所有移植/依赖自 NTMC 的物品和方块材质，必须从前置项目 `C:\Modding\NTMC` 对应的 `textures/` 资源目录中复制完整、原始的高质量贴图资产，以保证贴图的完整性与游戏内视觉效果。


