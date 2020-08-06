package database;

import database.models.Game;
import database.models.Message;
import database.models.Player;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class databaseManager {
    EntityManagerFactory entityManagerFactory =  Persistence.createEntityManagerFactory("FatalFuryPersistence");
    EntityManager em = entityManagerFactory.createEntityManager();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    public databaseManager(){}

    public String save(Object obj){
        String ok = "OK";
        try {
            EntityTransaction trans = em.getTransaction();
            trans.begin();
            em.persist(obj);
            trans.commit();
        }catch (Exception e){
            e.printStackTrace();
            ok = e.getMessage();
        }
        return ok;
    }

    public Object findByKey(Class c, Object id){
        try{
            return em.find(c, id);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getUserMessages(String user1, String user2){
        String sql = "SELECT * FROM message m WHERE ((m.receiver LIKE '" + user1 + "' OR m.transmitter LIKE '" + user1 + "')" +
                " AND (m.receiver LIKE '" + user2 + "' OR m.transmitter LIKE '" + user2 + "')) ORDER BY m.id ASC;";
        Query q = em.createNativeQuery(sql, Message.class);
        List<Message> list = q.getResultList();
        if(list == null){return new ArrayList<>();}
        return list;
    }

    public List<Game> getUserLastGames(String user){
        String sql = "SELECT * FROM game m WHERE (m.player1 LIKE '" + user + "' OR m.player2 LIKE '" + user + "') ORDER BY m.id DESC LIMIT 10;";
        Query q = em.createNativeQuery(sql, Game.class);
        try {
            List<Game> list = q.getResultList();
            if(list == null){return new ArrayList<>();}
            return list;
        }catch (Exception e){e.printStackTrace();}

        return null;
    }

    public Player findPlayerByEmail(String email){
        try{
            TypedQuery<Player> query = em.createQuery("SELECT p FROM PLAYER p WHERE p.email = :email", Player.class);
            TypedQuery<Player> result = query.setParameter("email", email);
            try{
                return result.getSingleResult();
            }catch (Exception e){return null;}
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String insert(Object obj, Object id){
        String ok = "OK";
        Object aux = findByKey(obj.getClass(), id);
        if(aux == null){
            ok = save(obj);
        }
        else{
            ok = "ERROR:El identificador ya está en uso.";
        }
        return ok;
    }

    public String insertPlayer(Player p){
        String ok = "OK";
        Player aux = (Player) findByKey(Player.class, p.getUsername());
        if(aux == null){
            aux = findPlayerByEmail(p.getEmail());
            if(aux == null){
                Set<ConstraintViolation<Player>> violations = validator.validate(p);
                if(violations.size() == 0) {
                    ok = save(p);
                }
                else{
                    ok = "";
                    int i = 0;
                    for (ConstraintViolation<Player> violation : violations) {
                        ok += violation.getMessage();
                        if(i < violations.size()-1){ok += "\n";}
                        ++i;
                    }
                }
            }
            else {
                ok = "ERROR:El correo electrónico ya está en uso.";
            }
        }
        else{
            ok = "ERROR:El nombre de usuario ya está en uso.";
        }
        return ok;
    }

    public String registerPlayer(String username, String email, String password){
        Player p = new Player(username, email, password, true);
        return insertPlayer(p);
    }

    public synchronized void close(){
        em.close();
        entityManagerFactory.close();
    }
}
