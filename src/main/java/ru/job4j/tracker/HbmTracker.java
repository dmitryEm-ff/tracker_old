package ru.job4j.tracker;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class HbmTracker implements Store, AutoCloseable {
    private StandardServiceRegistry registry;
    private SessionFactory sf;

    @Override
    public void init() {
        this.registry = new StandardServiceRegistryBuilder()
                .configure().build();
        this.sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
    }

    @Override
    public Item add(Item item) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(item);
        session.getTransaction().commit();
        session.close();
        return item;
    }

    @Override
    public boolean replace(String id, Item item) {
        if (findById(String.valueOf(id)) != null) {
            item.setId(Integer.parseInt(id));
            Session session = sf.openSession();
            session.beginTransaction();
            session.update(item);
            session.getTransaction().commit();
            session.close();
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        Item item = findById(id);
        if (item != null) {
            Session session = sf.openSession();
            session.beginTransaction();
            session.delete(item);
            session.getTransaction().commit();
            session.close();
            return true;
        }
        return false;
    }

    @Override
    public List<Item> findAll() {
        Session session = sf.openSession();
        session.beginTransaction();
        List items = session.createQuery("from ru.job4j.tracker.Item").list();
        session.getTransaction().commit();
        session.close();
        return items;
    }

    @Override
    public List<Item> findByName(String key) {
        Session session = sf.openSession();
        session.beginTransaction();
        List items = session.createQuery("from ru.job4j.tracker.Item where name = :key")
                .setParameter("key", key)
                .list();
        session.getTransaction().commit();
        session.close();
        return items;
    }

    @Override
    public Item findById(String id) {
        Session session = sf.openSession();
        session.beginTransaction();
        Item item = session.get(Item.class, Integer.parseInt(id));
        session.getTransaction().commit();
        session.close();
        return item;
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }
}