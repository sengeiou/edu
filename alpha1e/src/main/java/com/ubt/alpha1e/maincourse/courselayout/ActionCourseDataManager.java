package com.ubt.alpha1e.maincourse.courselayout;

import android.content.Context;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.base.popup.HorizontalGravity;
import com.ubt.alpha1e.base.popup.VerticalGravity;
import com.ubt.alpha1e.maincourse.model.CourseActionModel;
import com.ubt.alpha1e.maincourse.model.CourseOne1Content;
import com.ubt.alpha1e.maincourse.model.LocalActionRecord;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liuhai
 * @date：2017/12/14 15:54
 * @modifier：ubt
 * @modify_date：2017/12/14 15:54
 * [A brief description]
 * version
 */

public class ActionCourseDataManager {
    /**
     * 获取关卡一第一个课时数据
     *
     * @param context
     * @return
     */
    public static List<CourseOne1Content> getCardOneList(Context context) {
        List<CourseOne1Content> one1ContentList = new ArrayList<>();
        CourseOne1Content one1Content1 = new CourseOne1Content();
        one1Content1.setIndex(0);
        one1Content1.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_1"));
        one1Content1.setId(R.id.ll_frame);
        one1Content1.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content1.setActionPath(Constant.COURSE_ACTION_PATH + "AE_action editor2.hts");
        one1Content1.setTitle("时间轴");
        one1Content1.setDirection(0);
        one1Content1.setX(0);
        one1Content1.setY(-50);
        one1Content1.setVertGravity(VerticalGravity.CENTER);
        one1Content1.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList.add(one1Content1);

        CourseOne1Content one1Content3 = new CourseOne1Content();
        one1Content3.setIndex(2);
        one1Content3.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_3"));
        one1Content3.setId(R.id.rl_musicz_zpne);
        one1Content3.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content3.setActionPath(Constant.COURSE_ACTION_PATH + "AE_action editor3.hts");
        one1Content3.setTitle("音乐轴");
        one1Content3.setX(0);
        one1Content3.setY(-50);
        one1Content3.setVertGravity(VerticalGravity.CENTER);
        one1Content3.setHorizGravity(HorizontalGravity.RIGHT);
        one1Content3.setDirection(0);
        one1ContentList.add(one1Content3);

        CourseOne1Content one1Content4 = new CourseOne1Content();
        one1Content4.setIndex(3);
        one1Content4.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_4"));
        one1Content4.setId(R.id.iv_reset_index);
        one1Content4.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content4.setActionPath(Constant.COURSE_ACTION_PATH + "AE_action editor4.hts");
        one1Content4.setTitle("动作帧");
        one1Content4.setDirection(0);
        one1Content4.setX(80);
        one1Content4.setY(30);
        one1Content4.setVertGravity(VerticalGravity.ALIGN_BOTTOM);
        one1Content4.setHorizGravity(HorizontalGravity.RIGHT);
        one1ContentList.add(one1Content4);

        CourseOne1Content one1Content5 = new CourseOne1Content();
        one1Content5.setIndex(4);
        one1Content5.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_5"));
        one1Content5.setId(R.id.iv_add_frame);
        one1Content5.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
        one1Content5.setActionPath(Constant.COURSE_ACTION_PATH + "AE_action editor5.hts");
        one1Content5.setTitle("缩放时间轴");
        one1Content5.setDirection(1);
        one1Content5.setX(20);
        one1Content5.setY(0);
        one1Content5.setVertGravity(VerticalGravity.CENTER);
        one1Content5.setHorizGravity(HorizontalGravity.LEFT);
        one1ContentList.add(one1Content5);
//
//        CourseOne1Content one1Content5 = new CourseOne1Content();
//        one1Content5.setIndex(4);
//        one1Content5.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_5"));
//        one1Content5.setId(R.id.iv_add_frame);
//        one1Content5.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
//        one1Content5.setActionPath(Constant.COURSE_ACTION_PATH + "添加按钮.hts");
//        one1Content5.setTitle("添加按钮");
//        one1Content5.setDirection(1);
//        one1Content5.setX(20);
//        one1Content5.setY(0);
//        one1Content5.setVertGravity(VerticalGravity.CENTER);
//        one1Content5.setHorizGravity(HorizontalGravity.LEFT);
//        one1ContentList.add(one1Content5);
//
//        CourseOne1Content one1Content6 = new CourseOne1Content();
//        one1Content6.setIndex(5);
//        one1Content6.setContent(ResourceManager.getInstance(context).getStringResources("action_course_card1_1_6"));
//        one1Content6.setId(R.id.iv_play_music);
//        one1Content6.setVoiceName("{\"filename\":\"id_elephant.wav\",\"playcount\":1}");
//        one1Content6.setActionPath(Constant.COURSE_ACTION_PATH + "播放按钮.hts");
//        one1Content6.setTitle("播放按钮");
//        one1Content6.setDirection(0);
//        one1Content6.setX(-20);
//        one1Content6.setY(0);
//        one1Content6.setVertGravity(VerticalGravity.CENTER);
//        one1Content6.setHorizGravity(HorizontalGravity.RIGHT);
//        one1ContentList.add(one1Content6);
        return one1ContentList;
    }

