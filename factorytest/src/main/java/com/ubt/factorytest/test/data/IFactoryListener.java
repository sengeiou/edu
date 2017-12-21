package com.ubt.factorytest.test.data;

import com.ubt.factorytest.bluetooth.ubtbtprotocol.ProtocolPacket;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2017/12/14 19:07
 * @描述:
 */

public interface IFactoryListener {
    void onProtocolPacket(ProtocolPacket packet);
}
