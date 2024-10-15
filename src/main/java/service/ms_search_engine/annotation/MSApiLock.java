package service.ms_search_engine.annotation;


import service.ms_search_engine.data.BaseRequest;
import service.ms_search_engine.lock.MSRedisLockUtils.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
     * 多個 key 組合 一個 lock
     * ex: {“taskId”}, taskId=100, lock name 變成 taskId_100
     * 如果數值＝null 不帶上該 key
     * </p>
     */
    String[] paramNames() default {};

    /**
     * <p>
     * 指定 lock 特定 requestBody field value 作為 lockName，給多個 field name 會串接處理，串接順序由 array 的順序
     * 多個 key 組合 一個 lock
     * ex: {“taskId”}, taskId=100, lock name 變成 taskId_100
     * req 階層是
     * {
     *     "taskId": 100
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
     * 如果需要指定 reqBodyNames, 此欄位必須帶上，用於 reflection 使用
     */
    Class<?> reqBodyClass() default BaseRequest.class;

    /**
     * <p>
     * tryLockTime, 預設 0 ms;
     * </p>
     */
    long tryLockTime() default 0;

}
