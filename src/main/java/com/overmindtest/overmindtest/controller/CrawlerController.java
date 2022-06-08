package com.overmindtest.overmindtest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.overmindtest.overmindtest.CrawlerIMDbService;
import com.overmindtest.overmindtest.dto.CrawlerDTO;
import com.overmindtest.overmindtest.exception.CrawlerException;

@RestController
@RequestMapping(value = "/crawler")
public class CrawlerController {
	@Autowired
	private CrawlerIMDbService service;
	
	@GetMapping(value = "/{qtd}")
	public ResponseEntity<List<CrawlerDTO>> acaoListar(@PathVariable Integer qtd) throws CrawlerException {
		
			return ResponseEntity.ok().body(this.service.crawl(1, qtd));
	}

}
