package com.ubt.alpha1e_edu.blockly;

/**
 * @className RobotEmojiEnum
 *
 * @author wmma
 * @description 机器人表情枚举
 * @date 2017/03/09
 * @update
 *
 *  表情列表
 *  大哭   害羞   抓狂   冷酷    飞吻    你真棒
 *  装酷   嫌弃   生气   拜托    宝宝    撒娇
 *  卖萌   难受   求原谅    开心    惊讶   困惑  奸笑
 *  糗大了   撒花
 *
 */


public enum RobotEmojiEnum {

    CRY("cry", 0), SHY("shy",1), CRAZY("crazy", 2),
    COLD("cold", 3), KISS("kiss", 4), AWESOME("awesome", 5),
    COOL("cool", 6), DESPISE("despise", 7), ANGRY("angry", 8),
    PLEASE("please", 9), BABY("baby", 10), SPOILED("Spoiled", 11),
    CUTE("cute", 12), UNCOMFORTABLE("uncomfortable", 13), FORGIVE("forgive", 14),
    HAPPY("happy", 15), SURPRISED("Surprised", 16), CONFUSED("confused", 17),
    SINISTER_SMILE("SINISTER_SMILE", 18), EMBARASS("embarass", 19),SAHUAN("Sahuan", 20);


    private String value;
    private int ID;
    RobotEmojiEnum(String value, int ID){
        this.value = value;
        this.ID = ID;
    }

    public String getActionName() {
        return this.value;
    }

    public int getEmojiID() {
        return this.ID;
    }



}
