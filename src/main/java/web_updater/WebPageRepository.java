package web_updater;

import org.jsoup.nodes.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WebPageRepository extends JpaRepository<WebPage, String> {
	@Modifying
	@Query("update WebPage p set p.changed = ?1 where p.URL = ?2")
	void setChangedByURL(Boolean changed, String url);

	@Modifying
	@Query("update WebPage p set p.oldHtml = ?1 where p.URL = ?2")
	void setOldHTMLByURL(Document oldHTML, String url);
}
