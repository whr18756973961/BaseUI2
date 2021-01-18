package com.whr.baseui.baserv;

import java.util.List;

public interface IListDataOperation<T> {
    /**
     * 向列表中添加数据
     *
     * @param data
     */
    void addData(List<T> data);

    /**
     * 添加一条数据
     *
     * @param pos
     * @param data
     */
    void addData(int pos, T data);

    /**
     * 在末尾添加一条数据
     *
     * @param data
     */
    void addData(T data);

    /**
     * 改变某一条数据
     *
     * @param bean
     * @param index
     */
    void changeData(T bean, int index);

    /**
     * 移除某一条数据
     *
     * @param index
     */
    void removeData(int index);

    /**
     * 获取列表中所有的数据
     *
     * @return
     */
    List<T> getData();

    /**
     * 重置列表数据
     *
     * @param data
     */
    void setData(List<T> data);

    /**
     * 在某位置上批添加数据
     *
     * @param data
     */
    void addAllData(int index, List<T> data);
}
