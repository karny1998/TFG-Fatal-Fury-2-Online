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

/**
 * The type Database manager.
 */
public class databaseManager {
    /**
     * The Entity manager factory.
     */
    EntityManagerFactory entityManagerFactory =  Persistence.createEntityManagerFactory("FatalFuryPersistence");
    /**
     * The Em.
     */
    EntityManager em = entityManagerFactory.createEntityManager();
    /**
     * The Factory.
     */
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    /**
     * The Validator.
     */
    Validator validator = factory.getValidator();

    /**
     * Instantiates a new Database manager.
     */
    public databaseManager(){}

    /**
     * Save string.
     *
     * @param obj the obj
     * @return the string
     */
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

    /**
     * Remove string.
     *
     * @param obj the obj
     * @return the string
     */
    public String remove(Object obj){
        String ok = "OK";
        try {
            EntityTransaction trans = em.getTransaction();
            trans.begin();
            em.remove(obj);
            trans.commit();
        }catch (Exception e){
            e.printStackTrace();
            ok = e.getMessage();
        }
        return ok;
    }

    /**
     * Refresh.
     *
     * @param obj the obj
     */
    public void refresh(Object obj){
        em.refresh(obj);
    }

    /**
     * Find by key object.
     *
     * @param c  the c
     * @param id the id
     * @return the object
     */
    public Object findByKey(Class c, Object id){
        try{
            return em.find(c, id);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Get global ia times int.
     *
     * @return the int
     */
    public int getGlobalIaTimes(){
        String sql = "SELECT SUM(times_vs_global_ia) FROM player;";
        Query q = em.createNativeQuery(sql);
        int value = ((Number) q.getSingleResult()).intValue();
        return value;
    }

    /**
     * Get user messages list.
     *
     * @param user1 the user 1
     * @param user2 the user 2
     * @return the list
     */
    public List<Message> getUserMessages(String user1, String user2){
        String sql = "SELECT * FROM message m WHERE ((m.receiver LIKE '" + user1 + "' OR m.transmitter LIKE '" + user1 + "')" +
                " AND (m.receiver LIKE '" + user2 + "' OR m.transmitter LIKE '" + user2 + "')) ORDER BY m.id ASC;";
        Query q = em.createNativeQuery(sql, Message.class);
        List<Message> list = q.getResultList();
        if(list == null){return new ArrayList<>();}
        return list;
    }

    /**
     * Get user last games list.
     *
     * @param user the user
     * @return the list
     */
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

    /**
     * Get ranking list.
     *
     * @return the list
     */
    public List<Player> getRanking(){
        String sql = "SELECT * FROM player m ORDER BY m.rankscore DESC LIMIT 100;";
        Query q = em.createNativeQuery(sql, Player.class);
        try {
            List<Player> list = q.getResultList();
            if(list == null){return new ArrayList<>();}
            return list;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    /**
     * Find player by email player.
     *
     * @param email the email
     * @return the player
     */
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

    /**
     * Insert string.
     *
     * @param obj the obj
     * @param id  the id
     * @return the string
     */
    public String insert(Object obj, Object id){
        String ok = "OK";
        Object aux = findByKey(obj.getClass(), id);
        if(aux == null){
            ok = save(obj);
        }
        else{
            ok = "ERROR:Identifier already in use.";
        }
        return ok;
    }

    /**
     * Insert player string.
     *
     * @param p the p
     * @return the string
     */
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
                ok = "ERROR:Email already in use.";
            }
        }
        else{
            ok = "ERROR:Username already in use.";
        }
        return ok;
    }

    /**
     * Register player string.
     *
     * @param username the username
     * @param email    the email
     * @param password the password
     * @return the string
     */
    public String registerPlayer(String username, String email, String password){
        Player p = new Player(username, email, password, false);
        return insertPlayer(p);
    }

    /**
     * Close.
     */
    public synchronized void close(){
        em.close();
        entityManagerFactory.close();
    }
}
