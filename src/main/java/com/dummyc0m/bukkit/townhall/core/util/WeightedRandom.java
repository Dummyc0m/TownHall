package com.dummyc0m.bukkit.townhall.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Dummyc0m on 8/19/15.
 */
public class WeightedRandom {
    private static Random random = new Random();

    private WeightedRandom() {
        //UTIL CLASS
    }

    public static int getWeight(Collection<? extends IWeightedItem> IWeightedItems) {
        int sum = 0;
        for (IWeightedItem IWeightedItem : IWeightedItems) {
            sum += IWeightedItem.getWeight();
        }
        return sum;
    }

    public static IWeightedItem getRandomItem(Collection<? extends IWeightedItem> IWeightedItems, int totalWeight) {
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("Total weight smaller than 0");
        }
        int randomNumber = random.nextInt(totalWeight);
        return getItem(IWeightedItems, randomNumber);
    }

    private static IWeightedItem getItem(Collection<? extends IWeightedItem> IWeightedItems, int randomNumber) {
        Iterator<? extends IWeightedItem> itemIterator = IWeightedItems.iterator();
        IWeightedItem IWeightedItem;
        do {
            IWeightedItem = itemIterator.next();
            randomNumber -= IWeightedItem.getWeight();
        } while (randomNumber > 0);
        return IWeightedItem;
    }

    public static IWeightedItem getRandomItem(Collection<? extends IWeightedItem> IWeightedItems) {
        return getRandomItem(IWeightedItems, getWeight(IWeightedItems));
    }

    public interface IWeightedItem {
        int getWeight();
    }
}
