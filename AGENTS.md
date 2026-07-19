# Aletheia 开发者指南 (AGENTS.md)

本文件为开发智能体提供独立 Addon 模组 Aletheia 项目的架构、技术栈、开发规约与文档维护指南。

## 1. 技术栈与依赖架构

* **分支关系说明**:
  Aletheia Addon 的开发基于以下清晰的 HBM 模组分支演进关系：
  1. `HBM`：最上游的原始 Hbm's Nuclear Tech Mod。
  2. `HBM SPACE`：基于 HBM 衍生出的官方太空分支。Aletheia 作为核心前置依赖此分支，构建包位于 `libs/HBM-NTM-*.jar`。
  3. `NTMC`：基于 HBM SPACE 的 Hard Fork 分支。
  4. `本地 NTMC`：路径为 `C:\Modding\NTMC` 或相对路径 `../NTMC`，是本地进行二次开发的 NTMC 分支。

  Aletheia 的最终目标是作为一个独立的 Addon，在 HBM SPACE 上游基础上，通过代码与 Mixin 补全所有本地 NTMC
  中的独占特性，包括机器、流体、实体、配方、成就及贴图材质，从而在游戏体验上彻底取代 Hard Fork 的 NTMC 模组。

* **目标游戏版本**: Minecraft 1.7.10
* **开发框架与构建系统**:
  * 框架：Minecraft Forge 1.7.10
  * 核心语言：Java
  * 构建系统：Gradle Kotlin DSL 脚本 `build.gradle.kts` 与 `settings.gradle.kts`，搭配 `com.gtnewhorizons.gtnhconvention`
    插件与 Jabel 编译器插件。依赖由 `dependencies.gradle` 统一声明与加载。
  * 编译兼容：本地构建使用高版本 JDK 搭配 Jabel 进行编译，编译输出目标保持 Java 8 兼容性，构建指令为
    `.\gradlew.bat compileJava`。
* **主要依赖与支持**:
  依赖在 `dependencies.gradle` 中集中管理：
  * **Hbm's Nuclear Tech Mod Space Branch**: 核心前置模组，依赖存放于 `libs/HBM-NTM-*.jar`。
  * **DecayLib 衰变库**: 依赖存放于 `libs/DecayLib-*.jar`。
  * **移植对照参考源 NTMC 源码库**: 路径为 `C:\Modding\NTMC` 或相对路径 `../NTMC`。包含完整的本地 NTMC
    源码及全部像素美术资产。你在移植任何独占内容时，必须将其作为最高权威的对照蓝本进行核对与直接复制，确保完全无缝移植。
  * **NotEnoughItems**: 配方与物品检索支持，由 `dependencies.gradle` 引入。
  * **周边兼容支持**: 在 `dependencies.gradle` 中还引入了 InventoryTweaks、OpenComputers API 及 Applied Energistics 2
    等模组的编译依赖。
  * **Sedna 枪械框架**: 模组重用了 HBM SPACE 分支的模块化枪械系统，支持复杂的 3D 渲染和自定义枪械配置。
  * **Mixin 框架**: 模组采用 UniMixins 框架，全面支持 MixinExtras 和新版 Fabric Mixin 特性，向 HBM SPACE 和原版 Minecraft
    注入逻辑。通过 MixinBooterLegacy 进行双阶段装载：
    - **Early Mixins**: 由 `mixins.aletheia.json` 驱动，预留用于在游戏早期向 Minecraft 原版、Minecraft Forge 以及核心 Mod
      注入逻辑。
    - **Late Mixins**: 由 `mixins.aletheia.late.json` 驱动并由 `AletheiaLateMixins` 注册，用于向 HBM SPACE
      或其他常规模组注入逻辑，解决类加载生命周期冲突。

## 2. 项目架构指南

项目的核心源代码位于 `src/main/java`，资源文件位于 `src/main/resources`。

### 2.1 源代码结构与包职责划分

所有核心逻辑均位于包 `com.pppopipupu.aletheia` 下，按模块功能清晰划分为：

* **`com.pppopipupu.aletheia`**:
  * 模组入口与代理主类：包含 `Aletheia.java` 处理初始化注册与 FML 缺失映射重映射，`CommonProxy.java` 与 `ClientProxy.java`
    处理端代理及 TileEntity 与 Renderer 绑定。
  * 事件与机制：包含 `AletheiaCommonEventHandler.java`, `AletheiaDecayEventHandler.java`, `AletheiaDecayRegistry.java`,
    `ClientEventHandler.java`, `Config.java` 等全局逻辑。
* **`com.pppopipupu.aletheia.block` 与 `fluid`**:
  * 方块、QGP 等流体类定义，以及 `AletheiaBlocks.java` 和 `AletheiaFluids.java` 集中注册表。
* **`com.pppopipupu.aletheia.item` 与 `weapon`**:
  * 物品、炸弹、药剂、自定义枪械与弹药定义，以及 `AletheiaItems.java` 和 `AletheiaBullets.java` 集中注册表。
* **`com.pppopipupu.aletheia.tileentity` 与 `machine`**:
  * AMS 反应堆、Schrabidium Transmutator 转化机及各类重型机器逻辑与 TileEntity 实现。
* **`com.pppopipupu.aletheia.recipe`**:
  * 模组自定义配方、NTMC 移植配方及动态 Recipe 集中注册类 `AletheiaRecipes.java`。
