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

import dao.UserDAO;
import model.User;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

public class ReviewServletTest {

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
            System.out.println("IO Exception: " + e.getMessage());
        }

        //Set output for request's user information
        String username = null;
        String token = null;
        when(req.getParameter("username")).thenReturn(username);
        when(req.getParameter("token")).thenReturn(token);

        User testUser = null;
        mockStatic(UserDAO.class);
        when(UserDAO.getRoleIDandExpirationDate(username, token)).thenReturn(testUser);

        ReviewServlet reviewServlet = new ReviewServlet();
        try {
            reviewServlet.doGet(req, rep);
        } catch (Exception e) {
            System.out.println("Exception Caught: " + e.getMessage());
        }

        verify(writer).write("Unauthorized user!");
        verify(writer).write("Unauthorized user by id get!");
        verify(rep, atLeastOnce()).setStatus(401);
    }
}