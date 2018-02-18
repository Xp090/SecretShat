package me.xp090.secretshat.firebase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Xp090 on 17/02/2018.
 */

public class HashedList<K, V> {
    private final List<K> mList = new ArrayList<>();
    private final Map<K, V> mHashMap = new LinkedHashMap<>();

    public int put(K key, V value) {
        int index;
        if (!mHashMap.containsKey(key)) {
            mList.add(key);
            index = mList.size() - 1;
        } else {
            index = mList.indexOf(key);
        }
        mHashMap.put(key, value);
        return index;
    }

    public void remove(K key) {
        mHashMap.remove(key);
        mList.remove(key);
    }

    public V getElementByIndex(int index) {
        K key = mList.get(index);
        return getElementByKey(key);
    }

    public V getElementByKey(K key) {
        return mHashMap.get(key);
    }

    public int size() {
        return mList.size();
    }
}
