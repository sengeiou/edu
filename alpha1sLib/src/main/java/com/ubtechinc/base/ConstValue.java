package com.ubtechinc.base;

/**
 * 常量定义
 * 
 * @author chenlin
 * @update lihai  (0xB7 0xB5 0x35 add for 1P+ on 2016/10/10)
 * @update lihai  (0x36 above add for 1E on 2017/01/14)
 */
public class ConstValue {
	/**
	 * 握手
	 */
	public static final byte DV_HANDSHAKE = 0x01;
	/**
	 * 获取动作表名
	 */
	public static final byte DV_GETACTIONFILE = 0x02;
	/**
	 * 执行动作表
	 */
	public static final byte DV_PLAYACTION = 0x03;
	/**
	 * 停止播放
	 */
	public static final byte DV_STOPPLAY = 0x05;
	/**
	 * 声音控制：0x06 参数： 00 － 静音 01 - 打开声音
	 */
	public static final byte DV_VOICE = 0x06;
	/**
	 * 播放控制：0x07 参数：00 － 暂停 01 － 继续
	 */
	public static final byte DV_PAUSE = 0x07;

	/**
	 * 心跳
	 */
	public static final byte DV_XT = 0x08;

	/**
	 * 修改设备名 参数：新的设备名
	 */
	public static final byte DV_MODIFYNAME = 0x09;
	/**
	 * 读取状态：0x0a 下位机返回：声音状态(00+声音状态(00 静音 01有声音)) 播放状态(01+(播放状态00 暂停 01非暂停))
	 * 音量状态(02+1B(255)低字节在前) 灯状态：（03+00:关， 01开） SD卡状态：04 1-OK 0-NO
	 */
	public static final byte DV_READSTATUS = 0x0a;

	/**
	 * 调整音量 参数：(0~255)
	 */
	public static final byte DV_VOLUME = 0x0b;

	/**
	 * 掉电
	 */
	public static final byte DV_DIAODIAN = 0x0c;

	/**
	 * 灯控制 参数：0-关 1 开
	 */
	public static final byte DV_LIGHT = 0x0d;

	/** 时间校准 **/
	public static final byte DV_ADJUST_TIME = 0x0e;
	/** 读取闹铃时间 **/
	public static final byte DV_READ_ALARM = 0x0f;
	/** 设置闹铃时间 **/
	public static final byte DV_WRITE_ALARM = 0x10;
	/** 读版软件版本号 **/
	public static final byte DV_READ_SOFTWARE_VERSION = 0x11;
	/** 删除动作表 **/
	public static final byte DV_DELETE_FILE = 0x12;
	/** 修改文件名 **/
	public static final byte DV_MODIFY_FILENAME = 0x13;
	/** 传输开始 **/
	public static final byte DV_FILE_UPLOAD_START = 0x14;
	/** 传输中 **/
	public static final byte DV_FILE_UPLOADING = 0x15;
	/** 传输结束 **/
	public static final byte DV_FILE_UPLOAD_END = 0x16;
	/** 传输取消 **/
	public static final byte DV_FILE_UPLOAD_CANCEL = 0x17;
	/** 读电量 **/
	public static final byte DV_READ_BATTERY = 0x18;
	/** 低电量 **/
	public static final byte DV_READ_LOWBATTERY = 0x19;
	/** 读硬件版本号 **/
	public static final byte DV_READ_HARDWARE_VERSION = 0x20;
	/** 读版蓝牙版本号 **/
	public static final byte DV_READ_BLUETOOTH_VERSION = (byte) 0x9A;
	/** 蓝牙升级 **/
	public static final byte DV_BLUETOOTH_UPGRADE = 0x1A;

	/** 蓝牙升级 **/
	public static final byte DV_BLUETOOTH_UPGRADE_PERCENT = 0x2A;

	/**
	 * 设置default命令
	 */
	public static final byte DV_SET_ACTION_DEFAULT = 0X21;
	/**
	 * 传动作表
	 */
	public static final byte UV_GETACTIONFILE = (byte) 0X80;
	public static final byte UV_STOPACTIONFILE = (byte) 0x81;
	/**
	 * 动作结束主动上报
	 */
	public static final byte DV_ACTION_FINISH = (byte) 0x31;

