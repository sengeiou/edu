package com.ubt.alpha1e.action;

import android.content.Context;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.ui.BaseActivity;
import com.ubt.alpha1e.utils.log.UbtLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author admin
 * @className ActionData
 * @description   创建action用到的数据
 */


public class ActionData {

    private final String TAG = "ActionData";

    public static final String ACTION_TIME = "action_time";
    public static final String ACTION_ANGLE = "action_angle";
    public static final String ACTION_NAME = "action_name";
    public static final String ACTION_ICON = "action_icon";
    public static final String ACTION_LIST = "action_list";

    public static final String WARRIOR = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"220\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"39#13#18#91#149#137#83#59#75#110#100#102#120#104#70#80\",\n" +
            "        \"-xmlAllRunTime\": \"240\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"220\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"60#40#75#91#149#137#83#59#75#110#100#102#120#104#70#80\",\n" +
            "        \"-xmlAllRunTime\": \"240\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final String STOOP = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"440\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"90#17#90#90#160#90#85#76#112#88#95#95#102#71#86#85\",\n" +
            "        \"-xmlAllRunTime\": \"440\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1400\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"5#89#89#180#85#98#85#168#33#174#90#95#14#152#7#90\",\n" +
            "        \"-xmlAllRunTime\": \"1400\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final  String SQUAT = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"540\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"3\",\n" +
            "        \"-xmldata\": \"65#22#67#123#168#105#90#42#86#92#90#91#142#95#88#90\",\n" +
            "        \"-xmlAllRunTime\": \"540\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"540\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"3\",\n" +
            "        \"-xmldata\": \"38#23#3#140#180#157#90#22#9#140#90#91#157#172#42#90\",\n" +
            "        \"-xmlAllRunTime\": \"1660\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"540\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"3\",\n" +
            "        \"-xmldata\": \"60#20#61#117#160#125#90#63#76#110#90#91#120#106#72#90\",\n" +
            "        \"-xmlAllRunTime\": \"540\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final  String LEFTHAND = " {\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"440\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"90#30#60#90#80#30#80#60#76#110#100#80#120#104#70#95\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"440\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"90#20#70#90#160#110#90#60#76#110#94#90#120#104#70#86\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final  String RIGHTHAND = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"440\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"90#110#150#90#150#120#100#60#76#110#84#99#120#104#70#77\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"440\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"90#20#70#90#160#110#90#60#76#110#94#90#120#104#70#86\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final  String MECH_DANCE1 = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"420\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"47#13#20#109#22#12#81#53#57#120#96#83#118#113#62#95\",\n" +
            "        \"-xmlAllRunTime\": \"420\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"220\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"64#13#20#109#22#12#81#93#85#120#96#83#79#85#62#95\",\n" +
            "        \"-xmlAllRunTime\": \"440\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final  String MECH_DANCE2 = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"420\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"72#164#161#134#177#147#92#53#57#120#88#96#126#124#62#84\",\n" +
            "        \"-xmlAllRunTime\": \"420\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"220\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"72#164#161#118#177#147#92#78#76#120#88#96#100#99#62#85\",\n" +
            "        \"-xmlAllRunTime\": \"440\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final String HUG= "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1000\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"17#27#57#162#144#133#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"1000\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"17#7#66#166#169#114#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"17#27#57#162#144#133#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1500\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"93#20#66#86#156#127#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"1500\",\n" +
            "        \"-xmlFrameIndex\": \"4\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final   String HAPPY = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"300\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"0#6#72#166#172#91#90#37#47#115#106#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"300\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"200\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"32#15#72#180#171#94#90#60#76#110#90#90#139#134#60#74\",\n" +
            "        \"-xmlAllRunTime\": \"200\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"200\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"0#6#72#166#172#91#90#37#47#115#106#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"200\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"200\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"32#15#72#180#171#94#90#60#76#110#90#90#139#134#60#74\",\n" +
            "        \"-xmlAllRunTime\": \"200\",\n" +
            "        \"-xmlFrameIndex\": \"4\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"200\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"0#6#72#166#172#91#90#37#47#115#106#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"200\",\n" +
            "        \"-xmlFrameIndex\": \"5\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1000\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"90#5#90#89#172#91#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"1000\",\n" +
            "        \"-xmlFrameIndex\": \"6\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final   String SALUTE = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1000\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"3\",\n" +
            "        \"-xmldata\": \"95#15#76#86#170#97#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"1000\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"3\",\n" +
            "        \"-xmldata\": \"95#15#76#55#37#0#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"2000\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1500\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"3\",\n" +
            "        \"-xmldata\": \"93#20#66#86#156#127#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"1500\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final  String WALK = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"460\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"127#16#72#134#160#111#90#37#47#115#106#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"460\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"39#16#72#64#164#111#90#60#76#110#90#90#139#134#60#74\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final  String TWIST = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"420\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"91#45#95#124#175#155#79#59#75#110#100#91#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"420\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"220\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"90#45#95#132#172#163#58#73#70#119#110#70#101#95#67#100\",\n" +
            "        \"-xmlAllRunTime\": \"240\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"420\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"91#45#95#124#175#155#79#59#75#110#100#91#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"420\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"220\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"90#45#95#132#172#163#58#73#70#119#110#70#101#95#67#100\",\n" +
            "        \"-xmlAllRunTime\": \"240\",\n" +
            "        \"-xmlFrameIndex\": \"4\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final  String STEPPIN = " {\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"440\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"90#160#90#90#20#90#90#60#76#110#110#90#120#104#70#70\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"440\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"90#20#90#90#160#90#90#60#76#110#94#90#120#104#70#86\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";

    public static final String BENT = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1340\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"98#149#155#84#0#74#22#91#129#80#137#51#121#132#50#106\",\n" +
            "        \"-xmlAllRunTime\": \"1340\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1500\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"2\",\n" +
            "        \"-xmldata\": \"98#149#155#84#1#74#95#88#129#78#111#124#128#154#36#81\",\n" +
            "        \"-xmlAllRunTime\": \"1500\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";


    public static final String ARM = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"88#24#82#92#152#101#90#37#47#115#106#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"87#170#82#92#24#101#90#60#76#110#90#90#139#134#60#74\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"87#66#82#92#117#101#90#37#47#115#106#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"88#24#82#92#152#101#90#60#76#110#90#90#139#134#60#74\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"4\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"88#29#49#91#150#132#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"5\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";


    public static final String DANCE1 = " {\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"440\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"89#69#166#90#160#90#90#60#76#110#94#90#120#104#70#86\",\n" +
            "        \"-xmlAllRunTime\": \"600\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"440\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"89#68#166#94#118#6#90#60#76#110#94#90#120#104#70#86\",\n" +
            "        \"-xmlAllRunTime\": \"600\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"600\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"89#162#130#90#27#43#90#60#76#110#94#90#120#104#70#86\",\n" +
            "        \"-xmlAllRunTime\": \"700\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"700\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"4\",\n" +
            "        \"-xmldata\": \"91#26#64#90#153#116#90#60#76#110#94#90#120#104#70#86\",\n" +
            "        \"-xmlAllRunTime\": \"700\",\n" +
            "        \"-xmlFrameIndex\": \"4\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";


    public static final String DANCE2 = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"380\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"380\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"300\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"95#11#78#91#162#103#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"300\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"400\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"95#11#78#91#162#103#83#60#76#110#99#81#120#104#70#100\",\n" +
            "        \"-xmlAllRunTime\": \"400\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"400\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"159#6#89#135#161#122#78#41#119#53#118#81#129#104#70#100\",\n" +
            "        \"-xmlAllRunTime\": \"460\",\n" +
            "        \"-xmlFrameIndex\": \"4\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"400\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"95#11#78#91#162#103#83#60#76#110#99#81#120#104#70#100\",\n" +
            "        \"-xmlAllRunTime\": \"440\",\n" +
            "        \"-xmlFrameIndex\": \"5\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"400\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"33#2#74#3#161#106#95#53#76#110#84#94#142#76#116#66\",\n" +
            "        \"-xmlAllRunTime\": \"460\",\n" +
            "        \"-xmlFrameIndex\": \"6\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"400\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"95#11#78#91#162#103#95#60#76#110#84#93#120#104#70#84\",\n" +
            "        \"-xmlAllRunTime\": \"440\",\n" +
            "        \"-xmlFrameIndex\": \"7\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"400\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"159#6#89#136#161#122#78#41#119#53#118#81#129#104#70#100\",\n" +
            "        \"-xmlAllRunTime\": \"460\",\n" +
            "        \"-xmlFrameIndex\": \"8\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"400\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"95#11#78#91#162#103#83#60#76#110#97#83#120#104#70#98\",\n" +
            "        \"-xmlAllRunTime\": \"440\",\n" +
            "        \"-xmlFrameIndex\": \"9\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"400\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"169#11#78#144#165#127#82#104#82#147#114#83#120#104#70#98\",\n" +
            "        \"-xmlAllRunTime\": \"460\",\n" +
            "        \"-xmlFrameIndex\": \"10\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"300\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"95#11#78#91#162#103#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"300\",\n" +
            "        \"-xmlFrameIndex\": \"11\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"200\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"95#11#78#91#162#103#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"200\",\n" +
            "        \"-xmlFrameIndex\": \"12\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"300\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"113#17#80#103#152#121#90#35#36#124#117#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"480\",\n" +
            "        \"-xmlFrameIndex\": \"13\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"300\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"71#12#85#64#166#108#90#60#76#110#93#90#138#131#62#73\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"14\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"300\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"113#17#80#103#152#121#90#35#36#124#117#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"480\",\n" +
            "        \"-xmlFrameIndex\": \"15\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"300\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"16\",\n" +
            "        \"-xmldata\": \"71#12#85#64#166#108#90#60#76#110#93#90#138#131#62#73\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"16\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";



    public static final String CURTAIN = " {\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1500\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"90#90#90#90#90#90#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"1500\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"120#40#59#133#133#107#90#57#140#70#90#90#125#46#108#90\",\n" +
            "        \"-xmlAllRunTime\": \"1500\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"800\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"120#35#39#133#163#157#90#57#140#70#90#90#125#46#108#90\",\n" +
            "        \"-xmlAllRunTime\": \"1500\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"800\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"120#40#59#133#133#107#90#57#140#70#90#90#125#48#107#90\",\n" +
            "        \"-xmlAllRunTime\": \"800\",\n" +
            "        \"-xmlFrameIndex\": \"4\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"800\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"90#35#48#90#154#130#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"800\",\n" +
            "        \"-xmlFrameIndex\": \"5\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";


    public static final String BYE = "{\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"820\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"119#172#139#86#155#126#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"820\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"200\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"119#150#121#86#155#126#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"200\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"140\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"119#180#121#86#156#126#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"140\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"200\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"119#150#121#86#155#126#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"200\",\n" +
            "        \"-xmlFrameIndex\": \"4\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"140\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"119#180#121#86#156#126#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"140\",\n" +
            "        \"-xmlFrameIndex\": \"5\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1500\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"6\",\n" +
            "        \"-xmldata\": \"93#20#66#86#156#127#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"1500\",\n" +
            "        \"-xmlFrameIndex\": \"6\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";



    private Context context;
    private BaseActivity baseMvpActivity;

    public String [] basicAction ;
    public String [] advanceActionName;
    public static final int[] basicIconID = new int[] {R.drawable.xingzou, R.drawable.niuyao,
            R.drawable.dianjiao,R.drawable.cewanyao, R.drawable.shoubi,R.drawable.wudao1,
            R.drawable.wudao2, R.drawable.xiemu,R.drawable.zaijian};
    public static final int [] advanceIconID = new int[] {R.drawable.chuzhao,R.drawable.xiayao,R.drawable.dunxia,
            R.drawable.zuotaishou,R.drawable.taiyoushou, R.drawable.jixie1, R.drawable.jixie2,
            R.drawable.baobao,R.drawable.kaixin,R.drawable.jinli};


    private List<Map<String, Object>> listWarrior = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listStoop = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listSquat = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listLeftHand = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listRightHand = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listMechDance1 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listMechDance2 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listHug = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listHappy = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listSalute = new ArrayList<Map<String, Object>>();

    private List<Map<String, Object>> listWalk = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listTwist = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listSteppin = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listBent = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listArm = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listDance1 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listDance2 = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listCurtain = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listBye = new ArrayList<Map<String, Object>>();



    public ActionData(Context context){
        this.context = context;
        baseMvpActivity = (ActionsCreateActivity) context;
        initActionsNameData();
    }


    public void initActionsNameData(){
        UbtLog.d(TAG, "advanceActionName:" + advanceActionName + "---basicAction:" + basicAction);

        if(advanceActionName == null){
            advanceActionName = new String[]{baseMvpActivity.getStringResources("ui_advance_action_walk"), baseMvpActivity.getStringResources("ui_advance_action_twist"), baseMvpActivity.getStringResources("ui_advance_action_steppin"),baseMvpActivity.getStringResources("ui_advance_action_bent"), baseMvpActivity.getStringResources("ui_advance_action_arm"),
                    baseMvpActivity.getStringResources("ui_advance_action_dance1"), baseMvpActivity.getStringResources("ui_advance_action_dance2"), baseMvpActivity.getStringResources("ui_advance_action_curtain"), baseMvpActivity.getStringResources("ui_advance_action_bye")};
        }

        if(basicAction == null){
            basicAction = new String[]{baseMvpActivity.getStringResources("ui_basic_action_warrior"), baseMvpActivity.getStringResources("ui_basic_action_stoop"), baseMvpActivity.getStringResources("ui_basic_action_squat"), baseMvpActivity.getStringResources("ui_basic_action_left_hand"), baseMvpActivity.getStringResources("ui_basic_action_right_hand"),
                    baseMvpActivity.getStringResources("ui_basic_action_mech_dance1"), baseMvpActivity.getStringResources("ui_basic_action_mech_dance2"), baseMvpActivity.getStringResources("ui_basic_action_hug"),baseMvpActivity.getStringResources("ui_basic_action_happy"),baseMvpActivity.getStringResources("ui_basic_action_salute")};
        }

    }

    public void initActionList(){
        //初级动作
        listWarrior = actionInfo(initActionData(WARRIOR, listWarrior), basicAction[0], basicIconID[0]);
        listStoop = actionInfo(initActionData(STOOP, listStoop), basicAction[1], basicIconID[1]);
        listSquat = initActionData(SQUAT,listSquat);
        listLeftHand = initActionData(LEFTHAND, listLeftHand);
        listRightHand = initActionData(RIGHTHAND, listRightHand);
        listMechDance1 = initActionData(MECH_DANCE1, listMechDance1);
        listMechDance2 = initActionData(MECH_DANCE2, listMechDance2);
        listHug = initActionData(HUG, listHug);
        listHappy = initActionData(HAPPY, listHappy);
        listSalute = initActionData(SALUTE, listSalute);

        //高级动作
        listWalk = initActionData(WALK, listWalk);
        listTwist = initActionData(TWIST, listTwist);
        listSteppin = initActionData(STEPPIN, listSteppin);
        listBent = initActionData(BENT, listBent);
        listArm = initActionData(ARM, listArm);
        listDance1 = initActionData(DANCE1, listDance1);
        listDance2 = initActionData(DANCE2, listDance2);
        listCurtain = initActionData(CURTAIN,listCurtain);
        listBye = initActionData(BYE, listBye);

    }

    public List<Map<String, Object>> actionInfo(List<Map<String, Object>> list, String actionName, int actionIconId){
        List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ACTION_LIST, list);
        map.put(ACTION_NAME, actionName);
        map.put(ACTION_ICON, actionIconId);
        infoList.add(map);
        return  infoList;

    }


    public List<Map<String, Object>>  initActionData(String json, List<Map<String, Object>> list){
        try {
            JSONObject zuoJsonObject = new JSONObject(json);
            JSONArray zuoJsonArray= zuoJsonObject.getJSONArray("frame");
            for(int i=0; i<zuoJsonArray.length(); i++){
                Map<String, Object> map = new HashMap<String, Object>();
                JSONObject jsonObject = (JSONObject) zuoJsonArray.get(i);
                UbtLog.d(TAG, "jsonObject:" + jsonObject.toString());
                map.put(ACTION_TIME, jsonObject.get("-xmlRunTime"));
                map.put(ACTION_ANGLE, jsonObject.get("-xmldata"));
                list.add(i, map);
            }

            UbtLog.d(TAG, "list:" + list);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  list;


    }

}
