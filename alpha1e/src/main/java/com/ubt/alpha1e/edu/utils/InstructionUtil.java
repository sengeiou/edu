package com.ubt.alpha1e.edu.utils;

import android.content.Context;

import com.ubt.alpha1e.edu.AlphaApplication;
import com.ubt.alpha1e.edu.data.model.InstructionInfo;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 类名
 * @author lihai
 * @description 解析内置指令类。
 * @date 2017.04.11
 *
 */
public class InstructionUtil {

    private static final String TAG = "InstructionUtil";

    //获取全部指令类型
    public static final int INSTRUCTION_TYPE_ALL = -1;

    //定义存储全部列表对象
    private static List<InstructionInfo> mAllInstructionInfoList = new ArrayList<>();

    /**
     * 初始化指令方法
     * @param context 上下文
     */
    public static void initInsertInstruction(Context context){
        try {
            InputStream is = context.getAssets().open("instruction/instructions.xml");
            InstructionUtil parser = new InstructionUtil();
            mAllInstructionInfoList = parser.parse(is);
            UbtLog.d(TAG,"-初始化指令成功-");
        } catch (Exception e) {
            UbtLog.e(TAG, e.getMessage());
        }
    }

    /**
     * 解析指令XML
     * @param is
     * @return
     * @throws Exception
     */
    private static List<InstructionInfo> parse(InputStream is) throws Exception {
        List<InstructionInfo> instructions = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  //取得DocumentBuilderFactory实例
        DocumentBuilder builder = factory.newDocumentBuilder(); //从factory获取DocumentBuilder实例
        Document doc = builder.parse(is);   //解析输入流 得到Document实例
        Element rootElement = doc.getDocumentElement();
        NodeList items = rootElement.getElementsByTagName("instruction");
        //UbtLog.d(TAG , " items = " + items.getLength());
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            NodeList properties = item.getChildNodes();

            NamedNodeMap namedNodeMap = item.getAttributes();
            String type = "";
            String typeDes = "";
            String typeSub = "";
            String typeSubDes = "";
            for(int n = 0; n < namedNodeMap.getLength(); n++){
                String nodeName = namedNodeMap.item(n).getNodeName();
                String nodeValue = namedNodeMap.item(n).getNodeValue();
                //UbtLog.d(TAG,"nName = " + nodeName );
                if(nodeName.equals("type")){
                    type = nodeValue;
                }else if(nodeName.equals("typeDes")){
                    typeDes = nodeValue;
                }else if(nodeName.equals("typeSub")){
                    typeSub = nodeValue;
                }else if(nodeName.equals("typeSubDes")){
                    typeSubDes = nodeValue;
                }
            }
            for (int j = 0; j < properties.getLength(); j++) {
                Node property = properties.item(j);
                String nodeName = property.getNodeName();
                //UbtLog.d(TAG,"nodeName = " + nodeName );
                if(nodeName.equals("name")){
                    String name = property.getFirstChild().getNodeValue();

                    InstructionInfo instructionInfo = new InstructionInfo();
                    instructionInfo.type = type;
                    instructionInfo.typeDes = typeDes;
                    instructionInfo.typeSub = typeSub;
                    instructionInfo.typeSubDes = typeSubDes;
                    instructionInfo.name = name;
                    instructions.add(instructionInfo);

                    //UbtLog.d(TAG,"nodeValue = " + name);
                }
            }
        }
        UbtLog.d(TAG , " instructions = " + instructions.size());
        return instructions;
    }

    /**
     * 根据指令类型获取对应指令
     * @param type 指令类型
     * @return 返回列表
     */
    public static List<InstructionInfo> getInstructionInfoListByType(String type,String typeSub){
        List<InstructionInfo> mList = new ArrayList<>();
        if(mAllInstructionInfoList.isEmpty()){
            initInsertInstruction(AlphaApplication.getBaseActivity());
        }

        for(InstructionInfo instructionInfo : mAllInstructionInfoList){
            if(instructionInfo.type.equals(type)){
                //UbtLog.d(TAG,"typeSub = " + typeSub.equals(INSTRUCTION_TYPE_ALL) + "    instructionInfo = " + instructionInfo.selected);
                if(typeSub.equals(INSTRUCTION_TYPE_ALL + "")){
                    mList.add(instructionInfo);
                    continue;
                }

                if(!instructionInfo.typeSub.equals(typeSub)){
                    continue;
                }
                mList.add(instructionInfo);
            }
        }

        //默认第一个选中
        for(int i = 0 ; i < mList.size(); i++){
            if(i == 0){
                mList.get(i).selected = true;
            }else {
                mList.get(i).selected = false;
            }
        }
        return mList;
    }


}