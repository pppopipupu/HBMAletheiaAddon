package com.pppopipupu.aletheia;

import java.util.Collections;
import java.util.List;

import io.github.tox1cozz.mixinbooterlegacy.ILateMixinLoader;
import io.github.tox1cozz.mixinbooterlegacy.LateMixin;

@LateMixin
public class AletheiaLateMixins implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.aletheia.late.json");
    }
}
