package com.leo.device.model.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.leo.device.bean.data.Urine;

import java.util.List;

/**
 * @author Leo
 * @date 2019-05-05
 */
@Dao
public interface UrineDao {

    /**
     * 保存
     *
     * @param urine 排尿数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Urine... urine);

    /**
     * 保存
     *
     * @param list 排尿数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<Urine> list);

    /**
     * 查询指定时间和设备的排尿数据
     *
     * @param deviceId 设备号
     * @param minTime  最小时间
     * @param maxTime  最大时间
     * @return 指定时间和设备的排尿数据
     */
    @Query("SELECT * FROM Urine WHERE timestamp BETWEEN :minTime AND :maxTime AND deviceId == :deviceId")
    List<Urine> getByDeviceAndTime(String deviceId, long minTime, long maxTime);

    /**
     * 查询所有排尿数据
     *
     * @return 所有排尿数据
     */
    @Query("SELECT * FROM Urine")
    List<Urine> getAll();
}
