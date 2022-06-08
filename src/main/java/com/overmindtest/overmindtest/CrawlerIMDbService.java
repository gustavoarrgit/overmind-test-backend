package com.overmindtest.overmindtest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.overmindtest.overmindtest.dto.CrawlerDTO;
import com.overmindtest.overmindtest.exception.CrawlerException;

@Service
public class CrawlerIMDbService {
	private static final String TITLE_COLUMN = "titleColumn";
	private static final String AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) "
			+ "AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private static final String REVIEW_LINK = "reviews?ref_=tt_urv";
	private HashSet<String> links;
	private List<CrawlerDTO> resultados = Collections.emptyList();
	private List<String> frontiers;

	public CrawlerIMDbService() {
		links = new HashSet<>();
		frontiers = new ArrayList<>();
	}

	public void addFrontier(String fronteira) {
		frontiers.add(fronteira);
	}

	public List<CrawlerDTO> crawl(int level, int position) throws CrawlerException {
		if (position > 100) {
			throw new CrawlerException("Quantidade máxima é 100 posições");
		}
		addFrontier("https://www.imdb.com/chart/bottom");
		frontiers.forEach(url -> crawlerPageLinks(url, level));

		return crawlerElements(position);
	}

	private void crawlerPageLinks(String url, int depth) {
		if ((!links.contains(url) && (0 < depth) && !url.trim().isEmpty())) {
			try {
				links.add(url);
				Document document = Jsoup.connect(url).userAgent(AGENT).get();
				Elements link = document.select("a[href]");
				for (Element page : link) {
					crawlerPageLinks(page.attr("abs:href"), --depth);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private List<CrawlerDTO> crawlerElements(int qtd) {
		links.forEach(url -> {
			Document document;
			try {
				resultados = new ArrayList<>();
				document = Jsoup.connect(url).userAgent(AGENT).header("Accept-Language", "en").get();
				Elements tableElements = document.getElementsByAttributeValue("data-caller-name", "chart-bttm100movie");

				for (int pos = 0; pos < qtd; pos++) {
					String title = getElementsTitleColumn(tableElements, TITLE_COLUMN).get(pos).text();
					String position = getElementsTitleColumn(tableElements, "posterColumn").get(pos)
							.getElementsByAttributeValue("name", "rk").attr("data-value");
					String rating = tableElements.get(0).getElementsByAttributeValue("class", "ratingColumn imdbRating")
							.get(pos).text();
					List<String> actorList = new ArrayList<>();
					String director = diffActorDirectorAndComment(tableElements, pos, actorList);
					String comment = getCommentsForMovie(tableElements, pos);

					resultados.add(CrawlerDTO.builder().position(Integer.valueOf(position)).title(title)
							.rating(Double.valueOf(rating)).director(director).casting(actorList).comment(comment)
							.build());
					resultados = resultados.stream().sorted(Comparator.comparingInt(CrawlerDTO::getPosition).reversed())
							.collect(Collectors.toList());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return resultados;
	}

	private Elements getElementsTitleColumn(Elements tableElements, String param) {
		return tableElements.get(0).getElementsByAttributeValue("class", param);
	}

	private String diffActorDirectorAndComment(Elements tableElements, int pos, List<String> actorList) {
		String[] casting = getElementsTitleColumn(tableElements, TITLE_COLUMN).get(pos).childNode(1).attr("title")
				.split(Pattern.quote(","));
		String director = "";
		for (String actor : casting) {
			if (actor.contains("(dir.)")) {
				director = actor;
				continue;
			}
			actorList.add(actor);
		}
		return director;
	}

	private String getCommentsForMovie(Elements tableElements, int pos) throws IOException {
		Elements linkComments = getElementsTitleColumn(tableElements, TITLE_COLUMN).get(pos).select("a[href]");
		String url = linkComments.attr("href");
		Document d = Jsoup.connect(recoverLinkReview(url)).userAgent(AGENT).header("Accept-Language", "en")
				.data("ratingFilter", "5").post();
		Elements acessElements = d.getElementsByAttributeValue("class", "lister-item-content");

		return getComments(acessElements);
	}

	private String getComments(Elements acessElements) {
		int commentPosition = 0;
		try {
			while (acessElements.size() != commentPosition) {
				Elements comment = acessElements.get(commentPosition).getElementsByAttributeValue("class",
						"rating-other-user-rating");
				if (comment == null || comment.isEmpty()) {
					commentPosition++;
					continue;
				}
				int value = Integer.parseInt(((TextNode) comment.get(0).childNode(3).childNode(0)).text());
				if (value < 5) {
					commentPosition++;
				} else {
					return acessElements.get(commentPosition).getElementsByAttributeValue("class", "title").text();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private String recoverLinkReview(String url) {
		String[] idMovie = url.split("\\?");
		return "https://www.imdb.com" + idMovie[0] + REVIEW_LINK;
	}

	public void print() {
		resultados.forEach(a -> {
			System.out.println(a.toString() + "\n");
		});
	}

	public static void main(String[] args) {
		CrawlerIMDbService bwc = new CrawlerIMDbService();
		bwc.addFrontier("https://www.imdb.com/chart/bottom");
		try {
			bwc.crawl(1, 20);
		} catch (CrawlerException e) {
			e.printStackTrace();
		}
		bwc.print();
	}
}
