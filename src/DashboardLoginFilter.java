import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class DashboardLoginFilter
 */
@WebFilter(filterName = "DashboardLoginFilter", urlPatterns = {"/_dashboard/*"})
public class DashboardLoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("DashboardLoginFilter: " + httpRequest.getRequestURI());

        // check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            // keep default action: pass along the filter chain
            chain.doFilter(request, response);

            return;
        }

        // redirect to login page if the "user" attribute does not exist in session
        if (httpRequest.getSession().getAttribute("employee") == null) {
            httpResponse.sendRedirect("/_dashboard_login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("_dashboard_login.html");
        allowedURIs.add("_dashboard_login.js");
        allowedURIs.add("api/dashboard_login");
    }

    public void destroy() {
        // ignore
    }
}