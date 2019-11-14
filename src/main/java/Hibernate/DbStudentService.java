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
    public Transaction tx = null;

    public void saveStudent(DbStudent std) {
        Session session = factory.openSession();
        Integer stdId = null;

        try {
            tx = session.beginTransaction();

            stdId = (Integer) session.save(std);
            tx.commit();

        } catch (
                HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
            System.out.println("saved std: " + std.toString());

        }

    }

    public List<DbStudent> getStudentsList() {
        Session session = factory.openSession();
        List<DbStudent> studentsList = (List<DbStudent>) session.createQuery("FROM DbStudent").list();
        session.close();
        return studentsList;
    }
}
