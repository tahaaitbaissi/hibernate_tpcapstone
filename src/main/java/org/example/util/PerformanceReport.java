package org.example.util;

import org.hibernate.Session;
import org.hibernate.stat.Statistics;

import javax.persistence.EntityManager;

public class PerformanceReport {

    private final EntityManager em;

    public PerformanceReport(EntityManager em) {
        this.em = em;
    }

    public void printReport() {
        Session session = em.unwrap(Session.class);
        Statistics stats = session.getSessionFactory().getStatistics();

        System.out.println("=== Rapport performance ===");
        System.out.println("Requetes : " + stats.getQueryExecutionCount());
        System.out.println("L2 hits : " + stats.getSecondLevelCacheHitCount());
        System.out.println("L2 miss : " + stats.getSecondLevelCacheMissCount());
        System.out.println("L2 puts : " + stats.getSecondLevelCachePutCount());
        System.out.println("Transactions : " + stats.getTransactionCount());
        System.out.println("Duree max requete (ms) : " + stats.getQueryExecutionMaxTime());
    }
}
