package web_updater.database;

import org.springframework.data.jpa.repository.JpaRepository;
import web_updater.model.WebPage;

public interface WebPageRepository<T extends WebPage> extends JpaRepository<T, String> {
}
