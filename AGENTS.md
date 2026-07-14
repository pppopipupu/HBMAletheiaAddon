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
  * **移植对照参考源 (NTMC 源码库)**: 本地路径为 `C:\Modding\NTMC`。包含完整的 `本地 NTMC` 源码及全部像素美术资产。AGENT 在移植任何独占内容时，必须将其作为最高权威的对照蓝本进行核对与直接复制，确保完全无缝移植。
  * **NotEnoughItems (NEI)**: 配方与物品检索支持。
  * **Sedna 枪械框架**: 模组重用了 HBM SPACE 分支的模块化枪械系统，支持复杂的 3D 渲染和自定义枪械配置。
  * **Mixin**: 使用 Mixin 框架向 `HBM SPACE` 中织入自定义的升级机制以及爆炸拦截逻辑。

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

* **注释规范**: 不要在函数或逻辑内部编写任何的内联注释。不要在shader中编写任何注释。
* **Mixin规范**: 绝对不要使用任何的 `@Overwrite`，除非不用就无法实现功能，如果要使用也必须由用户 review 确认。应当优先使用 `@Inject` 配合 Duck 接口或 `@Redirect` 等安全的注入机制。
* **导入规范**: 绝对不要使用任何包名全称，能import包必须使用import。绝对不可以偷懒直接写全称，必须用编辑工具正常在顶部插入import。
*   **编译与运行测试**: 每次修改完代码后，必须通过本地终端运行 `$env:JAVA_HOME="C:\Program Files\Java\jdk-25.0.3"; .\gradlew.bat compileJava` 进行编译校验，确保没有语法和编译期报错。在编译通过后，应当在本地运行 `$env:JAVA_HOME="C:\Program Files\Java\jdk-25.0.3"; .\gradlew.bat runclient25` 启动客户端进行运行测试，确保没有任何运行时报错。
*   **日志规范**: 绝对禁止使用 `System.out` 或 `System.err` 输出调试或运行日志。在进行日志记录时，必须使用 Log4j 2 Logger，且必须只使用 `com.pppopipupu.aletheia.Aletheia.LOG`。
*   **操作与读写限制**: 你必须只读取 `C:\Modding\NTMC` 和 `C:\Users\pppop\Desktop\HBMAletheiaAddon` 内的文件，只能修改 `C:\Users\pppop\Desktop\HBMAletheiaAddon` 内的文件，绝对不可读取或修改任何其他文件。
*   **禁止制造临时字节码桩**: 严禁通过生成临时物理 `.class` 字节码或反编译 `.java` 文件（例如解压放入 `/com/` 或 `/libs/com/`）来蒙混通过编译。如果因调试需要临时产生了此类非项目本身的物理文件，必须在 turn 结束前彻底清除。
*   **资源贴图规范**: 绝对禁止使用代码/脚本生成 1x1 像素透明图、纯色块或低质量 Base64 占位图作为缺失的贴图资产。对于所有移植/依赖自 NTMC 的物品和方块材质，必须从前置项目 `C:\Modding\NTMC` 对应的 `textures/` 资源目录中复制完整、原始的高质量贴图资产，以保证贴图的完整性与游戏内视觉效果。


