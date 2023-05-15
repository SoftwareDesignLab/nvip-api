/**
 * Copyright 2023 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RSAT19CB0000020 awarded by the United
 * States Department of Homeland Security.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

import dao.ReviewDAO;
import dao.UserDAO;
import model.User;
import model.VulnerabilityDetails;
import model.VulnerabilityForReviewList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

public class ReviewServletTest {
    private static MockedStatic<UserDAO> mockedUser;

    private static MockedStatic<ReviewDAO> mockedReview;

    @BeforeClass
    public static void init() {
        mockedUser = mockStatic(UserDAO.class);
        mockedReview = mockStatic(ReviewDAO.class);
    }

    @AfterClass
    public static void close() {
        mockedUser.close();
        mockedReview.close();
    }

    @Test
    public void testHandleRequestGetNoUser() {
        //Mock servlet response/request
        HttpServletResponse rep = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);

        //Setup response's writer
        PrintWriter writer = mock(PrintWriter.class);
        try {
            when(rep.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        //Set output for request's user information
        String username = null;
        String token = null;
        when(req.getParameter("username")).thenReturn(username);
        when(req.getParameter("token")).thenReturn(token);

        User testUser = null;
        when(UserDAO.getRoleIDandExpirationDate(username, token)).thenReturn(testUser);

        ReviewServlet reviewServlet = new ReviewServlet();
        try {
            reviewServlet.doGet(req, rep);
        } catch (Exception e) {
            System.err.println("Exception Caught: " + e.getMessage());
        }

        verify(writer).write("Unauthorized user!");
        verify(writer).write("Unauthorized user by id get!");
        verify(rep, atLeastOnce()).setStatus(401);
    }

    @Test
    public void testHandleRequestGetNoCVE() {
        //Mock servlet response/request
        HttpServletResponse rep = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);

        //Setup response's writer
        PrintWriter writer = mock(PrintWriter.class);
        try {
            when(rep.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        //Setup user information
        when(req.getParameter("username")).thenReturn("testUser");
        when(req.getParameter("token")).thenReturn("1");
        User user = new User("1", "testUser", "testFirst", "testLast", "testEmail", 1);
        when(UserDAO.getRoleIDandExpirationDate("testUser", "1")).thenReturn(user);

        //Setup CVE search results information
        when(req.getParameter("cveID")).thenReturn(null);
        List<VulnerabilityForReviewList> reviewList = new LinkedList<>();
        reviewList.add(new VulnerabilityForReviewList("123", "123", "1"));
        reviewList.add(new VulnerabilityForReviewList("456", "456", "2"));
        reviewList.add(new VulnerabilityForReviewList("789", "789", "3"));
        when(ReviewDAO.getSearchResults(null, false, false, false, false)).thenReturn(reviewList);

        ReviewServlet reviewServlet = new ReviewServlet();
        try {
            reviewServlet.doGet(req, rep);
        } catch (Exception e) {
            System.err.println("Exception Caught: " + e.getMessage());
        }

        assertEquals(0, rep.getStatus());
        verify(writer).write("[\n" +
                "  {\n" +
                "    \"vuln_id\": \"123\",\n" +
                "    \"cve_id\": \"123\",\n" +
                "    \"status_id\": \"1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"vuln_id\": \"456\",\n" +
                "    \"cve_id\": \"456\",\n" +
                "    \"status_id\": \"2\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"vuln_id\": \"789\",\n" +
                "    \"cve_id\": \"789\",\n" +
                "    \"status_id\": \"3\"\n" +
                "  },\n" +
                "  3\n" +
                "]");
    }

    @Test
    public void testHandleRequestGet() {
        //Mock servlet response/request
        HttpServletResponse rep = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);

        //Setup response's writer
        PrintWriter writer = mock(PrintWriter.class);
        try {
            when(rep.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        //Setup user information
        when(req.getParameter("username")).thenReturn("testUser");
        when(req.getParameter("token")).thenReturn("1");
        User user = new User("1", "testUser", "testFirst", "testLast", "testEmail", 1);
        when(UserDAO.getRoleIDandExpirationDate("testUser", "1")).thenReturn(user);

        //Setup CVE search results information
        when(req.getParameter("cveID")).thenReturn("testCVE");
        VulnerabilityDetails vulnDetails = new VulnerabilityDetails("123", "123", "testDesc", "testStatus", "testClass", "testScore");
        when(ReviewDAO.getVulnerabilityDetails("testCVE")).thenReturn(vulnDetails);

        ReviewServlet reviewServlet = new ReviewServlet();
        try {
            reviewServlet.doGet(req, rep);
        } catch (Exception e) {
            System.err.println("Exception Caught: " + e.getMessage());
        }

        assertEquals(0, rep.getStatus());
        verify(writer).write("{\n" +
                "  \"vuln_id\": \"123\",\n" +
                "  \"cve_id\": \"123\",\n" +
                "  \"description\": \"testDesc\",\n" +
                "  \"status_id\": \"testStatus\",\n" +
                "  \"cvss_class\": \"testClass\",\n" +
                "  \"impact_score\": \"testScore\",\n" +
                "  \"vdoGroups\": {},\n" +
                "  \"vulnDomain\": []\n" +
                "}");
    }

    @Test
    public void testHandleRequestPostNoUser() {
        //Mock servlet response/request
        HttpServletResponse rep = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);

        //Setup response's writer
        PrintWriter writer = mock(PrintWriter.class);
        try {
            when(rep.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        //Set output for request's user information
        String username = null;
        String token = null;
        when(req.getParameter("username")).thenReturn(username);
        when(req.getParameter("token")).thenReturn(token);

        User testUser = null;
        when(UserDAO.getRoleIDandExpirationDate(username, token)).thenReturn(testUser);

        ReviewServlet reviewServlet = new ReviewServlet();
        try {
            reviewServlet.doPost(req, rep);
        } catch (Exception e) {
            System.err.println("Exception Caught: " + e.getMessage());
        }

        verify(writer, atLeastOnce()).write("Unauthorized user!");
        verify(rep, atLeastOnce()).setStatus(401);
    }

    @Test
    public void testHandleRequestPostAtomic() {
        //Mock servlet response/request
        HttpServletResponse rep = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);

        //Setup response's writer
        PrintWriter writer = mock(PrintWriter.class);
        try {
            when(rep.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        //Setup request's reader
        BufferedReader reader = mock(BufferedReader.class);
        try {
            when(req.getReader()).thenReturn(reader);
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        //Setup user information
        when(req.getParameter("username")).thenReturn("testUser");
        when(req.getParameter("token")).thenReturn("1");
        User user = new User("1", "testUser", "testFirst", "testLast", "testEmail", 1);
        user.setUserID(1);
        when(UserDAO.getRoleIDandExpirationDate("testUser", "1")).thenReturn(user);

        when(req.getParameter("complexUpdate")).thenReturn("false");
        when(req.getParameter("atomicUpdate")).thenReturn("true");
        when(req.getParameter("updateDailyTable")).thenReturn("false");

        when(req.getParameter("cveID")).thenReturn("123");
        when(req.getParameter("statusID")).thenReturn("4");
        when(req.getParameter("vulnID")).thenReturn("123");
        when(req.getParameter("info")).thenReturn("test description");
        when(req.getParameter("tweet")).thenReturn("false");

        ReviewServlet reviewServlet = new ReviewServlet();
        try {
            reviewServlet.doPost(req, rep);
        } catch (Exception e) {
            System.err.println("Exception Caught: " + e.getMessage());
        }

        verifyNoInteractions(writer);
        assertEquals(0, rep.getStatus());
    }
}