package web_updater;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WebPageRepository extends JpaRepository<WebPage, String> {
}
