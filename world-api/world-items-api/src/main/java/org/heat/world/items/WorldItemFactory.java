package org.heat.world.items;

public interface WorldItemFactory {
    /**
     * Create an item given a template id and a quantity
     * @param templateId an integer
     * @param quantity an integer
     * @return a non-null item
     */
    WorldItem create(int templateId, int quantity);
}
