package com.johnnyconsole.ims.persistence.impl;

import com.johnnyconsole.ims.persistence.User;
import com.johnnyconsole.ims.persistence.interfaces.UserDaoLocal;

import javax.ejb.Stateful;
import javax.enterprise.inject.Alternative;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateful
@Alternative
public class UserDaoImpl implements UserDaoLocal {

    @PersistenceContext(unitName="user")
    private EntityManager manager;

    @Override
    public User getUser(String username) {
        try {
            Query query = manager.createNamedQuery("User.FindByUsername");
            query.setParameter("username", username);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long userCount() {
        try {
            Query query = manager.createNamedQuery("User.FindCount");
            return (long) query.getSingleResult();
        } catch(Exception e) {
            return 0;
        }
    }

    @Override
    public List<User> getUsersExcept(String username) {
        Query query = manager.createNamedQuery("User.FindAllExcept");
        query.setParameter("username", username);
        return (List<User>) query.getResultList();
    }

    @Override
    public boolean userExists(String username) {
        Query query = manager.createNamedQuery("User.FindByUsername");
        query.setParameter("username", username);
        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean addUser(User user) {
        try {
            manager.persist(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeUser(User user, String myUsername) {
        try {
            if (user.username.equals(myUsername)) {
                return false;
            }
            manager.remove((manager.contains(user) ? user : manager.merge(user)));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean saveUser(User user) {
        try {
            manager.merge(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean verifyUser(String username, String passwordPlainText) {
        try {
            User user = getUser(username);
            return user.verifyPassword(passwordPlainText);
        } catch (Exception e) {
            return false;
        }
    }
}
