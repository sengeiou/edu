package com.ubt.alpha1e.action.model;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.base.ResourceManager;
import com.ubt.alpha1e.data.FileTools;
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
 * @className
 * @description
 * @date
 * @update
 */


public class ActionConstant {

    //出招
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

    public static final String SQUAT = "{\n" +
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

    public static final String LEFTHAND = " {\n" +
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

    public static final String RIGHTHAND = "{\n" +
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

    public static final String MECH_DANCE1 = "{\n" +
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

    public static final String MECH_DANCE2 = "{\n" +
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

    public static final String HUG = "{\n" +
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

    public static final String HAPPY = "{\n" +
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

    public static final String SALUTE = "{\n" +
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

    public static final String WALK = "{\n" +
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

    public static final String TWIST = "{\n" +
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

    public static final String STEPPIN = " {\n" +
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

    public static final String WOSHOU = " {\n" +
            "    \"frame\": [\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"1\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"18#12#63#87#165#101#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"47#12#63#87#165#101#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"2\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"18#12#63#87#165#101#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"500\",\n" +
            "        \"-xmlFrameStatus\": \"2\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"47#12#63#87#165#101#90#60#76#110#90#90#120#104#70#90\",\n" +
            "        \"-xmlAllRunTime\": \"500\",\n" +
            "        \"-xmlFrameIndex\": \"4\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"-xmlRunTime\": \"1500\",\n" +
            "        \"-xmlFrameStatus\": \"3\",\n" +
            "        \"-xmlFrameAll\": \"5\",\n" +
            "        \"-xmldata\": \"93#20#66#86#156#127#90#74#95#101#89#89#104#81#80#89\",\n" +
            "        \"-xmlAllRunTime\": \"1500\",\n" +
            "        \"-xmlFrameIndex\": \"5\"\n" +
            "      }\n" +
            "    ]\n" +
            "   \n" +
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

    /**
     * 基础动作图片ID
     */
    private static int[] basicIconID = new int[]{
            R.drawable.chuzhao, R.drawable.xiayao, R.drawable.dunxia,
            R.drawable.zuotaishou, R.drawable.taiyoushou, R.drawable.jixie1, R.drawable.jixie2,
            R.drawable.baobao, R.drawable.kaixin, R.drawable.jinli};

    /**
     * 高级动作图片ID
     */
    private static int[] advanceIconID = new int[]{R.drawable.xingzou, R.drawable.niuyao,
            R.drawable.dianjiao, R.drawable.cewanyao, R.drawable.shoubi,R.drawable.woshou,R.drawable.wudao1,
            R.drawable.wudao2, R.drawable.xiemu, R.drawable.zaijian};

    private static String[] baseActionJson = new String[]{WARRIOR, STOOP, SQUAT, LEFTHAND, RIGHTHAND, MECH_DANCE1
            , MECH_DANCE2, HUG, HAPPY, SALUTE};

    private static String[] highActionJson = new String[]{WALK, TWIST, STEPPIN, BENT, ARM, WOSHOU,DANCE1,  DANCE2, CURTAIN, BYE};


    private static String[] songs = {"", "flexin", "jingle bells", "london bridge is falling down",
            "twinkle twinkle little star", "yankee doodle dandy", "kind of light", "so good",
            "Sun Indie Pop", "The little robot", "zombie"};

