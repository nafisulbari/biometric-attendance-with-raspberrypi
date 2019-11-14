package Hibernate;

import Model.DbStudent;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class DbStudentService {

    public SessionFactory factory = new Configuration().configure().addAnnotatedClass(DbStudent.class).buildSessionFactory();
    public Session session = factory.openSession();
    public Transaction tx = null;

    public void saveStudent(DbStudent std) {

        Integer stdId = null;

        try {
            tx = session.beginTransaction();

            std.setStd_Id("16101237");
            String s = "gg";
            byte[] ba = s.getBytes();
            std.setFingerPrint(ba);

            stdId = (Integer) session.save(std);
            tx.commit();

        } catch (
                HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            System.out.println("saved std: " + std.toString());

        }

    }

    public List getStudentsList() {

        List studentsList = session.createQuery("FROM DbStudent").list();

        return studentsList;
    }
}
