# 更新日志 / Changelog

## 1.4.0
### 新增 Added
- 移植并实现锆诺克斯（Zirnox）反应堆对夸克-胶子等离子体（QGP）燃料棒与迪伽马（Digamma）燃料棒的支持。
- 注册 QGP 燃料棒、迪伽马燃料棒、RBMK QGP 燃料元件等物品及其对应的枯竭（Depleted）状态。
- 新增 QGP 及迪伽马相关的化学加工、PUREX 后处理与工作台合成配方。
- 引入 QGP 与迪伽马燃料棒的着色器（`qgp_column.frag`、`qgp_rod.frag`、`zirnox_digamma.frag`、`zirnox_digamma_column.frag`）与贴图资产。

### 更改 Changed
- 升级前置依赖 `HBM SPACE` 版本至 `1.0.27_X5758_H261`。
- 升级 DecayLib 依赖至 `1.1.3`（支持变质 tooltip 根据剩余 tick 数量自适应组装天与小时显示）。

### 修复 Fixed
- 修复锆诺克斯堆插入 QGP 或迪伽马燃料棒时特殊渲染损坏导致透明或世界渲染崩坏的 Bug。重构使其继承 `RenderZirnox` 以绘制本体模型，并规范了特殊光柱在绘制时的 OpenGL 状态（包含主纹理 `GL_TEXTURE_2D` 的关闭与恢复，以及混合 `GL_BLEND` 的配对清理）。

## 1.3.0
### 新增 Added
- 新增 NTMC 配方移植模块 `com.pppopipupu.aletheia.recipe.ntmc`，从 NTMC 源码逐字移植其更新日志中的配方改动，作为 Aletheia 对 HBM SPACE 的覆盖层（不删除任何既有配方）。
- 覆盖 / 新增的机器配方类别：
  - 坩埚炼钢（`crucible.steel` / `steelWrought` / `steelPig` / `steelMeteoric`）
  - 热解炉基岩矿焙烧（先移除 HBM 既有基岩矿条目再注入 NTMC 版）
  - 回转炉合金、血压缩制油（NTMC 实际为血液→重油）
  - 电解铝（`crystal_aluminium`、铝土→钠 200mB）、Tennessine（钚部件 + 磷粉 → 𬭊粉）
  - 结晶器石英种子、电弧焊赛特斯石英（AE2 兼容）
  - 流体分馏（`fp_*`）、硝酸 / 硫酸混合、离心机（烈焰棒 / 地狱煤 / 晶体钶→镎 / 瓦斯→泥浆）
  - 粒子加速器曝光天然铀→sa、铋+奇夸克→Pb209 及 muon/higgs/tachyon 阈值
  - PUREX 核燃料后处理（整体覆盖后重注册 NTMC 版）、装配机、化工混合、工作台/Forge 配方
  - 砧 construction/smithing tier 与结晶器生产力的逻辑覆盖（通过 `AletheiaRecipesNtmcOverrides` 在 postInit 阶段反射生效）

### 更改 Changed
- 升级 DecayLib 依赖至 `1.1.2`（替换 `libs/DecayLib-1.0.0-dev.jar`）。
- 更新 `AGENTS.md`：补全 Mixin 双阶段装载（Early/Late）说明与编码规约。

### 修复 Fixed
- 修正中文语言文件 `zh_CN.lang` 若干文本与错别字（如「蠢虫」→「蠹虫」、`agri.bottle` 去除「批量合成」、成就 `amsBase` 更名为 Project Aletheia / Omega）。
