package servlet;

import dao.MainDAO;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class MainServletTest{

    @Test
    public void testHandleRequest() {
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);

        PrintWriter writer = mock(PrintWriter.class);
        try {
            when(resp.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }

        //Setup mocked static classes
        mockStatic(MainDAO.class);
        Map<String, String> mainPageCounts = new HashMap();
        mainPageCounts.put("count1", "1");
        mainPageCounts.put("count2", "2");
        when(MainDAO.getMainPageCounts()).thenReturn(mainPageCounts);

        MainServlet mainServlet = new MainServlet();

        try {
            mainServlet.handleRequest(req, resp);
        } catch (ServletException e) {
            System.out.println("Servlet Exception " + e.getMessage());
        }

        assertEquals(0, resp.getStatus());
        verify(writer).write("{\"map\":{\"mainPageCounts\":{\"map\":{\"count1\":\"1\",\"count2\":\"2\"}}}}");
    }
}