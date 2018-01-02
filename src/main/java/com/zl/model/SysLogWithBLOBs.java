package com.zl.model;

/**
 * 此类是因为sys_log表的  oldValue 和 newValue 字段都是text类型
 * 这样使用的优点是：当我不需要去text类型的数据的时候，直接操作 SysLog对象就可以。节省性能开销
 *
 * @author jacky
 * @date 2017/10/26
 */
public class SysLogWithBLOBs extends SysLog {

    private String oldValue;

    private String newValue;

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue == null ? null : oldValue.trim();
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue == null ? null : newValue.trim();
    }
}