package com.pppopipupu.aletheia.weapon;

import java.util.ArrayList;
import java.util.Collection;

import com.hbm.items.weapon.sedna.BulletConfig;

public class BulletConfigList extends ArrayList<BulletConfig> {

    public BulletConfigList(Collection<? extends BulletConfig> c) {
        super(c);
    }

    @Override
    public BulletConfig get(int index) {
        if (index == 9001) {
            return AletheiaBullets.energy_pppop;
        }
        if (index == 9002) {
            return AletheiaBullets.energy_pppop_steel;
        }
        if (index >= super.size()) {
            return null;
        }
        return super.get(index);
    }

    @Override
    public int indexOf(Object o) {
        if (o == AletheiaBullets.energy_pppop) {
            return 9001;
        }
        if (o == AletheiaBullets.energy_pppop_steel) {
            return 9002;
        }
        return super.indexOf(o);
    }

    @Override
    public int size() {
        StackTraceElement[] stackTrace = Thread.currentThread()
            .getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if ("com.hbm.items.weapon.sedna.BulletConfig".equals(element.getClassName())
                && "<init>".equals(element.getMethodName())) {
                return super.size();
            }
        }
        return Math.max(super.size(), 10000);
    }
}
