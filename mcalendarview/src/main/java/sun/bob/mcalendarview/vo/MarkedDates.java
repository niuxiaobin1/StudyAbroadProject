package sun.bob.mcalendarview.vo;

import java.util.ArrayList;
import java.util.Observable;

import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.utils.CurrentCalendar;

/**
 * Created by bob.sun on 15/8/28.
 */
public class MarkedDates extends Observable {
    private static MarkedDates staticInstance;
    private ArrayList<DateData> data;
    private DateData currentSelectData= CurrentCalendar.getCurrentDateData();

    private MarkedDates(){
        super();
        data = new ArrayList<>();
    }

    public static MarkedDates getInstance(){
        if (staticInstance == null)
            staticInstance = new MarkedDates();
        return staticInstance;
    }

    public MarkStyle check(DateData date){
        int index = data.indexOf(date);
        if (index == -1) {
            return null;
        }
        return data.get(index).getMarkStyle();
    }

    public boolean remove(DateData date){
        this.setChanged();
        this.notifyObservers();
        return data.remove(date);

    }

    public MarkedDates add(DateData dateData){
        data.add(dateData);
        this.setChanged();
        this.notifyObservers();
        return this;
    }


    public ArrayList<DateData> getAll(){
        return data;
    }

    public MarkedDates removeAdd(){
        data.clear();
        this.setChanged();
        this.notifyObservers();
        return this;
    }


    public DateData getCurrentSelectData() {
        return currentSelectData;
    }

    public void setCurrentSelectData(DateData currentSelectData) {
        this.currentSelectData = currentSelectData;
        this.setChanged();
        this.notifyObservers();
    }

}
