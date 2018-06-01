package sun.bob.mcalendarview.adapters;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnExpDateClickListener;
import sun.bob.mcalendarview.utils.CurrentCalendar;
import sun.bob.mcalendarview.views.BaseCellView;
import sun.bob.mcalendarview.views.BaseMarkView;
import sun.bob.mcalendarview.views.DefaultCellView;
import sun.bob.mcalendarview.views.DefaultMarkView;
import sun.bob.mcalendarview.vo.DayData;
import sun.bob.mcalendarview.vo.MarkedDates;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

/**
 * Created by Bigflower on 2015/12/8.
 */
public class CalendarExpAdapter extends ArrayAdapter implements Observer {
    private ArrayList data;
    private int cellView = -1;
    private int markView = -1;

    public CalendarExpAdapter(Context context, int resource, ArrayList data) {
        super(context, resource);
        this.data = data;
        MarkedDates.getInstance().addObserver(this);
    }

    public CalendarExpAdapter setCellViews(int cellView, int markView) {
        this.cellView = cellView;
        this.markView = markView;
        return this;
    }


    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View ret = null;
        DayData dayData = (DayData) data.get(position);
        MarkStyle style = MarkedDates.getInstance().check(dayData.getDate());
        boolean marked = style != null;
        if (marked) {
            dayData.getDate().setMarkStyle(style);
            if (markView > 0) {
                BaseMarkView baseMarkView = (BaseMarkView) View.inflate(getContext(), markView, null);
                baseMarkView.setDisplayText(dayData);
                ret = baseMarkView;
            } else {
                ret = new DefaultMarkView(getContext());
                ((DefaultMarkView) ret).setDisplayText(dayData);
            }
        } else {
            if (cellView > 0) {
                BaseCellView baseCellView = (BaseCellView) View.inflate(getContext(), cellView, null);
                baseCellView.setDisplayText(dayData);
                ret = baseCellView;
            } else {
                ret = new DefaultCellView(getContext());
                ((DefaultCellView) ret).setTextColor(dayData.getText(), dayData.getTextColor());
            }
        }
        ((BaseCellView) ret).setDate(dayData.getDate());
        if (OnDateClickListener.instance != null) {
            ((BaseCellView) ret).setOnDateClickListener(OnDateClickListener.instance);
        }
//        if (dayData.getDate().equals(CurrentCalendar.getCurrentDateData()) &&
//                ret instanceof DefaultCellView) {
//            ((DefaultCellView) ret).setDateToday();
//
//        }
        if (dayData.getDate().equals(CurrentCalendar.getCurrentDateData())) {
            ((BaseCellView) ret).setOrientation(LinearLayout.VERTICAL);
            ((BaseCellView) ret).setGravity(Gravity.CENTER);
            View dotView = new View(getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(10, 10);
            dotView.setLayoutParams(lp);
            ShapeDrawable dot = new ShapeDrawable(new OvalShape());
            dot.getPaint().setColor(Color.BLUE);
            dotView.setBackground(dot);
            ((BaseCellView) ret).addView(dotView);
        }


        if (dayData.getDate().equals(MarkedDates.getInstance().getCurrentSelectData())) {
            if (ret instanceof DefaultCellView) {
                ((DefaultCellView) ret).setDateChoose();
            } else if (ret instanceof DefaultMarkView) {
                ((DefaultMarkView) ret).setDateChoose();
            }

        }
        return ret;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public void update(Observable observable, Object data) {
        this.notifyDataSetChanged();
    }
}