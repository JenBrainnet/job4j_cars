package ru.job4j.cars.configuration;

import jakarta.annotation.PreDestroy;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfiguration {

    private StandardServiceRegistry registry;

    @Bean(destroyMethod = "close")
    public SessionFactory sf() {
        registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        return new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
    }

    @PreDestroy
    public void destroy() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

}
