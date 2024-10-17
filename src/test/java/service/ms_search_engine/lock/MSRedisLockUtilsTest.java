package service.ms_search_engine.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import service.ms_search_engine.constant.StatusCode;
import service.ms_search_engine.exception.MsApiException;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MSRedisLockUtilsTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @InjectMocks
    private MSRedisLockUtils msRedisLockUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("測試正常取得 lock on Redis")
    void testRedisLock1() throws InterruptedException {
        String randomLockKey = String.valueOf(UUID.randomUUID());

        // 模擬 redissonClient 的行為
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), eq(TimeUnit.MICROSECONDS))).thenReturn(true);

        Lock lock = msRedisLockUtils.getLock(MSRedisLockUtils.MSLockGroup.SUMMIT_TASK_BY_TASK_ID, randomLockKey);

        // 檢查是否可以成功取得鎖
        boolean isLocked = msRedisLockUtils.tryLock(lock, 100);
        assertTrue(isLocked, "Lock should be acquired");

        // 確認 tryLock 是否被調用
        verify(rLock, times(1)).tryLock(100, TimeUnit.MICROSECONDS);

        // 模擬解鎖並驗證 unlock 調用
        msRedisLockUtils.unlock(lock);
        verify(rLock, times(1)).unlock();
    }

    @Test
    @DisplayName("測試同時取得 lock, 應該阻擋")
    void testRedisLock2() throws InterruptedException {
        String randomLockKey = String.valueOf(UUID.randomUUID());

        when(redissonClient.getLock(anyString())).thenReturn(rLock);

        // 第一次 tryLock 返回 true，表示鎖成功
        // 第二次 tryLock 返回 false，表示鎖被佔用
        when(rLock.tryLock(anyLong(), eq(TimeUnit.MICROSECONDS)))
                .thenReturn(true)   // 第一次嘗試鎖成功
                .thenReturn(false); // 第二次嘗試鎖失敗，因為鎖已被占用

        Lock lock1 = msRedisLockUtils.getLock(MSRedisLockUtils.MSLockGroup.SUMMIT_TASK_BY_TASK_ID, randomLockKey);
        Lock lock2 = msRedisLockUtils.getLock(MSRedisLockUtils.MSLockGroup.SUMMIT_TASK_BY_TASK_ID, randomLockKey);

        // 第一次鎖定
        boolean isLocked1 = msRedisLockUtils.tryLock(lock1, 100);
        assertTrue(isLocked1, "Lock should be acquired for lock1");

        // 第二次鎖定，預期應該失敗
        boolean isLocked2 = msRedisLockUtils.tryLock(lock2, 100);
        assertFalse(isLocked2, "Lock should not be available for lock2");

        // 驗證 tryLock 只調用了一次成功，第二次失敗
        verify(rLock, times(2)).tryLock(100, TimeUnit.MICROSECONDS);

        // 解鎖 lock1
        msRedisLockUtils.unlock(lock1);
        verify(rLock, times(1)).unlock();

        // 嘗試重新取得 lock2，預期應該成功
        when(rLock.tryLock(anyLong(), eq(TimeUnit.MICROSECONDS))).thenReturn(true);
        boolean isLockedRe = msRedisLockUtils.tryLock(lock2, 100);
        assertTrue(isLockedRe, "Lock should be acquired after unlocking lock1");

        // 驗證 unlock 和重新鎖定的行為
        verify(rLock, times(1)).unlock();
        verify(rLock, times(3)).tryLock(100, TimeUnit.MICROSECONDS);
    }

    @Test
    @DisplayName("MultiLock 相同 lockName 應拋出 MsApiException")
    void getMultiLock_sameLockName_shouldThrowException() throws MsApiException {
        // 模擬兩個具有相同 lockName 的 RLock
        RLock rLock1 = mock(RLock.class);
        RLock rLock2 = mock(RLock.class);

        // 設置兩個 RLock 的名稱相同
        when(rLock1.getName()).thenReturn("REDIS_LOCK_SUMMIT_TASK_BY_TASK_ID_key123");
        when(rLock2.getName()).thenReturn("REDIS_LOCK_SUMMIT_TASK_BY_TASK_ID_key123");

        // 嘗試調用 getMultiLock() 應該拋出 MsApiException
        MsApiException exception = assertThrows(MsApiException.class, () -> {
            msRedisLockUtils.getMultiLock(rLock1, rLock2);
        });

        // 驗證異常信息是否正確
        assertEquals(StatusCode.Base.BASE_LOCK_ERROR, exception.getStatus());
    }


    @Test
    @DisplayName("MultiLock 不同 lockName 正常工作")
    void getMultiLock_differentLockName_shouldWork() throws InterruptedException, MsApiException {
        // 模擬兩個具有不同 lockName 的 RLock
        RLock rLock1 = mock(RLock.class);
        RLock rLock2 = mock(RLock.class);

        when(rLock1.getName()).thenReturn("REDIS_LOCK_SUMMIT_TASK_BY_TASK_ID_key123");
        when(rLock2.getName()).thenReturn("REDIS_LOCK_SUMMIT_TASK_BY_TASK_ID_key456");

        RedissonMultiLock multiLock = mock(RedissonMultiLock.class);

        // 設置 redissonClient 返回 multiLock
        when(redissonClient.getMultiLock(rLock1, rLock2)).thenReturn(multiLock);

        // 模擬 tryLock 行為
        when(multiLock.tryLock(anyLong(), eq(TimeUnit.MICROSECONDS))).thenReturn(true);

        // 調用 getMultiLock()，應該不會拋出異常
        RedissonMultiLock resultMultiLock = msRedisLockUtils.getMultiLock(rLock1, rLock2);

        assertNotNull(resultMultiLock, "MultiLock should not be null");

        // 確認 tryLock 可以成功鎖定
        boolean isLocked = msRedisLockUtils.tryLock(resultMultiLock, 100);
        assertTrue(isLocked, "MultiLock should be successfully locked");

        // 解鎖
        msRedisLockUtils.unlock(resultMultiLock);
        verify(multiLock, times(1)).unlock();
    }
}
