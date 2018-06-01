package sun.bob.mcalendarview.listeners;

import android.view.View;

import sun.bob.mcalendarview.utils.CurrentCalendar;
import sun.bob.mcalendarview.views.DefaultCellView;
import sun.bob.mcalendarview.views.DefaultMarkView;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

/**
 * Created by Bigflower on 2015/12/10.
 * <p>
 * 分别要对上次的和这次的处理
 * 而今日和其他日也有区别 所以有两个判断
 * 1.对上次的点击判断
 * 2.对这次的点击判断
 */
public abstract class OnExpDateClickListener extends OnDateClickListener {

    private View lastClickedView;
    private DateData lastClickedDate = CurrentCalendar.getCurrentDateData();

    @Override
    public void onDateClick(View view, DateData date) {
        if (date.equals(MarkedDates.getInstance().getCurrentSelectData())) {
            return;
        }
        MarkedDates.getInstance().setCurrentSelectData(date);

//        // 判断上次的点击
//        if (lastClickedView != null) {
//            // 节约！
//            if (lastClickedView == view) {
//                return;
//            }
//
//            if (lastClickedView instanceof DefaultCellView) {
//                ((DefaultCellView) lastClickedView).setDateNormal();
//            } else if (lastClickedView instanceof DefaultMarkView) {
//                ((DefaultMarkView) lastClickedView).setDateNormal();
//            }
//
//        }
//        // 判断这次的点击
//
//        if (view instanceof DefaultCellView) {
//            ((DefaultCellView) view).setDateChoose();
//        } else if (view instanceof DefaultMarkView) {
//            ((DefaultMarkView) view).setDateChoose();
//        }
//
//        lastClickedView = view;
//        lastClickedDate = date;
        onClick(date);

    }

    public abstract void onClick(DateData date);

}
