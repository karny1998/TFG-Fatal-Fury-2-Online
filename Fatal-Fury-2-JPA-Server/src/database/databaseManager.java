package database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class databaseManager {
    EntityManagerFactory entityManagerFactory =  Persistence.createEntityManagerFactory("FatalFuryPersistence");
    EntityManager em = entityManagerFactory.createEntityManager();

    public databaseManager(){}

    public synchronized boolean save(Object obj){
        boolean ok = true;
        try {
            EntityTransaction trans = em.getTransaction();
            trans.begin();
            em.persist(obj);
            trans.commit();
        }catch (Exception e){
            e.printStackTrace();
            ok = false;
        }
        return ok;
    }

    public synchronized Object find(Class c, Object id){
        return em.find(c, id);
    }

    public synchronized void close(){
        em.close();
        entityManagerFactory.close();
    }
}
