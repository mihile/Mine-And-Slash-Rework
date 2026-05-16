package com.robertx22.mine_and_slash.database.data.requirements.bases;

import com.robertx22.mine_and_slash.database.data.gear_types.bases.BaseGearType;
import com.robertx22.mine_and_slash.saveclasses.item_classes.GearItemData;

public class GearRequestedFor {

    public BaseGearType forSlot;

    public GearRequestedFor(GearItemData data) {
        this.forSlot = data.GetBaseGearType();
    }

    public GearRequestedFor(BaseGearType slot) {
        this.forSlot = slot;
    }

}
