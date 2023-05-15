/**
 * Copyright 2023 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RSAT19CB0000020 awarded by the United
 * States Department of Homeland Security.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the �Software�), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package servlet;

import dao.MainDAO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class MainServletTest{
    private static MockedStatic<MainDAO> mockedMain;

    @BeforeClass
    public static void init() {
        mockedMain = mockStatic(MainDAO.class);
    }

    @AfterClass
    public static void close() {
        mockedMain.close();
    }

    @Test
    public void testHandleRequest() {
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);

        PrintWriter writer = mock(PrintWriter.class);
        try {
            when(resp.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        //Setup mocked static classes
        Map<String, String> mainPageCounts = new HashMap();
        mainPageCounts.put("count1", "1");
        mainPageCounts.put("count2", "2");
        when(MainDAO.getMainPageCounts()).thenReturn(mainPageCounts);

        MainServlet mainServlet = new MainServlet();

        try {
            mainServlet.doGet(req, resp);
        } catch (Exception e) {
            System.err.println("Exception Caught: " + e.getMessage());
        }

        assertEquals(0, resp.getStatus());
        verify(writer).write("{\"map\":{\"mainPageCounts\":{\"map\":{\"count1\":\"1\",\"count2\":\"2\"}}}}");
    }
}