// derived from Project 2 Login Cart example: https://github.com/UCI-Chenli-teaching/cs122b-project2-login-cart-example/blob/main/src/LoginFilter.java

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        // check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            // keep default action: pass along the filter chain
            chain.doFilter(request, response);

            return;
        }

        // redirect to login page if the "user" attribute does not exist in session
        if (httpRequest.getSession().getAttribute("user") == null) {
            httpResponse.sendRedirect("login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith) || requestURI.contains("_dashboard");
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("api/dashboard_login");

        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
    }

    public void destroy() {
        // ignore
    }
}