* **`com.pppopipupu.aletheia.render`**:
  * 自定义 3D 物品渲染器 `IItemRenderer`、TileEntity 特殊渲染 `TileEntitySpecialRenderer`、实体模型及 GLSL 着色器渲染逻辑。
* **`com.pppopipupu.aletheia.mixin`**:
  * 包含向 HBM SPACE 的 PWR、RBMK、爆炸系统等或原版与 Forge 注入的 Mixin 类。
* **`com.pppopipupu.aletheia.explosion` / `packet` / `entity` / `stats` / `inventory` / `interfaces`**:
  * 爆炸防爆过滤逻辑、网络数据包传输、自定义实体、统计数据与容器接口。

### 2.2 资源文件结构

* **`assets/aletheia/lang`**: 语言本地化文件 `en_US.lang` 与 `zh_CN.lang`。
* **`assets/aletheia/sounds.json`**: 注册枪械发射和音效资源。
* **`assets/aletheia/textures` / `models` / `shaders`**: 存放贴图材质、枪械与机器 3D 模型及 GLSL 着色器资源。
* **`mixins.aletheia.json`**: Early Mixin 配置。
* **`mixins.aletheia.late.json`**: Late Mixin 配置。

## 3. 编码规约

* **注释规范**: 不要在函数或逻辑内部编写任何的内联注释。不要在 shader 中编写任何注释。
* **本地化/译名规范**: 以NTMC/HBM
  SPACE的zh_cn.lang和en_us.lang为最终权威译名，不要看代码中的注册名就臆造本地化名称，例如不要将Sa326和SCHRABIDIUM混淆为不同的物质，它们都是模组的一种自造元素。
* **Mixin 规范**:
  - **安全注入原则**：严禁滥用 `@Overwrite`。除了极其特殊、非 `@Overwrite` 不可的情况之外，必须优先使用 `@Inject`、
    `@ModifyVariable` 或 `@Redirect`。
  - **客户端服务端分离准则**：在编写 Mixin 时一定要注意目标类与目标方法是否为仅客户端类，如果是仅客户端类，必须加到
    `mixins.aletheia.late.json` 的 `client` 数组中，切勿混淆。
  - **善用 UniMixins 与 MixinExtras**：采用 MixinExtras 的高级操作符例如 `@ModifyReceiver`、`@WrapOperation`、`@Local`、
    `@Share` 等，避免使用脆弱的 `@Inject` 在方法头部强制 `cancel()`，更不要使用暴力 `@Overwrite`。
  - **remap 重映射准则**：
    - 注入模组、Java 原生库或 Mojang 使用的开源库的类时，类级和方法及字段的 remap 统一设置为 `false`。
    - 注入 Minecraft 原版类时，remap 不写，默认为 `true`。
    - 注入模组类的原版方法时，类级保持 `remap = false`，原版方法单独指定 `remap = true`，不可省略。
  - **Mixin配置文件分配原则**：
    - **Early Mixin (`mixins.aletheia.json`)**：仅适用于需要尽早加载并向原版 Minecraft `net.minecraft.*`、Forge
      `net.minecraftforge.*` 或核心加载器注入的 Mixin。在此配置文件中的 Mixin 绝对不能显式引用或加载普通模组的类如
      `com.hbm.*`，否则会引发 ClassNotFoundException 或提前类加载导致崩溃。
    - **Late Mixin (`mixins.aletheia.late.json`)**：适用于所有向非原版及非核心模组注入的 Mixin，包括 HBM SPACE
      等依赖模组。大部分移植相关的 Mixin 都应该放在此处加载。
* **导入规范**: 绝对不要使用任何包名全称，能 import 包必须使用 import。绝对不可以偷懒直接写全称，必须用编辑工具正常在顶部插入
  import。
* **编译与运行测试**: 每次修改完代码后，必须使用配置好 Java 25 环境的终端运行 `.\gradlew.bat compileJava`
  进行编译校验，确保没有语法和编译期报错。在编译通过后，可以使用 `.\gradlew.bat runclient25` 启动客户端进行运行测试，确保没有任何运行时报错。
  **在检查测试和编译的日志输出时，严禁使用 `findstr`、`select-string`
  等过滤命令拦截关键信息，必须直接阅读编译与运行产生的全部日志内容，以确保能捕获任何隐性的异常、警告或潜在报错。**
* **日志规范**: 绝对禁止使用 `System.out` 或 `System.err` 输出调试或运行日志。在进行日志记录时，必须使用 Log4j 2
  Logger，且必须只使用 `com.pppopipupu.aletheia.Aletheia.LOG`。
* **操作与读写限制**: 你必须只读取 `C:\Modding\NTMC` 或相对路径 `../NTMC` 以及当前项目目录 `./`
  内的文件，只能修改当前项目目录内的文件，绝对不可读取或修改任何其他外部文件。
* **禁止制造临时字节码桩**: 严禁通过生成临时物理 `.class` 字节码或反编译 `.java` 文件（例如解压放入 `/com/` 或
  `/libs/com/`）来蒙混通过编译。如果因调试需要临时产生了此类非项目本身的物理文件，必须在 turn 结束前彻底清除。
* **资源贴图规范**: 绝对禁止使用代码或脚本生成 1x1 像素透明图、纯色块或低质量 Base64 占位图作为缺失的贴图资产。对于所有移植或依赖自
  NTMC 的物品和方块材质，必须从前置参考源 `C:\Modding\NTMC` 或 `../NTMC` 对应的 `textures/`
  资源目录中复制完整、原始的高质量贴图资产，以保证贴图的完整性与游戏内视觉效果。
