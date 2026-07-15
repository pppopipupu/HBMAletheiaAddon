# 更新日志 / Changelog

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
