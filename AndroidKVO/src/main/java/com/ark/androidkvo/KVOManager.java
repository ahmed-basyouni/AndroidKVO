/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Ahmed basyouni
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ark.androidkvo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * manager that hold all listeners for both the fields or IDs
 * Created by ahmed-basyouni on 1/28/17.
 */
public class KVOManager {

    final private List<KVOObserverObject> observers = new ArrayList<>();
    final private HashMap<String, List<WeakReference<KVOListener>>> identifiedObservers = new HashMap<>();

    final private static KVOManager ourInstance = new KVOManager();

    public static KVOManager getInstance() {
        return ourInstance;
    }

    private KVOManager() {
    }

    public void addObserver(KVOObserverObject observerObject) {
        if (!observers.contains(observerObject)) {
            observers.add(observerObject);
        }
    }

    public void removeObserver(KVOObserverObject observerObject) {
        if (observers.contains(observerObject)) {
            observers.remove(observerObject);
        }
    }

    public List<KVOObserverObject> getObservers() {
        return observers;
    }

    public void addIdentifiedObserver(String idKey, KVOListener listener) {
        WeakReference<KVOListener> weakListener = new WeakReference<KVOListener>(listener);
        if (identifiedObservers.get(idKey) == null) {
            List<WeakReference<KVOListener>> listenersList = new ArrayList<>();
            listenersList.add(weakListener);
            identifiedObservers.put(idKey, listenersList);
        } else {
            List<WeakReference<KVOListener>> listenersList = identifiedObservers.get(idKey);
            if (!listenersList.contains(weakListener)) {
                listenersList.add(weakListener);
                identifiedObservers.put(idKey, listenersList);
            }
        }

    }

    public void removeIdentifiedObserver(KVOListener listener) {
        for (List<WeakReference<KVOListener>> listenersList : identifiedObservers.values()) {
            for (Iterator<WeakReference<KVOListener>> iterator = listenersList.iterator(); iterator.hasNext(); ) {
                KVOListener observerObject = iterator.next().get();
                if (observerObject == null || observerObject.equals(listener)) {
                    // Remove the current element from the iterator and the list.
                    iterator.remove();
                }
            }
        }
    }

    public HashMap<String, List<WeakReference<KVOListener>>> getIdentifiedObservers () {
        return identifiedObservers;
    }

    public void addListenerForId(KVOListener listener,String idKey){
        addIdentifiedObserver(idKey, listener);
    }
}