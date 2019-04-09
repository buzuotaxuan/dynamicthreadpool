import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: Devy
 * @create: 2019-04-08 10:56
 **/
public class DynamicThreadPool {

    static {
//        System.setProperty("archaius.configurationSource.defaultFileName", "config.properties");
        System.setProperty("archaius.fixedDelayPollingScheduler.delayMills", "2000");
        System.setProperty("archaius.fixedDelayPollingScheduler.initialDelayMills", "2000");
    }

    private DynamicIntProperty coreSize = DynamicPropertyFactory.getInstance().getIntProperty(Constants.THREADPOOL_CODE_SIZE, 200);
    private DynamicIntProperty maximumSize = DynamicPropertyFactory.getInstance().getIntProperty(Constants.THREADPOOL_MAX_SIZE, 2000);
    private DynamicLongProperty aliveTime = DynamicPropertyFactory.getInstance().getLongProperty(Constants.THREADPOOL_ALIVE_TIME, 1000 * 60 * 5);

    public int getCoreSize() {
        return coreSize.get();
    }

    public int getMaximumSize() {
        return maximumSize.get();
    }

    public long getAliveTime() {
        return aliveTime.get();
    }

    private AtomicReference<ThreadPoolExecutor> poolExecutorRef = new AtomicReference<ThreadPoolExecutor>();
    private AtomicLong rejectedRequests = new AtomicLong(0);


    public  ThreadPoolExecutor getTpe(){
        return poolExecutorRef.get();
    }

    public void init()  {
        reNewThreadPool();
        Runnable c = new Runnable() {
            @Override
            public void run() {
                ThreadPoolExecutor p = poolExecutorRef.get();
                p.setCorePoolSize(coreSize.get());
                p.setMaximumPoolSize(maximumSize.get());
                p.setKeepAliveTime(aliveTime.get(),TimeUnit.MILLISECONDS);
                System.out.println("coreSize:"+coreSize);
                System.out.println("maximumSize:"+maximumSize);
                System.out.println("aliveTime:"+aliveTime);
            }
        };

        coreSize.addCallback(c);
        maximumSize.addCallback(c);
        aliveTime.addCallback(c);

        System.out.println("coreSize:"+coreSize);
        System.out.println("maximumSize:"+maximumSize);
        System.out.println("aliveTime:"+aliveTime);
        // TODO metrics reporting
    }

    private void reNewThreadPool() {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(coreSize.get(), maximumSize.get(), aliveTime.get(), TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
        ThreadPoolExecutor old = poolExecutorRef.getAndSet(poolExecutor);
        if (old != null) {
            System.out.println("i old ----");
            shutdownPoolExecutor(old);
        }
    }

    private void shutdownPoolExecutor(ThreadPoolExecutor old) {
        try {
            System.out.println("shutdown.......");
            old.shutdown();
            old.awaitTermination(1, TimeUnit.MINUTES);
            System.out.println("shutdown finsh");
        } catch (InterruptedException e) {
            e.printStackTrace();
            old.shutdownNow();
        }
    }

    public void subumit(Runnable runner){
        poolExecutorRef.get().submit(runner);
    }
}
