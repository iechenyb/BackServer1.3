package com.cyb.mulitthread;

import java.util.concurrent.CountDownLatch;

/**
 * ������
 * @author ���
 *
 */
class Worker {
    private String name;        // ����
    private long workDuration;  // ��������ʱ��

    /**
     * ������
     */
    public Worker(String name, long workDuration) {
        this.name = name;
        this.workDuration = workDuration;
    }

    /**
     * ��ɹ���
     */
    public void doWork() {
        System.out.println(name + " begins to work...");
        try {
            Thread.sleep(workDuration); // ������ģ�⹤��ִ�е�ʱ��
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println(name + " has finished the job...");
    }
}

/**
 * �����߳�
 * @author ���
 *
 */
class WorkerTestThread implements Runnable {
    private Worker worker;
    private CountDownLatch cdLatch;

    public WorkerTestThread(Worker worker, CountDownLatch cdLatch) {
        this.worker = worker;
        this.cdLatch = cdLatch;
    }

    public void run() {
        worker.doWork();        // �ù��˿�ʼ����
        cdLatch.countDown();    // ������ɺ󵹼�ʱ������1
    }
}

 public class CountDownLatchTest {

    private static final int MAX_WORK_DURATION = 5000;  // �����ʱ��
    private static final int MIN_WORK_DURATION = 1000;  // ��С����ʱ��

    // ��������Ĺ���ʱ��
    private static long getRandomWorkDuration(long min, long max) {
        return (long) (Math.random() * (max - min) + min);
    }

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(5);   // ��������ʱ�Ų�ָ������ʱ����Ϊ2
        Worker w1 = new Worker("���", getRandomWorkDuration(MIN_WORK_DURATION, MAX_WORK_DURATION));
        Worker w2 = new Worker("����", getRandomWorkDuration(MIN_WORK_DURATION, MAX_WORK_DURATION));

        new Thread(new WorkerTestThread(w1, latch)).start();
        new Thread(new WorkerTestThread(w2, latch)).start();

        try {
            latch.await();  // �ȴ�����ʱ�ż���0
            System.out.println("All jobs have been finished!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}