	/**
	 * 控制16个舵机
	 */
	public static final byte CTRL_ALL_ENGINE = (byte) 0x23;

	/**
	 * 单个舵机掉电
	 */

	public static final byte CTRL_ONE_ENGINE = (byte) 0x24;


	/**
	 * 读写SN命令。参数<P1><P2>。 P1 == 0，表示读SN，无P2参数；P1 ==
	 * 1，表示写SN，P2为写入的设备SN，不定长度最大16字节字符串。 设备应答：<P1><P2>。参数P1 ==
	 * 0，表示读SN，P2为读取的设备SN，不定长度最大16字节字符串； P1 ==
	 * 1，表示写SN，P2为写入状态，P2==0成功，P2==1失败。如果下发的SN和设备端一样，设备端不会进行写操作，但会反回成功标志。
	 */

	public static final byte READ_SN_CODE = (byte) 0x33;

	/**
	 * 读UDID(Unique device ID register)命令。无参。 设备应答：<P1>。参数 P1==设备MCU UDID，
	 * 不定长度，最大16字节，16进制。
	 */
	public static final byte READ_UID_CODE = (byte) 0x34;

	/**
	 * 设置边充边玩状态。参数<P1>。P1==1，表示允许边充边玩；P1==0，表示禁止边充边玩。
	 * 当主机发送动作执行命令且机器人处于禁止边充边玩状态时有回复，参数是0。
	 */
	public static final byte SET_PALYING_CHARGING = (byte) 0x32;

	/**
	 * 读取16个舵机角度 回复：1个参数，长度16B（对应1-16号舵机的角度）。 单个舵机角度含义：FF，舵机没应答；FE，舵机ID不对
	 */
	public static final byte READ_ALL_ENGINE = (byte) 0x25;

	/**
	 * 用FBCF协议头读B7看舵机个数和列表,以便后面操作舵机,表示支持UTF8,不回复B7表示为GBK旧版,不支持多舵机控制,并且主机只能使用FBBF协议头下发.
	 */
	public static final byte DV_HANDSHAKE_B_SEVEN = (byte) 0xB7;

    /**
     * 读取音源状态.
     */
    public static final byte DV_READ_AUDIO_SOURCE_STATE = (byte) 0xB5;

    /**
     * 切换音源.
     */
    public static final byte DV_SET_AUDIO_SOURCE = (byte) 0x35;


	/** ===================Alpha 1E start==================== **/

	/**
	 * 获取WIFI列表
	 */
	public static final byte DV_FIND_WIFI_LIST = (byte) 0x36;

	/**
	 * 网络连接
	 * 返回参数：0 未连接 1 连接中 2 连接成功 3 连接失败
	 */
	public static final byte DV_DO_NETWORK_CONNECT = (byte) 0x39;

	/**
	 * 获取联网状态
	 * status 连接状态 true 已连接  false 未联网
	 * name 连接名称 当status = true 时， name=当前联网名称 ip=联网时IP 当status = false时  name等于空 ip等于null
	 * 返回 {"status":"true","name":"","ip":""}
	 */
	public static final byte DV_READ_NETWORK_STATUS = (byte) 0x40;

	/**
	 * 读取自动升级状态.
	 * 0 为未开启， 1为已开启
	 */
	public static final byte DV_READ_AUTO_UPGRADE_STATE = (byte) 0x41;

	/**
	 * 切换自动升级状态.
	 * 0 为未开启， 1为已开启 , 2 设置中
	 */
	public static final byte DV_SET_AUTO_UPGRADE = (byte) 0x42;

	/**
	 * 升级本体软件.
	 */
	public static final byte DV_DO_UPGRADE_SOFT = (byte) 0x43;

