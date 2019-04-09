import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: Devy
 * @email: xinghui.jia@luckincoffee.com
 * @create: 2019-04-08 10:53
 **/
public class DynamicThreadPoolTest
{
    @Test
    public void testDynamic() throws InterruptedException {
        DynamicThreadPool dtp=new DynamicThreadPool();
        dtp.init();

        ThreadPoolExecutor tp=dtp.getTpe();
        tp.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000l);
                    System.out.println("----i m finnish------"+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        for(int i=0; i<10000; i++){
            Thread.sleep(3000l);
            dtp.subumit(new Runnable() {
                @Override
                public void run() {
                        try {
                            Thread.sleep(3000l);
                            System.out.println("----------"+Thread.currentThread().getName());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
            });
            ThreadPoolExecutor tp2=dtp.getTpe();
            System.out.println("getCorePoolSize: " + tp.getCorePoolSize()+"is alive---"+tp.isShutdown());
            System.out.println("getCorePoolSize2: " + tp2.getCorePoolSize()+"is alive---"+tp2.isShutdown());
        }

        Thread.sleep(1000000000l);
    }
}
