package com.regmode.Utils;

/**
 * Author:wang_sir
 * Time:2019/12/24 21:07
 * Description:This is BaseReg
 */
public abstract class BaseReg {
    /**
     * 调用减次接口
     *
     * @param size 需要减的次数
     */
    public abstract void setRegistCodeNumber(int size);

    /**
     * 检测注册码的状态
     */
    public abstract void checkRegStatus();


}
