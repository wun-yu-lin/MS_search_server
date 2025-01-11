package service.ms_search_engine.annotation;


import service.ms_search_engine.data.BaseRequestData;
import service.ms_search_engine.lock.MSRedisLockUtils.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用於 Redis 分散式鎖使用
 * 可用於 api 開頭進行 redis lock 的保護，避免 concurrent api
 * 實作部分由 MSApiLockAop 處理
 * 務必注意：請避免使用 primitive type, 避免 value = null, 產生預設值問題
 *  ex: int id , 應該使用 Integer id
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MSApiLock {

    /**
     * <p>
     * 必填，用於分類 Lock 的種類
     * </p>
     */
    MSLockGroup msLockGroup();

    /**
     * <p>
     * 指定 lock 特定 requestParam value 作為 lockName，給多個 param name 會串接處理，串接順序由 array 的順序
     * 多個 key 組合 一個 lockName
     * ex: {“taskId”}, taskId=100, lock name 變成 taskId_100
     * 如果數值不能是 null
     * </p>
     */
    String[] paramNames() default {};

    /**
     * <p>
     * 指定 lock 特定 requestBody field value 作為 lockName，給多個 field name 會串接處理，串接順序由 array 的順序
     * 多個 key 組合 一個 lockName
     * 如果數值不能是 null
     * ex: {“taskId”, "name"}, taskId=100, name=ABC lock name 變成 taskId_100_name_ABC
     * req 階層是
     * {
     *     "taskId": 100,
     *     "name": ABC
     * }
     *
     * ex: {“data.taskId”}, data.taskId=100, lock name 變成 data.taskId_100,
     * req 階層是
     * {
     *    "data": {
     *        "taskId": 100
     *    }
     * }
     * </p>
     */
    String[] reqBodyNames() default {};

    /**
     * 如果需要指定 reqBodyNames, 此欄位必須帶上，用於 reflection 使用, reqBodyClass 務必繼承 service.ms_search_engine.data.BaseRequest
     */
    Class<?> reqBodyClass() default BaseRequestData.class;

    /**
     * <p>
     * tryLockTime, 預設 0 ms;
     * </p>
     */
    long tryLockTime() default 0;

}
