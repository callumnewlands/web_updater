package web_updater.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web_updater.model.WebPage;

@Repository
public interface WebPageRepository<T extends WebPage> extends JpaRepository<T, String> {
}