    /**
     * 关卡课时及状态
     *
     * @return
     */
    public static List<CourseActionModel> getCourseActionModel(int card, int currentCourse) {
        List<CourseActionModel> list = new ArrayList<>();
        CourseActionModel model1 = null;
        CourseActionModel model2 = null;
        CourseActionModel model3 = null;
        CourseActionModel model4 = null;
        if (card == 1) {
            model1 = new CourseActionModel("1.认识时间轴", 0);
            model2 = new CourseActionModel("2.了解添加键", 0);
            model3 = new CourseActionModel("3.了解播放键", 0);
            list.add(model1);
            list.add(model2);
            list.add(model3);
        } else if (card == 2) {
            model1 = new CourseActionModel("1.了解动作库", 0);
            model2 = new CourseActionModel("2.初级动作库", 0);
            model3 = new CourseActionModel("3.高级动作库", 0);
            list.add(model1);
            list.add(model2);
            list.add(model3);
        } else if (card == 3) {
            model1 = new CourseActionModel("1.了解音乐库", 0);
            model2 = new CourseActionModel("2.添加音频", 0);
            list.add(model1);
            list.add(model2);
        } else if (card == 4) {
            model1 = new CourseActionModel("1.添加舞蹈动作", 0);
            model2 = new CourseActionModel("2.添加london音频", 0);
            model3 = new CourseActionModel("3.预览执行", 0);
            list.add(model1);
            list.add(model2);
            list.add(model3);
        } else if (card == 5) {
            model1 = new CourseActionModel("1.模板讲解", 0);
            model2 = new CourseActionModel("2.创建指定动作—踢腿", 0);
            model3 = new CourseActionModel("3.保存动作", 0);
            list.add(model1);
            list.add(model2);
            list.add(model3);
        } else if (card == 6) {
            model1 = new CourseActionModel("1.模板讲解", 0);
            model2 = new CourseActionModel("2.创建指定音频", 0);
            model3 = new CourseActionModel("3.添加创建音频", 0);
            model4 = new CourseActionModel("4.预览执行动作", 0);
            list.add(model1);
            list.add(model2);
            list.add(model3);
            list.add(model4);
        } else if (card == 7) {
            model1 = new CourseActionModel("1.了解目标动作", 0);
            model2 = new CourseActionModel("2.修改指定动作", 0);
            list.add(model1);
            list.add(model2);
        } else if (card == 8) {
            model1 = new CourseActionModel("1.创建指定动作", 0);
            model2 = new CourseActionModel("2.预览执行动作", 0);
            list.add(model1);
            list.add(model2);
        } else if (card == 9) {
            model1 = new CourseActionModel("1.创建指定动作", 0);
            model2 = new CourseActionModel("2.预览执行动作", 0);
            list.add(model1);
            list.add(model2);
        } else if (card == 10) {
            model1 = new CourseActionModel("1.设计动作", 0);
            list.add(model1);

        }
        if (currentCourse == 1) {
            model1.setStatu(2);
        } else if (currentCourse == 2) {
            model1.setStatu(1);
            model2.setStatu(2);
        } else if (currentCourse == 3) {
            model1.setStatu(1);
            model2.setStatu(1);
            model3.setStatu(2);
        } else if (currentCourse == 4) {
            model1.setStatu(1);
            model2.setStatu(1);
            model3.setStatu(1);
            model4.setStatu(2);
        }

        return list;
    }


    /**
     * 关卡列表页获取数据
     *
     * @param position
     * @param size
     * @return
     */
    public static List<CourseActionModel> getCourseDataList(int position, int size) {
        int level = 1;// 当前第几个课时
        int currentCourse = position + 1;
        LocalActionRecord record = DataSupport.findFirst(LocalActionRecord.class);
        if (null != record) {
            UbtLog.d("getDataList", "record===" + record.toString());
            int course = record.getCourseLevel();
            int recordlevel = record.getPeriodLevel();
            if (course == currentCourse) {//只有当最新记录跟position+1相等时才需要获取到课时
//                if (recordlevel < size) {
//                    level = ++recordlevel;
//                } else if (recordlevel == size) {
//                    level = 1;
//                }
                level = recordlevel;
            }
        }
        return getCourseActionModel(currentCourse, 1);
    }


}
