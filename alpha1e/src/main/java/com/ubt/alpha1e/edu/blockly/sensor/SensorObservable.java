package com.ubt.alpha1e.edu.blockly.sensor;

import com.ubt.alpha1e.edu.blockly.bean.QueryResult;

import java.util.Observable;
import java.util.Observer;

/**
 * @className SensorObservable
 *
 * @author wmma
 * @description 传感器事件被观察者
 * @date 2017/4/19
 * @update
 */


public class SensorObservable extends Observable {

    private final String TAG = SensorObservable.class.getSimpleName();

    private QueryResult data;

    private Observer activedObserver;

    private static SensorObservable instance;

    public static SensorObservable getInstance(){
        if(instance == null){
            synchronized (SensorObservable.class){
                if(instance == null) {
                    instance = new SensorObservable();
                }
            }
        }
        return instance;
    }

    public void setData(QueryResult data){
        if(this.countObservers() < 1){
            return;
        }
        this.data = data;
        setChanged();
        notifyObservers(data);
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
        reActiveObserver();
        this.activedObserver = observer;
    }

    /**
     * 重新监听
     */
    public void reActiveObserver(){
        if(this.activedObserver != null){
            this.addObserver(this.activedObserver);
            this.activedObserver = null;
        }
    }

    public void clearActiveObserver() {
        this.activedObserver = null;
    }
}