    /**
     * 解析动作json
     *
     * @param json
     */
    private static List<ActionDataModel> praseActionData(String json) {
        List<ActionDataModel> list = new ArrayList<>();
        try {
            JSONObject zuoJsonObject = new JSONObject(json);
            JSONArray zuoJsonArray = zuoJsonObject.getJSONArray("frame");
            for (int i = 0; i < zuoJsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) zuoJsonArray.get(i);
                UbtLog.d("initActionData", "jsonObject:" + jsonObject.toString());
                ActionDataModel actionDataModel = new ActionDataModel();
                actionDataModel.setXmlRunTime(jsonObject.optString("-xmlRunTime"));
                actionDataModel.setXmldata(jsonObject.optString("-xmldata"));
                list.add(actionDataModel);
            }
            UbtLog.d("initActionData", "list:" + list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取基本动作名称列表
     *
     * @param context
     * @return
     */
    private static String[] getBasicName(Context context) {
        String[] basicAction = new String[]{ResourceManager.getInstance(context).getStringResources("ui_basic_action_warrior"),
                ResourceManager.getInstance(context).getStringResources("ui_basic_action_stoop"),
                ResourceManager.getInstance(context).getStringResources("ui_basic_action_squat"),
                ResourceManager.getInstance(context).getStringResources("ui_basic_action_left_hand"),
                ResourceManager.getInstance(context).getStringResources("ui_basic_action_right_hand"),
                ResourceManager.getInstance(context).getStringResources("ui_basic_action_mech_dance1"),
                ResourceManager.getInstance(context).getStringResources("ui_basic_action_mech_dance2"),
                ResourceManager.getInstance(context).getStringResources("ui_basic_action_hug"),
                ResourceManager.getInstance(context).getStringResources("ui_basic_action_happy"),
                ResourceManager.getInstance(context).getStringResources("ui_basic_action_salute")};
        return basicAction;
    }

    /**
     * 获取高难度动作名称列表
     *
     * @param context
     * @return
     */
    private static String[] getHighActionName(Context context) {
        String[] highActionName = new String[]{ResourceManager.getInstance(context).getStringResources("ui_advance_action_walk"),
                ResourceManager.getInstance(context).getStringResources("ui_advance_action_twist"),
                ResourceManager.getInstance(context).getStringResources("ui_advance_action_steppin"),
                ResourceManager.getInstance(context).getStringResources("ui_advance_action_bent"),
                ResourceManager.getInstance(context).getStringResources("ui_advance_action_arm"),
                ResourceManager.getInstance(context).getStringResources("ui_advance_action_woshou"),
                ResourceManager.getInstance(context).getStringResources("ui_advance_action_dance1"),
                ResourceManager.getInstance(context).getStringResources("ui_advance_action_dance2"),
                ResourceManager.getInstance(context).getStringResources("ui_advance_action_curtain"),
                ResourceManager.getInstance(context).getStringResources("ui_advance_action_bye")};
        return highActionName;
    }


    /**
     * 获取基础动作列表
     *
     * @param context
     * @return
     */
    public static List<PrepareDataModel> getBasicActionList(Context context) {
        List<PrepareDataModel> list = new ArrayList<>();
        String[] baseActionNames = getBasicName(context);
        for (int i = 0; i < baseActionNames.length; i++) {
            PrepareDataModel prepareDataModel = new PrepareDataModel();
            prepareDataModel.setPrepareName(baseActionNames[i]);
            prepareDataModel.setDrawableId(basicIconID[i]);
            prepareDataModel.setList(praseActionData(baseActionJson[i]));
            list.add(prepareDataModel);
        }
        return list;
    }


    /**
     * 获取高级动作列表
     *
     * @param context
     * @return
     */
    public static List<PrepareDataModel> getHighActionList(Context context) {
        List<PrepareDataModel> list = new ArrayList<>();
        String[] highActionNames = getHighActionName(context);
        for (int i = 0; i < highActionNames.length; i++) {
            PrepareDataModel prepareDataModel = new PrepareDataModel();
            prepareDataModel.setPrepareName(highActionNames[i]);
            prepareDataModel.setDrawableId(advanceIconID[i]);
            prepareDataModel.setList(praseActionData(highActionJson[i]));
            list.add(prepareDataModel);
        }
        return list;
    }


    /**
     * 获取音乐列表
     *
     * @param mContext
     * @return
     */
    public static List<PrepareMusicModel> getMusicList(Context mContext) {
        List<PrepareMusicModel> listSongs = new ArrayList<>();
        for (int i = 0; i < songs.length; i++) {
            PrepareMusicModel prepareMusicModel = new PrepareMusicModel();
            prepareMusicModel.setMusicName(songs[i]);
            prepareMusicModel.setMusicType(0);
            listSongs.add(prepareMusicModel);
        }

        UbtLog.d("getMusicList", "record:" + FileTools.readFiles(FileTools.record).toString());
        List<String> listRecord = FileTools.readFiles(FileTools.record);
        for (int i = 0; i < listRecord.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            String name = listRecord.get(i);
            name = name.substring(0, name.length() - 4);
            UbtLog.d("getMusicList", "record name:" + name);

            PrepareMusicModel prepareMusicModel = new PrepareMusicModel();
            prepareMusicModel.setMusicName(name);
            prepareMusicModel.setMusicType(1);
            listSongs.add(prepareMusicModel);
        }
        return listSongs;
    }


    /**
     * 根据密度获取Params
     *
     * @param density
     * @param imageView
     * @return
     */
    public static ViewGroup.LayoutParams getIvRobotParams(float density, ImageView imageView) {
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        int n = 2;
        if (density > 2 && density < 3) {
            n = 3;
        } else if (density == 3.0) {
            n = 3;
        } else if (density == 4.0) {
            n = 4;
        } else if (density == 5.0) {
            n = 5;
        }
        UbtLog.d("getIvRobotParams", "width:" + params.width + "--height:" + params.height);
        params.width = params.width / 2 * n;
        params.height = params.height / 2 * n;
        return params;
    }

}
