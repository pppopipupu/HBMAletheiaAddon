package com.pppopipupu.aletheia.interfaces;

public interface IModuleMachineAccess {

    void aletheia$setUltimateCount(int count);

    void aletheia$setProductionMult(int mult);

    void aletheia$setSpeedMult(int mult);

    void aletheia$setPowerMult(double mult);

    int aletheia$getUltimateCount();

    int aletheia$getProductionMult();

    int aletheia$getSpeedMult();

    double aletheia$getPowerMult();
}
