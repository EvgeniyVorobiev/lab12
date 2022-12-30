package org.example;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.HibernateException;
import org.hibernate.PessimisticLockException;
import org.hibernate.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static long exceptionCount = 0;
    public static void main(String[] args) throws InterruptedException {
        long time = System.currentTimeMillis();
        long optimisticTime;
        long pessimisticTime;

        optimistic();
        optimisticTime = System.currentTimeMillis() - time;


        Session session2 = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session2.getTransaction().begin();
        List<Items> items = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            items.add(new Items(0, 0));
            session2.persist(items.get(i));
        }
        session2.getTransaction().commit();
        time = System.currentTimeMillis();
        pessimistic(session2);
        pessimisticTime = System.currentTimeMillis() - time;

        System.out.println("Оптимисты закончили свою работу за " + optimisticTime + " мс");
        System.out.println("Кол-во исключительных оптимистов: " + exceptionCount);
        System.out.println("Пессимисты закончили свою работу за " + pessimisticTime + " мс");
    }



    public static void UncheckableSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void optimistic() throws InterruptedException {
        Session session1 = HibernateSessionFactoryUtil.getSessionFactory().openSession();

        session1.getTransaction().begin();

        List<Items> items = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            items.add(new Items(0, 0));
            session1.persist(items.get(i));
        }

        session1.getTransaction().commit();

        CountDownLatch cdl = new CountDownLatch(8);

        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                Session session_t = HibernateSessionFactoryUtil.getSessionFactory().openSession();

                Random rnd = new Random();

                for (int j = 0; j < 20000; j++) {
                    try {
                        session_t.beginTransaction();
                        Items item = session_t.get(Items.class, rnd.nextInt(40) + 1);
                        item.setValue(item.getValue() + 1);
                        session_t.getTransaction().commit();
                    }
                    catch (OptimisticLockException a) {
                        session_t.getTransaction().rollback();
                        j--;
                        exceptionCount++;
                    }

                    UncheckableSleep(2);
                }
                System.out.println("Готово");


                session_t.close();

                cdl.countDown();
            }).start();
        }
        cdl.await();

        List res = session1.createQuery("select sum(i.value) from Items i").getResultList();
        System.out.println("Сумма всех values: " + res.get(0));
        session1.close();
    }

    public static void pessimistic(Session session) throws InterruptedException {





        CountDownLatch cdl = new CountDownLatch(8);

        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                Session session_t = HibernateSessionFactoryUtil.getSessionFactory().openSession();

                for (int j = 0; j < 20000; j++) {
                    try {
                        session_t.beginTransaction();

                        Items item = session_t.createQuery("from Items where id = :id", Items.class)
                                .setParameter("id", (int) (Math.random() * 40 + 1))
                                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                                .getSingleResult();

                        item.setValue(item.getValue() + 1);

                        session_t.persist(item);
                        session_t.getTransaction().commit();
                    }
                    catch (HibernateException e) {
                        session_t.getTransaction().rollback();
                        j--;
                    }
                    catch(OptimisticLockException art){
                        j--;
                        session_t.getTransaction().rollback();
                    }

                    UncheckableSleep(2);
                }

                System.out.println("Готово");

                session_t.close();

                cdl.countDown();
            }).start();
        }
        cdl.await();

        List res = session.createQuery("select sum(i.value) from Items i").getResultList();
        System.out.println("Сумма всех values: " + res.get(0));


        session.close();
    }
}