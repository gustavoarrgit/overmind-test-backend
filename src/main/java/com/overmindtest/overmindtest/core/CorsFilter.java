package com.overmindtest.overmindtest.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

	@Value("${cors.origenPermitida}")
	private String originPermitida;

	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final String ACCESS_CONTROL_ALLOW_HEADRS = "Access-Control-Allow-Headers";
	private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	private static final String ACCESS_CONTROL_METHOD_AGE = "Access-Control-Method-Age";

	private static final String HEADERS_PERMITIDOS = "origin, content-type, accept, authorization";
	private static final String CREDENCIAIS_PERMITIDAS = "true";
	private static final String METHODS_PERMITIDOS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
	private static final String VALOR_METHOD_AGE = "4500";

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, originPermitida);
		response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, METHODS_PERMITIDOS);
		response.setHeader(ACCESS_CONTROL_ALLOW_HEADRS, HEADERS_PERMITIDOS);
		response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, CREDENCIAIS_PERMITIDAS);
		response.setHeader(ACCESS_CONTROL_METHOD_AGE, VALOR_METHOD_AGE);

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(request, response);
		}
	}

}
