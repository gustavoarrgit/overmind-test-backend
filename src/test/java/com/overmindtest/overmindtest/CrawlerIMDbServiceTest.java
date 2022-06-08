package com.overmindtest.overmindtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.overmindtest.overmindtest.exception.CrawlerException;

@ExtendWith(MockitoExtension.class)
 class CrawlerIMDbServiceTest {
	@InjectMocks
	private CrawlerIMDbService service;
	
	@Test
	void testCrawlerIMDb() throws CrawlerException {
		service.crawl(1,10);
		
	}
}