	/**
	 * 升级本体软件.
	 * status : 1 下载中 2 下载成功 0 下载失败
	 * progress : 下载进度
	 * totalSize : 文件总大小
	 * {"status":"1","progress":"20","totalSize":"16M"}
	 */
	public static final byte DV_DO_UPGRADE_PROGRESS = (byte) 0x44;

	/**
	 * 是否进入安装软件（1 电量充足进入安装，0 电量不足，不进入安装）.
	 */
	public static final byte DV_DO_UPGRADE_STATUS = (byte) 0x45;

	/**
	 * Alpha 1E 下载动作.
	 *
	 * 发送：{"actionId":"1","actionName":"name","actionPath":"http://"}
	 * actionId : 动作ID
	 * actionName : 动作名称
	 * actionPath : 动作下载URL
	 *
	 * 回复：{"status":"1","progress":"20","actionId":"1"}
	 * status : 1 下载中 2 下载成功 3 未联网 4 解压失败 0 下载失败
	 * progress : 下载进度
	 * actionId : 动作ID
	 */
	public static final byte DV_DO_DOWNLOAD_ACTION = (byte) 0x46;

	/**
	 * 判断动作文件是否存在
	 * 发送参数：文件名
	 * 回复 0 不存在 1 存在
	 */
	public static final byte DV_DO_CHECK_ACTION_FILE_EXIST = (byte) 0x47;

	/**
	 * 读取红外传感器与障碍物的距离
	 * 参数 01 开启上报， 00 停止上报
	 */
	public static final byte DV_READ_INFRARED_DISTANCE = (byte) 0x50;

	/**
	 * 检查机器人是否听到固定语音状态
	 */
	public static final byte DV_CHECK_SPEECH_STATE = (byte) 0x51;

	/**
	 * 读取机器人的姿态角度（前倾角/后倾角/左倾角/右倾角 角度）
	 */
	public static final byte DV_READ_ROBOT_GYROSCOPE_DATA = (byte) 0x52;

	/**
	 * 读取机器人跌倒状态
	 */
	public static final byte DV_READ_ROBOT_FALL_DOWN = (byte) 0x53;

	/**
	 * 读取机器人加速度, 0x01开启上报， 0x00关闭上报
	 */
	public static final byte DV_READ_ROBOT_ACCELERATION = (byte) 0x54;


	/**
	 * 设置播放音效
	 */
	public static final byte DV_SET_PLAY_SOUND = (byte) 0x60;

	/**
	 * 设置led灯效
	 */
	public static final byte DV_SET_LED_LIGHT = (byte) 0x61;

	/**
	 * 设置播放语音
	 */
	public static final byte DV_SET_PLAY_SPEECH = (byte) 0x62;

	/**
	 * 设置播放表情
	 */
	public static final byte DV_SET_PLAY_EMOJI = (byte) 0x63;

	/**
	 *设置停止播放音效
	 */
	public static final byte DV_SET_STOP_VOICE = (byte) 0x64;

	/**
	 * 停止播放表情
	 */
	public static final byte DV_SET_STOP_EMOJI = (byte) 0x69;

	/**
	 * 停止眼睛灯光
	 */

	public static final byte DV_SET_STOP_LED_LIGHT = (byte) 0x65;

	/**
	 * Alpha1E 正在播放，参数：当前播放的动作名
	 */
	public static final byte DV_CURRENT_PLAY_NAME = (byte) 0x66;

	/**
	 * 请求wifi传输port
	 */
	public static final byte REQUEST_WIFI_PORT = (byte) 0x6A;


	/**
	 * 1E机器人拍头打断事件，该事件是机器人主动上报
	 */
	public static final byte DV_TAP_HEAD = (byte) 0x70;

	/**
	 * 1E机器人跌倒事件，该事件是机器人主动上报
	 */
	public static final byte DV_FALL_DOWN = (byte) 0x71;


	/**
	 * 1E机器人获取产品productID和DSN
	 */
	public static final byte DV_PRODUCT_AND_DSN = (byte) 0x72;


	/**
	 * 1E机器人发送CLIENT ID
	 */
	public static final byte DV_CLIENT_ID = (byte) 0x73;

}
